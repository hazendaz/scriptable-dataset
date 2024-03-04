/*
 * scriptable-dataset (https://github.com/hazendaz/scriptable-dataset)
 *
 * Copyright 2011-2024 Hazendaz.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of The Apache Software License,
 * Version 2.0 which accompanies this distribution, and is available at
 * https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Contributors:
 *     Gunnar Morling
 *     Hazendaz (Jeremy Landis).
 */
package de.gmorling.scriptabledataset.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages handlers, that shall always executed for scripts in a given language. These standard handlers will be called
 * <b>before</b> any custom handlers in the pre invocation and <b>after</b> any custom handlers in the post invocation.
 */
public class StandardHandlerConfig {

    /** The standard handlers. */
    private static Map<String, List<ScriptInvocationHandler>> standardHandlers;

    /**
     * Instantiates a new standard handler config.
     */
    private StandardHandlerConfig() {
        // Prevent Instantiation
    }

    static {

        standardHandlers = new HashMap<>();

        ServiceLoader<ScriptInvocationHandler> serviceLoader = ServiceLoader.load(ScriptInvocationHandler.class);
        try {
            for (ScriptInvocationHandler scriptInvocationHandler : serviceLoader) {

                List<ScriptInvocationHandler> handlersForLanguage = standardHandlers
                        .get(scriptInvocationHandler.getLanguageName());

                if (handlersForLanguage == null) {
                    handlersForLanguage = new ArrayList<>();
                    standardHandlers.put(scriptInvocationHandler.getLanguageName(), handlersForLanguage);
                }

                handlersForLanguage.add(scriptInvocationHandler);
            }
        } catch (ServiceConfigurationError error) {
            Logger logger = LoggerFactory.getLogger(StandardHandlerConfig.class);
            logger.error(
                    "Loading of standard script invocation handlers failed, most likely due to an unknown handler implementation given in META-INF/services {}",
                    ScriptInvocationHandler.class.getName());
            standardHandlers = Collections.emptyMap();
        }
    }

    /**
     * Gets the standard handlers by language.
     *
     * @param language
     *            the language
     *
     * @return the standard handlers by language
     */
    public static List<ScriptInvocationHandler> getStandardHandlersByLanguage(String language) {
        if (standardHandlers.containsKey(language)) {
            return Collections.unmodifiableList(standardHandlers.get(language));
        }
        return Collections.emptyList();
    }
}
