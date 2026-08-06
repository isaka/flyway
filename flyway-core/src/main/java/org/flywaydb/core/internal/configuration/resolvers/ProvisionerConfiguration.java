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
package org.flywaydb.core.internal.configuration.resolvers;

import static org.flywaydb.core.internal.configuration.ConfigUtils.isRedgate;

import java.util.HashMap;
import java.util.Map;
import org.flywaydb.core.api.CoreErrorCode;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.configuration.models.ConfigurationModel;
import org.flywaydb.core.internal.configuration.models.EnvironmentModel;
import org.flywaydb.core.internal.configuration.models.FlywayEnvironmentModel;
import org.flywaydb.core.internal.configuration.models.ResolvedEnvironment;

public class ProvisionerConfiguration {

    public static void requireDryRunUnsetForProvision(final PropertyResolverContext context) {
        if (context.getConfiguration().getDryRunOutput() != null) {
            throw new FlywayException("Provisioning "
                + context.getEnvironmentName()
                + " would alter the environment or have side effects, so is not supported with dry run enabled.",
                CoreErrorCode.CONFIGURATION);
        }
    }

    public static void requireDryRunUnsetForReprovision(final PropertyResolverContext context) {
        if (context.getConfiguration().getDryRunOutput() != null) {
            throw new FlywayException("Reprovisioning "
                + context.getEnvironmentName()
                + " would alter the environment or have side effects, so is not supported with dry run enabled.",
                CoreErrorCode.CONFIGURATION);
        }
    }

    public static ClassicConfiguration createConfigurationWithEnvironment(final Configuration configuration,
        final String environmentName,
        final EnvironmentModel environmentModel) {

        // Note that doing a regular clone can cause a loop during resolving
        // This is because ClassicConfig will attempt to read the datasource property, which in turn resolves the default
        // environment in order to clone it. As such, we have to clone the underlying configuration model instead.
        final var newConfigurationModel = ConfigurationModel.clone(configuration.getModernConfig());
        final var newConfiguration = new ClassicConfiguration(newConfigurationModel);
        final var environments = new HashMap<>(configuration.getModernConfig().getEnvironments());
        environments.put(environmentName, scopedTo(environments.get(environmentName), environmentModel));
        newConfiguration.setAllEnvironments(environments);
        newConfiguration.setEnvironment(environmentName);
        newConfiguration.setProvisionMode(ProvisionerMode.Skip);
        newConfiguration.setCallbacks(configuration.getCallbacks());
        newConfiguration.setPluginRegister(configuration.getPluginRegister().getCopy());
        newConfiguration.setWorkingDirectory(configuration.getWorkingDirectory());
        if (isRedgate()) {
            newConfiguration.setDryRunOutput(configuration.getDryRunOutput());
        }

        return newConfiguration;
    }

    /**
     * Overlays the connection details onto the environment's existing definition rather than replacing it, so that
     * everything the overlay does not carry - the per-environment {@code flyway.*} overrides and the display name -
     * survives.
     * <p>
     * Resolvers are cleared explicitly because a merge would keep them, and a surviving resolver leaves
     * {@code EnvironmentResolver} walking keys whose plugins may not be registered. The overlay's {@code provisioner}
     * of "none" is what stops a provisioner re-entering resolution of the environment it is resolving.
     */
    private static EnvironmentModel scopedTo(final EnvironmentModel existing, final EnvironmentModel overlay) {
        // Merged into a fresh model on both paths, so clearing the resolvers below cannot reach back into the model the
        // caller passed. Note this makes the returned model a new object, not a deep copy: EnvironmentModel.merge takes
        // collections by reference, so schemas can still be the caller's list. Nothing here mutates one - the fields
        // set below are replaced wholesale - but a caller that mutates the returned model in place should not assume
        // otherwise.
        final EnvironmentModel merged = existing == null
            ? new EnvironmentModel().merge(overlay)
            : existing.merge(overlay);
        merged.setResolvers(Map.of());
        return merged;
    }

    /**
     * The {@code flyway} block of a scoped configuration's current environment, for a caller that needs a setting to
     * take effect on it.
     * <p>
     * {@code ClassicConfiguration}'s setters write the root {@code flyway} block, while the matching getters prefer the
     * environment's own block. A provisioner setting a value straight after scoping therefore has to write it here, or
     * an environment-level value silently wins instead.
     *
     * @throws FlywayException if the current environment is not in the map. Inserting one instead would let a caller
     *                         appear to succeed while writing settings onto a freshly invented environment with no url.
     */
    public static FlywayEnvironmentModel environmentOverridesOf(final ClassicConfiguration configuration) {
        final String environmentName = configuration.getCurrentEnvironmentName();
        final EnvironmentModel environment = configuration.getModernConfig().getEnvironments().get(environmentName);
        if (environment == null) {
            throw new FlywayException("Environment '"
                + environmentName
                + "' is not present in this configuration, so there are no overrides to set on it.",
                CoreErrorCode.CONFIGURATION);
        }
        return environment.getFlyway();
    }

    public static ClassicConfiguration createConfigurationWithEnvironment(final PropertyResolverContext context,
        final ResolvedEnvironment resolvedEnvironment) {
        return createConfigurationWithEnvironment(context.getConfiguration(),
            context.getEnvironmentName(),
            resolvedEnvironment.toEnvironmentModel());
    }

    public static ClassicConfiguration createConfigurationWithEnvironment(final PropertyResolverContext context,
        final EnvironmentModel environmentModel) {
        return createConfigurationWithEnvironment(context.getConfiguration(),
            context.getEnvironmentName(),
            environmentModel);
    }
}
