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

#ifndef BDG_PALO_BE_SRC_RPC_CHECKSUM_H
#define BDG_PALO_BE_SRC_RPC_CHECKSUM_H

namespace palo {

/** Compute fletcher32 checksum for arbitary data. See
 * http://en.wikipedia.org/wiki/Fletcher%27s_checksum for more information
 * about the algorithm. Fletcher32 is the default checksum used in palo.
 *
 * @param data Pointer to the input data
 * @param len Input data length in bytes
 * @return The calculated checksum
 */
    extern uint32_t fletcher32(const void *data, size_t len);
} // namespace palo

#endif //BDG_PALO_BE_SRC_RPC_CHECKSUM_H
