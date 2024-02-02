package com.org.nic.prabandh.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

public interface CostingRptService {

	ResponseEntity<?> downloadCostingReport(Integer stateId, Integer regionType, String planYear) throws IOException;


	ResponseEntity<?> downloadStateCostingReport(Integer regionId, String planYear)throws IOException;

}
