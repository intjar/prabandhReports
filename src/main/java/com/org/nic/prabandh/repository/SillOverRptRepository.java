package com.org.nic.prabandh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.nic.prabandh.bean.SpillOverReportDto;
import com.org.nic.prabandh.model.MstStateModel;

@Repository
public interface SillOverRptRepository extends CrudRepository<MstStateModel, Integer>{

	@Query(nativeQuery = true , value = "select * from report.view_spillover_report_without_scheme_final where state = :stateId \r\n"
			+ "order by major_component_id, sub_component_id, activity_master_id, activity_master_details_id")
	public List<SpillOverReportDto> findAllByState(@Param("stateId") Integer stateId);
	
	

}
