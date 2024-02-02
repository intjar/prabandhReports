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
			+ "        WHERE pawpbd.state = :stateId  and pawpbd.plan_year=:planYear "
			+ "        GROUP BY GROUPING SETS ((pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id, pawpbd.activity_master_id, pawpbd.activity_master_details_id), (pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id, pawpbd.activity_master_id), (pawpbd.scheme_id, pawpbd.major_component_id, pawpbd.sub_component_id), (pawpbd.scheme_id, pawpbd.major_component_id), (pawpbd.scheme_id), ())\r\n"
			+ "      ) \r\n"
			+ "      aa \r\n"
			+ "       left join prb_data pd on (pd.id= aa.activity_master_details_id)\r\n"
			+ "       left join prb_activity_master pam on (pam.id= aa.activity_master_id)\r\n"
			+ "       left join prb_sub_component psc on (psc.sub_component_id= aa.sub_component_id)\r\n"
			+ "       left join prb_major_component pmc   on (pmc.prb_major_component_id = aa.major_component_id)\r\n"
			+ "       left join prb_schemes ps on (ps.id= aa.scheme_id\\:\\:numeric)\r\n"
			+ "       order by aa.scheme_id , aa.major_component_id , aa.sub_component_id , aa.activity_master_id ,aa.activity_master_details_id,pd.serial_order")
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
