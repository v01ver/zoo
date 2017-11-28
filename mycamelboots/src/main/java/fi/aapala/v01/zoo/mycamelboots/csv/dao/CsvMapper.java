/*
 * 
 */
package fi.aapala.v01.zoo.mycamelboots.csv.dao;

import java.util.List;
import java.util.Map;

/**
 * Payroll mybatis mapper class and it uses pay.xml SQL definition file.
 * 
 * @author aapalae
 *
 */
//@Mapper
public interface CsvMapper {

	List selecttop100(Map body);
		
	List searchByTitle(Map body);
	
}
