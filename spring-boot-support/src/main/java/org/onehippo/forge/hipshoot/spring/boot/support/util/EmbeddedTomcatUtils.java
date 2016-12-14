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
package org.onehippo.forge.hipshoot.spring.boot.support.util;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Server;
import org.apache.catalina.Service;

/**
 * Embedded Tomcat Utilities.
 */
public class EmbeddedTomcatUtils {

    private EmbeddedTomcatUtils() {
    }

    /**
     * Get {@link Server} instance associated with this {@link Context}.
     * @param context context
     * @return {@link Server} instance associated with this {@link Context}
     */
    public static Server getServer(Context context) {
        Server server = null;

        Container host = context.getParent();

        if (host != null) {
            Container engine = host.getParent();

            if (engine != null && engine instanceof Engine) {
                Service service = ((Engine) engine).getService();

                if (service != null) {
                    server = service.getServer();
                }
            }
        }

        return server;
    }
}
