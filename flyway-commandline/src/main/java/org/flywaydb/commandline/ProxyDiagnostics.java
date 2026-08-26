/*-
 * ========================LICENSE_START=================================
 * flyway-commandline
 * ========================================================================
 * Copyright (C) 2010 - 2026 Red Gate Software Ltd
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.flywaydb.commandline;

import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;

/**
 * Reports which proxy mechanism, if any, is in effect for outbound HTTP(S) calls made by this JVM.
 * <p>
 * Flyway does not configure a proxy at runtime - the actual proxy behaviour is controlled by JVM
 * properties set before the JVM starts (either explicit {@code http(s).proxyHost} properties passed via
 * {@code JAVA_ARGS}, or {@code java.net.useSystemProxies} passed by the launcher scripts). This class only
 * detects and logs which of those mechanisms is active, so the effective state is visible at debug level
 * even though {@code flyway.url} and other URL-like config values are redacted in the config dump.
 */
@CustomLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProxyDiagnostics {

    public enum ProxyMechanism {
        EXPLICIT_JVM_PROPERTIES,
        OS_SYSTEM_PROXIES,
        NONE
    }

    static ProxyMechanism determineEffectiveProxyMechanism() {
        if (hasExplicitProxyHost()) {
            return ProxyMechanism.EXPLICIT_JVM_PROPERTIES;
        }
        if (Boolean.parseBoolean(System.getProperty("java.net.useSystemProxies"))) {
            return ProxyMechanism.OS_SYSTEM_PROXIES;
        }
        return ProxyMechanism.NONE;
    }

    private static boolean hasExplicitProxyHost() {
        return StringUtils.hasText(System.getProperty("http.proxyHost")) || StringUtils.hasText(System.getProperty("https.proxyHost"));
    }

    public static void logEffectiveProxyState() {
        switch (determineEffectiveProxyMechanism()) {
            case EXPLICIT_JVM_PROPERTIES -> LOG.debug(
                "Proxy: explicit JVM properties in effect (http.proxyHost=" + System.getProperty("http.proxyHost")
                    + ", http.proxyPort=" + System.getProperty("http.proxyPort")
                    + ", https.proxyHost=" + System.getProperty("https.proxyHost")
                    + ", https.proxyPort=" + System.getProperty("https.proxyPort")
                    + ", http.nonProxyHosts=" + System.getProperty("http.nonProxyHosts") + ")");
            case OS_SYSTEM_PROXIES -> LOG.debug("Proxy: OS system proxies in effect (java.net.useSystemProxies=true)");
            case NONE -> LOG.debug("Proxy: no proxy configured");
        }
    }
}
