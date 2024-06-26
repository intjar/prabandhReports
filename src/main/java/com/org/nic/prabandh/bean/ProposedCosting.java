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
	public String getRecuringNonrecuring();
	public Double getTotApprovedBudget();
	public Double getTotExpenditure();
	public Integer getProposedPhysicalQuantity();
	public Double getProposedUnitCost();
	public Double getProposedFinancialAmount();
	public String getCoordinatorRemarks();
	
	
	public Double getApprovedbudgetrecurring();
	public Double getExpenditurerecurring_31();
	public Double getApprovedbudgetnonrecurring();
	public Double getExpenditurenonrecurring_31();
	public Integer getSerial_order();
	public Double getSpillOverApprovalBudget23();
	public Double getAnticipatedExpenditureSpillOver();

}