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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaConfiguration;
import org.onehippo.forge.hipshoot.spring.boot.support.customizer.DefaultTomcatContextCustomizer;
import org.onehippo.forge.hipshoot.spring.boot.support.customizer.DefaultTomcatServerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.util.StringUtils;

/**
 * An extension of {@link TomcatEmbeddedServletContainerFactory} to be able to deploy additional WARs
 * packaged at <code>classpath:META-INF/hipshoot/embedded-catalina/webapps/</code>.
 * <P>
 * If an environment property, <code>hipshoot.embedded.catalina.wars</code>, is provided with a comma separated
 * string for the war file names (e.g, <code>hipshoot.embedded.catalina.wars="site.war, cms.war"</code>), this
 * will look up those war file resources under <code>classpath:META-INF/hipshoot/embedded-catalina/webapps/</code>,
 * extract the war file resources to a local file system directory designated as local embedded tomcat webapps
 * directory by another environment property, <code>hipshoot.embedded.catalina.appBase</code>, and add each war
 * as web application context during the startup.
 * </P>
 * <P>
 * This reads the environment properties like the following:
 * </P>
 * <UL>
 *   <LI>
 *     <CODE>hipshoot.embedded.catalina.appBase</CODE>:
 *     The path of the web application base path (i.e, <code>$CATALINA_BASE/webapps</code> folder)
 *     where the additional war resources packaged in <code>classpath:META-INF/hipshoot/embedded-catalina/webapps/</code>
 *     should be deployed onto.
 *   </LI>
 *   <LI>
 *     <CODE>hipshoot.embedded.catalina.wars</CODE>:
 *     Comma separated string for the packaged war file resource names under <code>classpath:META-INF/hipshoot/embedded-catalina/webapps/</code>.
 *     e.g, <code>"site.war, cms.war"</code>.
 *   </LI>
 * </UL>
 * <P>
 * Please see {@link CatalinaConfiguration} for a full list of the available properties.
 * </P>
 */
public class AppsDeployingTomcatEmbeddedServletContainerFactory extends TomcatEmbeddedServletContainerFactory {

    private static Logger log = LoggerFactory.getLogger(AppsDeployingTomcatEmbeddedServletContainerFactory.class);

    /**
     * The classpath resource path under which war file resources will be looked up to deploy.
     */
    private static final String EMBEDDED_CATALINA_WEBAPPS_PATH = "META-INF/hipshoot/embedded-catalina/webapps";

    /**
     * Default byte buffer size used when copying resources to files.
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Local web application base directory (i.e, webapps folder).
     */
    private File appBaseDirectory;

    /**
     * {@link Tomcat} customizers.
     */
    private List<TomcatCustomizer> tomcatCustomizers = new LinkedList<>();

    /**
     * Embedded tomcat configuration.
     */
    private final CatalinaConfiguration catalinaConfiguration;

    /**
     * Constructs with an {@link CatalinaConfiguration}.
     * @param catalinaConfig {@link CatalinaConfiguration}
     */
    public AppsDeployingTomcatEmbeddedServletContainerFactory(final CatalinaConfiguration catalinaConfig) {
        super();

        this.catalinaConfiguration = catalinaConfig;

        addTomcatCustomizers(new DefaultTomcatServerCustomizer(catalinaConfig));
        addContextCustomizers(new DefaultTomcatContextCustomizer(catalinaConfig));

        setPersistSession(catalinaConfiguration.isPersistSession());

        final String appBase = catalinaConfiguration.getAppBase();

        if (appBase != null && !appBase.isEmpty()) {
            File appBaseDir = new File(appBase);

            if (!appBaseDir.isDirectory()) {
                appBaseDir.mkdirs();
            }

            log.info("Embedded catalog appBase: {}", appBaseDir.getAbsolutePath());
            setAppBaseDirectory(appBaseDir);

            extractEmbeddedWars(catalinaConfiguration);
        }
    }

    /**
     * Add {@link Tomcat} customizer(s).
     * @param customizers {@link Tomcat} customizer(s)
     */
    public void addTomcatCustomizers(TomcatCustomizer ... customizers) {
        if (tomcatCustomizers != null) {
            for (TomcatCustomizer customizer : customizers) {
                tomcatCustomizers.add(customizer);
            }
        }
    }

    /**
     * {@inheritDoc}
     * <P>
     * Overridden to scan the packaged war file resources and deploy those before startup.
     * </P>
     */
    @Override
    protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
        try {
            tomcat.enableNaming();

            for (TomcatCustomizer tomcatCustomizer : tomcatCustomizers) {
                tomcatCustomizer.customize(tomcat);
            }

            final Collection<TomcatContextCustomizer> contextCustomizers = getTomcatContextCustomizers();

            String contextPath;
            String basePath;
            Manager manager;

            for (Map.Entry<String, String> entry : getWebappPathsMap().entrySet()) {
                contextPath = entry.getKey();
                basePath = entry.getValue();
                Context context = tomcat.addWebapp(contextPath, basePath);
                WebappLoader webappLoader = new WebappLoader(Thread.currentThread().getContextClassLoader());
                context.setLoader(webappLoader);

                if (!isPersistSession()) {
                    manager = context.getManager();

                    if (manager == null) {
                        manager = new StandardManager();
                        context.setManager(manager);
                    }

                    if (manager instanceof StandardManager) {
                        ((StandardManager) manager).setPathname(null);
                    }
                }

                StandardRoot standardRoot = new StandardRoot(context);
                standardRoot.setCachingAllowed(
                        catalinaConfiguration.getServer().getDefaultContext().getResources().isCachingAllowed());
                standardRoot.setCacheMaxSize(
                        catalinaConfiguration.getServer().getDefaultContext().getResources().getCacheMaxSize());
                context.setResources(standardRoot);

                if (contextCustomizers != null) {
                    for (TomcatContextCustomizer contextCustomizer : contextCustomizers) {
                        contextCustomizer.customize(context);
                    }
                }
            }
        } catch (ServletException ex) {
            throw new IllegalStateException("Failed to add webapp", ex);
        }

        return super.getTomcatEmbeddedServletContainer(tomcat);
    }

    /**
     * Returns web application base directory (i.e, webapps directory) of the embedded tomcat.
     * @return web application base directory (i.e, webapps directory) of the embedded tomcat
     */
    protected File getAppBaseDirectory() {
        return appBaseDirectory;
    }

    /**
     * Sets web application base directory (i.e, webapps directory) of the embedded tomcat.
     * @param appBaseDirectory web application base directory (i.e, webapps directory) of the embedded tomcat
     */
    protected void setAppBaseDirectory(File appBaseDirectory) {
        this.appBaseDirectory = appBaseDirectory;
    }

    private Map<String, String> getWebappPathsMap() {
        Map<String, String> webappPathsMap = null;

        if (getAppBaseDirectory() != null) {
            webappPathsMap = new LinkedHashMap<>();
            File webappsDir = getAppBaseDirectory();

            if (webappsDir.isDirectory()) {
                String fileName;
                String baseFileName;
                String contextPath;
                String basePath;

                for (File file : webappsDir.listFiles()) {
                    fileName = file.getName();
                    int offset = fileName.lastIndexOf('.');
                    baseFileName = (offset != -1) ? fileName.substring(0, offset) : fileName;

                    if ("ROOT".equals(baseFileName)) {
                        contextPath = "";
                    } else {
                        contextPath = "/" + baseFileName;
                    }

                    basePath = file.getAbsolutePath();

                    if (file.isDirectory() || (file.isFile() && file.getName().endsWith(".war"))) {
                        webappPathsMap.put(contextPath, basePath);
                    }
                }
            }
        }

        if (webappPathsMap == null) {
            return Collections.emptyMap();
        }

        return webappPathsMap;
    }

    private void extractEmbeddedWars(CatalinaConfiguration config) {
        final String wars = config.getWars();

        if (wars != null && !wars.isEmpty()) {
            String [] tokens = StringUtils.tokenizeToStringArray(wars, ",");
            String warName;

            for (String token : tokens) {
                warName = token.trim();

                if (!warName.isEmpty()) {
                    URL warRes = Thread.currentThread().getContextClassLoader()
                            .getResource(EMBEDDED_CATALINA_WEBAPPS_PATH + "/" + warName);

                    if (warRes != null) {
                        InputStream is = null;
                        BufferedInputStream bis = null;
                        OutputStream os = null;
                        BufferedOutputStream bos = null;

                        try {
                            is = warRes.openStream();
                            bis = new BufferedInputStream(is);
                            os = new FileOutputStream(new File(getAppBaseDirectory(), warName));
                            bos = new BufferedOutputStream(os);
                            copy(bis, bos);
                            log.info("Deployed embedded war, {}.", warName);
                        } catch (IOException e) {
                            log.error("Failed to copy {} to {}.", warName, getAppBaseDirectory(), e);
                        } finally {
                            closeQuietly(bos);
                            closeQuietly(os);
                            closeQuietly(bis);
                            closeQuietly(is);
                        }
                    }
                }
            }
        }
    }

    private void copy(InputStream is, OutputStream os) throws IOException {
        byte [] buffer = new byte[BUFFER_SIZE];
        int readLen = is.read(buffer, 0, BUFFER_SIZE);

        while (readLen != -1) {
            os.write(buffer, 0, readLen);
            readLen = is.read(buffer, 0, BUFFER_SIZE);
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {
            }
        }
    }

}
