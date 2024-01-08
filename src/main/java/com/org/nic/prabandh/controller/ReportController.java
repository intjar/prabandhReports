package com.org.nic.prabandh.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.nic.prabandh.service.StateRptService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api")
@Slf4j
public class ReportController {
	

	@Autowired
	StateRptService stateRptService;
		
	
	@GetMapping(value = "costing-report/{stateId}/{planYear}")
	public ResponseEntity<?> fetchDelhiCostingReport(
			@PathVariable(name = "stateId") Integer stateId,
			@PathVariable(name = "planYear") String planYear
			) throws IOException{ 
		

		//return costingReportUtill.downloadCostingReport();
		return stateRptService.downloadCostingReport(stateId,planYear);
	}


}
