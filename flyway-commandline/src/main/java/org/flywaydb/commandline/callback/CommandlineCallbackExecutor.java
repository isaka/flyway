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
package org.flywaydb.commandline.callback;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.api.callback.CallbackEvent;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.callback.InternalCallback;
import org.flywaydb.core.internal.callback.SimpleContext;
import org.flywaydb.core.internal.reports.ReportDetails;

@RequiredArgsConstructor
public final class CommandlineCallbackExecutor<E extends CallbackEvent<E>> {

    private final Configuration configuration;
    private final Class<E> eventType;
    private List<InternalCallback<E>> callbacks;

    public void onReportGeneratedEvent(final E event, final ReportDetails reportDetails) {
        loadCallbacks();
        final var context = new SimpleContext(configuration, reportDetails);
        for (final var callback : callbacks.stream().filter(c -> c.supports(event, context)).toList()) {
            callback.handle(event, context);
        }
    }

    private void loadCallbacks() {
        if (callbacks != null) {
            return;
        }
        callbacks = configuration.getPluginRegister()
            .getInstancesOf(InternalCallback.class)
            .stream()
            .filter(c -> c.supportsEventType(eventType))
            .map(c -> (InternalCallback<E>) c)
            .toList();
    }
}
