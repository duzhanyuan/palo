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

#ifndef  BDG_PALO_BE_SRC_QUERY_EXEC_MYSQL_SCANNER_H
#define  BDG_PALO_BE_SRC_QUERY_EXEC_MYSQL_SCANNER_H

#include <stdlib.h>
#include <string>
#include <vector>
#include <mysql/mysql.h>

#include "common/status.h"

namespace palo {

struct MysqlScannerParam {
    std::string host;
    std::string port;
    std::string user;
    std::string passwd;
    std::string db;
    unsigned long client_flag;
    MysqlScannerParam(): client_flag(0) { }
};

// Mysql Scanner for scan data from mysql
class MysqlScanner {
public:
    MysqlScanner(const MysqlScannerParam& param);
    ~MysqlScanner();

    Status open();
    Status query(const std::string& query);

    // query for PALO
    Status query(const std::string& table, const std::vector<std::string>& fields,
                 const std::vector<std::string>& filters);
    Status get_next_row(char** *buf, unsigned long** lengths, bool* eos);

    int field_num() const {
        return _field_num;
    }
private:
    Status _error_status(const std::string& prefix) {
        std::stringstream msg;
        msg << prefix << " Err: " << mysql_error(_my_conn);
        LOG(WARNING) << msg.str();
        return Status(msg.str());
    }

    const MysqlScannerParam& _my_param;
    MYSQL* _my_conn;
    MYSQL_RES* _my_result;
    std::string _sql_str;
    bool _is_open;
    int _field_num;
};

}

#endif
