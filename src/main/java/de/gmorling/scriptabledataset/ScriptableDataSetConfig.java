/*
 * scriptable-dataset (https://github.com/hazendaz/scriptable-dataset)
 *
 * Copyright 2011-2023 Hazendaz.
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

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

import org.apache.commons.lang3.Validate;

/**
 * Configures the usage of one scripting language with a prefix and a list of invocation handlers.
 *
 * @author Gunnar Morling
 */
@ToString
public class ScriptableDataSetConfig {

    private final String prefix;

    private final String languageName;

    private final List<ScriptInvocationHandler> handlers = new ArrayList<>();

    /**
     * Creates a new ScriptableDataSetConfig.
     *
     * @param languageName
     *            The name of the language as expected by the JSR 223 scripting engine manager, e.g. "jruby". May not be
     *            null.
     * @param prefix
     *            A prefix, which shall precede fields in a ScriptableDataSet in that language, e.g. "jruby:". May not
     *            be null.
     */
    public ScriptableDataSetConfig(String languageName, String prefix) {
        this(languageName, prefix, null);
    }

    /**
     * Creates a new ScriptableDataSetConfig.
     *
     * @param languageName
     *            The name of the language as expected by the JSR 223 scripting engine manager, e.g. "jruby". May not be
     *            null.
     * @param prefix
     *            A prefix, which shall precede fields in a ScriptableDataSet in that language, e.g. "jruby:". May not
     *            be null.
     * @param handlers
     *            An optional list with handlers to be applied for fields with the given prefix.
     */
    public ScriptableDataSetConfig(String languageName, String prefix, List<ScriptInvocationHandler> handlers) {

        super();

        Validate.notNull(languageName);
        Validate.notNull(prefix);

        this.prefix = prefix;
        this.languageName = languageName;

        if (handlers != null) {
            this.handlers.addAll(handlers);
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public String getLanguageName() {
        return languageName;
    }

    public List<ScriptInvocationHandler> getHandlers() {
        return handlers;
    }

}
