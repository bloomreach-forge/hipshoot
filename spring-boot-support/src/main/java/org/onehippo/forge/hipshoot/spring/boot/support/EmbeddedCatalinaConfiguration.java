/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.forge.hipshoot.spring.boot.support;

import org.springframework.core.env.Environment;

class EmbeddedCatalinaConfiguration {

    private String base;
    private String appBase;
    private String wars;

    public EmbeddedCatalinaConfiguration() {
    }

    public EmbeddedCatalinaConfiguration(final Environment env) {
        base = env.getProperty("hipshoot.embedded.catalina.base");
        appBase = env.getProperty("hipshoot.embedded.catalina.appBase");
        wars = env.getProperty("hipshoot.embedded.catalina.wars");
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    public String getWars() {
        return wars;
    }

    public void setWars(String wars) {
        this.wars = wars;
    }

}
