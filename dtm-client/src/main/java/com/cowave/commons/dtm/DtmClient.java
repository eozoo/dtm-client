/*
 * Copyright (c) 2017～2024 Cowave All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.cowave.commons.dtm;

import com.cowave.commons.dtm.impl.Saga;
import com.cowave.commons.dtm.impl.Tcc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@RequiredArgsConstructor
public class DtmClient {

    private final DtmService dtmService;

    /**
     * 创建saga
     */
    public Saga saga() {
        return new Saga(null, dtmService);
    }

    /**
     * 创建saga，并指定gid
     */
    public Saga saga(String gid) {
        return new Saga(gid, dtmService);
    }

    /**
     * 创建tcc
     */
    public DtmResult tcc(DtmOperator<Tcc> function) throws Exception {
        Tcc tcc = new Tcc("",null, dtmService);
        return tcc.prepare(function);
    }

    /**
     * 创建tcc，并指定gid
     */
    public DtmResult tcc(String gid, DtmOperator<Tcc> function) throws Exception {
        Tcc tcc = new Tcc("", gid, dtmService);
        return tcc.prepare(function);
    }

    /**
     * 创建tcc
     */
    public DtmResult tcc(String gid, DtmOperator<Tcc> function, String branchPrefix) throws Exception {
        Tcc tcc = new Tcc(branchPrefix,null, dtmService);
        return tcc.prepare(function);
    }
}
