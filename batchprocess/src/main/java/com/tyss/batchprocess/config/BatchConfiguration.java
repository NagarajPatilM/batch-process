package com.tyss.batchprocess.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.JobLauncher;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;

import com.tyss.batchprocess.dto.EmployeeBean;
import com.tyss.batchprocess.processor.ProcessorForDob;
import com.tyss.batchprocess.processor.ProcessorForDoj;

import lombok.extern.java.Log;

/**
 * The class {@code BatchConfiguration} is a Configuration class where all the
 * properties are configured
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
	 * Reader that reads the data only related to today's date from the database by
	 * executing dobQuery
	 * 
	 * @return ({@link JdbcCursorItemReader}
	 */
	@Bean
	public JdbcCursorItemReader<EmployeeBean> dobReader() {
		JdbcCursorItemReader<EmployeeBean> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql(dobQuery);
		reader.setRowMapper(new EmployeeRowMapper());
		return reader;
	}

	/**
	 * Reader that reads the data only related to today's date from the database by
	 * executing dojQuery
	 * 
	 * @return ({@link JdbcCursorItemReader}
	 */
	@Bean
	public JdbcCursorItemReader<EmployeeBean> dojReader() {
		JdbcCursorItemReader<EmployeeBean> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql(dojQuery);
		reader.setRowMapper(new EmployeeRowMapper());
		return reader;
	}

	/**
	 * This method executes at a specified time mentioned in the cron expression
	 */
	@Scheduled(cron = "0 20-30 5 * * ?")
	public void perform() {
		log.info("Job Started at :" + new java.util.Date());
		try {
			JobParameters param = new JobParametersBuilder().addDate("date", new Date())
					.addLong("time", System.currentTimeMillis())
					/* .addString("JobID", String.valueOf(System.currentTimeMillis())) */.toJobParameters();
			JobExecution execution = jobLauncher.run(exportDobDoj11(), param);
			log.info("Job finished with status :" + execution.getStatus());
		} catch (Exception e) {
			log.severe("Exception occurred while running the perform() !!! Exception is : " + e.getStackTrace());
		}
	}

	private class EmployeeRowMapper implements RowMapper<EmployeeBean> {

		/**
		 * {@code mapRow} execution depends on no. of rows present in the ResultSet In
		 * other words, if there are n no. of records this method will be executed for n
		 * no. of times.
		 * 
		 * @param rs ResultSet that contains the records
		 * 
		 * @return {@code EmployeeBean}
		 */
		@Override
		public EmployeeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			System.out.println("Inside mapRow");
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

	// creates an object of {@code ProcessorForDob}
	@Bean
	public ProcessorForDob processorForDob() {
		return new ProcessorForDob();
	}

	// creates an object of {@code ProcessorForDoj}
	@Bean
	public ProcessorForDoj processorForDoj() {
		return new ProcessorForDoj();
	}

	// step related to execution of data related to dob
	@Bean
	public Step stepForDob() {
		return stepBuilderFactory.get("stepForDob").<EmployeeBean, EmployeeBean>chunk(10).reader(dobReader())
				.processor(processorForDob()).writer(items -> {
					// do nothing
				}).build();
	}

	// step related to execution of data related to doj
	@Bean
	public Step stepForDoj() {
		return stepBuilderFactory.get("stepForDoj").<EmployeeBean, EmployeeBean>chunk(10).reader(dojReader())
				.processor(processorForDoj()).writer(items -> {
					// do nothing
				}).build();
	}

	// method that is responsible for parallel processing of stepForDob and
	// stepForDoj
	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("spring_batch");
	}

	@Bean
	public Flow splitFlow() {
		return new FlowBuilder<SimpleFlow>("splitFlow").split(taskExecutor()).add(flow1(), flow2()).build();
	}

	@Bean
	public Flow flow1() {
		return new FlowBuilder<SimpleFlow>("flow1").start(stepForDob()) // .next(stepForDoj())
				.build();
	}

	@Bean
	public Flow flow2() {
		return new FlowBuilder<SimpleFlow>("flow2").start(stepForDoj()).build();
	}

	@Bean
	public Job exportDobDoj11() {
		return jobBuilderFactory.get("exportDobDoj11").start(splitFlow()).build().build();
	}

	/**
	 * Methos that creates {@link SimpleJobLauncher} instance by setting
	 * {@link JobRepository}
	 * 
	 * @param jobRepository
	 * @return {@link SimpleJobLauncher} the implementation class of
	 *         {@link JobLauncher} interface
	 */
	@Bean
	public SimpleJobLauncher simpleJobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}

}