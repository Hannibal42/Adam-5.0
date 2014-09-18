class Start{
	public static void main(String[] args){
		DBController ct = DBController.getInstance();
		ct.initDBConnection();
		ct.execute("Test");
	}
}