package fi.aapala.v01.zoo.mycamelboots.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import fi.aapala.v01.zoo.mycamelboots.csv.dao.CsvMapper;

@Component
public class RestBuilder extends RouteBuilder {

	private static final Log LOG = LogFactory.getLog(RestBuilder.class);

	@Autowired
	private CsvMapper csvmapper;

	@Autowired
	@Qualifier(CamelConfiguration.BEAN_DATA_FORMAT_JSONMAP)
	private DataFormat mapFormat;
	
	
	@Override
	public void configure() throws Exception {

		rest("/csv").consumes("application/json").produces("application/json")
		.get("/top100")
		.route()
		.setBody().groovy("return [:]")
		.to("direct:v01csvtop100")
		.endRest()
		
		.get("/search/title/{title}")
		.route()
		.setBody().groovy("return [title:request.headers.title]")
		.to("direct:v01searchtitle")
		.endRest()
		.post("/search/title")
		.route()
		.to("direct:v01searchtitle")
		.endRest()
		;
		
		rest("/hello")
		.get("/groovy/{name}")
		.to("language:groovy:classpath:/script/hello.groovy")
		.post("/freemarker/{role}")
		.route()
		.to("freemarker:/template/hello.ftl")
		.unmarshal(mapFormat)
		.endRest()
		
		;

		
		from("direct:v01csvtop100")
		.setBody().method(csvmapper, "selecttop100")
		.to("mock:r")
		;
		
		from("direct:v01searchtitle")
		.setBody().method(csvmapper, "searchByTitle")
		.to("mock:r")
		;
	
	}

}
