/*
 * scriptable-dataset (https://github.com/hazendaz/scriptable-dataset)
 *
 * Copyright 2011-2025 Hazendaz.
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
package de.gmorling.scriptabledataset;

import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;
import de.gmorling.scriptabledataset.handlers.StandardHandlerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ITable implementation, that allows the usage of script statements as field values.
 */
public class ScriptableTable implements ITable {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(ScriptableTable.class);

    /** The wrapped. */
    private ITable wrapped;

    /** The engines by prefix. */
    private Map<String, ScriptEngine> enginesByPrefix = new HashMap<>();

    /** The handlers by prefix. */
    private Map<String, List<ScriptInvocationHandler>> handlersByPrefix = new HashMap<>();

    /**
     * Creates a new ScriptableTable.
     *
     * @param wrapped
     *            The ITable to be wrapped by this scriptable table. May not be null.
     * @param configurations
     *            An list with configurations
     */
    public ScriptableTable(ITable wrapped, List<ScriptableDataSetConfig> configurations) {

        this.wrapped = wrapped;

        ScriptEngineManager manager = new ScriptEngineManager();

        // load the engines
        for (ScriptableDataSetConfig oneConfig : configurations) {

            ScriptEngine engine = manager.getEngineByName(oneConfig.getLanguageName());

            if (engine == null) {
                throw new RuntimeException(
                        "No scripting engine found for language \"" + oneConfig.getLanguageName() + "\".");
            }
            enginesByPrefix.put(oneConfig.getPrefix(), engine);

            List<ScriptInvocationHandler> handlers = getAllHandlers(oneConfig);

            for (ScriptInvocationHandler oneHandler : handlers) {
                oneHandler.setScriptEngine(engine);
            }

            handlersByPrefix.put(oneConfig.getPrefix(), handlers);

            logger.info("Registered scripting engine {} for language {}.", engine, oneConfig.getLanguageName());
        }
    }

    @Override
    public int getRowCount() {
        return wrapped.getRowCount();
    }

    @Override
    public ITableMetaData getTableMetaData() {
        return wrapped.getTableMetaData();
    }

    @Override
    public Object getValue(int row, String column) throws DataSetException {

        Object theValue = wrapped.getValue(row, column);

        // only strings can be processed
        if (theValue instanceof String) {
            String script = (String) theValue;

            for (Entry<String, ScriptEngine> oneEntry : enginesByPrefix.entrySet()) {

                String prefix = oneEntry.getKey();

                // found engine for prefix
                if (script.startsWith(prefix)) {

                    ScriptEngine engine = oneEntry.getValue();
                    script = script.substring(prefix.length());

                    List<ScriptInvocationHandler> handlers = handlersByPrefix.get(oneEntry.getKey());

                    // preInvoke
                    for (ScriptInvocationHandler handler : handlers) {
                        script = handler.preInvoke(script);
                    }

                    logger.debug("Executing script: {}", script);

                    // the actual script evaluation
                    try {
                        theValue = engine.eval(script);
                    } catch (ScriptException e) {
                        throw new RuntimeException(e);
                    }

                    // call postInvoke in reversed order
                    Collections.reverse(handlers);
                    for (ScriptInvocationHandler handler : handlers) {
                        theValue = handler.postInvoke(theValue);
                    }
                }
            }
        }

        return theValue;
    }

    /**
     * Returns a list with all standard handlers registered for the language of the config and all handlers declared in
     * the config itself.
     *
     * @param config
     *            A config object.
     *
     * @return A list with handlers. Never null.
     */
    private List<ScriptInvocationHandler> getAllHandlers(ScriptableDataSetConfig config) {

        List<ScriptInvocationHandler> theValue = new ArrayList<>(
                StandardHandlerConfig.getStandardHandlersByLanguage(config.getLanguageName()));

        // custom handlers
        theValue.addAll(config.getHandlers());

        return theValue;
    }
}
