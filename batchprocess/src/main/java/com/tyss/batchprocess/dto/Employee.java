package com.tyss.batchprocess.dto;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mas_employee")
public class Employee implements Serializable{
	
	private Date DOB;
	private Date DOJ;
	@Column(name = "mail_id")
	private String mailId;

}
