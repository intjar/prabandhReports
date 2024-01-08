package com.org.nic.prabandh.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

public interface StateRptService {

	ResponseEntity<?> downloadCostingReport(Integer stateId, String planYear) throws IOException;

}
