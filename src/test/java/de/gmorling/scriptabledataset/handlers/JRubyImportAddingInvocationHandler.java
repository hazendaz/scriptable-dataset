/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2025 Hazendaz
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
