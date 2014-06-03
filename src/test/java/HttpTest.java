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
import org.springframework.cloud.service.common.RedisServiceInfo;


public class HttpTest {
	
	
	@Test
	public void accept() throws Exception{
		Fibonacci f = new Fibonacci();
		long now = System.currentTimeMillis();
		f.fib(50);
		System.out.println(System.currentTimeMillis()-now);
	}
	
	
	
	public class Fibonacci {
	    public  long fib(int n) {
	        if (n <= 1) return n;
	        else return fib(n-1) + fib(n-2);
	    }

	   

	}
	
	
	
	
}
