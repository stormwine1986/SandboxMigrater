package org.ilm.tools.sandbox.migrate.assistant.internal;

import java.sql.Connection;
import java.sql.DriverManager;

public class DerbyConncetionFactory {
	
	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static String protocol = "jdbc:derby:";
	
	public static Connection getConnection(String dbname) throws Exception {
		Class.forName(driver).newInstance();
		return DriverManager.getConnection(protocol + dbname);
	}
}
