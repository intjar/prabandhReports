package com.org.nic.prabandh.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.org.nic.prabandh.model.MastStatesTentative;

@Repository
public interface MastStatesTentativeRepository extends CrudRepository<MastStatesTentative, Integer>{

}
