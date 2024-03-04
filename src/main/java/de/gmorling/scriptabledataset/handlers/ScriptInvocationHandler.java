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

import javax.script.ScriptEngine;

/**
 * Implementations can be registered with a ScriptableDataSet to be called before and after script contained in a data
 * set field is executed. This can be used to add commonly used import statements for all scripts of a given language or
 * to post-process the result of a script execution.
 * <p>
 * Implementations must define a default constructor, if they shall be used as standard handler for a language.
 */
public interface ScriptInvocationHandler {

    /**
     * Must return the name of the scripting language for which this handler can be registered, as expected by the JSR
     * 223 scripting engine manager, e.g. "jruby".
     *
     * @return The name of the scripting language.
     */
    String getLanguageName();

    /**
     * Will be called before a script contained in a field of a data set is executed.
     *
     * @param script
     *            The script to be executed.
     *
     * @return The script to be executed, enriched with common imports for example.
     */
    String preInvoke(String script);

    /**
     * Will be called after a script contained in a field of a data set is executed.
     *
     * @param object
     *            The result of the script execution.
     *
     * @return The result of the script execution, possibly modified by this handler.
     */
    Object postInvoke(Object object);

    /**
     * Makes the scripting engine available to handler implementations.
     *
     * @param engine
     *            The scripting engine used to execute the current script.
     */
    void setScriptEngine(ScriptEngine engine);
}
