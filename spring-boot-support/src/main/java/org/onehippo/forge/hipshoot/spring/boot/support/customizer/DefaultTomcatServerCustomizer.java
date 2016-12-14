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
package org.onehippo.forge.hipshoot.spring.boot.support.customizer;

import java.util.Map;

import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.onehippo.forge.hipshoot.spring.boot.support.TomcatCustomizer;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaConfiguration;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaEnvironment;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaGlobalNamingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default embedded tomcat customizer implementation.
 */
public class DefaultTomcatServerCustomizer implements TomcatCustomizer {

    private static Logger log = LoggerFactory.getLogger(DefaultTomcatServerCustomizer.class);

    private final CatalinaConfiguration catalinaConfig;

    public DefaultTomcatServerCustomizer(final CatalinaConfiguration catalinaConfig) {
        this.catalinaConfig = catalinaConfig;
    }

    @Override
    public void customize(final Tomcat tomcat) {
        addGlobalNamingEnvironments(tomcat);
        addGlobalNamingResources(tomcat);
    }

    private void addGlobalNamingEnvironments(final Tomcat tomcat) {
        String type;
        String name;
        String value;
        ContextEnvironment environment;

        for (CatalinaEnvironment envConf : catalinaConfig.getServer().getDefaultContext().getEnvironments()) {
            type = envConf.getType();
            name = envConf.getName();
            value = envConf.getValue();

            environment = new ContextEnvironment();
            environment.setType(type);
            environment.setName(name);
            environment.setValue(value);

            log.info("Adding global naming environment: type='{}', name='{}', value='{}'.", type, name, value);
            tomcat.getServer().getGlobalNamingResources().addEnvironment(environment);
        }
    }

    private void addGlobalNamingResources(final Tomcat tomcat) {
        String name;
        String auth;
        String type;
        Map<String, Object> props;
        String propName;
        Object propValue;

        for (CatalinaGlobalNamingResource resConf : catalinaConfig.getServer().getGlobalNamingResources()) {
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
