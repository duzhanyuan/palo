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

#ifndef BDG_PALO_BE_SRC_QUERY_EXEC_OLAP_META_READER_H
#define BDG_PALO_BE_SRC_QUERY_EXEC_OLAP_META_READER_H

#include <boost/shared_ptr.hpp>
#include <list>
#include <vector>
#include <string>
#include <utility>

#include "common/status.h"
#include "exec/olap_common.h"
#include "gen_cpp/PlanNodes_types.h"
#include "runtime/descriptors.h"
#include "runtime/tuple.h"

namespace palo {

class StorageShowHints;
class RuntimeProfile;

/*
 * @breif ��ȡolap engine meta�Ľӿ�
 */
class EngineMetaReader {
public:
    EngineMetaReader(
        boost::shared_ptr<PaloScanRange> scan_range);

    ~EngineMetaReader();

    /**
     * @brief   ��reader��reader��ʵ�ֿ���������ӿ�����ɳ�ʼ����
     * �������ӡ���������Ȳ���.
     *
     * @author  Hu Jie
     * @date    2013/8/30
     *
     * @return  �ɹ�����E_OK�����󷵻ظ�ֵ.
     */
    Status open();

    /**
     * @brief
     *
     * @author  Hu Jie
     * @date    2013/8/30
     *
     * @return  �ɹ�����E_OK�����󷵻ظ�ֵ.
     */
    Status get_hints(
        int block_row_count,
        bool is_begin_include,
        bool is_end_include,
        std::vector<OlapScanRange>& scan_key_range,
        std::vector<OlapScanRange>* sub_scan_range, 
        RuntimeProfile* profile);

    Status close();

private:
    boost::shared_ptr<PaloScanRange> _scan_range;
};

} // namespace palo

#endif
