//package de.ifsr.adam;

/**
* The DBController is a singelton that manages the connection to the sqlite database.
*/

import java.sql.*;

public class DBController {

	private static final String DB_NAME = "Ergebnisse.db"; // TODO: This should be more flexibel
	private static final DBController dbcontroller = new DBController();
	private static Connection connection;
	private static final String DB_PATH = System.getProperty("user.dir") + "/" + DB_NAME;  //Gets the current working folder 

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e){
			System.err.println("Fehler beim Laden des JDBC-Treibers");
			e.printStackTrace();
		}
	}

	private DBController(){
	}

	public static DBController getInstance(){
		return dbcontroller;
	}

	/**
	* Initializes the Connection and creates a thread that closes the connection when the Runtime is shutdown  
	*/
	public void initDBConnection(){

		try {
			if (connection != null)
				return;
			System.out.println("Creating Connection to Database...");
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			if (!connection.isClosed())
				System.out.println("...Connection established");
		}
		catch(SQLException e){
			throw new RuntimeException(e);
		}

 		//Creates a Thread that closes the DB Connection, runs when the Runtime is shutdown 
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run(){
				try {
					if (!connection.isClosed() && connection != null){
						connection.close();
						if(connection.isClosed())
							System.out.println("Connection to Database closed.");
					}					
				}
				catch (SQLException e){
					e.printStackTrace();
				}
			}
		});
	}


	/**
	* @param A SQL Query
	* @return Creates a prepared statement.
	*/
	public PreparedStatement getPreparedStatement(String query) throws SQLException{
			return connection.prepareStatement(query);
	}

	public Statement getStatement() throws SQLException {
		return connection.createStatement();
	}

	/**
	* Sets the auto commit mode of the sql databse
	*/
	public void setAutoCommit(Boolean mode) throws SQLException{
		connection.setAutoCommit(mode);
	}

	/**
	* commits all querys
	*/
	public void commit() throws SQLException{
		connection.commit();
	}

}