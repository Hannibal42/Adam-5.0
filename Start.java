import java.sql.*;

class Start{
	public static void main(String[] args){
		DBController ct = DBController.getInstance();
		ct.initDBConnection();
		ct.execute("DROP TABLE IF EXISTS test;");
		ct.execute("CREATE TABLE test(test1,test2);");
		try {
		PreparedStatement stmt = ct.getStatement("INSERT INTO test VALUES (?,?)");
		stmt.setString(1,"Simon");
		stmt.setString(2,"Hanisch");
		stmt.executeUpdate();
	 	}
	 	catch(SQLException e){
	 		e.printStackTrace();
	 	}
	}
}