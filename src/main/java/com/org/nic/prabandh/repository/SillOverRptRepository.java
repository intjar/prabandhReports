package com.org.nic.prabandh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.nic.prabandh.bean.Snippet.SpilOverSchemeTotalProj;
import com.org.nic.prabandh.bean.SpillOverReportDto;
import com.org.nic.prabandh.model.MstStateModel;

@Repository
public interface SillOverRptRepository extends CrudRepository<MstStateModel, Integer>{

	@Query(nativeQuery = true , value = "select * from report.view_spillover_report_without_scheme_final where state = :stateId \r\n"
			+ "order by major_component_id, sub_component_id, activity_master_id, activity_master_details_id")
	public List<SpillOverReportDto> findAllByState(@Param("stateId") Integer stateId);

	
	@Query(nativeQuery = true , value = "select sum(spillover_amount), pd.scheme_id, pd.scheme_name  from prb_ann_wrk_pln_bdgt_spill_over pawpbso , prb_data pd \r\n"
			+ "where state =:stateId and pawpbso.activity_master_details_id = pd.id group by pd.scheme_id , pd.scheme_name order by scheme_id")
	public List<SpilOverSchemeTotalProj> findTotalByState(@Param("stateId") Integer stateId);
	
	

}
