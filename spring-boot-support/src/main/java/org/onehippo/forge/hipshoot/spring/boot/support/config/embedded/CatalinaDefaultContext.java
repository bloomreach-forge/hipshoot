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
package org.onehippo.forge.hipshoot.spring.boot.support.config.embedded;

import java.util.ArrayList;
import java.util.List;

public class CatalinaDefaultContext {

    private CatalinaManager manager = new CatalinaManager();
    private CatalinaResources resources = new CatalinaResources();
    private List<CatalinaParameter> parameters = new ArrayList<>();
    private List<CatalinaEnvironment> environments = new ArrayList<>();

    public CatalinaManager getManager() {
        return manager;
    }

    public void setManager(CatalinaManager manager) {
        this.manager = manager;
    }

    public CatalinaResources getResources() {
        return resources;
    }

    public void setResources(CatalinaResources resources) {
        this.resources = resources;
    }

    public List<CatalinaParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<CatalinaParameter> parameters) {
        this.parameters = parameters;
    }

    public List<CatalinaEnvironment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<CatalinaEnvironment> environments) {
        this.environments = environments;
    }

}
