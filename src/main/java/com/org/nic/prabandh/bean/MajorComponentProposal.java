package com.org.nic.prabandh.bean;

public interface MajorComponentProposal {
	public String getMajorComponentName();
	public String getRecuringNonrecuring();
	public Double getFinancialAmount();
	public Double getProposedFinancialAmount();
	public Integer getMajorComponentIdWithoutScheme();
	
	public Double getApprovedBudgetRecurring();
	public Double getApprovedBudgetNonRecurring();
	public Double getTotApprovedBudget();
	public Double getExpenditureRecurring_31();
	public Double getExpenditureNonRecurring_31();
	public Double getTotExpenditure();
	public Double getSpillOverApprovalBudget23();
	public Double getAnticipatedExpenditureSpillOver();
	public Double getRecommendedFinancialAmount();
	public Double getTotalBudget();
	public Double getTotalExpenditure();

}