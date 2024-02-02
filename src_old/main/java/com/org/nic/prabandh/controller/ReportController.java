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
		return costingRptService.downloadCostingReport(regionId,regionType, planYear);
	}

	
	
	@GetMapping(value = "state-costing/{regionId}/{planYear}")
	public ResponseEntity<?> fetchStateCostingReport(
			@PathVariable(name = "regionId") Integer regionId,
			@PathVariable(name = "planYear") String planYear) throws IOException {
		
		return costingRptService.downloadStateCostingReport(regionId,planYear);
	}

}
