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

#include "olap/column_file/stream_name.h"

namespace palo {
namespace column_file {

StreamName::StreamName(uint32_t unique_column_id, StreamInfoMessage::Kind kind) : 
        _unique_column_id(unique_column_id),
        _kind(kind) {}

bool StreamName::operator < (const StreamName& another) const {
    if (_kind == StreamInfoMessage::ROW_INDEX ||
            another._kind == StreamInfoMessage::ROW_INDEX) {
        // if both are indexes
        if (_kind == another._kind) {
            return _unique_column_id < another._unique_column_id;
        } else {
            return _kind < another._kind;
        }
    } else {
        if (_unique_column_id != another._unique_column_id) {
            return _unique_column_id < another._unique_column_id;
        } else {
            return _kind < another._kind;
        }
    }
}

bool StreamName::operator == (const StreamName& another) const {
    return _unique_column_id == another._unique_column_id &&
           _kind == another._kind;
}

}  // namespace column_file
}  // namespace palo
