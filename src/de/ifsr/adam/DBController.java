/* 
 * The MIT License
 *
 * Copyright 2014 Simon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.ifsr.adam;

/**
 * The DBController is a singelton that manages the connection to the sqlite-database. A new
 * Database Ergebnisse.db is created in the working directory.
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;

import org.apache.log4j.Logger;

public class DBController {

    private static final String DB_NAME = "Ergebnisse.db";
    private static final DBController dbcontroller = new DBController();
    private static Connection connection;
    private static final String DB_PATH = System.getProperty("user.dir") + "/" + DB_NAME;
    final static Logger log = Logger.getLogger(DBController.class.getName());

    static {
	try {
	    Class.forName("org.sqlite.JDBC");
	} catch (ClassNotFoundException e) {
	    log.error("Fehler beim Laden des JDBC-Treibers", e);
	}
    }

    private DBController() {
    }

    public static DBController getInstance() {
	return dbcontroller;
    }

    /**
     * Initializes the Connection and creates a thread that closes the connection when the Runtime
     * is shutdown. Must be called before any statements can be created/executed.
     */
    public void initDBConnection() {

	try {
	    if (connection != null) {
		return;
	    }
	    log.info("Creating Connection to Database...");
	    connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
	    if (!connection.isClosed()) {
		log.info("...Connection established");
	    }
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}

	//Creates a Thread that closes the DB Connection, runs when the Runtime is shutdown 
	Runtime.getRuntime().addShutdownHook(new Thread() {

	    @Override
	    public void run() {
		try {
		    if (!connection.isClosed() && connection != null) {
			connection.close();
			if (connection.isClosed()) {
			    log.info("Connection to Database closed.");
			}
		    }
		} catch (SQLException e) {
		    log.error(e);
		}
	    }
	});
    }

    /**
     * @param query A SQL query
     * @return Creates a prepared statement.
     * @throws java.sql.SQLException
     */
    public PreparedStatement getPreparedStatement(String query) throws SQLException {
	return connection.prepareStatement(query);
    }

    /**
     * @return Creates a statement.
     * @throws java.sql.SQLException
     */
    public Statement getStatement() throws SQLException {
	return connection.createStatement();
    }

    /**
     * Sets the auto commit mode of the sql-database.
     *
     * @param mode
     * @throws java.sql.SQLException
     */
    public void setAutoCommit(Boolean mode) throws SQLException {
	connection.setAutoCommit(mode);
    }

    /**
     * commits all querys
     *
     * @throws java.sql.SQLException
     */
    public void commit() throws SQLException {
	connection.commit();
    }

    /**
     * 
     * @return All the table names currently in the database
     */
    public ArrayList<String> getTableNames() {
	initDBConnection();
	String query = "SELECT name FROM sqlite_master WHERE type='table';";
	ArrayList<String> tableNames = new ArrayList<>();
	try {
	    Statement stmt = connection.createStatement();
	    ResultSet resultSet = stmt.executeQuery(query);
	    while (resultSet.next()) {
		tableNames.add(resultSet.getString(1));
	    }
	} catch (Exception e) {
	    log.error(e);
	}
	return tableNames;
    }
    
    /**
     * Warning: This function only works on tables with INTEGER as column type.
     * @param tableName the table you want to operate on.
     * @return the column names of a table
     */
    private ArrayList<String> getColumnNames(String tableName){
	initDBConnection();
	String query = "SELECT sql FROM sqlite_master "
	    + "WHERE tbl_name ='" + tableName + "' AND type='table';";
	ArrayList<String> columnNames = new ArrayList<>();
	try{
	    Statement stmt = connection.createStatement();
	    ResultSet resultSet = stmt.executeQuery(query);
	    resultSet.next();
	    String columnName = resultSet.getString(1);
	    columnName = columnName.substring(columnName.indexOf("(")+1,columnName.length()-1); //Removes everything outside the brackets
	    columnName = columnName.replace("INTEGER", ""); //Removes the column type INTEGER
	    columnName = columnName.replace(" ", ""); //Removes spaces
	    columnNames = new ArrayList(Arrays.asList(columnName.split(",")));
	} catch (Exception e) {
	    log.error(e);
	}
	
	return columnNames;
    }
    
    /**
     * Normalizes the tabels into one format. Each questions is represented by one column with
     * values 1-n that represent the possible answers.
     */
    public void normalizeTables(){
	Iterator<String> tableNames = getTableNames().iterator();
	while(tableNames.hasNext()){
	    String tableName = tableNames.next();
	    normalizeTable(tableName);
	    }
	}
   
    
    /**
     * Normalizes the specified table
     * @param tableName 
     */
    public void normalizeTable(String tableName){
	ArrayList<String> columnNames = getColumnNames(tableName);
	for (int i = 0; i < columnNames.size(); i++) {
	    String columnName = columnNames.get(i);
	    String prefix;
	    
	    try{
		prefix = columnName.substring(0,columnName.indexOf("_"));//Gets the prefix of the columnName
		
	    } catch (StringIndexOutOfBoundsException e){
		prefix = " "; //Causes the while loop to fail
	    }
	    
	    ArrayList<String> toBejoined = new ArrayList<>();
	    int k = 1;
	    while(columnName.startsWith(prefix) && (i+k) < columnNames.size()){
		toBejoined.add(columnName);
		columnName = columnNames.get(i+k);
		k++;
	    }
	    
	    if(toBejoined.size() > 1){
		joinColumns(toBejoined, tableName, prefix);
		i = i + k - 2; //Jumps to the next index behind the columns that get joined
	    }
	}
    }
    
    /** TODO: Fix
     * Joins the specified columns into one. Input:[F15_1,15,_2,15_3] Output: F15
     * @param columnNames 
     * @param tableName 
     * @param newColumnName the name of the new column
     */
    private void joinColumns(ArrayList<String> columnNames, String tableName, String newColumnName){
	initDBConnection();

	try {	    
	    Statement stmt = this.getStatement();
	    stmt.execute("ALTER TABLE " + tableName + " ADD " + newColumnName + ";");
	} catch (SQLException e) {
	    //log.debug(e); TODO: Do I need login for this?
	}
	
	String query = "SELECT ";
	Iterator<String> iter = columnNames.iterator();
	while(iter.hasNext()){
	    query += iter.next() + ", ";
	}
	//query = query.substring(0, query.length()-2); //Removes ", " from the end of the string;
	query += "rowid FROM " + tableName;
	
	try {
	    setAutoCommit(false);
	    String updateQuery = "UPDATE " + tableName + " SET " + newColumnName + "=?"+ 
		    " WHERE rowid=?;";
	    
	    PreparedStatement prepStmt = this.getPreparedStatement(updateQuery); //TODO: Index einstellen
	    Statement stmt = this.getStatement();
	    ResultSet resultSet = stmt.executeQuery(query);
	    while(resultSet.next()){
		int value;
		prepStmt.setInt(2, resultSet.getInt("rowid"));
		for(int i = 1; i <= columnNames.size()-1; i++){ //-1 so you dont end up in the rowid	
		    value = resultSet.getInt(i);
		    if(value == 1){
			prepStmt.setInt(1, i);
		    }
		    System.out.println("Result: " + resultSet.getInt(i)+ ", " + resultSet.getInt("rowid")); //TODO: Remove
		}
		prepStmt.addBatch();
	    }
	    prepStmt.executeBatch();
	    this.commit();
	} catch(SQLException e) {
	    log.error(e);
	} finally {
	    try{
		setAutoCommit(true);
	    } catch (SQLException e){
		log.error(e);
	    }
	}
    }

    
    public static void main(String[] args){
	BasicConfigurator.configure();
	Logger.getRootLogger().setLevel(Level.ALL);
	DBController dbc = new DBController();
	ArrayList<String> tableNames = dbc.getTableNames();
	System.out.println(dbc.getColumnNames(tableNames.get(0)));
	dbc.normalizeTables();
    }
}


