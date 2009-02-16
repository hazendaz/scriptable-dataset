package de.gmorling.scriptabledataset;

import org.apache.commons.lang.Validate;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;

/**
 * <p>
 * An implementation of a <a href="http://www.dbunit.org/">DBUnit</a>
 * <code>IDataSet</code>, that allows the use of script expressions in its
 * fields. In order to use a certain scripting language in a scriptable data
 * set, a JSR 223 compatible script engine has to exist for that language.
 * </p>
 * <p>
 * Using the JRuby engine e.g., a scriptable data set file could look like this:
 * </p>
 * 
 * <pre>
 * &lt;dataset&gt;
 *     &lt;location num=&quot;jruby:12/2&quot; addr=&quot;jruby:'Webster Street'.reverse&quot; date=&quot;jruby:DateTime::now() - 14&quot;/&gt;
 * &lt;/dataset&gt;
 * </pre>
 * 
 * A ScriptableDataSet can be created as follows:
 * 
 * <pre>
 * IDataSet wrapped = ...;
 * 
 * List&lt;Class&lt;? extends ScriptInvocationHandler&gt;&gt; handlers = new ArrayList&lt;Class&lt;? extends ScriptInvocationHandler&gt;&gt;();
 * IDataSet scriptableDS = new ScriptableDataSet(
 * 		wrapped, new ScriptableDataSetConfig(&quot;jruby&quot;, &quot;jruby:&quot;, handlers));
 * </pre>
 * 
 * where
 * <ul>
 * <li><b>jruby</b> is the name of a scripting language as understood by
 * <code>javax.script.ScriptEngineManager</code></li>
 * <li><b>jruby:</b> is a prefix, that shall precede fields in that scripting
 * language</li>
 * <li><b>handlers</b> is an optional list of
 * <code>ScriptInvocationHandler</code>s, that can be used to pre-process
 * scripts (e.g. to add common imports) and post-process scripts (e.g. to
 * convert results into data types understood by DBUnit).</li>
 * </ul>
 * 
 * @author Gunnar Morling
 */
public class ScriptableDataSet extends AbstractDataSet {

	private IDataSet wrapped;

	private ScriptableDataSetConfig[] configurations;

	/**
	 * Creates a new ScriptableDataSet.
	 * 
	 * @param wrapped
	 *            Another data set to be wrapped by this scriptable data set.
	 *            Must not be null.
	 * @param configurations
	 *            At least one scriptable data set configuration.
	 */
	public ScriptableDataSet(IDataSet wrapped, ScriptableDataSetConfig... configurations) {

		Validate.notNull(wrapped);
		Validate.notNull(configurations);
		Validate.noNullElements(configurations);

		this.wrapped = wrapped;
		this.configurations = configurations;
	}

	@Override
	protected ITableIterator createIterator(boolean reversed) throws DataSetException {

		return new ScriptableIterator(reversed ? wrapped.reverseIterator() : wrapped.iterator(), configurations);
	}

}
