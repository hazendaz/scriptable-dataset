# scriptable-dataset #

[![Java CI](https://github.com/hazendaz/scriptable-dataset/workflows/Java%20CI/badge.svg)](https://github.com/hazendaz/scriptable-dataset/actions?query=workflow%3A%22Java+CI%22)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.hazendaz/scriptable-dataset/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.hazendaz/scriptable-dataset)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

![hazendaz](src/site/resources/images/hazendaz-banner.jpg)

See site page [here](http://hazendaz.github.io/scriptable-dataset/)

## Forked ##

Original repo has not been touched in nearly 7 years.  While the code base is not all that difficult it did need some updating.  This serves as updated release since original is effectively no longer supported.

## Overview ##

An implementation of a [DBUnit](http://dbunit.sourceforge.net/) ```IDataSet```, that allows the use of script expressions in its fields. In order to use a certain scripting language in a scriptable data set, a [JSR 223](http://jcp.org/en/jsr/detail?id=223) (Scripting for the Java&trade; Platform") compatible script engine has to exist for that language.

Using the [JRuby](http://jruby.org/) engine e.g., a scriptable data set file could look like this:

```
<dataset>
    <location num="jruby:12/2" addr="jruby:'Webster Street'.reverse" date="jruby:DateTime::now() - 14" />
</dataset>
```

A ScriptableDataSet can be created as follows:

```
IDataSet wrapped = ...;

List<ScriptInvocationHandler> handlers = new ArrayList<Class<? extends ScriptInvocationHandler>>();
handlers.add(new JRubyImportAddingInvocationHandler());

IDataSet scriptableDS = 
        new ScriptableDataSet(wrapped, new ScriptableDataSetConfig("jruby", "jruby:", handlers));
```

where

- **jruby** is the name of a scripting language as understood by javax.script.ScriptEngineManager.
- **jruby** is a prefix, that shall precede fields in that scripting language.
- **handlers** is an optional list of ScriptInvocationHandlers, that can be used to pre-process scripts (e.g. to add common imports) and post-process scripts (e.g. to convert results into data types understood by DBUnit).
