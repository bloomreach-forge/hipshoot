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

import java.util.Map;

import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaConfiguration;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaGlobalNamingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultTomcatServerCustomizer implements TomcatCustomizer {

    private static Logger log = LoggerFactory.getLogger(DefaultTomcatServerCustomizer.class);

    @Override
    public void customize(final Tomcat tomcat, final CatalinaConfiguration config) {
        addGlobalNamingResources(tomcat, config);
    }

    private void addGlobalNamingResources(final Tomcat tomcat, final CatalinaConfiguration config) {
        String name;
        String auth;
        String type;
        Map<String, Object> props;
        String propName;
        Object propValue;

        for (CatalinaGlobalNamingResource resConf : config.getServer().getGlobalNamingResources()) {
            name = resConf.getName();
            auth = resConf.getAuth();
            type = resConf.getType();

            ContextResource resource = new ContextResource();

            resource.setName(name);
            resource.setAuth(auth);
            resource.setType(type);

            props = resConf.getProperties();

            for (Map.Entry<String, Object> prop : props.entrySet()) {
                propName = prop.getKey();
                propValue = prop.getValue();
                resource.setProperty(propName, propValue);
            }

            log.info("Adding global naming resource: name='" + name + "', auth='" + auth + "', type='" + type
                    + "', props={}", props);
            tomcat.getServer().getGlobalNamingResources().addResource(resource);
        }
    }

}
