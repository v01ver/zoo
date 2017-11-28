/*
 * 
 */
package fi.aapala.zoo.mycamelboots.test;

import java.io.IOException;
import java.util.Map;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.aapala.v01.zoo.mycamelboots.Application;


@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junittest")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestBuilderTest {
	private static final Log LOG = LogFactory.getLog(RestBuilderTest.class);
	
	@Autowired
	protected TestRestTemplate restTemplate;
	

	private ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}


	

	@Test
	public void test001Top100() throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		Object r = restTemplate.exchange("/camel/csv/top100", HttpMethod.GET, httpEntity, Object.class).getBody();
		LOG.info("MAP " + this.prettyJSON(r));
	}

	@Test
	public void test002SearchTitle() throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		Object r = restTemplate.exchange("/camel/csv/search/title/SERGEANT", HttpMethod.GET, httpEntity, Object.class).getBody();
		LOG.info("MAP " + this.prettyJSON(r));
	}
	@Test
	public void test003SearchTitlePost() throws IOException {
		Map titleBody = mapper.readValue(this.getClass().getResourceAsStream("/testdata/title.json"), Map.class);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> httpEntity = new HttpEntity<>(titleBody, headers);
		Object r = restTemplate.exchange("/camel/csv/search/title", HttpMethod.POST, httpEntity, Object.class).getBody();
		LOG.info("MAP " + this.prettyJSON(r));
	}

	@Test
	public void test004GroovyHello() throws JsonProcessingException {
		Object r = restTemplate.getForObject("/camel/hello/groovy/Joe", Object.class);
		LOG.info("MAP " + this.prettyJSON(r));
	}
	
	@Test
	public void test005FreemarkerHello() throws IOException {
		Map body = mapper.readValue(this.getClass().getResourceAsStream("/testdata/hello.json"), Map.class);
		Object r = restTemplate.postForObject("/camel/hello/freemarker/User", body,Object.class);
		LOG.info("MAP " + this.prettyJSON(r));
	}
	
	protected String prettyJSON(Object o) throws JsonProcessingException {
		String json=null;
		json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
		return json;
	}	

	
}
