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
package org.onehippo.forge.hipshoot.spring.boot.support.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegating Servlet that defers the initialization of the delegate servlet and delegates calls to the delegate
 * servlet.
 * <P>
 * This delegating servlet with 'deferring initialization' feature can be useful if you want to start a web application
 * fast enough without waiting for a servlet initialization to complete.
 * For example, when you deploy spring boot based web application(s) onto a cloud based platform, you don't have to
 * worry about the timeout issue (e.g, 60 seconds by default somewhere) due to a long time initializing servlet.
 * </P>
 * <P>
 * For example, suppose you have a servlet that may take long time to initialize itself like the following:
 * </P>
 * <PRE>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;LongTimeInitServlet&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;org.example.LongTimeInitServlet&lt;/servlet-class&gt;
 *   &lt;load-on-startup&gt;100&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 * </PRE>
 * You can change the {@code servlet-class} to <code>org.onehippo.forge.hipshoot.spring.boot.support.servlet.DeferredInitDelegatingServlet</code>
 * and provide the real delegate servlet class name in the {@code DeferredInitDelegatingServlet.delegateServletClass}
 * init parameter instead like the following example:
 * <PRE>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;LongTimeInitServlet&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;org.onehippo.forge.hipshoot.spring.boot.support.servlet.DeferredInitDelegatingServlet&lt;/servlet-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;DeferredInitDelegatingServlet.delegateServletClass&lt;/param-name&gt;
 *     &lt;param-value&gt;org.example.LongTimeInitServlet&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 *   &lt;load-on-startup&gt;100&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 * </PRE>
 * <P>
 * Then this servlet class will pass the servlet initialization phase fast because the real initialization of the
 * delegate servlet will be done asynchronously so that the servlet container may start all the other web applications
 * faster without having to wait for the delegate servlet to complete the initialization phase.
 * </P>
 */
public class DeferredInitDelegatingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(DeferredInitDelegatingServlet.class);

    private static final String DELEGATE_SERVLET_CLASS_INIT_PARAM = DeferredInitDelegatingServlet.class.getSimpleName()
            + ".delegateServletClass";

    private static final String DEFERRED_INIT_DISABLED_INIT_PARAM = DeferredInitDelegatingServlet.class.getSimpleName()
            + ".deferredInitDisabled";

    private HttpServlet delegate;

    private Class<? extends HttpServlet> delegateServletClass;

    private Thread delegateServletInitThread;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        final boolean deferredInitDisabled = Boolean
                .parseBoolean(servletConfig.getInitParameter(DEFERRED_INIT_DISABLED_INIT_PARAM));

        String delegateServletClassName = servletConfig.getInitParameter(DELEGATE_SERVLET_CLASS_INIT_PARAM);

        if (delegateServletClassName != null) {
            delegateServletClassName = delegateServletClassName.trim();
        }

        if (delegateServletClassName == null || delegateServletClassName.isEmpty()) {
            throw new ServletException("Please set '" + DELEGATE_SERVLET_CLASS_INIT_PARAM + "' init parameter.");
        }

        try {
            Class<? extends HttpServlet> clazz = (Class<? extends HttpServlet>) Thread.currentThread()
                    .getContextClassLoader().loadClass(delegateServletClassName);

            if (!HttpServlet.class.isAssignableFrom(clazz)) {
                throw new ServletException(
                        "The delegate servlet class is not an HttpServlet: " + delegateServletClassName);
            }

            delegateServletClass = clazz;
        } catch (ClassNotFoundException e) {
            throw new ServletException(
                    "Cannot find the delegate servlet class: " + delegateServletClassName + ". " + e);
        }

        if (deferredInitDisabled) {
            initializeDelegateServlet();
        } else {
            delegateServletInitThread = new Thread(new DelegateServletInitializationRunner(),
                    "DelegateServletInitializationRunner");
            delegateServletInitThread.setDaemon(true);
            delegateServletInitThread.start();
        }
    }

    @Override
    public void destroy() {
        if (delegateServletInitThread != null && delegateServletInitThread.isAlive()) {
            try {
                delegateServletInitThread.interrupt();
            } catch (SecurityException e) {
                log.error("Failed to interrupt delegateServletInitThread due to security exception.", e);
            }
        }

        if (delegate != null) {
            delegate.destroy();
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (delegate == null) {
            if (res instanceof HttpServletResponse) {
                ((HttpServletResponse) res).sendError(HttpServletResponse.SC_BAD_GATEWAY,
                        DeferredInitDelegatingServlet.class.getSimpleName() + ": Delegate servlet not loaded yet.");
            }

            return;
        }

        delegate.service(req, res);
    }

    private void initializeDelegateServlet() {
        final ClassLoader threadContextLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader servletContextLoader = getServletContext().getClassLoader();

        try {
            if (threadContextLoader != servletContextLoader) {
                Thread.currentThread().setContextClassLoader(servletContextLoader);
            }

            final long t0 = System.currentTimeMillis();
            HttpServlet servlet = delegateServletClass.newInstance();
            servlet.init(getServletConfig());
            delegate = servlet;
            log.info("Initialization of delegate servlet ({}) was done in {}ms", delegateServletClass,
                    System.currentTimeMillis() - t0);
        } catch (InstantiationException e) {
            log.error("Failed to instantiate delegate servlet.", e);
        } catch (IllegalAccessException e) {
            log.error("Illegal access while trying to instantiating delegate servlet.", e);
        } catch (ServletException e) {
            log.error("Failed to initialize delegate servlet.", e);
        } catch (Exception e) {
            log.error("Exception occurred while initializing delegate servlet.", e);
        } finally {
            if (threadContextLoader != servletContextLoader) {
                Thread.currentThread().setContextClassLoader(threadContextLoader);
            }
        }
    }

    private class DelegateServletInitializationRunner implements Runnable {
        @Override
        public void run() {
            initializeDelegateServlet();
        }
    }
}