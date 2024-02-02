package com.org.nic.prabandh.bean;
public interface ProposedCosting {

	public Integer getActivityMasterId();
	public Integer getSubComponentId();
	public Integer getMajorComponentId();
	public Integer getSchemeId();
	public Integer getActivityMasterDetailsId();
	public Double getFinancialAmount();
	public Integer getPhysicalQuantity();
	public Double getUnitCost();
	public String getSchemeName();
	public String getMajorComponentName();
	public String getSubComponentName();
	public String getActivityMasterName();
	public String getActivityMasterDetailName();
	public Integer getRecuringNonrecuring();
	
	
	public Double getProposedPhysicalQuantity();
	public Double getProposedUnitCost();
	public Double getProposedFinancialAmount();
	public String getCoordinatorRemarks();
	

}