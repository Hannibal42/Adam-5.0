import java.sql.*;


class DBController {

	private static final String DB_NAME = "Ergebnisse.db";
	private static final DBController dbcontroller = new DBController();
	private static Connection connection;
	private static final String DB_PATH = System.getProperty("user.dir") + "/" + DB_NAME;  //Gets the current working folder 


	static {
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println(DB_PATH);
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

 		//Closes the connection when the runtime is terminated
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

	public void execute(String query){
		try{
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE IF EXISTS test;");
			stmt.executeUpdate("CREATE TABLE test(test1,test2);");
			connection.close();
		}
		catch(SQLException e){
			System.out.println(e);
		}

	}

}