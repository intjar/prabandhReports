package com.org.nic.prabandh.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

public interface CostingRptService {

	ResponseEntity<?> downloadStateDistCostingReport(Integer stateId, Integer regionType, String planYear) throws IOException;


	ResponseEntity<?> downloadRecommendationReport(Integer regionId, String planYear, boolean isDetails)throws IOException;


}
