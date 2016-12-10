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

import org.apache.catalina.startup.Tomcat;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaConfiguration;

/**
 * Callback interface that can be used to customize a {@link Tomcat}.
 */
public interface TomcatCustomizer {

    /**
     * Customize the {@link Tomcat} instance.
     * @param tomcat the {@link Tomcat} instance to customize
     * @param catalinaConfiguration embedded tomcat configuration
     */
    public void customize(final Tomcat tomcat, final CatalinaConfiguration catalinaConfiguration);

}