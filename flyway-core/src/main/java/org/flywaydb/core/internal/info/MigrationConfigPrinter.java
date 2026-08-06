/*-
 * ========================LICENSE_START=================================
 * flyway-core
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
package org.flywaydb.core.internal.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.logging.Log;
import org.flywaydb.core.api.migration.baseline.BaselineMigrationConfigurationExtension;
import org.flywaydb.core.internal.util.AsciiTable;
import org.flywaydb.core.internal.util.StringUtils;

public class MigrationConfigPrinter {
    public static void printIfNoOnDiskMigrations(final Log log,
        final MigrationInfo[] infos,
        final Configuration configuration) {
        printIfNoOnDiskMigrations(log, infos, configuration, List.of());
    }

    public static void printIfNoOnDiskMigrations(final Log log,
        final MigrationInfo[] infos,
        final Configuration configuration,
        final List<List<String>> extraRows) {
        final boolean hasOnDiskMigrations = Arrays.stream(infos)
            .map(MigrationInfo::getPhysicalLocation)
            .anyMatch(StringUtils::hasLength);

        if (!hasOnDiskMigrations) {
            log.info("No migrations found on disk.\nHere are some relevant configuration settings.");
            print(log, configuration, extraRows);
        }
    }

    private static void print(final Log log, final Configuration configuration, final List<List<String>> extraRows) {
        final Location[] locations = configuration.getLocations();
        final String locationsValue = String.join(", ", Arrays.stream(locations).map(Location::toString).toList());

        final Location[] callbackLocations = configuration.getCallbackLocations();
        final String callbackLocationsValue = String.join(", ",
            Arrays.stream(callbackLocations).map(Location::toString).toList());

        final String workingDirectory = configuration.getWorkingDirectory();
        final String workingDirectoryValue = (workingDirectory != null && !workingDirectory.isEmpty())
            ? workingDirectory
            : System.getProperty("user.dir");

        final String repeatablePrefixValue = configuration.getRepeatableSqlMigrationPrefix();

        final String sqlPrefixValue = configuration.getSqlMigrationPrefix();

        final BaselineMigrationConfigurationExtension baselineExt = configuration.getPluginRegister()
            .getExact(BaselineMigrationConfigurationExtension.class);
        final String baselinePrefixValue = baselineExt.getBaselineMigrationPrefix();

        final String sqlSeparatorValue = configuration.getSqlMigrationSeparator();

        final String[] sqlSuffixes = configuration.getSqlMigrationSuffixes();
        final String sqlSuffixesValue = String.join(", ", sqlSuffixes);

        final List<String> columns = List.of("Setting", "Value");
        final List<List<String>> rows = new ArrayList<>(List.of(List.of("locations", locationsValue),
            List.of("callbackLocations", callbackLocationsValue),
            List.of("workingDirectory", workingDirectoryValue),
            List.of("repeatableSqlMigrationPrefix", repeatablePrefixValue),
            List.of("sqlMigrationPrefix", sqlPrefixValue),
            List.of("baselineMigrationPrefix", baselinePrefixValue),
            List.of("sqlMigrationSeparator", sqlSeparatorValue),
            List.of("sqlMigrationSuffixes", sqlSuffixesValue)));
        rows.addAll(extraRows);

        log.info("\n" + new AsciiTable(columns, rows, true, "", "No settings found").render());
    }
}
