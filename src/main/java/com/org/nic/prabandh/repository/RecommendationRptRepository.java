package com.org.nic.prabandh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.nic.prabandh.bean.MajorComponentProposal;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.bean.RecurringNonRecurring;
import com.org.nic.prabandh.bean.Spillover;
import com.org.nic.prabandh.model.MstStateModel;

@Repository
public interface RecommendationRptRepository extends CrudRepository<MstStateModel, Integer>{

	@Query(nativeQuery = true , value = "select dd.*,bb.coordinatorRemarks from( (select coalesce(round((approved_budget_non_recurring+approved_budget_recurring),5),0) as TotApprovedBudget,coalesce(round((expenditure_recurring_31+expenditure_non_recurring_31),5),0) as TotExpenditure,\r\n"
			+ "	  round(approved_budget_recurring,5) as approvedBudgetRecurring,round(expenditure_recurring_31,5)as expenditureRecurring_31,\r\n"
			+ "round(approved_budget_non_recurring,5) as approvedBudgetNonRecurring,round(expenditure_non_recurring_31,5) as expenditureNonRecurring_31,\r\n"
			+ "      round(SpillOverApproval23,5) as SpillOverApprovalBudget23,round(AnticipatedExpenditureSpillOver,5) as AnticipatedExpenditureSpillOver,round(bb.financial_amount,5) as financialAmount,round(bb.unit_cost,5)as unitCost,bb.physical_quantity as physicalQuantity,\r\n"
			+ "	  round(bb.proposed_financial_amount,5) as proposedFinancialAmount,round(bb.proposed_unit_cost,5)as proposedUnitCost,bb.proposed_physical_quantity\\:\\:integer as proposedPhysicalQuantity,\r\n"
			+ "	  case when (bb.activity_master_id is null ) then 888888 else bb.activity_master_id end as activityMasterId,\r\n"
			+ "      case when (bb.sub_component_id is null ) then 777777 else bb.sub_component_id end as subComponentId,\r\n"
			+ "      case when (bb.major_component_id is null ) then 666666 else bb.major_component_id end as majorComponentId,\r\n"
			+ "      case when (bb.scheme_id is null ) then '555555' else bb.scheme_id end as schemeId,\r\n"
			+ "      case when (bb.activity_master_details_id is null ) then 999999 else bb.activity_master_details_id end as activityMasterDetailsId, \r\n"
			+ "	  case when (pd.recuring_nonrecuring = 1 ) then 'R' when (pd.recuring_nonrecuring = 2) then 'NR' else 'NA' end as recuringNonrecuring, \r\n"
			+ "      ps.title as schemeName, pmc.title as  majorComponentName , psc.title as  subComponentName , pam.title as activityMasterName , pd.activity_master_details_name as activityMasterDetailName,pd.serial_order \r\n"
			+ "	  from (\r\n"
			+ "		  \r\n"
			+ "		  select cc.scheme_id,cc.major_component_id,cc.sub_component_id,cc.activity_master_id,cc.activity_master_details_id,\r\n"
			+ "		  sum(approved_budget_recurring) as approved_budget_recurring,sum(expenditure_recurring_31) as expenditure_recurring_31,sum(approved_budget_non_recurring) as approved_budget_non_recurring,\r\n"
			+ "		  sum(expenditure_non_recurring_31) as expenditure_non_recurring_31,sum(SpillOverApproval23) as SpillOverApproval23,sum(AnticipatedExpenditureSpillOver) as AnticipatedExpenditureSpillOver,sum(financial_amount) as financial_amount, sum(physical_quantity) as physical_quantity,\r\n"
			+ "		  sum(financial_amount)/ nullif(sum(physical_quantity),0) as unit_cost,sum(proposed_financial_amount) as proposed_financial_amount, sum(proposed_physical_quantity) as proposed_physical_quantity,sum(proposed_financial_amount)/ nullif(sum(proposed_physical_quantity),0) as proposed_unit_cost from (\r\n"
			+ "select aa.scheme_id,aa.major_component_id,aa.sub_component_id,aa.activity_master_id,aa.activity_master_details_id,\r\n"
			+ "approved_budget_recurring,expenditure_recurring_31,approved_budget_non_recurring,expenditure_non_recurring_31,AnticipatedExpenditureSpillOver,SpillOverApproval23,0 as financial_amount, 0 as physical_quantity,0 as unit_cost,0 as proposed_financial_amount, 0 as proposed_physical_quantity,0 as proposed_unit_cost from \r\n"
			+ " (select scheme_id,major_component_id,sub_component_id,activity_master_id,activity_master_details_id,budget_amount as approved_budget_recurring,progress_amount as expenditure_recurring_31,\r\n"
			+ " 0 as approved_budget_non_recurring,0 as expenditure_non_recurring_31,0 as AnticipatedExpenditureSpillOver,\r\n"
			+ "	   0 as SpillOverApproval23 from prb_ann_wrk_pln_bdgt_prev_progress \r\n"
			+ "where state=:stateId \r\n"
			+ "union all\r\n"
			+ "select scheme_id,major_component_id,sub_component_id,activity_master_id,activity_master_details_id,0 as approved_budget_recurring,0 as expenditure_recurring_31,fresh_approval_financial_amount\r\n"
			+ "as approved_budget_non_recurring,exp_against_fresh_app_fin as expenditure_non_recurring_31,financial_amount_progress_inception as AnticipatedExpenditureSpillOver,\r\n"
			+ "	   financial_amount_cummu_inception as SpillOverApproval23 from prb_ann_wrk_pln_bdgt_spill_over\r\n"
			+ "where state=:stateId  ) aa \r\n"
			+ "union all\r\n"
			+ "SELECT pawpbd.scheme_id,\r\n"
			+ "          pawpbd.major_component_id,\r\n"
			+ "          pawpbd.sub_component_id,\r\n"
			+ "          pawpbd.activity_master_id,\r\n"
			+ "          pawpbd.activity_master_details_id,\r\n"
			+ "		  0 as approved_budget_recurring,0 as expenditure_recurring_31,\r\n"
			+ "		  0 as approved_budget_non_recurring,0 as expenditure_non_recurring_31,\r\n"
			+ "		  0 as AnticipatedExpenditureSpillOver,\r\n"
			+ "		  0 as SpillOverApproval23,\r\n"
			+ "		  pawpbd.financial_amount AS financial_amount,\r\n"
			+ "          pawpbd.physical_quantity AS physical_quantity,\r\n"
			+ "          pawpbd.financial_amount/ nullif(pawpbd.physical_quantity,0) as unit_cost, \r\n"
			+ "		  pawpbd.proposed_financial_amount AS proposed_financial_amount,\r\n"
			+ "          pawpbd.proposed_physical_quantity AS proposed_physical_quantity,\r\n"
			+ "          pawpbd.proposed_financial_amount/ nullif(pawpbd.proposed_physical_quantity,0) as proposed_unit_cost\r\n"
			+ "		  \r\n"
			+ "          FROM prb_state_ann_wrk_pln_bdgt_data pawpbd\r\n"
			+ "        WHERE pawpbd.state = :stateId  and pawpbd.plan_year=:planYear \r\n"
			+ "        \r\n"
			+ "		   ) cc GROUP BY GROUPING SETS ((cc.scheme_id, cc.major_component_id, cc.sub_component_id, cc.activity_master_id, cc.activity_master_details_id), (cc.scheme_id, cc.major_component_id, cc.sub_component_id, cc.activity_master_id), (cc.scheme_id, cc.major_component_id, cc.sub_component_id), (cc.scheme_id, cc.major_component_id), (cc.scheme_id), ())\r\n"
			+ "	   ) bb \r\n"
			+ "	  left join prb_data pd on (pd.id= bb.activity_master_details_id)\r\n"
			+ "       left join prb_activity_master pam on (pam.id= bb.activity_master_id)\r\n"
			+ "       left join prb_sub_component psc on (psc.sub_component_id= bb.sub_component_id)\r\n"
			+ "       left join prb_major_component pmc   on (pmc.prb_major_component_id = bb.major_component_id)\r\n"
			+ "       left join prb_schemes ps on (ps.id= bb.scheme_id\\:\\:numeric)) dd\r\n"
			+ "	    left join \r\n"
			+ "		(Select activity_master_details_id,coordinator_remarks as coordinatorRemarks from prb_state_ann_wrk_pln_bdgt_data ab where ab.plan_year=:planYear  and ab.state=:stateId ) bb\r\n"
			+ "		on (bb.activity_master_details_id= dd.activityMasterDetailsId)\r\n"
			+ "	   )\r\n"
			+ "       order by dd.schemeId , dd.majorComponentId , dd.subComponentId , dd.activityMasterId ,dd.activityMasterDetailsId,dd.serial_order")
	public List<ProposedCosting> findAllByStateAndPlanYear(@Param("stateId") Integer stateId, @Param("planYear") String planYear);
	
	
		@Query(nativeQuery=true, value ="select majorComponentName,round(coalesce(approvedBudgetRecurring,0),5) as approvedBudgetRecurring,round(coalesce(approvedBudgetNonRecurring,0),5) as approvedBudgetNonRecurring,\r\n"
				+ "round((coalesce(approvedBudgetNonRecurring,0)+coalesce(approvedBudgetRecurring,0)),5) as TotApprovedBudget,round(coalesce(expenditureRecurring_31,0),5) as expenditureRecurring_31,\r\n"
				+ "round(coalesce(expenditureNonRecurring_31,0),5) as expenditureNonRecurring_31,round((coalesce(expenditureRecurring_31,0)+coalesce(expenditureNonRecurring_31,0)),5) as TotExpenditure,\r\n"
				+ "round(coalesce(SpillOverApprovalBudget23,0),5) as SpillOverApprovalBudget23,\r\n"
				+ "round(coalesce(AnticipatedExpenditureSpillOver,0),5) as AnticipatedExpenditureSpillOver,\r\n"
				+ "round((coalesce(approvedBudgetNonRecurring,0)+coalesce(approvedBudgetRecurring,0)+coalesce(SpillOverApprovalBudget23,0)),5) as TotalBudget,\r\n"
				+ "round((coalesce(expenditureRecurring_31,0)+coalesce(expenditureNonRecurring_31,0)+coalesce(AnticipatedExpenditureSpillOver,0)),5) as TotalExpenditure,\r\n"
				+ "round(financialAmount,5) as financialAmount,round(RecommendedFinancialAmount,5) as RecommendedFinancialAmount \r\n"
				+ "from(\r\n"
				+ "	SELECT case when (pmc.title is null) then 'Total' else pmc.title end as  majorComponentName,sum(approved_budget_recurring) as approvedBudgetRecurring,\r\n"
				+ "	sum(expenditure_recurring_31) as expenditureRecurring_31,sum(approved_budget_non_recurring) as approvedBudgetNonRecurring,\r\n"
				+ "	sum(expenditure_non_recurring_31) as expenditureNonRecurring_31,sum(SpillOverApproval23) as SpillOverApprovalBudget23,\r\n"
				+ "	sum(AnticipatedExpenditureSpillOver) as AnticipatedExpenditureSpillOver,\r\n"
				+ "	sum(cc.financial_amount) AS financialAmount,sum(cc.proposed_financial_amount) AS RecommendedFinancialAmount from\r\n"
				+ "	(select major_component_id,approved_budget_recurring,expenditure_recurring_31,\r\n"
				+ "	approved_budget_non_recurring,expenditure_non_recurring_31,AnticipatedExpenditureSpillOver,\r\n"
				+ "	SpillOverApproval23,0 as financial_amount,0 as proposed_financial_amount \r\n"
				+ "	from (\r\n"
				+ "		select major_component_id,budget_amount as approved_budget_recurring,progress_amount as expenditure_recurring_31,\r\n"
				+ "		0 as approved_budget_non_recurring,0 as expenditure_non_recurring_31,0 as AnticipatedExpenditureSpillOver,\r\n"
				+ "		0 as SpillOverApproval23 from prb_ann_wrk_pln_bdgt_prev_progress \r\n"
				+ "		where state=:stateId \r\n"
				+ "		union all\r\n"
				+ "		select major_component_id,0 as approved_budget_recurring,0 as expenditure_recurring_31,fresh_approval_financial_amount\r\n"
				+ "		as approved_budget_non_recurring,exp_against_fresh_app_fin as expenditure_non_recurring_31,financial_amount_progress_inception as AnticipatedExpenditureSpillOver,\r\n"
				+ "		financial_amount_cummu_inception as SpillOverApproval23 from prb_ann_wrk_pln_bdgt_spill_over\r\n"
				+ "		where state=:stateId  \r\n"
				+ "	) aa \r\n"
				+ "	union all\r\n"
				+ "	SELECT \r\n"
				+ "	pawpbd.major_component_id,\r\n"
				+ "	0 as approved_budget_recurring,0 as expenditure_recurring_31,\r\n"
				+ "	0 as approved_budget_non_recurring,0 as expenditure_non_recurring_31,\r\n"
				+ "	0 as AnticipatedExpenditureSpillOver,\r\n"
				+ "	0 as SpillOverApproval23,\r\n"
				+ "	pawpbd.financial_amount AS financial_amount,\r\n"
				+ "	pawpbd.proposed_financial_amount AS proposed_financial_amount\r\n"
				+ "	FROM prb_state_ann_wrk_pln_bdgt_data pawpbd\r\n"
				+ "	WHERE pawpbd.state = :stateId  and plan_year =:planYear ) cc\r\n"
				+ "	left join prb_major_component pmc   on (pmc.prb_major_component_id = cc.major_component_id)\r\n"
				+ "	group by grouping sets ((pmc.title),())\r\n"
				+ ") bb\r\n"
				+ "order by majorComponentName")
		public List<MajorComponentProposal> findMajorComponentProposalForStateRecommendation(@Param("stateId") Integer stateId, @Param("planYear") String planYear);

		
		@Query(nativeQuery=true, value ="select\r\n"
				+ "sum(financial_amount) filter (where pd.recuring_nonrecuring= 1) as recuring,\r\n"
				+ "sum(financial_amount) filter (where pd.recuring_nonrecuring= 2 ) as noRecuring,\r\n"
				+ "sum(financial_amount)  as  total,state as stateId, pd.scheme_id as schemeId\r\n"
				+ "from prb_state_ann_wrk_pln_bdgt_data pawpbd ,  prb_data pd \r\n"
				+ "where pawpbd.activity_master_details_id = pd.id \r\n"
				+ "and state  =:stateId and plan_year =:planYear "
				+ "group by state,pd.scheme_id ")
		public List<RecurringNonRecurring> findStatePlanList(@Param("stateId") Integer stateId, @Param("planYear") String planYear);
		
		
		
		@Query(nativeQuery=true, value ="select \r\n"
				+ "coalesce(recuring_23_24,0) as recuring,\r\n"
				+ "coalesce(norecuring_23_24,0) as noRecuring,\r\n"
				+ "(coalesce(recuring_23_24,0) + coalesce(norecuring_23_24,0) ) as total,\r\n"
				+ "aa.state as stateId, aa.scheme_id as schemeId\r\n"
				+ "from  (\r\n"
				+ "	select sum(fresh_approval_financial_amount) filter (where pd.recuring_nonrecuring= 2 ) as norecuring_23_24,state, pd.scheme_id \r\n"
				+ "	from prb_ann_wrk_pln_bdgt_spill_over pawpbso , prb_data pd \r\n"
				+ "	where pawpbso.activity_master_details_id = pd.id \r\n"
				+ "	and state =:stateId  group by  state, pd.scheme_id \r\n"
				+ ") aa , (\r\n"
				+ "	select  sum(budget_amount) filter (where pd.recuring_nonrecuring= 1 ) as recuring_23_24 ,pd.scheme_id, state\r\n"
				+ "	from prb_ann_wrk_pln_bdgt_prev_progress pawpbpp , prb_data pd \r\n"
				+ "	where pawpbpp.activity_master_details_id  = pd.id \r\n"
				+ "	and state =:stateId  group by state , pd.scheme_id \r\n"
				+ ") bb \r\n"
				+ "where aa.state =  bb.state and aa.scheme_id= bb.scheme_id ")
		public List<RecurringNonRecurring> findBudgetRecurringNonRecurring2324(@Param("stateId") Integer stateId);
		
		
		

		@Query(nativeQuery=true, value ="select \r\n"
				+ "coalesce(recuring_23_24,0) as recuring,\r\n"
				+ "coalesce(norecuring_23_24,0) as norecuring,\r\n"
				+ "(coalesce(recuring_23_24,0) + coalesce(norecuring_23_24,0) ) as total,\r\n"
				+ "aa.state as stateId, aa.scheme_id as schemeId	  \r\n"
				+ "from (\r\n"
				+ "	select sum(exp_against_fresh_app_fin) filter (where pd.recuring_nonrecuring= 2 ) as norecuring_23_24,state,pd.scheme_id \r\n"
				+ "	from prb_ann_wrk_pln_bdgt_spill_over pawpbso , prb_data pd \r\n"
				+ "	where pawpbso.activity_master_details_id = pd.id  and state =:stateId  "
				+ "	group by  state, pd.scheme_id \r\n"
				+ ") aa , (\r\n"
				+ "	select  sum(progress_amount) filter (where pd.recuring_nonrecuring= 1 ) as recuring_23_24 , pd.scheme_id,state\r\n"
				+ "	from prb_ann_wrk_pln_bdgt_prev_progress pawpbpp , prb_data pd \r\n"
				+ "	where pawpbpp.activity_master_details_id  = pd.id and state =:stateId "
				+ "	group by state , pd.scheme_id \r\n"
				+ ") bb \r\n"
				+ "where aa.state =  bb.state and aa.scheme_id= bb.scheme_id")
		public List<RecurringNonRecurring> findExpexpenditureRecurNonRecur2324(@Param("stateId") Integer stateId);
		
		
		
		@Query(nativeQuery=true, value ="SELECT pd.scheme_name as schemeName,\r\n"
				+ "sum(round(pawpbd.proposed_financial_amount,5)) filter( where pd.recuring_nonrecuring = 1) AS recuring,\r\n"
				+ "sum(round(pawpbd.proposed_financial_amount,5)) filter( where pd.recuring_nonrecuring = 2) AS noRecuring,\r\n"
				+ "sum(round(pawpbd.proposed_financial_amount,5)) AS total\r\n"
				+ "FROM prb_state_ann_wrk_pln_bdgt_data pawpbd ,prb_data pd \r\n"
				+ "where pawpbd.activity_master_details_id = pd.id \r\n"
				+ "and state  =:stateId and plan_year =:planYear \r\n"
				+ "group by pd.scheme_name  \r\n"
				+ "order by pd.scheme_name")
		public List<RecurringNonRecurring> getRecommendationList(@Param("stateId") Integer stateId, @Param("planYear") String planYear);
		
		
		
		@Query(nativeQuery=true, value ="select ps.title as SchemeName,aa.SpillOverApproval23,aa.AnticipatedExpenditureSpillOver\r\n"
				+ "from (\r\n"
				+ "	select scheme_id,round(coalesce(sum(financial_amount_cummu_inception),0),5) as SpillOverApproval23,round(coalesce(sum(financial_amount_progress_inception),0),5) as AnticipatedExpenditureSpillOver\r\n"
				+ "	from prb_ann_wrk_pln_bdgt_spill_over where state=:stateId \r\n"
				+ "	group by scheme_id\r\n"
				+ ") aa \r\n"
				+ "left join prb_schemes ps on ps.id=aa.scheme_id\\:\\:int\r\n"
				+ "order by ps.id")
		public List<Spillover> getSpilloverListList(@Param("stateId") Integer stateId);


		@Query(nativeQuery=true, value ="SELECT case when (pmc.title is null) then 'Total' else pmc.title end as  majorComponentName,\r\n"
				+ "case when (recuringNonrecuring is null) then 'Total' else recuringNonrecuring end as recuringNonrecuring,\r\n"
				+ "round(coalesce(sum(cc.financial_amount),0),5) AS financialAmount,round(coalesce(sum(cc.proposed_financial_amount),0),5) AS RecommendedFinancialAmount \r\n"
				+ "from(\r\n"
				+ "	SELECT pawpbd.major_component_id,\r\n"
				+ "	pawpbd.activity_master_details_id,\r\n"
				+ "	case when (pd.recuring_nonrecuring = 1 ) then 'R' when (pd.recuring_nonrecuring = 2) then 'NR' end as recuringNonrecuring,\r\n"
				+ "	pawpbd.financial_amount AS financial_amount,\r\n"
				+ "	pawpbd.proposed_financial_amount AS proposed_financial_amount\r\n"
				+ "	FROM prb_state_ann_wrk_pln_bdgt_data pawpbd\r\n"
				+ "	left join prb_data pd on (pd.id=pawpbd.activity_master_details_id)\r\n"
				+ "	WHERE pawpbd.state = :stateId  and plan_year =:planYear \r\n"
				+ ") cc\r\n"
				+ "left join prb_major_component pmc   on (pmc.prb_major_component_id = cc.major_component_id)\r\n"
				+ "group by grouping sets ((pmc.title,cc.recuringNonrecuring),(pmc.title),())\r\n"
				+ "order by majorComponentName, recuringNonrecuring	")
		public List<MajorComponentProposal> findMajorComponentStatePlan(@Param("stateId") Integer stateId, @Param("planYear") String planYear);

}