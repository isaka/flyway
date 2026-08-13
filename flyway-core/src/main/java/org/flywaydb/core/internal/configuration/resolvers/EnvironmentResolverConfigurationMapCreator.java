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

import java.util.Map;
import java.util.stream.Collectors;
import org.flywaydb.core.api.CoreErrorCode;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.extensibility.ConfigurationExtension;
import org.flywaydb.core.extensibility.Plugin;
import org.flywaydb.core.internal.configuration.models.EnvironmentModel;
import org.flywaydb.core.internal.plugin.PluginRegister;
import tools.jackson.core.JacksonException.Reference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

public class EnvironmentResolverConfigurationMapCreator {

    public Map<String, ConfigurationExtension> createMap(final EnvironmentModel environmentModel,
        final PluginRegister pluginRegister) {

        if (environmentModel.getResolvers() != null) {
            return environmentModel.getResolvers()
                .keySet()
                .stream()
                .collect(Collectors.toMap(key -> key, v -> getResolverConfig(environmentModel, pluginRegister, v)));
        }
        return null;
    }

    private ConfigurationExtension getResolverConfig(final EnvironmentModel environmentModel,
        final PluginRegister pluginRegister,
        final String key) {
        final Class<?> clazz = getResolverConfigClassFromKey(pluginRegister, key);

        if (clazz != null) {
            try {
                final var data = environmentModel.getResolvers().get(key);
                return (ConfigurationExtension) new ObjectMapper().rebuild()
                    .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                    .build()
                    .convertValue(data, clazz);
            } catch (final MismatchedInputException e) {
                //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
                throw new FlywayException("Error reading resolver configuration for resolver "
                    + key
                    + ". Expected type '"
                    + e.getTargetType().getSimpleName()
                    + "' for "
                    + e.getPath().stream().map(Reference::getPropertyName).collect(Collectors.joining(".")),
                    CoreErrorCode.CONFIGURATION);
            } catch (final IllegalArgumentException e) {
                throw new FlywayException("Error reading resolver configuration for resolver " + key,
                    e,
                    CoreErrorCode.CONFIGURATION);
            }
        }

        throw new FlywayException("Unable to find resolver: " + key);
    }

    private Class<? extends Plugin> getResolverClassFromKey(final PluginRegister pluginRegister, final String key) {
        Plugin plugin = pluginRegister.getInstancesOf(EnvironmentProvisioner.class)
            .stream()
            .filter(p -> matchesNameOrAlias(p, key))
            .findFirst()
            .orElse(null);

        if (plugin == null) {
            plugin = pluginRegister.getInstancesOf(PropertyResolver.class)
                .stream()
                .filter(p -> matchesNameOrAlias(p, key))
                .findFirst()
                .orElse(null);
        }

        if (plugin != null) {
            return plugin.getClass();
        }

        throw new FlywayException("Unable to find resolver: " + key);
    }

    private boolean matchesNameOrAlias(final Plugin plugin, final String key) {
        if (plugin.getName().equalsIgnoreCase(key)) {
            return true;
        }

        if (plugin instanceof final PropertyResolver resolver) {
            return resolver.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(key));
        }

        return false;
    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    private Class<?> getResolverConfigClassFromKey(final PluginRegister pluginRegister, final String key) {
        final Class<? extends Plugin> resolverClass = getResolverClassFromKey(pluginRegister, key);
        if (resolverClass == null) {
            return null;
        }

        final Plugin plugin = pluginRegister.getExact(resolverClass);
        if (plugin instanceof final EnvironmentProvisioner environmentProvisioner) {
            return environmentProvisioner.getConfigClass();
        }
        if (plugin instanceof final PropertyResolver propertyResolver) {
            return propertyResolver.getConfigClass();
        }

        throw new FlywayException("Unable to find resolver: " + key);
    }
}
