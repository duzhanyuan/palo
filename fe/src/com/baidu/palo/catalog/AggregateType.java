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

package com.baidu.palo.catalog;

import com.baidu.palo.thrift.TAggregationType;

import com.google.common.collect.Lists;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by zhaochun on 14-7-30.
 */
public enum AggregateType {
    SUM("SUM"),
    MIN("MIN"),
    MAX("MAX"),
    REPLACE("REPLACE"),
    HLL_UNION("HLL_UNION"),
    NONE("NONE");

    private static EnumMap<AggregateType, EnumSet<PrimitiveType>> compatibilityMap;

    static {
        compatibilityMap = new EnumMap<AggregateType, EnumSet<PrimitiveType>>(AggregateType.class);
        List<PrimitiveType> primitiveTypeList = Lists.newArrayList();

        primitiveTypeList.add(PrimitiveType.TINYINT);
        primitiveTypeList.add(PrimitiveType.SMALLINT);
        primitiveTypeList.add(PrimitiveType.INT);
        primitiveTypeList.add(PrimitiveType.BIGINT);
        primitiveTypeList.add(PrimitiveType.LARGEINT);
        primitiveTypeList.add(PrimitiveType.FLOAT);
        primitiveTypeList.add(PrimitiveType.DOUBLE);
        primitiveTypeList.add(PrimitiveType.DECIMAL);
        compatibilityMap.put(SUM, EnumSet.copyOf(primitiveTypeList));

        primitiveTypeList.clear();
        primitiveTypeList.add(PrimitiveType.TINYINT);
        primitiveTypeList.add(PrimitiveType.SMALLINT);
        primitiveTypeList.add(PrimitiveType.INT);
        primitiveTypeList.add(PrimitiveType.BIGINT);
        primitiveTypeList.add(PrimitiveType.LARGEINT);
        primitiveTypeList.add(PrimitiveType.FLOAT);
        primitiveTypeList.add(PrimitiveType.DOUBLE);
        primitiveTypeList.add(PrimitiveType.DECIMAL);
        primitiveTypeList.add(PrimitiveType.DATE);
        primitiveTypeList.add(PrimitiveType.DATETIME);
        compatibilityMap.put(MIN, EnumSet.copyOf(primitiveTypeList));

        primitiveTypeList.clear();
        primitiveTypeList.add(PrimitiveType.TINYINT);
        primitiveTypeList.add(PrimitiveType.SMALLINT);
        primitiveTypeList.add(PrimitiveType.INT);
        primitiveTypeList.add(PrimitiveType.BIGINT);
        primitiveTypeList.add(PrimitiveType.LARGEINT);
        primitiveTypeList.add(PrimitiveType.FLOAT);
        primitiveTypeList.add(PrimitiveType.DOUBLE);
        primitiveTypeList.add(PrimitiveType.DECIMAL);
        primitiveTypeList.add(PrimitiveType.DATE);
        primitiveTypeList.add(PrimitiveType.DATETIME);
        compatibilityMap.put(MAX, EnumSet.copyOf(primitiveTypeList));

        primitiveTypeList.clear();
        compatibilityMap.put(REPLACE, EnumSet.allOf(PrimitiveType.class));
       
        primitiveTypeList.clear();
        primitiveTypeList.add(PrimitiveType.HLL);
        compatibilityMap.put(HLL_UNION, EnumSet.copyOf(primitiveTypeList));
    
        compatibilityMap.put(NONE, EnumSet.allOf(PrimitiveType.class));
    }
    private final String sqlName;

    private AggregateType(String sqlName) {
        this.sqlName = sqlName;
    }

    public static boolean checkCompatibility(AggregateType aggType, PrimitiveType priType) {
        return compatibilityMap.get(aggType).contains(priType);
    }

    public String toSql() {
        return sqlName;
    }

    @Override
    public String toString() {
        return toSql();
    }

    public boolean checkCompatibility(PrimitiveType priType) {
        return checkCompatibility(this, priType);
    }

    public TAggregationType toThrift() {
        switch (this) {
            case SUM:
                return TAggregationType.SUM;
            case MAX:
                return TAggregationType.MAX;
            case MIN:
                return TAggregationType.MIN;
            case REPLACE:
                return TAggregationType.REPLACE;
            case NONE:
                return TAggregationType.NONE;
            case HLL_UNION:
                return TAggregationType.HLL_UNION;
            default:
                return null;
        }
    }
}

