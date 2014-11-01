package de.ifsr.adam;

/**
* The DBController is a singelton that manages the connection to the sqlite-database. A new Database Ergebnisse.db is created in the working directory.
*/

import java.sql.*;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class DBController {

	private static final String DB_NAME = "Ergebnisse.db";
	private static final DBController dbcontroller = new DBController();
	private static Connection connection;
	private static final String DB_PATH = System.getProperty("user.dir") + "/" + DB_NAME;
        final static Logger log = Logger.getLogger(ImageGenerator.class.getName());

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e){
			log.error("Fehler beim Laden des JDBC-Treibers",e);
		}
	}

	private DBController(){
	}

	public static DBController getInstance(){
		return dbcontroller;
	}

	/**
	* Initializes the Connection and creates a thread that closes the connection when the Runtime is shutdown. Must be called before any statements can be created/executed. 
	*/
	public void initDBConnection(){

		try {
			if (connection != null)
				return;
			log.info("Creating Connection to Database...");
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			if (!connection.isClosed())
				log.info("...Connection established");
		}
		catch(SQLException e){
			throw new RuntimeException(e);
		}

 		//Creates a Thread that closes the DB Connection, runs when the Runtime is shutdown 
		Runtime.getRuntime().addShutdownHook(new Thread() {
                    
                        @Override
			public void run(){
				try {
					if (!connection.isClosed() && connection != null){
						connection.close();
						if(connection.isClosed())
							log.info("Connection to Database closed.");
					}					
				}
				catch (SQLException e){
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
    public PreparedStatement getPreparedStatement(String query) throws SQLException{
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
    * @param mode
    * @throws java.sql.SQLException
    */
    public void setAutoCommit(Boolean mode) throws SQLException{
	connection.setAutoCommit(mode);
    }

    /**
    * commits all querys
    * @throws java.sql.SQLException
    */
    public void commit() throws SQLException{
	connection.commit();
    }
        
    public ArrayList<String> getTableNames(){
        initDBConnection();
        String query = "SELECT name FROM sqlite_master WHERE type='table';";
        ArrayList<String> tableNames = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            while(resultSet.next()) {
                tableNames.add(resultSet.getString(1));
            }    
        }
        catch(Exception e){
            log.error(e);
        }
        return tableNames;
    }
}