// Copyright (c) 2017, Baidu.com, Inc. All Rights Reserved

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.baidu.palo.analysis;

import com.baidu.palo.catalog.Column;
import com.baidu.palo.catalog.ColumnType;
import com.baidu.palo.common.AnalysisException;
import com.baidu.palo.common.ErrorCode;
import com.baidu.palo.common.ErrorReport;
import com.baidu.palo.qe.ShowResultSetMetaData;

import com.google.common.base.Strings;

// Show rollup statement, used to show rollup information of one table.
//
// Syntax:
//      SHOW ROLLUP { FROM | IN } table [ FROM db ]
public class ShowRollupStmt extends ShowStmt {
    private static final ShowResultSetMetaData META_DATA =
            ShowResultSetMetaData.builder()
                    .addColumn(new Column("Table", ColumnType.createVarchar(20)))
                    .addColumn(new Column("RollupHandler", ColumnType.createVarchar(30)))
                    .addColumn(new Column("Columns", ColumnType.createVarchar(50)))
                    .build();
    private TableName tbl;
    private String db;

    public ShowRollupStmt(TableName tbl, String db) {
        this.tbl = tbl;
        this.db = db;
    }

    public String getDb() {
        return tbl.getDb();
    }

    public String getTbl() {
        return tbl.getTbl();
    }

    @Override
    public void analyze(Analyzer analyzer) throws AnalysisException {
        // if both `db` and `table` have database have database info, use `db` information.
        // 1. use `db` database info
        // 2. use `table` database info
        // 3. use default database info in analyzer.
        if (tbl == null) {
            ErrorReport.reportAnalysisException(ErrorCode.ERR_NO_TABLES_USED);
        }
        if (!Strings.isNullOrEmpty(db)) {
            // overwrite database in tbl.
            tbl.setDb(db);
        }
        tbl.analyze(analyzer);
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("SHOW ROLLUP FROM ").append(tbl.toSql());
        return sb.toString();
    }

    @Override
    public String toString() {
        return toSql();
    }

    @Override
    public ShowResultSetMetaData getMetaData() {
        return META_DATA;
    }
    
    @Override
    public RedirectStatus getRedirectStatus() {
        return RedirectStatus.FORWARD_NO_SYNC;
    }
}
