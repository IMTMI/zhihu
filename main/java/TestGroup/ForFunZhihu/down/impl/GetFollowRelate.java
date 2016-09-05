package TestGroup.ForFunZhihu.down.impl;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import TestGroup.ForFunZhihu.database.impl.MysqlConnect;
import TestGroup.ForFunZhihu.login.impl.zhihu.ZhihuLogin;
import TestGroup.ForFunZhihu.tools.Constant;

public class GetFollowRelate {

	static {
		try {
			result = MysqlConnect.getConn().createStatement().executeQuery("select url,id,hash_id from "+ Constant.UNIQUE_LINK_TABLE+" ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static ResultSet result;
	
	static Connection con = MysqlConnect.getConn();
	
	public static void main(String[] args) {
	try {
		new ZhihuLogin().login();
		while(result.next()){
			new GetSomeOneFollow(result.getString("url"), result.getString("hash_id")).dealData();;
		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}	
	}
	
	
}
