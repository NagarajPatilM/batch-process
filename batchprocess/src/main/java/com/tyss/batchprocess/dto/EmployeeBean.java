package com.tyss.batchprocess.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mas_employee")
public class EmployeeBean implements Serializable{
	
	@Column(name = "dob")
	private Date dateOfBirth;
	
	@Column(name = "doj")
	private Date dateOfJoin;
	
	@Column(name = "mail_id")
	private String mailId;

}
