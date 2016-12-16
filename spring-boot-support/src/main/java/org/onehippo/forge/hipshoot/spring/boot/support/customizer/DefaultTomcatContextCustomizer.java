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

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaConfiguration;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaEnvironment;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaNamingResource;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;

/**
 * Default {@link Context} customizer implementation.
 */
public class DefaultTomcatContextCustomizer implements TomcatContextCustomizer {

    private static Logger log = LoggerFactory.getLogger(DefaultTomcatContextCustomizer.class);

    private final CatalinaConfiguration catalinaConfig;

    public DefaultTomcatContextCustomizer(final CatalinaConfiguration catalinaConfig) {
        this.catalinaConfig = catalinaConfig;
    }

    @Override
    public void customize(final Context context) {
        addDefaultContextApplicationParameters(context);
        addDefaultContextNamingEnvironments(context);
        addDefaultContextNamingResources(context);
    }

    private void addDefaultContextApplicationParameters(final Context context) {
        ApplicationParameter appParam;

        for (CatalinaParameter param : catalinaConfig.getServer().getDefaultContext().getParameters()) {
            appParam = new ApplicationParameter();
            appParam.setName(param.getName());
            appParam.setValue(param.getValue());
            appParam.setOverride(param.isOverride());

            log.info("Adding default context application parameter, {}, in context ('{}').", appParam,
                    context.getPath());
            context.addApplicationParameter(appParam);
        }
    }

    private void addDefaultContextNamingEnvironments(final Context context) {
        ContextEnvironment environment;
        ContextEnvironment existingContextEnvironment;

        for (CatalinaEnvironment envConf : catalinaConfig.getServer().getDefaultContext().getEnvironments()) {
            existingContextEnvironment = context.getNamingResources().findEnvironment(envConf.getName());

            if (!envConf.isOverride() || existingContextEnvironment == null) {
                environment = new ContextEnvironment();
                environment.setType(envConf.getType());
                environment.setName(envConf.getName());
                environment.setValue(envConf.getValue());

                log.info("Adding default context naming environment: name='{}', type='{}', in context ('{}').",
                        envConf.getName(), envConf.getType(), context.getPath());
                context.getNamingResources().addEnvironment(environment);
            }
        }
    }

    private void addDefaultContextNamingResources(final Context context) {
        Map<String, Object> props;
        String propName;
        Object propValue;

        for (CatalinaNamingResource resConf : catalinaConfig.getServer().getDefaultContext().getNamingResources()) {
            ContextResource resource = new ContextResource();

            resource.setName(resConf.getName());
            resource.setAuth(resConf.getAuth());
            resource.setType(resConf.getType());

            props = resConf.getProperties();

            for (Map.Entry<String, Object> prop : props.entrySet()) {
                propName = prop.getKey();
                propValue = prop.getValue();
                resource.setProperty(propName, propValue);
            }

            log.info("Adding default context naming resource: name='{}', type='{}', in context ('{}').",
                    resConf.getName(), resConf.getType(), context.getPath());
            context.getNamingResources().addResource(resource);
        }
    }
}
