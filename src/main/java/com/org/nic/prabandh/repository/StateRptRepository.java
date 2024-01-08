/*package com.org.nic.prabandh.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.org.nic.prabandh.model.StateRptModel;


@Repository
public interface StateRptRepository extends CrudRepository<StateRptModel, Integer>{
	
	public List<StateRptModel> findAllByOrderBySchemeIdAscMajorComponentIdAscSubComponentIdAscActivityMasterIdAscActivityMasterDetailsIdAsc();

	List<StateRptModel> findBySchemeIdOrderBySchemeIdAscMajorComponentIdAscSubComponentIdAscActivityMasterIdAscActivityMasterDetailsIdAsc(Integer schemeId);


	
}
*/