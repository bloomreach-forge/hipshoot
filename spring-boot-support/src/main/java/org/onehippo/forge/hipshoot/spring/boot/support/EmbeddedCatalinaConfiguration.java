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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

/**
 * Configuration properties for {@link AppsDeployingTomcatEmbeddedServletContainerFactory}.
 */
@ConfigurationProperties(prefix = "hipshoot.embedded.catalina")
public class EmbeddedCatalinaConfiguration {

    /**
     * The base path of the embedded tomcat. i.e, <code>$CATALINA_BASE</code>.
     */
    private String base;

    /**
     * The web application base directory path. i.e, <code>$CATALINA_BASE/webapps</code>.
     */
    private String appBase;

    /**
     * Comma separated string value for the packaged war file resource names. e.g, <code>"site.war, cms.war"</code>.
     */
    private String wars;

    /**
     * Default constructor.
     */
    public EmbeddedCatalinaConfiguration() {
    }

    /**
     * Constructs by passing an {@link Environment}, so this reads each property manually.
     * @param env
     */
    public EmbeddedCatalinaConfiguration(final Environment env) {
        base = env.getProperty("hipshoot.embedded.catalina.base");
        appBase = env.getProperty("hipshoot.embedded.catalina.appBase");
        wars = env.getProperty("hipshoot.embedded.catalina.wars");
    }

    /**
     * Returns the base path of the embedded tomcat. i.e, <code>$CATALINA_BASE</code>.
     * @return the base path of the embedded tomcat. i.e, <code>$CATALINA_BASE</code>
     */
    public String getBase() {
        return base;
    }

    /**
     * Sets the base path of the embedded tomcat. i.e, <code>$CATALINA_BASE</code>.
     * @param base the base path of the embedded tomcat. i.e, <code>$CATALINA_BASE</code>
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * Returns the web application base directory path. i.e, <code>$CATALINA_BASE/webapps</code>.
     * @return the web application base directory path. i.e, <code>$CATALINA_BASE/webapps</code>
     */
    public String getAppBase() {
        return appBase;
    }

    /**
     * Sets the web application base directory path. i.e, <code>$CATALINA_BASE/webapps</code>.
     * @param appBase the web application base directory path. i.e, <code>$CATALINA_BASE/webapps</code>
     */
    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    /**
     * Returns comma separated string value for the packaged war file resource names. e.g, <code>"site.war, cms.war"</code>.
     * @return comma separated string value for the packaged war file resource names. e.g, <code>"site.war, cms.war"</code>
     */
    public String getWars() {
        return wars;
    }

    /**
     * Sets comma separated string value for the packaged war file resource names. e.g, <code>"site.war, cms.war"</code>.
     * @param wars comma separated string value for the packaged war file resource names. e.g, <code>"site.war, cms.war"</code>
     */
    public void setWars(String wars) {
        this.wars = wars;
    }

}
