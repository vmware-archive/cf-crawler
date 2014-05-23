import java.io.ByteArrayInputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;


public class HttpTest {
	
	
	@Test
	public void accept() throws Exception{
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(2000)
		        .setSocketTimeout(1000)
		        .setConnectTimeout(1000)
		        .build();
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://www.rabbitmq.com");
		get.addHeader(new BasicHeader("Accept", "text/html"));
		get.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		response = (CloseableHttpResponse) client.execute(get);
		System.out.println(response.getStatusLine());
		ContentType ct = ContentType.parse(response.getHeaders("Content-Type")[0].getValue());
		if(ct.getMimeType().equals(ContentType.TEXT_HTML.getMimeType())){
		byte[] content = EntityUtils.toByteArray(response.getEntity());
		long endTime = System.currentTimeMillis();
		
		ByteArrayInputStream input = new ByteArrayInputStream(content);
		System.out.println(content.length);
		}
	}
	
	
	
	
	
	
	
}
