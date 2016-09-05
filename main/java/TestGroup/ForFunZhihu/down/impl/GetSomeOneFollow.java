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
import TestGroup.ForFunZhihu.tools.Clinet;
import TestGroup.ForFunZhihu.tools.Constant;
import TestGroup.ForFunZhihu.tools.XsrfValueGet;

public class GetSomeOneFollow {
	/**
	 * 数据库连接
	 */
	private static Connection conn = MysqlConnect.getConn();   //数据库连接 
	
	/**
	 * 此人的hash_id,在异步请求的需要使用到
	 */
	private String hash_id;
	
	/**
	 * 此人主页链接
	 */
	private String link;
	
	/**
	 * 此人关注数量
	 */
	private int followNumber;
	
	/**
	 * 异步情请求的查询条件
	 */
	private JSONObject jsonObject = new JSONObject();
	
	/**
	 * 所有异步请求使用一个查询条件,设置不同的entity的值,得到不同的查询结果
	 */
	private HttpPost post = new HttpPost(){
		{
			try {
				this.setHeader("Accept", "*/*");
				this.setHeader("Accept-Encoding", "gzip, deflate");
				this.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
				this.setHeader("Connection", "keep-alive");
				this.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				this.setHeader("Host", "www.zhihu.com");
				this.setHeader("Origin", "https://www.zhihu.com");
				this.setHeader("Referer", GetSomeOneFollow.this.link+"/followees");
				this.setHeader("X-Requested-With", "XMLHttpRequest");
				this.setHeader("X-Xsrftoken",XsrfValueGet.xsrfValue);
				this.setURI(new URI("https://www.zhihu.com/node/ProfileFolloweesListV2"));
			} catch (Exception e) {
				
			}
		
		}
	};
	
	/**
	 * 查询条件需要使用的
	 */
	private  int offset = 0;
	
	/**
	 * 一次查询的增加值
	 */
	private static int addLadder = 20;
	
	
	
	public GetSomeOneFollow(String link,String hash_id){
		this.link = link;
		this.hash_id = hash_id;
	}
	
	public void dealTop20() throws SQLException, ParseException, ClientProtocolException, IOException{
		HttpGet getHomePage = new HttpGet(this.link+"/followees");
		Document doc = Jsoup.parse(EntityUtils.toString(Clinet.httpClient.execute(getHomePage).getEntity(),Consts.UTF_8));
		Elements elements = doc.getElementsByAttributeValue("class", "author-link-line");
		for(Element elemnt:elements){
			inertIntoDataBase(conn.createStatement(),elemnt.getElementsByTag("a").get(0).attr("href"));
		}
		Elements elements2 = doc.getElementsByAttributeValue("class", "zg-gray-normal");
		this.followNumber = Integer.valueOf(elements2.get(0).nextElementSibling().nextElementSibling().text());
	}
	
	/**
	 * @throws URISyntaxException
	 * @throws ParseException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void dealOther() throws URISyntaxException, ParseException, ClientProtocolException, IOException{
		jsonObject.put("offset", offset+addLadder);
		jsonObject.put("order_by", "created");
		jsonObject.put("hash_id", this.hash_id);
		//dd14712cc8edf62751dcbf8e559f308a//通过再次去网站请求，发现这个值还是一样的，如果传递null系统会报错

		post.setEntity(new UrlEncodedFormEntity(new LinkedList<NameValuePair>(){
			{
				this.add(new BasicNameValuePair("method","next"));
				this.add(new BasicNameValuePair("params",jsonObject.toString()));
			}
		}, Consts.UTF_8));
		String response = EntityUtils.toString(Clinet.httpClient.execute(post).getEntity(),Consts.UTF_8);
		analysisResonse(response);
		for(int i=0;i<(this.followNumber/20);i++){
			offset = offset + addLadder;
			jsonObject.put("offset", offset);
			post.setEntity(new UrlEncodedFormEntity(new LinkedList<NameValuePair>(){
				{
					this.add(new BasicNameValuePair("method","next"));
					this.add(new BasicNameValuePair("params",jsonObject.toString()));
				}
			}, Consts.UTF_8));
			response = EntityUtils.toString(Clinet.httpClient.execute(post).getEntity(),Consts.UTF_8);
			analysisResonse(response);
		}
	}

	private void analysisResonse(String response) {
		try {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void inertIntoDataBase(Statement temp, String oprateString) throws SQLException{
		String sql = "insert into "+Constant.TEMPTABLE+" values(\""+ this.hash_id +"\",\""+oprateString+"\")";
		temp.executeUpdate(sql);
	}
	
	public void dealData(){
		try {
			System.out.println("开始处理数据！");
			this.dealTop20();
			this.dealOther();
			System.out.println("数据处理结束！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
