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
package de.gmorling.scriptabledataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import de.gmorling.scriptabledataset.handlers.JRubyImportAddingInvocationHandler;
import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for ScriptableDataSet.
 */
class ScriptableDataSetTest {

    /** The connection. */
    private static Connection connection;

    /** The db unit connection. */
    private static IDatabaseConnection dbUnitConnection;

    /** The result set. */
    private ResultSet resultSet;

    /**
     * Initialize connection.
     *
     * @throws Exception
     *             the exception
     */
    @BeforeAll
    static void initializeConnection() throws Exception {
        connection = DriverManager.getConnection("jdbc:derby:derbyTest;create=true");
        connection.setAutoCommit(false);

        dbUnitConnection = new DatabaseConnection(connection);
    }

    /**
     * Creates the table.
     *
     * @throws Exception
     *             the exception
     */
    @BeforeEach
    void createTable() throws Exception {
        connection.createStatement().execute("create table location(num int, addr varchar(40), date timestamp)");
    }

    /**
     * Rollback transaction.
     *
     * @throws Exception
     *             the exception
     */
    @AfterEach
    void rollbackTransaction() throws Exception {
        if (resultSet != null) {
            resultSet.close();
        }
        connection.rollback();
    }

    /**
     * Close connection.
     *
     * @throws Exception
     *             the exception
     */
    @AfterAll
    static void closeConnection() throws Exception {
        dbUnitConnection.close();
    }

    /**
     * Test for using JRuby as scripting language.
     *
     * @throws Exception
     *             In case of any error.
     */
    @Test
    void jRubyScript() throws Exception {
        IDataSet dataSet = new ScriptableDataSet(
                new FlatXmlDataSetBuilder().build(ScriptableDataSetTest.class.getResourceAsStream("jruby.xml")),
                new ScriptableDataSetConfig("jruby", "jruby:"));

        insertDataSetAndCreateResultSet(dataSet);

        assertNextRow(resultSet, 6, "teertS retsbeW", addDaysToToday(-14));
    }

    /**
     * Test for using Groovy as scripting language.
     *
     * @throws Exception
     *             In case of any error.
     */
    @Test
    void groovyScript() throws Exception {
        IDataSet dataSet = new ScriptableDataSet(
                new FlatXmlDataSetBuilder().build(ScriptableDataSetTest.class.getResourceAsStream("groovy.xml")),
                new ScriptableDataSetConfig("groovy", "groovy:"));

        insertDataSetAndCreateResultSet(dataSet);

        assertNextRow(resultSet, 6, "teertS retsbeW", addDaysToToday(-14));
    }

    /**
     * Test for using JRuby and Groovy within one data set file.
     *
     * @throws Exception
     *             In case of any error.
     */
    @Test
    void dataSetWithMultipleLanguages() throws Exception {
        IDataSet dataSet = new ScriptableDataSet(
                new FlatXmlDataSetBuilder()
                        .build(ScriptableDataSetTest.class.getResourceAsStream("multiple_languages.xml")),
                new ScriptableDataSetConfig("jruby", "jruby:"), new ScriptableDataSetConfig("groovy", "groovy:"));

        insertDataSetAndCreateResultSet(dataSet);

        assertNextRow(resultSet, 6, "teertS retsbeW", addDaysToToday(-14));
        assertNextRow(resultSet, 6, "teertS retsbeW", addDaysToToday(-14));
    }

    /**
     * Test for using JRuby as scripting language in conjunction with a special invocation handler.
     *
     * @throws Exception
     *             In case of any error.
     */
    @Test
    void customHandler() throws Exception {
        List<ScriptInvocationHandler> handlers = new ArrayList<>();
        handlers.add(new JRubyImportAddingInvocationHandler());

        IDataSet dataSet = new ScriptableDataSet(
                new FlatXmlDataSetBuilder().build(ScriptableDataSetTest.class.getResourceAsStream("customhandler.xml")),
                new ScriptableDataSetConfig("jruby", "jruby:", handlers));

        insertDataSetAndCreateResultSet(dataSet);

        assertNextRow(resultSet, 1, "Webster Street", addDaysToToday(-14));
    }

    /**
     * Test for usage of an unknown scripting engine.
     *
     * @throws Exception
     *             In case of any error.
     */
    @Test
    void unknownScriptingEngine() throws Exception {
        IDataSet dataSet = new ScriptableDataSet(
                new FlatXmlDataSetBuilder()
                        .build(ScriptableDataSetTest.class.getResourceAsStream("unknownscriptingengine.xml")),
                new ScriptableDataSetConfig("unknown", "unknown:"));

        assertThrows(RuntimeException.class, () -> {
            DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);
        });
    }

    /**
     * Insert data set and create result set.
     *
     * @param dataSet
     *            the data set
     *
     * @throws DatabaseUnitException
     *             the database unit exception
     * @throws SQLException
     *             the SQL exception
     */
    private void insertDataSetAndCreateResultSet(IDataSet dataSet) throws DatabaseUnitException, SQLException {
        DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);
        resultSet = connection.createStatement().executeQuery("SELECT num, addr, date FROM location ORDER BY num");
    }

    /**
     * Assert next row.
     *
     * @param rs
     *            the rs
     * @param expectedInt
     *            the expected int
     * @param expectedString
     *            the expected string
     * @param expectedDate
     *            the expected date
     *
     * @throws SQLException
     *             the SQL exception
     */
    private void assertNextRow(ResultSet rs, int expectedInt, String expectedString, Date expectedDate)
            throws SQLException {
        if (!rs.next()) {
            fail("Data set should have a row.");
        }

        assertEquals(expectedInt, rs.getObject(1));
        assertEquals(expectedString, rs.getObject(2));
        assertEquals(DateUtils.truncate(expectedDate, Calendar.DATE),
                DateUtils.truncate(rs.getObject(3), Calendar.DATE));
    }

    /**
     * Adds the days to today.
     *
     * @param numberOfDays
     *            the number of days
     *
     * @return the date
     */
    private Date addDaysToToday(int numberOfDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, numberOfDays);

        return calendar.getTime();
    }
}
