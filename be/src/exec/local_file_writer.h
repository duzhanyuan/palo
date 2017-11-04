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

#ifndef BDG_PALO_BE_SRC_EXEC_LOCAL_FILE_WRITER_H
#define BDG_PALO_BE_SRC_EXEC_LOCAL_FILE_WRITER_H

#include <stdio.h>

#include "exec/file_writer.h"

namespace palo {

class LocalFileWriter : public FileWriter {
public:
    LocalFileWriter(const std::string& path, int64_t start_offset);
    virtual ~LocalFileWriter();

     Status open() override;

    virtual Status write(const uint8_t* buf, size_t buf_len, size_t* written_len) override;

    virtual void close() override;

private:
    std::string _path;
    int64_t _start_offset;
    FILE* _fp;
};

} // end namespace palo

#endif // BDG_PALO_BE_SRC_EXEC_LOCAL_FILE_WRITER_H
