# scriptable-dataset #
 
[![Build Status](https://travis-ci.org/hazendaz/scriptable-dataset.svg?branch=master)](https://travis-ci.org/hazendaz/scriptable-dataset)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.hazendaz/scriptable-dataset/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.hazendaz/scriptable-dataset)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

![hazendaz](src/site/resources/images/hazendaz-banner.jpg)

See site page [here](http://hazendaz.github.io/scriptable-dataset/)

## Forked ##

Original repo has not been touched in nearly 7 years.  While the code base is not all that difficult it did need some updating.  This serves as updated release since original is effectively no longer supported.

## Overview ##

An implementation of a [DBUnit](http://www.dbunit.org/) ```IDataSet```, that allows the use of script expressions in its fields. In order to use a certain scripting language in a scriptable data
set, a [JSR 223](http://jcp.org/en/jsr/detail?id=223) (Scripting for the Java&trade; Platform") compatible script engine has to exist for that language.

Using the [JRuby](http://jruby.org/) engine e.g., a scriptable data set file could look like this:

```  
<pre>
  &lt;dataset&gt;
    &lt;location num=&quot;jruby:12/2&quot; addr=&quot;jruby:'Webster Street'.reverse&quot; date=&quot;jruby:DateTime::now() - 14&quot;/&gt;
  &lt;/dataset&gt;
</pre>
```

A ScriptableDataSet can be created as follows:

```  
<pre>
  IDataSet wrapped = ...;
  
  List&lt;ScriptInvocationHandler&gt; handlers = new ArrayList&lt;Class&lt;? extends ScriptInvocationHandler&gt;&gt;();
  handlers.add(new JRubyImportAddingInvocationHandler());
  
  IDataSet scriptableDS = new ScriptableDataSet(
    wrapped, new ScriptableDataSetConfig(&quot;jruby&quot;, &quot;jruby:&quot;, handlers));
</pre>
```

where

```
<ul>
    <li><b>jruby</b> is the name of a scripting language as understood by javax.script.ScriptEngineManager</li>
    <li><b>jruby:</b> is a prefix, that shall precede fields in that scripting language</li>
    <li><b>handlers</b> is an optional list of ScriptInvocationHandlers, that can be used to pre-process scripts (e.g. to add common imports) and post-process scripts (e.g. to convert results into data types understood by DBUnit).</li>
</ul>
```