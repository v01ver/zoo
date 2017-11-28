/*
 * 
 */
package fi.aapala.v01.zoo.mycamelboots.csv.dao;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * CSV DAO configurations.
 * 
 * @author aapala@gmail.com
 *
 */
@Configuration
@MapperScan(value = CsvDaoConfig.MAPPER_PACKAGE, sqlSessionFactoryRef = CsvDaoConfig.DATA_SOURCE_FACTORY)
public class CsvDaoConfig {

	public static final String MAPPER_PACKAGE = "fi.aapala.v01.zoo.mycamelboots.csv.dao";

	public static final String DATA_SOURCE_PREFIX = "v01.csv.datasource";

	public static final String DATA_CONF = "datasource.csv";

	public static final String DATA_SOURCE = "v01CSVDataSource";
	public static final String DATA_SOURCE_TX = DATA_SOURCE + "Tx";
	public static final String DATA_SOURCE_TEMPLATE = DATA_SOURCE + "Template";
	public static final String DATA_SOURCE_FACTORY = "v01CSVDataSourceFactory";

	@Autowired
	@Qualifier(DATA_SOURCE)
	private DataSource dataSource;

	@Value("${v01.csv.mybatis.config-location}")
	private String configLocation;

	@Primary
	@Bean(name = DATA_SOURCE)
	@ConfigurationProperties(prefix = DATA_SOURCE_PREFIX)
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = DATA_SOURCE_FACTORY)
	public SqlSessionFactory dataSourceFactory(@Qualifier(DATA_SOURCE) DataSource ds) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(ds);
		factoryBean.setConfigLocation(new ClassPathResource(configLocation));
		SqlSessionFactory r = null;
		r = factoryBean.getObject();
		return r;
	}

	@Primary
	@Bean(name = DATA_SOURCE_TX)
	public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier(DATA_SOURCE) DataSource ds) {
		DataSourceTransactionManager r = new DataSourceTransactionManager();
		r.setDataSource(ds);
		return r;

	}

	@Primary
	@Bean(name = DATA_SOURCE_TEMPLATE)
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier(DATA_SOURCE_FACTORY) SqlSessionFactory factory) {
		return new SqlSessionTemplate(factory);
	}

}
