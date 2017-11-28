package fi.aapala.v01.zoo.mycamelboots;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot main Application.
 * @author aapala@gmail.com
 *
 */
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends SpringBootServletInitializer {
	
	private static final Logger LOG = LogManager.getLogger(Application.class.getName());
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		LOG.info("SpringApplicationBuilder");
        return application.sources(Application.class);
    }
	
	
    /**
     * Application main method.
     * @param args
     * @throws Exception
     */
	public static void main(String[] args)  {
		LOG.info("main:");
		SpringApplication.run(Application.class, args);
	}	
	
}