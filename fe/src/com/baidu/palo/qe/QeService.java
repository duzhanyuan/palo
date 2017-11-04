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

package com.baidu.palo.qe;

import com.baidu.palo.catalog.Catalog;
import com.baidu.palo.common.Config;
import com.baidu.palo.common.InternalException;
import com.baidu.palo.mysql.MysqlServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

// 这是整个前端服务的包装，包括创建支持MySQL协议的服务
public class QeService {
    private static final Logger LOG = LogManager.getLogger(QeService.class);

    private int port;
    // MySQL protocol service
    private MysqlServer mysqlServer;

    @Deprecated
    public QeService(int port) {
        this.port = port;
    }

    public QeService(int port, ConnectScheduler scheduler) {
        // Set up help module
        try {
            HelpModule.getInstance().setUpModule();
        } catch (Exception e) {
            LOG.error("Help module failed, because:", e);
        }
        this.port = port;
        mysqlServer = new MysqlServer(port, scheduler);
    }

    @Deprecated
    public void setup() throws IOException, InternalException {
        // Set up help module
        try {
            HelpModule.getInstance().setUpModule();
        } catch (Exception e) {
            LOG.error("Help module failed, because:", e);
        }
        // setup MySQL protocol service
        // TODO(zhaochun): remove settings.
        ConnectScheduler scheduler = new ConnectScheduler(Config.qe_max_connection);
        mysqlServer = new MysqlServer(port, scheduler);
    }

    public void start() throws IOException {
        while (true) {
            if (!Catalog.getInstance().canRead()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            break;
        }

        if (!mysqlServer.start()) {
            LOG.error("mysql server start failed");
            System.exit(-1);
        }
        LOG.info("QE service start.");
    }

    public MysqlServer getMysqlServer() {
        return mysqlServer;
    }

    public void setMysqlServer(MysqlServer mysqlServer) {
        this.mysqlServer = mysqlServer;
    }

}

