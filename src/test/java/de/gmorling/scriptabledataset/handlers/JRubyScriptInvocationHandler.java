/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2025 Hazendaz
 */
package de.gmorling.scriptabledataset.handlers;

import javax.script.Invocable;
import javax.script.ScriptEngine;

import org.jruby.RubyObject;

/**
 * A <code>ScriptInvocationHandler</code> for JRuby scripts. It adds some commonly used imports to the begin of each
 * script and converts Objects returned by the scripting engine into types processable by DBUnit.
 */
public class JRubyScriptInvocationHandler implements ScriptInvocationHandler {

    /** The engine. */
    private ScriptEngine engine;

    @Override
    public String getLanguageName() {
        return "jruby";
    }

    @Override
    public String preInvoke(String script) {
        return "require 'date';" + script;
    }

    @Override
    public Object postInvoke(Object object) {
        if (object instanceof RubyObject) {
            RubyObject rubyObject = (RubyObject) object;
            if (rubyObject.getMetaClass().getName().equals("DateTime")) {
                Invocable i = (Invocable) engine;
                try {
                    object = i.invokeMethod(object, "strftime", "%Y-%m-%d");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return object;
    }

    @Override
    public void setScriptEngine(ScriptEngine engine) {
        this.engine = engine;
    }

}
