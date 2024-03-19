package com.org.nic.prabandh.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.nic.prabandh.service.CostingRptService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class ReportController {

	@Autowired
	CostingRptService costingRptService;

	@GetMapping(value = "costing-report/{regionId}/{regionType}/{planYear}")
	public ResponseEntity<?> fetchCostingReport(
			@PathVariable(name = "regionId") Integer regionId,
			@PathVariable(name = "regionType") Integer regionType,
			@PathVariable(name = "planYear") String planYear) throws IOException {
		return costingRptService.downloadStateDistCostingReport(regionId,regionType, planYear);
	}

	
	@GetMapping(value = "recommendation-details/{regionId}/{planYear}")
	public ResponseEntity<?> fetchRecommendationDetailsReport(
			@PathVariable(name = "regionId") Integer regionId,
			@PathVariable(name = "planYear") String planYear) throws IOException {
		
		boolean isDetails=true;
		return costingRptService.downloadRecommendationReport(regionId,planYear,isDetails);
	}
	
	@GetMapping(value = "draft-PAB-details/{regionId}/{planYear}")
	public ResponseEntity<?> fetchDraftPABMinutsDetailsReport(
			@PathVariable(name = "regionId") Integer regionId,
			@PathVariable(name = "planYear") String planYear) throws IOException {
		return costingRptService.downloadDraftPABReport(regionId,planYear);
	}
	
	
	
	@GetMapping(value = "recommendation/{regionId}/{planYear}")
	public ResponseEntity<?> fetchRecommendationReport(
			@PathVariable(name = "regionId") Integer regionId,
			@PathVariable(name = "planYear") String planYear) throws IOException {
		
		boolean isDetails=false;
		return costingRptService.downloadRecommendationReport(regionId,planYear,isDetails);
	}
	
	
	@GetMapping(value = "spillover/{regionId}/{planYear}")
	public ResponseEntity<?> fetchSpilloverReport(
			@PathVariable(name = "regionId") Integer regionId,
			@PathVariable(name = "planYear") String planYear) throws IOException {
		
		return costingRptService.downloadSpilloverReport(regionId,planYear);
	}
	
	
	@GetMapping(value = "annexure/{regionId}/{planYear}")
	public ResponseEntity<?> fetchAnnexureReport(
			@PathVariable(name = "regionId") Integer regionId,
			@PathVariable(name = "planYear") String planYear) throws IOException {
		
		return costingRptService.downloadAnnexureReport(regionId,planYear);
	}

}
