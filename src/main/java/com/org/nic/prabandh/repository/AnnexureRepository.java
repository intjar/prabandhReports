package com.org.nic.prabandh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.org.nic.prabandh.bean.AnnexureDetailsList;
import com.org.nic.prabandh.bean.AnnexureSchemeDetails;
import com.org.nic.prabandh.model.MstStateModel;

public interface AnnexureRepository extends CrudRepository<MstStateModel, Integer>{

	

	
	
	
	@Query(nativeQuery = true, value = "select distinct  pd.id as activity_master_details_id ,pd.scheme_name , pd.major_component_name , pd.sub_component_name ,pd.activity_master_name , pd.activity_master_details_name ,\r\n"
			+ "    psawpbd.proposed_physical_quantity ,psawpbd.proposed_financial_amount\r\n"
			+ "    from public.prb_state_ann_wrk_pln_bdgt_data psawpbd   , prb_data pd \r\n"
			+ "    where state =:stateId  and plan_year = :planYear and proposed_physical_quantity  > 0 and pd.id = psawpbd.activity_master_details_id \r\n"
			+ "    and pd.dd_school = '1'" )
	List<AnnexureSchemeDetails> findDetailsByStateAndYear(@Param("stateId") Integer stateId,@Param("planYear") String planYear);

	
	@Query(nativeQuery = true, value = "select am.quantity , psm.udise_sch_code , psm.school_name , psm.district_name , am.activity_master_details_id  from \r\n"
			+ "public.prb_state_ann_wrk_pln_bdgt_data_physical_asset am inner join public.prb_school_master psm on am.asset_code = psm.udise_sch_code \r\n"
			+ "where  state = :stateId and plan_year = :planYear " )
	List<AnnexureDetailsList> findDetailsListByStateAndYear(@Param("stateId") Integer stateId,@Param("planYear") String planYear);

}
