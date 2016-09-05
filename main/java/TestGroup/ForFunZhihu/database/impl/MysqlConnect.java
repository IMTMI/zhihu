package TestGroup.ForFunZhihu.database.impl;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import TestGroup.ForFunZhihu.tools.Constant;

public class MysqlConnect {

	public static Connection conn;
	
	
	private MysqlConnect(){
		
	}
	
	static{
		try {
			Class.forName(Constant.DRIVER);//加载MySql驱动
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
  //连接数据库  
		//DriverManager.registerDriver(new org.gjt.mm.mysql.Driver());    //这句话可需可不需  
		//DriverManager.registerDriver(new com.mysql.jdbc.Driver());      //同上  
	}
	public static  Connection getConn() {
			try {
				conn = DriverManager.getConnection(Constant.MYSQL_DATE_BASE_DIR, Constant.MYSQL_USER, Constant.MYSQL_PWD);
			} catch (SQLException e) {
				e.printStackTrace();
			}//获取一个链接
			return conn;
	}
	
}
