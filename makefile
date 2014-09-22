#C:\Users\Simon\Desktop\JAdam\Tools\sqlite-jdbc-3.7.2.jar
#
#	java -classpath ".;sqlite-jdbc-3.7.2.jar" SQLiteJDBC

#defines the java compiler
JCC = javac

#defines the compilation flags
JFlags = -g -classpath ".;sqlite-jdbc-3.7.2.jar"

#typing 'make' invokes the first entry target in the makefile
default: View.class DBController.class CSVReader.class

View.class: View.java
	$(JCC) $(JFlags) View.java
DBController.class: DBController.java
	$(JCC) $(JFlags) DBController.java
CSVReader.class: CSVReader.java
	$(JCC) $(JFlags) CSVReader.java

#delets all .class files
clean:
	$(RM) *.class
