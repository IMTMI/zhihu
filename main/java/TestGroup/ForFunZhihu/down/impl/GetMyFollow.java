package TestGroup.ForFunZhihu.down.impl;



import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import TestGroup.ForFunZhihu.database.impl.MysqlConnect;
import TestGroup.ForFunZhihu.login.impl.zhihu.ZhihuLogin;
import TestGroup.ForFunZhihu.tools.Clinet;
import TestGroup.ForFunZhihu.tools.Constant;
import TestGroup.ForFunZhihu.tools.XsrfValueGet;

public class GetMyFollow{

	private static Connection conn = MysqlConnect.getConn();   //数据库连接 
	
	private static JSONObject jsonObject = new JSONObject();//此次查询条件
	
	private static int offset = 0;
	
	private static int addLadder = 20;
	
	static String xsrfValue = XsrfValueGet.xsrfValue;//在很多操作中都会用到的操作,提取为类变量
	
	
	private static void getFirstTop20() throws ParseException, ClientProtocolException, IOException, SQLException {
		HttpGet getHomePage = new HttpGet(Constant.followUrl);
		Document doc = Jsoup.parse(EntityUtils.toString(Clinet.httpClient.execute(getHomePage).getEntity(),Consts.UTF_8));
		Elements elements = doc.getElementsByAttributeValue("class", "author-link-line");
		for(Element elemnt:elements){
			inertIntoDataBase(conn.createStatement(),elemnt.getElementsByTag("a").get(0).attr("href"));
		}
	}
	
	public static void getFollowees() throws ClientProtocolException, IOException, URISyntaxException{
		//method:next
		//params:{"offset":20,"order_by":"created","hash_id":"dd14712cc8edf62751dcbf8e559f308a"}
		jsonObject.put("offset", offset+addLadder);
		jsonObject.put("order_by", "created");
		jsonObject.put("hash_id", "dd14712cc8edf62751dcbf8e559f308a");
								 //dd14712cc8edf62751dcbf8e559f308a//通过再次去网站请求，发现这个值还是一样的，如果传递null系统会报错
		JSONObject outJson = new JSONObject();
		outJson.put("method", "next");
		outJson.put("params", jsonObject);
		System.out.println(jsonObject.toJSONString());
        HttpPost post = new HttpPost();
		post.setHeader("Accept", "*/*");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		post.setHeader("Host", "www.zhihu.com");
		post.setHeader("Origin", "https://www.zhihu.com");
		post.setHeader("Referer", "https://www.zhihu.com/people/peng-san-shui/followees");
		post.setHeader("X-Requested-With", "XMLHttpRequest");
		post.setHeader("X-Xsrftoken", xsrfValue);
		//Content-Length:132
		//Cookie:d_c0="ABDAwVZB0wmPTlEVss-yTFPSM9TipK_kAkE=|1461575921"; _za=b2cd8ee8-abe1-402c-9dc5-fdf10b904c2d; _zap=02497b51-93e1-4003-b618-b20e102956c8; q_c1=71524e85616d4e66b3525c713b619b7b|1469791883000|1461575920000; _xsrf=6296de8fb7bf7faa70c48dd3bfc903ae; l_cap_id="ZWRkYWZiNWIwMzUwNGQ5NmFiZmQ1NjJkNTI0MDM5ODY=|1470457073|eedb01f71f527e5e5558b1af1f74305a3c12d132"; cap_id="NmMzZDE4ZDhlNjAxNGNjNjk5MGIzYWY4MjBiOWNlYTA=|1470457073|1642b35bc058c540c0308a7b5a18b0aa326b2a01"; login="ZDM2ZGFlZGFmOWQ1NGU2ZjgwZDRiOGFjNWIzNDgxYjY=|1470457084|4ba16a2a1d3a15d0d88c6a09508d4e5567e9ae91"; z_c0=Mi4wQUFBQTRJMGpBQUFBRU1EQlZrSFRDUmNBQUFCaEFsVk5fZkhNVndBSVVkZV9LMFhwZG5TY0REblhlWnd1cEpsUGV3|1470457085|b990902fb84803aab45bddc6d8b3ddcec05d53cd; __utmt=1; a_t="2.0AAAA4I0jAAAXAAAAzZDNVwAAAOCNIwAAABDAwVZB0wkXAAAAYQJVTf3xzFcACFHXvytF6XZ0nAw513mcLqSZT3voIVBGYHxl19x4VAfdlCLnTSCiCg=="; __utma=51854390.638982859.1470495825.1470495825.1470495825.1; __utmb=51854390.21.9.1470497424666; __utmc=51854390; __utmz=51854390.1470495825.7.6.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmv=51854390.100-1|2=registration_date=20140106=1^3=entry_date=20140106=1
		//X-Xsrftoken:6296de8fb7bf7faa70c48dd3bfc903ae7
		post.setURI(new URI("https://www.zhihu.com/node/ProfileFolloweesListV2"));
		//post.setEntity(new StringEntity(outJson.toJSONString()));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(new LinkedList<NameValuePair>(){
			{
				this.add(new BasicNameValuePair("method","next"));
				this.add(new BasicNameValuePair("params",jsonObject.toString()));
			}
		}, Consts.UTF_8);
		
		post.setEntity(entity);
		String response = EntityUtils.toString(Clinet.httpClient.execute(post).getEntity(),Consts.UTF_8);
		System.out.println(post.getAllHeaders().toString());
		try {
			analySis(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<6;i++){
			nextFollowRequest();
		}

		
		//List<BasicNameValuePair> queryList= new ArrayList<>();
		//queryList.add(new BasicNameValuePair("method", "next"));
		//post.setEntity(new UrlEncodedFormEntity().);
		//post.setEntity(new StringEntity(, charset));
		System.out.println("for debug!");
	}
	
	public static void nextFollowRequest(){
		offset = offset + addLadder;
		jsonObject.put("offset", offset);
		HttpPost post = new HttpPost();
		post.setHeader("Accept", "*/*");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		post.setHeader("Host", "www.zhihu.com");
		post.setHeader("Origin", "https://www.zhihu.com");
		post.setHeader("Referer", "https://www.zhihu.com/people/peng-san-shui/followees");
		post.setHeader("X-Requested-With", "XMLHttpRequest");
		post.setHeader("X-Xsrftoken", xsrfValue);
		//Content-Length:132
		//Cookie:d_c0="ABDAwVZB0wmPTlEVss-yTFPSM9TipK_kAkE=|1461575921"; _za=b2cd8ee8-abe1-402c-9dc5-fdf10b904c2d; _zap=02497b51-93e1-4003-b618-b20e102956c8; q_c1=71524e85616d4e66b3525c713b619b7b|1469791883000|1461575920000; _xsrf=6296de8fb7bf7faa70c48dd3bfc903ae; l_cap_id="ZWRkYWZiNWIwMzUwNGQ5NmFiZmQ1NjJkNTI0MDM5ODY=|1470457073|eedb01f71f527e5e5558b1af1f74305a3c12d132"; cap_id="NmMzZDE4ZDhlNjAxNGNjNjk5MGIzYWY4MjBiOWNlYTA=|1470457073|1642b35bc058c540c0308a7b5a18b0aa326b2a01"; login="ZDM2ZGFlZGFmOWQ1NGU2ZjgwZDRiOGFjNWIzNDgxYjY=|1470457084|4ba16a2a1d3a15d0d88c6a09508d4e5567e9ae91"; z_c0=Mi4wQUFBQTRJMGpBQUFBRU1EQlZrSFRDUmNBQUFCaEFsVk5fZkhNVndBSVVkZV9LMFhwZG5TY0REblhlWnd1cEpsUGV3|1470457085|b990902fb84803aab45bddc6d8b3ddcec05d53cd; __utmt=1; a_t="2.0AAAA4I0jAAAXAAAAzZDNVwAAAOCNIwAAABDAwVZB0wkXAAAAYQJVTf3xzFcACFHXvytF6XZ0nAw513mcLqSZT3voIVBGYHxl19x4VAfdlCLnTSCiCg=="; __utma=51854390.638982859.1470495825.1470495825.1470495825.1; __utmb=51854390.21.9.1470497424666; __utmc=51854390; __utmz=51854390.1470495825.7.6.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmv=51854390.100-1|2=registration_date=20140106=1^3=entry_date=20140106=1
		//X-Xsrftoken:6296de8fb7bf7faa70c48dd3bfc903ae7
		try {
			post.setURI(new URI("https://www.zhihu.com/node/ProfileFolloweesListV2"));
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//post.setEntity(new StringEntity(outJson.toJSONString()));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(new LinkedList<NameValuePair>(){
			{
				this.add(new BasicNameValuePair("method","next"));
				this.add(new BasicNameValuePair("params",jsonObject.toString()));
			}
		}, Consts.UTF_8);
		
		post.setEntity(entity);
		try {
			analySis(EntityUtils.toString(Clinet.httpClient.execute(post).getEntity(),Consts.UTF_8));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void inertIntoDataBase(Statement temp, String oprateString) throws SQLException {
		String sql = "insert into "+Constant.LINK_TABLE+" values(\""+ oprateString +"\",\"\")";
		System.out.println(oprateString);
		temp.executeUpdate(sql);
	}
	
	public static void analySis(String response) throws Exception{
		System.out.println("未变前"+response);
		response = response.replaceAll("\\&quot;", "\"");
		System.out.println(response);
		JSONObject json = (JSONObject) JSON.parse(response);
		String a = json.get("msg").toString();
		System.out.println(a);
		a = a.substring(1, a.length()-1);
		response = response.replaceAll("\\\"", "\"");
		System.out.println(response);
		Document doc = Jsoup.parse(a);
		System.out.println(doc);
		Elements aName = doc.getElementsByTag("span");
		System.out.println(aName.size());
		Elements elements = new Elements();
		for(Element element:aName){
			if(element.attr("class").equals("\\\"author-link-line\\\"")){
				elements.add(element);
			}
		}
		System.out.println(elements.size());
		System.out.println(elements.toString());
		System.out.println(conn);
		Statement temp = conn.createStatement();
		for(Element tempElement:elements){
			String oprateString;
			oprateString = (oprateString = tempElement.getElementsByTag("a").get(0).attr("href")).substring(2,oprateString.length()-2);
			inertIntoDataBase(temp, oprateString);
		}
		System.out.println(aName.size());
		
	}
	
	public static void main(String[] args) {
		try {
			new ZhihuLogin().login();
			getFirstTop20();
			getFollowees();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
