/*-
 * ========================LICENSE_START=================================
 * flyway-database-postgresql
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
package org.flywaydb.database.postgresql.authentication;

import java.util.Properties;
import lombok.CustomLog;
import org.flywaydb.core.internal.license.FlywayUpgradeMessage;
import org.flywaydb.core.internal.authentication.PgpassFileReader;

@CustomLog
public class PostgreSQLExternalAuthSupportStub implements PostgreSQLExternalAuthSupport {

    @Override
    public Properties getExternalAuthProperties(final String url, final String username) {
        PgpassFileReader pgpassFileReader = new PgpassFileReader();
        if (pgpassFileReader.getPgpassFilePath() != null) {
            LOG.info(FlywayUpgradeMessage.generate("pgpass file '" + pgpassFileReader.getPgpassFilePath() + "'",
                "use this for database authentication"));
        }
        return new Properties();
    }

    @Override
    public int getPriority() {
        return -100;
    }
}
