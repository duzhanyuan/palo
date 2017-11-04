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

package com.baidu.palo.broker.bos;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TProcessor;

import com.baidu.palo.thrift.TPaloBrokerService;
import com.baidu.palo.common.Log4jConfig;
import com.baidu.palo.common.ThriftServer;

public class BrokerBootstrap {

    public static void main(String[] args) {
        try {
            System.out.println("try to start hdfs broker");
            final String brokerHome = System.getenv("BROKER_HOME");
            if (brokerHome == null || StringUtils.isEmpty(brokerHome)) {
                System.out.println("BROKER_HOME is not set, exit");
                return;
            }

            new BrokerConfig().init(brokerHome + "/conf/baidu_bos_broker.conf");
            Log4jConfig.initLogging();

            TProcessor tprocessor = new TPaloBrokerService.Processor<TPaloBrokerService.Iface>(
                    new BOSBrokerServiceImpl());
            ThriftServer server = new ThriftServer(BrokerConfig.broker_ipc_port, tprocessor);
            server.start();
            while (true) {
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static boolean createAndLockPidFile(String pidFilePath)
            throws IOException {
        File pid = new File(pidFilePath);
        RandomAccessFile file = new RandomAccessFile(pid, "rws");
        try {
            FileLock lock = file.getChannel().tryLock();
            if (lock == null) {
                return false;
            }

            // if system exit abnormally, file will not be deleted
            pid.deleteOnExit();

            String name = ManagementFactory.getRuntimeMXBean().getName();
            file.write(name.split("@")[0].getBytes(Charsets.UTF_8));
            return true;
        } catch (OverlappingFileLockException e) {
            file.close();
            return false;
        } catch (IOException e) {
            file.close();
            throw e;
        }
    }
}
