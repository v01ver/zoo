package fi.aapala.v01.zoo.mycamelboots;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Camel context configuration.
 * 
 * @author aapala@gmail.com
 *
 */
@Configuration
public class HttpConfiguration {

	@Value("${http-client.max-total-connections:200}")
	private int maxTotalConnections;

	@Value("${http-client.connections-per-route:20}")
	private int connectionsPerRoute;

	@Value("${http-client.connection-time-to-live:5000}")
	private long connectionTimeToLive;

	@Bean(name = "v01-http")
	public HttpComponent httpComponent(@Qualifier("v01-httpClientConfigurer") HttpClientConfigurer httpClientConfigurer) {
		final HttpComponent httpComponent = new HttpComponent();
		httpComponent.setHttpClientConfigurer(httpClientConfigurer);
		return httpComponent;
	}

	@Bean(name = "v01-https")
	public HttpComponent httpsComponent(@Qualifier("v01-httpClientConfigurer") HttpClientConfigurer httpClientConfigurer, @Qualifier("v01-hostnameVerifier") HostnameVerifier virifier) {
		final HttpComponent httpComponent = new HttpComponent();
		httpComponent.setHttpClientConfigurer(httpClientConfigurer);
		KeyStoreParameters ksp = new KeyStoreParameters();
		ksp.setPassword("keystorePassword");
		 
		KeyManagersParameters kmp = new KeyManagersParameters();
		kmp.setKeyStore(ksp);
		kmp.setKeyPassword("keyPassword");
		SSLContextParameters scp = new SSLContextParameters();
		scp.setKeyManagers(kmp);
		httpComponent.setSslContextParameters(scp);
		httpComponent.setX509HostnameVerifier(virifier);
		return httpComponent;
	}
	
	@Bean(name = "v01-httpClientConfigurer")
	public HttpClientConfigurer httpClientConfigurer(@Qualifier("v01-redirectStrategy") RedirectStrategy redirectStrategy  ) {
		HttpClientConfigurer conf = new HttpClientConfigurer() {
			@Override
			public void configureHttpClient(HttpClientBuilder builder) {
				builder.setRedirectStrategy(redirectStrategy);
				builder.setConnectionTimeToLive(connectionTimeToLive,TimeUnit.MILLISECONDS);
				builder.setMaxConnPerRoute(connectionsPerRoute);
				builder.setMaxConnTotal(maxTotalConnections);
				//builder.disableRedirectHandling();
			}
			
		};
		return conf;
	}
	
		
	@Bean(name = "v01-redirectStrategy")
	public RedirectStrategy laxRedirectStrategy() {
		LaxRedirectStrategy rs  = new LaxRedirectStrategy();
		return new MyRedirectStrategy();
	}
	
	class MyRedirectStrategy extends LaxRedirectStrategy implements RedirectStrategy{

		@Override
		public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext ctx)
				throws ProtocolException {
			
			HttpUriRequest r = super.getRedirect(request, response, ctx);
			return r;
		}

		@Override
		public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
			// TODO Auto-generated method stub
			return super.isRedirected(request, response, context);
		}
		
	}
	
	@Bean(name = "v01-hostnameVerifier")
	public HostnameVerifier hostnameVerifier() {
		HostnameVerifier r = new HostnameVerifier()	{

			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}	
			};
			
		return r;
		
	}
	
}
