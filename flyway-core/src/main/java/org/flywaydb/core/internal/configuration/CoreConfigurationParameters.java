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
package org.flywaydb.core.internal.configuration;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.flywaydb.core.extensibility.ConfigurationParameter;

public enum CoreConfigurationParameters {
    DRIVER(new ConfigurationParameter("driver", "Fully qualified classname of the JDBC driver", false)),
    URL(new ConfigurationParameter("url", "Jdbc url to use to connect to the database", false)),
    USER(new ConfigurationParameter("user", "User to use to connect to the database", false)),
    PASSWORD(new ConfigurationParameter("password", "Password to use to connect to the database", false)),

    CONNECT_RETRIES(new ConfigurationParameter("connectRetries",
        "Maximum number of retries when attempting to connect to the database",
        false)),
    INIT_SQL(new ConfigurationParameter("initSql",
        "SQL statements to run to initialize a new database connection",
        false)),
    SCHEMAS(new ConfigurationParameter("schemas", "Comma-separated list of the schemas managed by Flyway", false)),
    TABLE(new ConfigurationParameter("table", "Name of Flyway's schema history table", false)),
    LOCATIONS(new ConfigurationParameter("locations", "Classpath locations to scan recursively for migrations", false)),
    FAIL_ON_MISSING_LOCATIONS(new ConfigurationParameter("failOnMissingLocations",
        "Whether to fail if a location specified in the flyway.locations option doesn't exist",
        false)),
    RESOLVERS(new ConfigurationParameter("resolvers", "Comma-separated list of custom MigrationResolvers", false)),
    SKIP_DEFAULT_RESOLVERS(new ConfigurationParameter("skipDefaultResolvers",
        "Skips default resolvers (jdbc, sql and Spring-jdbc)",
        false)),
    SQL_MIGRATION_PREFIX(new ConfigurationParameter("sqlMigrationPrefix",
        "File name prefix for versioned SQL migrations",
        false)),
    UNDO_SQL_MIGRATION_PREFIX(new ConfigurationParameter("undoSqlMigrationPrefix",
        "[teams] File name prefix for undo SQL migrations",
        false)),
    REPEATABLE_SQL_MIGRATION_PREFIX(new ConfigurationParameter("repeatableSqlMigrationPrefix",
        "File name prefix for repeatable SQL migrations",
        false)),
    SQL_MIGRATION_SEPARATOR(new ConfigurationParameter("sqlMigrationSeparator",
        "File name separator for SQL migrations",
        false)),
    SQL_MIGRATION_SUFFIXES(new ConfigurationParameter("sqlMigrationSuffixes",
        "Comma-separated list of file name suffixes for SQL migrations",
        false)),
    STREAM(new ConfigurationParameter("stream", "[teams] Stream SQL migrations when executing them", false)),
    BATCH(new ConfigurationParameter("batch", "[teams] Batch SQL statements when executing them", false)),
    GROUP(new ConfigurationParameter("group", "Whether to group all pending migrations together in the same transaction when applying them", false)),
    MIXED(new ConfigurationParameter("mixed", "Allow mixing transactional and non-transactional statements", false)),
    ENCODING(new ConfigurationParameter("encoding", "Encoding of SQL migrations", false)),
    DETECT_ENCODING(new ConfigurationParameter("detectEncoding",
        "[teams] Whether Flyway should try to automatically detect SQL migration file encoding",
        false)),
    EXECUTE_IN_TRANSACTION(new ConfigurationParameter("executeInTransaction",
        "Whether SQL should execute within a transaction",
        false)),
    PLACEHOLDER_REPLACEMENT(new ConfigurationParameter("placeholderReplacement",
        "Whether placeholders should be replaced",
        false)),
    PLACEHOLDERS(new ConfigurationParameter("placeholders", "Placeholders to replace in sql migrations", false)),
    PLACEHOLDER_PREFIX(new ConfigurationParameter("placeholderPrefix", "Prefix of every placeholder", false)),
    PLACEHOLDER_SUFFIX(new ConfigurationParameter("placeholderSuffix", "Suffix of every placeholder", false)),
    SCRIPT_PLACEHOLDER_PREFIX(new ConfigurationParameter("scriptPlaceholderPrefix",
        "Prefix of every script placeholder",
        false)),
    SCRIPT_PLACEHOLDER_SUFFIX(new ConfigurationParameter("scriptPlaceholderSuffix",
        "Suffix of every script placeholder",
        false)),
    LOCK_RETRY_COUNT(new ConfigurationParameter("lockRetryCount",
        "The maximum number of retries when trying to obtain a lock",
        false)),
    JDBC_PROPERTIES(new ConfigurationParameter("jdbcProperties",
        "Properties to pass to the JDBC driver object",
        false)),
    INSTALLED_BY(new ConfigurationParameter("installedBy",
        "Username that will be recorded in the schema history table",
        false)),
    TARGET(new ConfigurationParameter("target", "Target version up to which Flyway should process migrations", false)),
    CHERRY_PICK(new ConfigurationParameter("cherryPick",
        "[teams] Comma separated list of migrations that Flyway should consider",
        false)),
    SKIP_EXECUTING_MIGRATIONS(new ConfigurationParameter("skipExecutingMigrations",
        "Whether Flyway should skip actually executing the contents of the migrations",
        false)),
    OUT_OF_ORDER(new ConfigurationParameter("outOfOrder", "Allows migrations to be run \"out of order\"", false)),
    CALLBACKS(new ConfigurationParameter("callbacks",
        "Comma-separated list of FlywayCallback classes, or locations to scan for FlywayCallback classes",
        false)),
    SKIP_DEFAULT_CALLBACKS(new ConfigurationParameter("skipDefaultCallbacks", "Skips default callbacks (sql)", false)),
    VALIDATE_ON_MIGRATE(new ConfigurationParameter("validateOnMigrate", "Validate when running migrate", false)),
    VALIDATE_MIGRATION_NAMING(new ConfigurationParameter("validateMigrationNaming",
        "Validate file names of SQL migrations (including callbacks)",
        false)),
    IGNORE_MIGRATION_PATTERNS(new ConfigurationParameter("ignoreMigrationPatterns",
        "Patterns of migrations and states to ignore",
        false)),
    CLEAN_DISABLED(new ConfigurationParameter("cleanDisabled", "Whether to disable clean", false)),
    BASELINE_VERSION(new ConfigurationParameter("baselineVersion",
        "Version to tag schema with when executing baseline",
        false)),
    BASELINE_DESCRIPTION(new ConfigurationParameter("baselineDescription",
        "Description to tag schema with when executing baseline",
        false)),
    BASELINE_ON_MIGRATE(new ConfigurationParameter("baselineOnMigrate",
        "Baseline on migrate against uninitialized non-empty schema",
        false)),
    CONFIG_FILES(new ConfigurationParameter("configFiles", "Comma-separated list of config files to use", false)),
    CONFIG_FILE_ENCODING(new ConfigurationParameter("configFileEncoding",
        "Encoding to use when loading the config files",
        false)),
    JAR_DIRS(new ConfigurationParameter("jarDirs",
        "Comma-separated list of dirs for Jdbc drivers & Java migrations",
        false)),
    CREATE_SCHEMAS(new ConfigurationParameter("createSchemas",
        "Whether Flyway should attempt to create the schemas specified in the schemas property",
        false)),
    DRY_RUN_OUTPUT(new ConfigurationParameter("dryRunOutput",
        "[teams] File where to output the SQL statements of a migration dry run",
        false)),
    ERROR_OVERRIDES(new ConfigurationParameter("errorOverrides",
        "[teams] Rules to override specific SQL states and errors codes",
        false)),
    COLOR(new ConfigurationParameter("color",
        "Whether to colorize output. Values: always, never, or auto (default)",
        false)),
    OUTPUT_FILE(new ConfigurationParameter("outputFile",
        "Send output to the specified file alongside the console",
        false)),
    OUTPUT_TYPE(new ConfigurationParameter("outputType",
        "Serialise the output in the given format, Values: json",
        false));

    @Getter
    private final ConfigurationParameter configurationParameter;

    CoreConfigurationParameters(final ConfigurationParameter parameter) {
        this.configurationParameter = parameter;
    }

    public static List<ConfigurationParameter> getConfigurationParameters(final CoreConfigurationParameters... parameters) {
        return Arrays.stream(parameters).map(CoreConfigurationParameters::getConfigurationParameter).toList();
    }
}
