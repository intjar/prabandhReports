package com.org.nic.prabandh.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "master_states")
public class MstStateModel {
	
	@Id
	@Column(name = "state_id")
	private Integer stateId;
	
	@Column(name = "state_name")
	private String stateName;	
	
	@Column(name = "udise_state_code")
	private String udiseStateCode;
	
	
	@Column(name = "lgd_state_id")
	private Integer lgdStateId;
	
	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name = "state_status")
	private Integer stateStatus;
	
	@Column(name = "year_id")
	private Integer yearId;
	
	@Column(name = "is_ut")
	private Integer isUT;

}
