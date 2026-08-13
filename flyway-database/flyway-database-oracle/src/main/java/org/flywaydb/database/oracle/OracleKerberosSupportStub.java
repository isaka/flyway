/*-
 * ========================LICENSE_START=================================
 * flyway-database-oracle
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
package org.flywaydb.database.oracle;

import java.util.Properties;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.extensibility.LicenseGuard;
import org.flywaydb.core.internal.license.FlywayEditionUpgradeRequiredException;

public class OracleKerberosSupportStub implements OracleKerberosSupport{
    @Override
    public void configureKerberos(final Configuration config,
        final OracleConfigurationExtension configurationExtension,
        final Properties props,
        final ClassLoader classLoader) {
        if (configurationExtension.getWalletLocation() != null) {
            throw new FlywayEditionUpgradeRequiredException(LicenseGuard.getTier(config),
                "oracle.net.wallet_location");
        }
        if (!config.getKerberosConfigFile().isEmpty()) {
            throw new FlywayEditionUpgradeRequiredException(LicenseGuard.getTier(config),
                "oracle.kerberos.config.file");
        }
    }

    @Override
    public int getPriority() {
        return -100;
    }
}
