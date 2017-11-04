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

package com.baidu.palo.load;

import com.baidu.palo.analysis.BrokerDesc;
import com.baidu.palo.analysis.ColumnSeparator;
import com.baidu.palo.analysis.DataDescription;
import com.baidu.palo.analysis.Expr;
import com.baidu.palo.catalog.BrokerTable;
import com.baidu.palo.catalog.Database;
import com.baidu.palo.catalog.OlapTable;
import com.baidu.palo.catalog.Partition;
import com.baidu.palo.catalog.Table;
import com.baidu.palo.common.AnalysisException;
import com.baidu.palo.common.DdlException;
import com.baidu.palo.common.io.Text;
import com.baidu.palo.common.io.Writable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A broker file group information, one @DataDescription will
 * produce one BrokerFileGroup. After parsed by broker, detailed
 * broker file information will be saved here.
 */
public class BrokerFileGroup implements Writable {
    private static final Logger LOG = LogManager.getLogger(BrokerFileGroup.class);

    // input
    private DataDescription dataDescription;

    // Now we don't save this in image, only use this to parse DataDescription;
    // if we have this require later, we save this here.
    private BrokerDesc brokerDesc;

    private long tableId;
    private String valueSeparator;
    private String lineDelimiter;
    private boolean isNegative;
    private List<Long> partitionIds;
    private List<String> valueNames;
    private List<String> filePathes;

    // This column need expression to get column
    private Map<String, Expr> exprColumnMap;


    // Used for recovery from edit log
    private BrokerFileGroup() {
    }

    // Used for broker table, no need to parse
    public BrokerFileGroup(BrokerTable table) throws AnalysisException {
        this.tableId = table.getId();
        this.valueSeparator = ColumnSeparator.convertSeparator(table.getColumnSeparator());
        this.lineDelimiter = table.getLineDelimiter();
        this.isNegative = false;
        this.filePathes = table.getPaths();
    }

    public BrokerFileGroup(DataDescription dataDescription, BrokerDesc desc) {
        this.dataDescription = dataDescription;
        this.brokerDesc = desc;
        exprColumnMap = dataDescription.getParsedExprMap();
    }

    // NOTE: DBLock will be held
    // This will parse the input DataDescription to list for BrokerFileInfo
    public void parse(Database db) throws DdlException {
        // tableId
        Table table = db.getTable(dataDescription.getTableName());
        if (table == null) {
            throw new DdlException("Unknown table(" + dataDescription.getTableName()
                    + ") in database(" + db.getFullName() + ")");
        }
        if (!(table instanceof OlapTable)) {
            throw new DdlException("Table(" + table.getName() + ") is not OlapTable");
        }
        OlapTable olapTable = (OlapTable) table;
        tableId = table.getId();

        // partitionId
        if (dataDescription.getPartitionNames() != null) {
            if (dataDescription.getPartitionNames().size() == 0) {
                throw new DdlException("Partition names size is 0, at least 1.");
            }
            partitionIds = Lists.newArrayList();
            for (String pName : dataDescription.getPartitionNames()) {
                Partition partition = olapTable.getPartition(pName);
                if (partition == null) {
                    throw new DdlException("Unknown partition(" + pName + ") in table("
                            + table.getName() + ")");
                }
                partitionIds.add(partition.getId());
            }
        }

        // valueNames
        if (dataDescription.getColumnNames() != null) {
            valueNames = Lists.newArrayList(dataDescription.getColumnNames());
        }

        // column
        valueSeparator = dataDescription.getColumnSeparator();
        if (valueSeparator == null) {
            valueSeparator = "\t";
        }
        lineDelimiter = dataDescription.getLineDelimiter();
        if (lineDelimiter == null) {
            lineDelimiter = "\n";
        }
        isNegative = dataDescription.isNegative();

        // FilePath
        filePathes = dataDescription.getFilePathes();
    }

    public long getTableId() {
        return tableId;
    }

    public String getValueSeparator() {
        return valueSeparator;
    }

    public String getLineDelimiter() {
        return lineDelimiter;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public List<String> getValueNames() {
        return valueNames;
    }

    public List<Long> getPartitionIds() {
        return partitionIds;
    }

    public List<String> getFilePathes() {
        return filePathes;
    }

    public Map<String, Expr> getExprColumnMap() {
        return exprColumnMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BrokerFileGroup{tableId=").append(tableId);
        if (partitionIds != null) {
            sb.append(",partitionIds=[");
            int idx = 0;
            for (long id : partitionIds) {
                if (idx++ != 0) {
                    sb.append(",");
                }
                sb.append(id);
            }
            sb.append("]");
        }
        if (valueNames != null) {
            sb.append(",valueNames=[");
            int idx = 0;
            for (String name : valueNames) {
                if (idx++ != 0) {
                    sb.append(",");
                }
                sb.append(name);
            }
            sb.append("]");
        }
        sb.append(",valueSeparator=").append(valueSeparator)
                .append(",lineDelimiter=").append(lineDelimiter)
                .append(",isNegative=").append(isNegative);
        sb.append(",fileInfos=[");
        int idx = 0;
        for (String path : filePathes) {
            if (idx++ != 0) {
                sb.append(",");
            }
            sb.append(path);
        }
        sb.append("]");
        sb.append("}");

        return sb.toString();
    }


    @Override
    public void write(DataOutput out) throws IOException {
        // tableId
        out.writeLong(tableId);
        // valueSeparator
        Text.writeString(out, valueSeparator);
        // lineDelimiter
        Text.writeString(out, lineDelimiter);
        // isNegative
        out.writeBoolean(isNegative);
        // partitionIds
        if (partitionIds == null) {
            out.writeInt(0);
        } else {
            out.writeInt(partitionIds.size());
            for (long id : partitionIds) {
                out.writeLong(id);
            }
        }
        // valueNames
        if (valueNames == null) {
            out.writeInt(0);
        } else {
            out.writeInt(valueNames.size());
            for (String name : valueNames) {
                Text.writeString(out, name);
            }
        }
        // filePathes
        out.writeInt(filePathes.size());
        for (String path : filePathes) {
            Text.writeString(out, path);
        }
        // expr column map
        if (exprColumnMap == null) {
            out.writeInt(0);
        } else {
            int size = exprColumnMap.size();
            out.writeInt(size);
            for (Map.Entry<String, Expr> entry : exprColumnMap.entrySet()) {
                Text.writeString(out, entry.getKey());
                Expr.writeTo(entry.getValue(), out);
            }
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        tableId = in.readLong();
        valueSeparator = Text.readString(in);
        lineDelimiter = Text.readString(in);
        isNegative = in.readBoolean();
        // partitionIds
        {
            int partSize = in.readInt();
            if (partSize > 0) {
                partitionIds = Lists.newArrayList();
                for (int i = 0; i < partSize; ++i) {
                    partitionIds.add(in.readLong());
                }
            }
        }
        // valueNames
        {
            int valueNameSize = in.readInt();
            if (valueNameSize > 0) {
                valueNames = Lists.newArrayList();
                for (int i = 0; i < valueNameSize; ++i) {
                    valueNames.add(Text.readString(in));
                }
            }
        }
        // fileInfos
        {
            int size = in.readInt();
            filePathes = Lists.newArrayList();
            for (int i = 0; i < size; ++i) {
                filePathes.add(Text.readString(in));
            }
        }
        // expr column map
        {
            int size = in.readInt();
            if (size > 0) {
                exprColumnMap = Maps.newHashMap();
                for (int i = 0; i < size; ++i) {
                    final String name = Text.readString(in);
                    exprColumnMap.put(name, Expr.readIn(in));
                }
            }
        }
    }

    public static BrokerFileGroup read(DataInput in) throws IOException {
        BrokerFileGroup fileGroup = new BrokerFileGroup();
        fileGroup.readFields(in);
        return fileGroup;
    }
}
