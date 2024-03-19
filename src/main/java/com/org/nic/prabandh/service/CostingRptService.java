package com.org.nic.prabandh.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

public interface CostingRptService {

	ResponseEntity<?> downloadStateDistCostingReport(Integer stateId, Integer regionType, String planYear) throws IOException;


	ResponseEntity<?> downloadRecommendationReport(Integer regionId, String planYear, boolean isDetails)throws IOException;


	ResponseEntity<?> downloadSpilloverReport(Integer regionId, String planYear)throws IOException;
	
	public ResponseEntity<?> downloadDraftPABReport(Integer regionId, String planYear)throws IOException;


	ResponseEntity<?> downloadAnnexureReport(Integer regionId, String planYear)throws IOException;


}
