package com.tyss.batchprocess.config;

import java.sql.ResultSet;
import java.sql.SQLException;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;

import com.tyss.batchprocess.dto.EmployeeBean;
import com.tyss.batchprocess.processor.EmployeeItemProcessor;

import lombok.extern.java.Log;

/**
 * 
 * Configuration class where all the properties are configured
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

	@Value("${query.dob}")
	private String dobQuery;

	@Value("${query.doj}")
	private String dojQuery;

	/**
	 * 
	 * @return
	 */
	@Bean
	public JdbcCursorItemReader<EmployeeBean> dobReader() {
		JdbcCursorItemReader<EmployeeBean> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql(dobQuery);
		reader.setRowMapper(new EmployeeRowMapper());
		return reader;
	}
	
	@Bean
	public JdbcCursorItemReader<EmployeeBean> dojReader() {
		JdbcCursorItemReader<EmployeeBean> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql(dojQuery);
		reader.setRowMapper(new EmployeeRowMapper());
		return reader;
	}s


	@Scheduled(cron = "0 0-10 20 * * ?")
	public void perform() {
		log.info("Job Started at :" + new java.util.Date());
		JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();
		try {
			JobExecution execution = jobLauncher.run(exportDob(), param);
			log.info("Job finished with status :" + execution.getStatus());
		} catch (Exception e) {
			log.severe("Exception occurred while running the Batch !!! Exception is : " + e.getStackTrace());
		}
	}

	private class EmployeeRowMapper implements RowMapper<EmployeeBean> {

		/**
		 * {@code mapRow} execution depends on no. of rows present in the ResultSet In
		 * other words, if there are n no. of records this method will be executed for n
		 * no. of times
		 */

		@Override
		public EmployeeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			EmployeeBean employee = new EmployeeBean();
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
	public Step step1() {
		return stepBuilderFactory.get("step1").<EmployeeBean, EmployeeBean>chunk(10).reader(reader())
				.processor(processor()).writer(items -> {
					// do nothing
				}).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<EmployeeBean, EmployeeBean>chunk(10).reader(reader1())
				.processor(processor()).writer(items -> {
					// do nothing
				}).build();
	}

	@Bean
	public SimpleJobLauncher simpleJobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}

    //Job 1
	@Bean
	public Job exportDob() {
		return jobBuilderFactory.get("exportDob").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
	}

	// Job 2
	@Bean
	public Job exportDoj() {
		return jobBuilderFactory.get("exportDoj").incrementer(new RunIdIncrementer()).flow(step2()).end().build();
	}

}
