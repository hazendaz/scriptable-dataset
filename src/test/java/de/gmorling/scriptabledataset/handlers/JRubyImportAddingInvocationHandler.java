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
package de.gmorling.scriptabledataset.handlers;

import javax.script.ScriptEngine;

/**
 * The Class JRubyImportAddingInvocationHandler.
 */
public class JRubyImportAddingInvocationHandler implements ScriptInvocationHandler {

    @Override
    public String getLanguageName() {
        return "jruby";
    }

    @Override
    public String preInvoke(String script) {
        return "require 'bigdecimal'; require 'bigdecimal/math'; include BigMath; " + script;
    }

    @Override
    public Object postInvoke(Object object) {
        return object;
    }

    @Override
    public void setScriptEngine(ScriptEngine engine) {
        // Do nothing
    }

}
