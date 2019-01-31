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

import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;

/**
 * An extension of {@link TomcatServletWebServerFactory} to be able to deploy additional WARs
 * packaged at <code>classpath:META-INF/hipshoot/embedded-catalina/webapps/</code>.
 *
 * @deprecated Use {@link AppsDeployingTomcatServletWebServerFactory} instead.
 */
@Deprecated
public class AppsDeployingTomcatEmbeddedServletContainerFactory extends AppsDeployingTomcatServletWebServerFactory {

    /**
     * Constructs with an {@link CatalinaConfiguration}.
     * @param catalinaConfig {@link CatalinaConfiguration}
     */
    public AppsDeployingTomcatEmbeddedServletContainerFactory(final CatalinaConfiguration catalinaConfig) {
        super(catalinaConfig);
    }

}
