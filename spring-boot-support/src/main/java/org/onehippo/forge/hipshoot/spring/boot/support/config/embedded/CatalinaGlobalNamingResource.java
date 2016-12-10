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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public class CatalinaGlobalNamingResource {

    private String name;
    private String auth;
    private String type;
    private Map<String, Object> properties = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setPropertiesString(String propertiesString) {
        if (propertiesString == null || propertiesString.isEmpty()) {
            return;
        }

        Map<String, Object> props = new LinkedHashMap<>();
        String [] keyValue;

        for (String token : StringUtils.tokenizeToStringArray(propertiesString, ",")) {
            token = token.trim();
            keyValue = StringUtils.tokenizeToStringArray(token, "=");

            for (int i = 0; i < keyValue.length; i++) {
                keyValue[i] = keyValue[i].trim();
            }

            if (keyValue.length == 2) {
                props.put(keyValue[0], keyValue[1]);
            }
        }

        setProperties(props);
    }
}
