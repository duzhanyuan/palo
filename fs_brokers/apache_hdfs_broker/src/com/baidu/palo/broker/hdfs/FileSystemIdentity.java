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

package com.baidu.palo.broker.hdfs;

public class FileSystemIdentity {

    private final String hostName;
    private final String ugiInfo;
    
    public FileSystemIdentity(String hostName, String ugiInfo) {
        this.hostName = hostName;
        this.ugiInfo = ugiInfo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((hostName == null) ? 0 : hostName.hashCode());
        result = prime * result + ((ugiInfo == null) ? 0 : ugiInfo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileSystemIdentity other = (FileSystemIdentity) obj;
        if (hostName == null) {
            if (other.hostName != null) {
                return false;
            }
        } else if (!hostName.equals(other.hostName)) {
            return false;
        }
        if (ugiInfo == null) {
            if (other.ugiInfo != null) {
                return false;
            }
        } else if (!ugiInfo.equals(other.ugiInfo)) {
            return false;
        }
        return true;
    }
}
