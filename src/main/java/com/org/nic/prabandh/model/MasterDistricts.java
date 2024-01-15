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
@Table(name = "master_districts")
public class MasterDistricts {
	
	@Id
	@Column(name = "district_id")
	private Integer districtId;
	
	
	@Column(name = "district_state_id")
	private Integer districtStateId;
	
	@Column(name = "district_name")
	private String districtName;	
	
	@Column(name = "udise_district_code")
	private String udiseDistrictCode;
	
	@Column(name = "udise_state_code")
	private String udiseStateCode;
	
	@Column(name = "inityear")
	private String initYear;
	
	
	@Column(name = "lgd_district_id")
	private Integer lgdDistrictId;
	
	@Column(name = "lgd_state_id")
	private Integer lgdStateId;
	
	
	@Column(name = "district_status")
	private Integer districtStatus;
	
	@Column(name = "year_id")
	private Integer yearId;
	
	@Column(name = "district_order")
	private Integer districtOrder;

}
