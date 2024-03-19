package com.org.nic.prabandh.bean;

public interface SpillOverReportDto {

	public Integer getState();
	public Integer getMajor_component_id();
	public Integer getSub_component_id();
	public Integer getActivity_master_id();
	public Integer getActivity_master_details_id();

	public String getMajor_component_name();
	public String getSub_component_name();
	public String getActivity_master_name();
	public String getActivity_master_details_name();

	public Double getTotal_physical_budget_approved();
	public Double getTotal_financial_budget_approved();

	public Double getPhysical_quantity_progress_complete_inception();
	public Double getPhysical_quantity_progress_progress_inception();
	public Double getFinancial_amount_progress_inception();

	public Double getPhysical_quantity_not_started();
	public Double getPhysical_quantity_spill_over();
	public Double getFinancial_amount_spill_over();
}