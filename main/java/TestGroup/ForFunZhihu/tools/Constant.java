package TestGroup.ForFunZhihu.tools;


/**
 * 
 * @author msi
 *
 */
public interface Constant {

    String followUrl = "https://www.zhihu.com/people/peng-san-shui/followees";
	String MYSQL_DATE_BASE_DIR = "jdbc:mysql://localhost:3306/zhihucrawler2?characterEncoding=utf8";  
	String MYSQL_USER = "root";    
	String MYSQL_PWD = "yuanda";
	String DRIVER = "org.gjt.mm.mysql.Driver";
	String LINK_TABLE = "my_follow";
	String UNIQUE_LINK_TABLE = "table_data";
	
	String RELATE = "relate";
	
	String TEMPTABLE = "my_follow_follow";
	
	String NODATA = "no_data";
}
