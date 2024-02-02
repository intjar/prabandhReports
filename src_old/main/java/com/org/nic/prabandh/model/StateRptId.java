package com.org.nic.prabandh.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class StateRptId implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer schemeId;
	private Integer majorComponentId;
	private Integer subComponentId;
	private Integer activityMasterId;
	private Integer activityMasterDetailsId;
}
