package TestGroup.ForFunZhihu.login.impl.zhihu;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import TestGroup.ForFunZhihu.login.face.Login;
import TestGroup.ForFunZhihu.tools.Clinet;
import TestGroup.ForFunZhihu.tools.XsrfValueGet;

public class ZhihuLogin implements Login{

	
	public static void main(String[] args) {
		Login login = new ZhihuLogin();
		try {
			login.login();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String login() throws Exception {
		 List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
	        valuePairs.add(new BasicNameValuePair("_xsrf" , XsrfValueGet.xsrfValue));//缺少这个值会报错
	        valuePairs.add(new BasicNameValuePair("rememberme", "true"));

	        //获取验证码
	        HttpGet getCaptcha = new HttpGet("http://www.zhihu.com/captcha.gif?r=" + System.currentTimeMillis() + "&type=login");
	        CloseableHttpResponse imageResponse = Clinet.httpClient.execute(getCaptcha);
	        FileOutputStream out = new FileOutputStream("c:/zhihu.gif");
	        byte[] bytes = new byte[8192];
	        int len;
	        while ((len = imageResponse.getEntity().getContent().read(bytes)) != -1) {
	            out.write(bytes,0,len);
	        }
	        out.close();
	        System.out.print("请输入验证码，验证码路径c:/zhihu.gif");
	        Scanner scanner = new Scanner(System.in);
	        String captcha = scanner.next();
	        scanner.close();
	        valuePairs.add(new BasicNameValuePair("captcha", captcha));

	        //完成登陆请求的构造
	        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
	        HttpPost post = new HttpPost("http://www.zhihu.com/login/email");
	        post.setEntity(entity);
	        CloseableHttpResponse loginResponse = Clinet.httpClient.execute(post);//登录，执行此语句后，代表此客户端处于登录状态
	        return EntityUtils.toString(loginResponse.getEntity(), Consts.UTF_8);
	}

	
}
