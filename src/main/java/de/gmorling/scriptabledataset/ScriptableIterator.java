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

import java.util.List;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;

/**
 * The Class ScriptableIterator.
 */
public class ScriptableIterator implements ITableIterator {

    /** The wrapped. */
    private ITableIterator wrapped;

    /** The configurations. */
    private List<ScriptableDataSetConfig> configurations;

    /**
     * Instantiates a new scriptable iterator.
     *
     * @param wrapped
     *            the wrapped
     * @param configurations
     *            the configurations
     */
    public ScriptableIterator(ITableIterator wrapped, List<ScriptableDataSetConfig> configurations) {
        this.wrapped = wrapped;
        this.configurations = configurations;
    }

    @Override
    public ITable getTable() throws DataSetException {
        return new ScriptableTable(wrapped.getTable(), configurations);
    }

    @Override
    public ITableMetaData getTableMetaData() throws DataSetException {
        return wrapped.getTableMetaData();
    }

    @Override
    public boolean next() throws DataSetException {
        return wrapped.next();
    }

}
