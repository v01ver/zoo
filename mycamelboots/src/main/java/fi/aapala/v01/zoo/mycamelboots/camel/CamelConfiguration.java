package fi.aapala.v01.zoo.mycamelboots.camel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.JavaUuidGenerator;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.UseOriginalAggregationStrategy;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Camel context configuration.
 * @author aapala@gmail.com
 *
 */
@Configuration
public class CamelConfiguration{
	
	public static final String BEAN_DATA_FORMAT_JSONMAP = "v01MapDataFormat";
	public static final String BEAN_DATA_FORMAT_JSONLIST = "v01ListDataFormat";
	public static final String BEAN_MAP_STRATEGY = "v01MapAggregationStrategy";
	public static final String BEAN_UUID = "v01JavaUuidGenerator";
	public static final String BEAN_USE_ORIGINAL = "v01UseOriginalAggregationStrategy";
	public static final String DATE_FORMAT_UTC_ISO_8601  = "yyyy-MM-dd'T'HH:mm:ss";

	
	
	
	public static final String KEY = "tmpV01MapAggregationStrategyKey";
	public static final String MAP_KEY_OLD = "old";
	public static final String NEW_VALUE_PREFIX = "new";

	
	@Value("${v01.contextpath:/camel}")
	private String camelContextpath;
	
	@Value("${v01.enableCORS:true}")
	private boolean camelEnableCORS;
	
	@Value("${v91.prettyprint:true}")
	private String camelPrettyPrint;
	

	
    
    @Bean(name=BEAN_MAP_STRATEGY)
    public AggregationStrategy mapAggregationStrategy(){
    		
    	AggregationStrategy r = new AggregationStrategy() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
				String key = null;
				Map mapO;
				if ( key == null ) {
					
				}
				
				Object out = oldExchange.getIn().getBody(Object.class);
				out = (out==null)?new HashMap():out;
				
				if( out instanceof Map ) {
					mapO = (Map) out;
				}else {
					mapO = new HashMap<>();
					mapO.put(MAP_KEY_OLD, out);
				}
				
				Object newo = newExchange.getIn().getBody(Object.class);
				newo = (newo==null)?new HashMap():newo;
				if ( oldExchange.getIn().getHeaders().containsKey(KEY) ) {
					key = (String)oldExchange.getIn().getHeaders().get(KEY);
				} else {
					int i = 0;
					while(true) {
						if (  mapO.containsKey(NEW_VALUE_PREFIX + i)) {
							i++;
						}else {
							key = NEW_VALUE_PREFIX + i;
							break;
						}
					}
				}
				mapO.put(key, newo);
				newExchange.getIn().setBody(mapO, Map.class);
				return newExchange;
			}
    		
    	};
    	
    	return r;
    
    
    }
    
    
	@Bean(name=BEAN_USE_ORIGINAL)
	public UseOriginalAggregationStrategy UseOriginalAggregationStrategy() {
		return new UseOriginalAggregationStrategy();
	}


    @Bean(name=BEAN_DATA_FORMAT_JSONLIST)
    public DataFormat getListDataFormat(){
        JacksonDataFormat R = new JacksonDataFormat();
        R.setUnmarshalType(List.class);
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_UTC_ISO_8601);
		ObjectMapper oMapper = new ObjectMapper();
        oMapper.setDateFormat(df);
		R.setObjectMapper(oMapper);
    	return R;
    }
    
    
    
    @Bean(name=BEAN_DATA_FORMAT_JSONMAP)
    @Primary
    public DataFormat mapDataFormat(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT_UTC_ISO_8601));
		return new JacksonDataFormat(mapper, Map.class);
    }
    
    
    @Bean
    public UseOriginalAggregationStrategy useOriginalAggregationStrategy(){
    	return new UseOriginalAggregationStrategy();
    }
    
    @Bean(name="uuid")
    public JavaUuidGenerator javaUuidGenerator(){
    	JavaUuidGenerator r = new JavaUuidGenerator();
    	return r;
    }
	
    @Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(
            new CamelHttpTransportServlet(), camelContextpath + "/*");
        servlet.setName("CamelServlet");
        return servlet;
    }
    
    
    @Component
    class RestConfBuilder extends RouteBuilder {
    	@Override
    	public void configure() throws Exception {
    		restConfiguration().contextPath(camelContextpath)
    			.bindingMode(RestBindingMode.auto).dataFormatProperty(camelPrettyPrint, "true").setEnableCORS(camelEnableCORS);
    	}
    }
    
    
    

	
}

