/*
 * Copyright 2013 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.netflix.zuul;

import java.io.File;


/**
 *
 * 动态code编译器,主要职责是为了处理动态新增 的groovy类型的 Filter 文件
 * Interface to generate Classes from source code
 * User: mcohen
 * Date: 5/30/13
 * Time: 11:35 AM
 */
public interface DynamicCodeCompiler {
    Class compile(String sCode, String sName) throws Exception;

    Class compile(File file) throws Exception;
}
