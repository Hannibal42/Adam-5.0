import java.sql.*;
/* The DBController class is used to manage the DBConnection and takes all the sql query */

class DBController {

	private static final String DB_NAME = "Ergebnisse.db"; // This should be more flexibel
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
	
	public PreparedStatement getStatement(String query) throws SQLException{
			return connection.prepareStatement(query);
	}

	public void setAutoCommit(Boolean mode) throws SQLException{
		connection.setAutoCommit(mode);
	}

	public void commit() throws SQLException{
		connection.commit();
	}

	//Is there a better way to do this?
	public void execute(String query){
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(query);
		}
		catch(SQLException e){
			System.out.println(e); //Maybe add some loging here
		}

	}

}