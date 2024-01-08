package com.org.nic.prabandh.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.org.nic.prabandh.model.MstStateModel;

@Repository
public interface StateMasterRepository extends CrudRepository<MstStateModel, Integer>{
	

}
