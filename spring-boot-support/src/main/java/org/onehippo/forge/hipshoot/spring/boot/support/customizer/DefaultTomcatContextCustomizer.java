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

import org.apache.catalina.Context;
import org.apache.catalina.Server;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaConfiguration;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaParameter;
import org.onehippo.forge.hipshoot.spring.boot.support.util.EmbeddedTomcatUtils;
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
        bindGlobalContextParameters(context);
        bindGlobalNamingEnvironments(context);
        bindGlobalNamingResources(context);
    }

    private void bindGlobalContextParameters(final Context context) {
        final Server server = EmbeddedTomcatUtils.getServer(context);

        if (server != null) {
            String name;
            String value;
            boolean override;
            ApplicationParameter appParam;

            for (CatalinaParameter param : catalinaConfig.getServer().getDefaultContext().getParameters()) {
                name = param.getName();
                value = param.getValue();
                override = param.isOverride();
                appParam = new ApplicationParameter();
                appParam.setName(name);
                appParam.setValue(value);
                appParam.setOverride(override);
                context.addApplicationParameter(appParam);
                log.info("Binding global context application parameter, {}, in context ('{}').", appParam,
                        context.getPath());
            }
        }
    }

    private void bindGlobalNamingEnvironments(final Context context) {
        final Server server = EmbeddedTomcatUtils.getServer(context);

        if (server != null) {
            ContextEnvironment[] contextEnvironments = server.getGlobalNamingResources().findEnvironments();
            boolean override;
            ContextEnvironment existingContextEnvironment;

            for (ContextEnvironment contextEnvironment : contextEnvironments) {
                override = contextEnvironment.getOverride();
                existingContextEnvironment = context.getNamingResources().findEnvironment(contextEnvironment.getName());

                if (!override || existingContextEnvironment == null) {
                    context.getNamingResources().addEnvironment(contextEnvironment);
                    log.info("Binding global context environment, '{}', in context ('{}').",
                            contextEnvironment.getName(), context.getPath());
                }
            }
        }
    }

    private void bindGlobalNamingResources(final Context context) {
        final Server server = EmbeddedTomcatUtils.getServer(context);

        if (server != null) {
            ContextResource[] contextResources = server.getGlobalNamingResources().findResources();

            for (ContextResource contextResource : contextResources) {
                context.getNamingResources().addResource(contextResource);
                log.info("Binding global context resource, '{}', in context ('{}').", contextResource.getName(),
                        context.getPath());
            }
        }
    }
}
