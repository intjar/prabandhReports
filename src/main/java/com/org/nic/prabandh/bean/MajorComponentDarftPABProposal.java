package com.org.nic.prabandh.bean;

public interface MajorComponentDarftPABProposal {
	
	public String getMajorComponentName();
	public String getRecuringNonrecuring();
	public Integer getFinancialAmount();
	public Integer getProposedFinancialAmount();
	public Integer getMajorComponentIdWithoutScheme();
	
	public Integer getApprovedBudgetRecurring();
	public Integer getApprovedBudgetNonRecurring();
	public Integer getTotApprovedBudget();
	public Integer getExpenditureRecurring_31();
	public Integer getExpenditureNonRecurring_31();
	public Integer getTotExpenditure();
	public Integer getSpillOverApprovalBudget23();
	public Integer getAnticipatedExpenditureSpillOver();
	public Integer getRecommendedFinancialAmount();
	public Integer getTotalBudget();
	public Integer getTotalExpenditure();


}
