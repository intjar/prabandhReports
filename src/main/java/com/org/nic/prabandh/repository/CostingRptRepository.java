package com.org.nic.prabandh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.nic.prabandh.bean.MajorComponentProposal;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.bean.RecurringNonRecurring;
import com.org.nic.prabandh.model.MstStateModel;

@Repository
public interface CostingRptRepository extends CrudRepository<MstStateModel, Integer>{

	
//	@Query(nativeQuery=true, value ="select \r\n"
//			+ "      financial_amount as financialAmount,unit_cost as unitCost,physical_quantity as physicalQuantity,\r\n"
//			+ "      case when (aa.activity_master_id is null ) then 888888 else aa.activity_master_id end  as activityMasterId,\r\n"
//			+ "      case when (aa.sub_component_id is null ) then 777777 else aa.sub_component_id end as subComponentId,\r\n"
//			+ "      case when (aa.major_component_id is null ) then 666666 else aa.major_component_id end as majorComponentId,\r\n"
//			+ "      case when (aa.scheme_id is null ) then '555555' else aa.scheme_id end as schemeId,\r\n"
//			+ "      case when (aa.activity_master_details_id is null ) then 999999 else aa.activity_master_details_id end as activityMasterDetailsId,\r\n"
//			+ "      ps.title  as schemeName,pmc.title as  majorComponentName,psc.title as  subComponentName,\r\n"
//			+ "      pam.title as activityMasterName,pd.activity_master_details_name as activityMasterDetailName,pd.recuring_nonrecuring as recuringNonrecuring\r\n"
//			+ "      from (\r\n"
//			+ "      SELECT sum(pawpbd.financial_amount) AS financial_amount,\r\n"
//			+ "          sum(pawpbd.physical_quantity) AS physical_quantity,\r\n"
//			+ "          sum(pawpbd.financial_amount)/ nullif(sum(pawpbd.physical_quantity),0) as unit_cost ,\r\n"
//			+ "          pawpbd.scheme_id,pawpbd.major_component_id,pawpbd.sub_component_id,\r\n"
//			+ "          pawpbd.activity_master_id,pawpbd.activity_master_details_id\r\n"
//			+ "        FROM prb_ann_wrk_pln_bdgt_data pawpbd\r\n"
//			+ "        WHERE pawpbd.state = :stateId  and pawpbd.plan_year=:planYear "
//			+ "        GROUP BY GROUPING SETS ((pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id, pawpbd.activity_master_id, pawpbd.activity_master_details_id), (pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id, pawpbd.activity_master_id), (pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id), (pawpbd.scheme_id, pawpbd.major_component_id), (pawpbd.scheme_id), ())\r\n"
//			+ "      ) \r\n"
//			+ "      aa \r\n"
//			+ "       left join prb_data pd on (pd.id= aa.activity_master_details_id)\r\n"
//			+ "       left join prb_activity_master pam on (pam.id= aa.activity_master_id)\r\n"
//			+ "       left join prb_sub_component psc on (psc.sub_component_id= aa.sub_component_id)\r\n"
//			+ "       left join prb_major_component pmc   on (pmc.prb_major_component_id = aa.major_component_id)\r\n"
//			+ "       left join prb_schemes ps on (ps.id= aa.scheme_id\\:\\:numeric)\r\n"
//			+ "       order by aa.scheme_id , aa.major_component_id , aa.sub_component_id , aa.activity_master_id ,aa.activity_master_details_id,pd.serial_order")
	@Query(nativeQuery = true , value = "select coalesce(round((approved_budget_non_recurring+approved_budget_recurring),5),0) as TotApprovedBudget,\r\n"
			+ "coalesce(round((expenditure_recurring_31+expenditure_non_recurring_31),5),0) as TotExpenditure,\r\n"
			+ "round(approved_budget_recurring,5) as approved_budget_recurring,\r\n"
			+ "round(expenditure_recurring_31,5)as expenditure_recurring_31,\r\n"
			+ "round(approved_budget_non_recurring,5) as approved_budget_non_recurring,\r\n"
			+ "round(expenditure_non_recurring_31,5) as expenditure_non_recurring_31,\r\n"
			+ "round(financial_amount,5) as financialAmount,\r\n"
			+ "round(unit_cost,5)as unitCost,physical_quantity as physicalQuantity,\r\n"
			+ "	  case when (bb.activity_master_id is null ) then 888888 else bb.activity_master_id end activityMasterId,\r\n"
			+ "      case when (bb.sub_component_id is null ) then 777777 else bb.sub_component_id end subComponentId,\r\n"
			+ "      case when (bb.major_component_id is null ) then 666666 else bb.major_component_id end majorComponentId,\r\n"
			+ "      case when (bb.scheme_id is null ) then '555555' else bb.scheme_id end schemeId,\r\n"
			+ "      case when (bb.activity_master_details_id is null ) then 999999 else bb.activity_master_details_id end as activityMasterDetailsId, \r\n"
			+ "      ps.title as schemeName, pmc.title as  majorComponentName , psc.title as  subComponentName , pam.title as activityMasterName , pd.activity_master_details_name as activityMasterDetailName ,pd.recuring_nonrecuring as recuringNonrecuring\r\n"
			+ "	  from (\r\n"
			+ "		  \r\n"
			+ "		  select cc.scheme_id,cc.major_component_id,cc.sub_component_id,cc.activity_master_id,cc.activity_master_details_id,\r\n"
			+ "		  sum(approved_budget_recurring) as approved_budget_recurring,sum(expenditure_recurring_31) as expenditure_recurring_31,sum(approved_budget_non_recurring) as approved_budget_non_recurring,sum(expenditure_non_recurring_31) as expenditure_non_recurring_31,sum(financial_amount) as financial_amount, sum(physical_quantity) as physical_quantity,sum(financial_amount)/ nullif(sum(physical_quantity),0) as unit_cost from (\r\n"
			+ "select aa.scheme_id,aa.major_component_id,aa.sub_component_id,aa.activity_master_id,aa.activity_master_details_id,\r\n"
			+ "approved_budget_recurring,expenditure_recurring_31,approved_budget_non_recurring,expenditure_non_recurring_31,0 as financial_amount, 0 as physical_quantity,0 as unit_cost from \r\n"
			+ " (select scheme_id,major_component_id,sub_component_id,activity_master_id,activity_master_details_id,budget_amount as approved_budget_recurring,progress_amount as expenditure_recurring_31,\r\n"
			+ " 0 as approved_budget_non_recurring,0 as expenditure_non_recurring_31 from prb_ann_wrk_pln_bdgt_prev_progress \r\n"
			+ "where state=104\r\n"
			+ "union\r\n"
			+ "select scheme_id,major_component_id,sub_component_id,activity_master_id,activity_master_details_id,0 as approved_budget_recurring,0 as expenditure_recurring_31,fresh_approval_financial_amount\r\n"
			+ "as approved_budget_non_recurring,exp_against_fresh_app_fin as expenditure_non_recurring_31 from prb_ann_wrk_pln_bdgt_spill_over\r\n"
			+ "where state=104 ) aa \r\n"
			+ "union\r\n"
			+ "SELECT pawpbd.scheme_id,\r\n"
			+ "          pawpbd.major_component_id,\r\n"
			+ "          pawpbd.sub_component_id,\r\n"
			+ "          pawpbd.activity_master_id,\r\n"
			+ "          pawpbd.activity_master_details_id,\r\n"
			+ "		  0 as approved_budget_recurring,0 as expenditure_recurring_31,\r\n"
			+ "		  0 as approved_budget_non_recurring,0 as expenditure_non_recurring_31,\r\n"
			+ "		  pawpbd.financial_amount AS financial_amount,\r\n"
			+ "          pawpbd.physical_quantity AS physical_quantity,\r\n"
			+ "          pawpbd.financial_amount/ nullif(pawpbd.physical_quantity,0) as unit_cost \r\n"
			+ "          FROM prb_ann_wrk_pln_bdgt_data pawpbd\r\n"
			+ "        WHERE pawpbd.state =:stateId and plan_year=:planYear\r\n"
			+ "		   ) cc GROUP BY GROUPING SETS ((cc.scheme_id, cc.major_component_id, cc.sub_component_id, cc.activity_master_id, cc.activity_master_details_id), (cc.scheme_id, cc.major_component_id, cc.sub_component_id, cc.activity_master_id), (cc.scheme_id, cc.major_component_id, cc.sub_component_id), (cc.scheme_id, cc.major_component_id), (cc.scheme_id), ())\r\n"
			+ "	   ) bb \r\n"
			+ "	  left join prb_data pd on (pd.id= bb.activity_master_details_id)\r\n"
			+ "       left join prb_activity_master pam on (pam.id= bb.activity_master_id)\r\n"
			+ "       left join prb_sub_component psc on (psc.sub_component_id= bb.sub_component_id)\r\n"
			+ "       left join prb_major_component pmc   on (pmc.prb_major_component_id = bb.major_component_id)\r\n"
			+ "       left join prb_schemes ps on (ps.id= bb.scheme_id\\:\\:numeric)\r\n"
			+ "       order by bb.scheme_id , bb.major_component_id , bb.sub_component_id , bb.activity_master_id ,bb.activity_master_details_id,pd.serial_order")
	public List<ProposedCosting> findAllByStateAndPlanYear(@Param("stateId") Integer stateId, @Param("planYear") String planYear);

	
	
	@Query(nativeQuery=true, value ="select \r\n"
			+ "      financial_amount as financialAmount,unit_cost as unitCost,physical_quantity as physicalQuantity,\r\n"
			+ "      case when (aa.activity_master_id is null ) then 888888 else aa.activity_master_id end  as activityMasterId,\r\n"
			+ "      case when (aa.sub_component_id is null ) then 777777 else aa.sub_component_id end as subComponentId,\r\n"
			+ "      case when (aa.major_component_id is null ) then 666666 else aa.major_component_id end as majorComponentId,\r\n"
			+ "      case when (aa.scheme_id is null ) then '555555' else aa.scheme_id end as schemeId,\r\n"
			+ "      case when (aa.activity_master_details_id is null ) then 999999 else aa.activity_master_details_id end as activityMasterDetailsId,\r\n"
			+ "      ps.title  as schemeName,pmc.title as  majorComponentName,psc.title as  subComponentName,\r\n"
			+ "      pam.title as activityMasterName,pd.activity_master_details_name as activityMasterDetailName,pd.recuring_nonrecuring as recuringNonrecuring\r\n"
			+ "      from (\r\n"
			+ "      SELECT sum(pawpbd.financial_amount) AS financial_amount,\r\n"
			+ "          sum(pawpbd.physical_quantity) AS physical_quantity,\r\n"
			+ "          sum(pawpbd.financial_amount)/ nullif(sum(pawpbd.physical_quantity),0) as unit_cost ,\r\n"
			+ "          pawpbd.scheme_id,pawpbd.major_component_id,pawpbd.sub_component_id,\r\n"
			+ "          pawpbd.activity_master_id,pawpbd.activity_master_details_id\r\n"
			+ "        FROM prb_ann_wrk_pln_bdgt_data pawpbd\r\n"
			+ "        WHERE pawpbd.district = :districtId  and pawpbd.plan_year=:planYear "
			+ "        GROUP BY GROUPING SETS ((pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id, pawpbd.activity_master_id, pawpbd.activity_master_details_id), (pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id, pawpbd.activity_master_id), (pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id), (pawpbd.scheme_id, pawpbd.major_component_id), (pawpbd.scheme_id), ())\r\n"
			+ "      ) \r\n"
			+ "      aa \r\n"
			+ "       left join prb_data pd on (pd.id= aa.activity_master_details_id)\r\n"
			+ "       left join prb_activity_master pam on (pam.id= aa.activity_master_id)\r\n"
			+ "       left join prb_sub_component psc on (psc.sub_component_id= aa.sub_component_id)\r\n"
			+ "       left join prb_major_component pmc   on (pmc.prb_major_component_id = aa.major_component_id)\r\n"
			+ "       left join prb_schemes ps on (ps.id= aa.scheme_id\\:\\:numeric)\r\n"
			+ "       order by aa.scheme_id , aa.major_component_id , aa.sub_component_id , aa.activity_master_id ,aa.activity_master_details_id,pd.serial_order")
	public List<ProposedCosting> findAllByDistrictAndPlanYear(@Param("districtId") Integer stateId, @Param("planYear") String planYear);


	@Query(nativeQuery=true, value ="select sum(financial_amount) as financialAmount  , pmcws.major_component_id_without_scheme as majorComponentIdWithoutScheme,\r\n"
			+ "pmcws.major_component_name_without_scheme as majorComponentName\r\n"
			+ "from prb_ann_wrk_pln_bdgt_data pawpbd , prb_major_component pmc , prb_major_component_without_scheme pmcws\r\n"
			+ "where pmc.prb_major_component_id = pawpbd.major_component_id \r\n"
			+ "and  pmcws.major_component_id_without_scheme= pmc.major_component_id_without_scheme \r\n"
			+ "and state  =:stateId and plan_year =:planYear "
			+ "group by  pmcws.major_component_id_without_scheme , pmcws.major_component_name_without_scheme order by pmcws.major_component_name_without_scheme")
	public List<MajorComponentProposal> findMajorComponentProposal(@Param("stateId") Integer stateId, @Param("planYear") String planYear);
	
//===========================	State Costing Sheet Recommendation=================================
	@Query(nativeQuery=true, value ="SELECT pmc.title as  majorComponentName,\r\n" + 
			"	  round(sum(pawpbd.financial_amount),5) AS financialAmount,\r\n" + 
			"	  coalesce(round(sum(pawpbd.proposed_financial_amount),5),0) AS majorComponentIdWithoutScheme\r\n" + 
			"            \r\n" + 
			"         FROM prb_state_ann_wrk_pln_bdgt_data pawpbd\r\n" + 
			"		 left join prb_major_component pmc   on (pmc.prb_major_component_id = pawpbd.major_component_id)\r\n" + 
			"        WHERE pawpbd.state =:stateId and plan_year =:planYear \r\n" + 
			"		group by grouping sets ((pmc.title),())\r\n" + 
			"		 order by majorComponentName")
	public List<MajorComponentProposal> findMajorComponentProposalForStateRecommendation(@Param("stateId") Integer stateId, @Param("planYear") String planYear);

//========================End===========================

	@Query(nativeQuery=true, value ="select\r\n"
			+ "sum(financial_amount) filter (where pd.recuring_nonrecuring= 1) as recuring,\r\n"
			+ "sum(financial_amount) filter (where pd.recuring_nonrecuring= 2 ) as noRecuring,\r\n"
			+ "sum(financial_amount)  as  total,state as stateId, pd.scheme_id as schemeId\r\n"
			+ "from prb_ann_wrk_pln_bdgt_data pawpbd ,  prb_data pd \r\n"
			+ "where pawpbd.activity_master_details_id = pd.id \r\n"
			+ "and state  =:stateId and plan_year =:planYear "
			+ "group by state,pd.scheme_id ")
	public List<RecurringNonRecurring> findRecurringNonRecurring(@Param("stateId") Integer stateId, @Param("planYear") String planYear);


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

}
