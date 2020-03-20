package com.tyss.batchprocess.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;

import com.tyss.batchprocess.dto.Employee;
import com.tyss.batchprocess.processor.EmployeeItemProcessor;

import lombok.extern.java.Log;

/**
 * 
 * @author CBT
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
@Log
@ComponentScan("com.tyss.batchprocess")
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private SimpleJobLauncher jobLauncher;

	@Autowired
	private DataSource dataSource;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${query}")
	private String query;

	/**
	 * 
	 * @return
	 */
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(dbUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean
	public JdbcCursorItemReader<Employee> reader() {
		JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();

		reader.setDataSource(dataSource);
		reader.setSql(query);
		reader.setRowMapper(new EmployeeRowMapper());
		return reader;
	}

	/*
	 * @Bean public ResourcelessTransactionManager resourcelessTransactionManager()
	 * { return new ResourcelessTransactionManager(); }
	 * 
	 * @Bean public MapJobRepositoryFactoryBean
	 * mapJobRepositoryFactory(ResourcelessTransactionManager txManager) throws
	 * Exception { MapJobRepositoryFactoryBean factory = new
	 * MapJobRepositoryFactoryBean(txManager); factory.afterPropertiesSet(); return
	 * factory; }
	 */

	@Scheduled(cron = "0 0-10 16 * * ?")
	public void perform() {

		log.info("Job Started at :" + new java.util.Date());
		JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();
		try {
			JobExecution execution = jobLauncher.run(exportEmployeeJob(), param);

			log.info("Job finished with status :" + execution.getStatus());
		} catch (Exception e) {
			log.severe("Exception occurred while running the Batch !!! Exception is : " + e.getStackTrace());
		}
	}

	public class EmployeeRowMapper implements RowMapper<Employee> {

		@Override
		public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
			Employee employee = new Employee();
			
				employee.setMailId(rs.getString("mail_id"));
				employee.setDateOfBirth(rs.getDate("DOB"));
				employee.setDateOfJoin(rs.getDate("DOJ"));
				log.info("DOB ========>" + rs.getString("DOB"));
				log.info("DOJ ========>" + rs.getString("DOJ"));
				log.info("mail id ======> " + rs.getString("mail_id"));
				log.info("------------------------------------------->");
			
			return employee;
		}
	}

	@Bean
	public EmployeeItemProcessor processor() {
		return new EmployeeItemProcessor();
	}

	@Bean
	public FlatFileItemWriter<Employee> writer() {
		FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<Employee>();

		writer.setResource(new ClassPathResource("Employee.txt"));

		DelimitedLineAggregator<Employee> lineAggregator = new DelimitedLineAggregator<Employee>();
		lineAggregator.setDelimiter(",");

		BeanWrapperFieldExtractor<Employee> fieldExtractor = new BeanWrapperFieldExtractor<Employee>();
		fieldExtractor.setNames(new String[] { "dateOfBirth", "dateOfJoin", "mailId" });
		lineAggregator.setFieldExtractor(fieldExtractor);

		writer.setLineAggregator(lineAggregator);

		/*
		 * writer.setLineAggregator(new DelimitedLineAggregator<Employee>() { {
		 * setDelimiter(","); setFieldExtractor(new
		 * BeanWrapperFieldExtractor<Employee>() { { setNames(new String[] { "mailId"
		 * }); } });
		 * 
		 * } });
		 */

		return writer;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Employee, Employee>chunk(10).reader(reader()).processor(processor())
				.writer(writer()).build();
	}

	@Bean
	public Job exportEmployeeJob() {
		return jobBuilderFactory.get("exportEmployeeJob").incrementer(new RunIdIncrementer()).flow(step1()).end()
				.build();
	}

	@Bean
	public SimpleJobLauncher simpleJobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}

}
