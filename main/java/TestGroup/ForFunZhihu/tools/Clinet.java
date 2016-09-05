package TestGroup.ForFunZhihu.tools;



import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Clinet {
	public static RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
	public static CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
}
