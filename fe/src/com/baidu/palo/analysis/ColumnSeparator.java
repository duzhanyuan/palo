// Modifications copyright (C) 2017, Baidu.com, Inc.
// Copyright 2017 The Apache Software Foundation

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
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

import com.baidu.palo.common.AnalysisException;

import java.io.StringWriter;

public class ColumnSeparator {
    private static final String HEX_STRING = "0123456789ABCDEF";

    private final String oriSeparator;
    private String separator;

    public ColumnSeparator(String separator) {
        this.oriSeparator = separator;
        this.separator = null;
    }

    public String getColumnSeparator() {
        return separator;
    }

    private static byte[] hexStrToBytes(String hexStr) {
        String upperHexStr = hexStr.toUpperCase();
        int length = upperHexStr.length() / 2;
        char[] hexChars = upperHexStr.toCharArray();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            bytes[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return bytes;
    }

    private static byte charToByte(char c) {
        return (byte) HEX_STRING.indexOf(c);
    }

    public void analyze() throws AnalysisException {
        this.separator = convertSeparator(oriSeparator);
    }

    public static String convertSeparator(String originStr) throws AnalysisException {
        if (originStr == null) {
            throw new AnalysisException("Column separator is null");
        }

        if (originStr.toUpperCase().startsWith("\\X")) {
            String hexStr = originStr.substring(2);
            // check hex str
            if (hexStr.isEmpty()) {
                throw new AnalysisException("Hex str is empty");
            }
            for (char hexChar : hexStr.toUpperCase().toCharArray()) {
                if (HEX_STRING.indexOf(hexChar) == -1) {
                    throw new AnalysisException("Hex str format error");
                }
            }
            if (hexStr.length() % 2 != 0) {
                throw new AnalysisException("Hex str length error");
            }

            // transform to separator
            StringWriter writer = new StringWriter();
            for (byte b : hexStrToBytes(hexStr)) {
                writer.append((char) b);
            }
            return writer.toString();
        } else {
            return originStr;
        }
    }

    public String toSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(oriSeparator).append("'");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toSql();
    }
}
