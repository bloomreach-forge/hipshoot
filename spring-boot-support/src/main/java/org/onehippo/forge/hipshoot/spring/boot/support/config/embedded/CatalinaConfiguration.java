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

import org.onehippo.forge.hipshoot.spring.boot.support.AppsDeployingTomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for {@link AppsDeployingTomcatEmbeddedServletContainerFactory}.
 */
@Component
@ConfigurationProperties(prefix = "hipshoot.embedded.catalina")
public class CatalinaConfiguration {

    /**
     * The web application base directory path. i.e, <code>$CATALINA_BASE/webapps</code>.
     */
    private String appBase;

    /**
     * Comma separated string value for the packaged war file resource names. e.g, <code>"site.war, cms.war"</code>.
     */
    private String wars;

    /**
     * Flag whether or not persist session is enabled.
     */
    private boolean persistSession;

    /**
     * Flag whether or not JNDI is enabled.
     */
    private boolean namingEnabled = true;

    /**
     * Server configuration.
     */
    private CatalinaServer server = new CatalinaServer();

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

    /**
     * Returns true if session persistence is turned on.
     * @return true if session persistence is turned on
     */
    public boolean isPersistSession() {
        return persistSession;
    }

    /**
     * Sets flag whether or not session persistence is turned on.
     * @param persistSession flag whether or not session persistence is turned on
     */
    public void setPersistSession(boolean persistSession) {
        this.persistSession = persistSession;
    }

    /**
     * Returns true if JNDI is turned on.
     * @return true if JNDI is turned on
     */
    public boolean isNamingEnabled() {
        return namingEnabled;
    }

    /**
     * Sets flag whether or not JNDI is turned on.
     * @param namingEnabled flag whether or not JNDI is turned on
     */
    public void setNamingEnabled(boolean namingEnabled) {
        this.namingEnabled = namingEnabled;
    }

    /**
     * Returns Server configuration.
     * @return Server configuration
     */
    public CatalinaServer getServer() {
        return server;
    }

    /**
     * Sets Server configuration
     * @param server Server configuration
     */
    public void setServer(CatalinaServer server) {
        this.server = server;
    }

}
