/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slf4j.impl;

import java.security.PrivilegedAction;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.jboss.logmanager.LogContext;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.doPrivileged;

public final class Slf4jLoggerFactory implements ILoggerFactory {

    /**
     * JBossLoggerAdapter cache
     */
    private static final Map<String, Logger> loggerMap = new HashMap<>();
    private static final LogContext logContext = LogContext.getLogContext();

    /**
     * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
     */
    public Logger getLogger(String name) {
        Logger slf4jLogger;

        // protect against concurrent access of loggerMap
        synchronized (loggerMap) {
            slf4jLogger = loggerMap.computeIfAbsent(name, n -> doPrivileged((PrivilegedAction<Logger>) () -> {

                // create a new jboss logger
                final org.jboss.logmanager.Logger jbossLogger = logContext.getLogger(name);

                // wrap it with an adapter
                return (Logger) new Slf4jLogger(jbossLogger);
            }));
        }
        return slf4jLogger;
    }
}
