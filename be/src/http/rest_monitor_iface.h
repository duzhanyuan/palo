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

#ifndef BDG_PALO_BE_UTIL_REST_MONITOR_IFACE_H
#define BDG_PALO_BE_UTIL_REST_MONITOR_IFACE_H

#include <sstream>

namespace palo {

// This is a interface used to monitor internal module running state.
class RestMonitorIface {
public:
    virtual ~RestMonitorIface() { }

    // this is called when client want to know it's content
    virtual void debug(std::stringstream& ss) = 0;
};

}

#endif
