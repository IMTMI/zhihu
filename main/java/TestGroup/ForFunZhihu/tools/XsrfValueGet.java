package TestGroup.ForFunZhihu.tools;



import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class XsrfValueGet {

	public static String xsrfValue = xsrfValue();
	
	private static String xsrfValue(){
		try {
			HttpGet getHomePage = new HttpGet("http://www.zhihu.com/");
	        //填充登陆请求中基本的参数
	        CloseableHttpResponse response = Clinet.httpClient.execute(getHomePage);
	        String responseHtml = EntityUtils.toString(response.getEntity());
	        xsrfValue = responseHtml.split("<input type=\"hidden\" name=\"_xsrf\" value=\"")[1].split("\"/>")[0];
	        response.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
        return xsrfValue;
	}
}
