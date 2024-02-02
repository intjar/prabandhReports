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
@Table(name = "master_states_tentative_proposed")
public class MastStatesTentative {

	@Id
	@Column(name = "state_id")
	private Integer stateId;

	@Column(name = "lgd_state_id")
	private Integer lgdStateId;

	@Column(name = "is_active")
	private Integer isActive;

	@Column(name = "is_ut")
	private Integer isUt;

	@Column(name = "year_id")
	private Integer yearId;
	
	@Column(name = "state_order")
	private Integer stateOrder;

	@Column(name = "state_status")
	private Double stateStatus;
	
	@Column(name = "id")
	private Double id;
	
	@Column(name = "tentative_central_share")
	private Double tentativeCentralShare;
	
	@Column(name = "tentative_state_share")
	private Double tentativeStateShare;
	
	@Column(name = "tentative_total_estimates")
	private Double tentativeTotalEstimates;
	
	@Column(name = "center_share_percent")
	private Double centerSharePercent;
}