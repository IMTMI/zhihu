package TestGroup.ForFunZhihu.down.impl;



import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.http.Consts;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import TestGroup.ForFunZhihu.database.impl.MysqlConnect;
import TestGroup.ForFunZhihu.login.impl.zhihu.ZhihuLogin;
import TestGroup.ForFunZhihu.tools.Clinet;
import TestGroup.ForFunZhihu.tools.Constant;

public class GetHashId {
public static void main(String[] args) throws Exception {
	new ZhihuLogin().login();
	ResultSet result = MysqlConnect.getConn().createStatement().executeQuery("select url,id,hash_id,hash_id_b from "+ Constant.UNIQUE_LINK_TABLE+" ");//获取关注的人
	while (result.next()) {
		if( result.getObject("hash_id_b")==null){
			String id = result.getObject("id").toString();
			Document doc = Jsoup.parse(EntityUtils.toString(Clinet.httpClient.execute(new HttpGet(result.getObject("url").toString())).getEntity(),Consts.UTF_8));
			String hash_id = null;
			Elements elements1 = doc.getElementsByAttributeValue("class", "zm-profile-header-op-btns clearfix");
			for(Element  element:elements1){
				hash_id = element.childNode(1).attr("data-id");
				if(hash_id==null||hash_id.trim().equals("")){
					throw new Exception("this is wrong in get hashid!");
				}
				MysqlConnect.getConn().createStatement().execute("update "+ Constant.UNIQUE_LINK_TABLE+" set hash_id_b = +\""+hash_id+"\" where id ="+id);
				break;
			}
			getMainPage(id,hash_id,doc);
		}
	}
}
	public static String getHashId(String link) throws Exception {
		Document doc = Jsoup.parse(EntityUtils.toString(Clinet.httpClient.execute(new HttpGet(link)).getEntity(),Consts.UTF_8));
		Elements elements1 = doc.getElementsByAttributeValue("class", "zm-profile-header-op-btns clearfix");
		for(Element  element:elements1){
			String hashid = element.childNode(1).attr("data-id");
			if(hashid==null||hashid.trim().equals("")){
				throw new Exception("this is wrong in get hashid!");
			}
			return hashid;
		}
		throw new Exception("this is wrong in get hashid!");
	}
	
	public static void getMainPage(String id,String hash_id,Document doc){
		String name =  "";
		try {
			name = doc.getElementsByAttributeValue("class", "name").get(1).textNodes().get(0).toString();
		} catch (Exception e) {
			name = Constant.NODATA;
		}
		String intro = "";
		try {
			intro = doc.getElementsByAttributeValue("class", "bio ellipsis").get(0).attr("title");
		} catch (Exception e) {
			intro = Constant.NODATA;
		}
		
		String address = getData(doc,"location item",0);
		String work = getData(doc,"employment item",0);
		String career = getData(doc,"business item",0);
		String school = getData(doc,"education item",0);
		String college = getData(doc,"education-extra item",0);
		String detail = "";
		try {
			Elements temp = doc.getElementsByAttributeValue("class", "content");
			if(temp.size()<6){
				detail = Constant.NODATA;
			}else{
				detail = doc.getElementsByAttributeValue("class", "content").get(0).textNodes().get(0).toString();
			}
		} catch (Exception e) {
			detail = Constant.NODATA;
		}
		inertData(new String[]{id,hash_id,name,intro,address,work,career,school,college,detail});
	}
	
	public static String getData(Document doc,String className,int orderIndex){
		try {
			return doc.getElementsByAttributeValue("class",className).get(orderIndex).attr("title");
		} catch (Exception e) {
			return Constant.NODATA;
		}
	}
	
public static void inertData(String[] arg){
		
		String sql = "insert into personDetail values(";
		String param = "";
		param = param  +arg[0]  + ",";
		for(int i=1;i<arg.length-1;i++){
			param = param +"\"" +arg[i] +"\"" + ",";
		}
		param = param +"\""+ arg[arg.length-1]+"\"";
		sql = sql + param	+ ")";
		try {
			MysqlConnect.getConn().createStatement().execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(sql);
			e.printStackTrace();
		}
	}
}
