package com.org.nic.prabandh.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.model.MastStatesTentative;
import com.org.nic.prabandh.model.MstStateModel;
import com.org.nic.prabandh.repository.CostingRptRepository;
import com.org.nic.prabandh.repository.MastStatesTentativeRepository;
import com.org.nic.prabandh.repository.StateMasterRepository;
import com.org.nic.prabandh.utill.CostingReportPdf;


@Service
public class StateRptServiceImpl implements StateRptService{


	 
	@Autowired
	CostingReportPdf costingReportUtill;
	
	@Autowired
	StateMasterRepository stateMasterRepository;
	
	@Autowired
	MastStatesTentativeRepository mastStatesTentativeRepository;
	
	@Autowired
	CostingRptRepository costingRptRepository;

	@Override
	public ResponseEntity<?> downloadCostingReport(Integer stateId, String planYear) throws IOException{
		
		MstStateModel mstStateModel = null;
		String stateName = null;
		
		Optional<MstStateModel>  stateMaster =  stateMasterRepository.findById(stateId);
		if(stateMaster.isPresent()) 
			mstStateModel =stateMaster.get();
			stateName = mstStateModel.getStateName() != null ? mstStateModel.getStateName():"";
		
		
			Optional<MastStatesTentative>  stateTentive =  mastStatesTentativeRepository.findById(stateId);
			
			/*	List<StateRptModel>  withZerosListObj =  stateRptRepository.findAllByOrderBySchemeIdAscMajorComponentIdAscSubComponentIdAscActivityMasterIdAscActivityMasterDetailsIdAsc();
				
				Double elementaryRecuring=0d,elementaryNonrecuring=0d,secondaryRecuring=0d,secondaryNonrecuring=0d,teacherRecuring=0d,teacherNonrecuring=0d;
				List<StateRptModel> listObj =new ArrayList<>();
				for (StateRptModel obj : withZerosListObj) {
					if(obj.getPhysicalQuantity() != 0 && obj.getUnit_cost() != 0 && obj.getFinancialAmount() != 0) {
						listObj.add(obj);
						if(obj.getSchemeId()==1) {
							if(obj.getRecuringNonrecuring()==1) {
								elementaryRecuring=elementaryRecuring+obj.getFinancialAmount();
							}
							if(obj.getRecuringNonrecuring()==2) {
								elementaryNonrecuring=elementaryNonrecuring+obj.getFinancialAmount();
							}
						}
						if(obj.getSchemeId()==2) {
							if(obj.getRecuringNonrecuring()==1) {
								secondaryRecuring=secondaryRecuring+obj.getFinancialAmount();
							}
							if(obj.getRecuringNonrecuring()==2) {
								secondaryNonrecuring=secondaryNonrecuring+obj.getFinancialAmount();
							}
						}
						if(obj.getSchemeId()==3) {
							if(obj.getRecuringNonrecuring()==1) {
								teacherRecuring=teacherRecuring+obj.getFinancialAmount();
							}
							if(obj.getRecuringNonrecuring()==2) {
								teacherNonrecuring=teacherNonrecuring+obj.getFinancialAmount();
							}
						}
						
					}
						
				}*/
			//List<StateRptModel>  withZerosListObj =  stateRptRepository.findAllByOrderBySchemeIdAscMajorComponentIdAscSubComponentIdAscActivityMasterIdAscActivityMasterDetailsIdAsc();
			List<ProposedCosting>  withZerosListObj =  costingRptRepository.findAllByNativeQuery(stateId,planYear);
			List<ProposedCosting> listObj =new ArrayList<>();
			Map<Integer, Double[]> recurNonrecurFinAmtTot = new HashMap<>();
			for (ProposedCosting obj : withZerosListObj) {
			   if(obj !=null && obj.getPhysicalQuantity() !=null && obj.getUnitCost() !=null && obj.getFinancialAmount() !=null && obj.getPhysicalQuantity() != 0 && obj.getUnitCost() != 0 && obj.getFinancialAmount() != 0) {
			       listObj.add(obj);
			       Integer schemeId = obj.getSchemeId();
			       Integer recuringNonrecuring = obj.getRecuringNonrecuring();
			       if(recuringNonrecuring !=null) {
			    	   recurNonrecurFinAmtTot.putIfAbsent(schemeId, new Double[]{0d, 0d});
			    	   recurNonrecurFinAmtTot.get(schemeId)[recuringNonrecuring - 1] += obj.getFinancialAmount();
			       }
			   }
			}
			
			/*for (Map.Entry<Integer, Double[]> entry : recurNonrecurFinAmtTot.entrySet()) {
				   Integer schemeId = entry.getKey();
				   Double[] amounts = entry.getValue();
				   Double recuring = amounts[0];
				   Double nonrecuring = amounts[1];
			
				   // Now you can use schemeId, recuring, and nonrecuring
				   System.out.println("Scheme ID: " + schemeId);
				   System.out.println("Recuring Amount: " + recuring);
				   System.out.println("Nonrecuring Amount: " + nonrecuring);
				}*/
			
		
		
		Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> groupedByFields = new TreeMap<>();

		for (ProposedCosting model : listObj) {
		   int schemeId = model.getSchemeId();
		   int majorComponentId = model.getMajorComponentId();
		   int subComponentId = model.getSubComponentId();
		   int activityMasterId = model.getActivityMasterId();
		   int activityMasterDetailsId = model.getActivityMasterDetailsId();
		   
		   groupedByFields.computeIfAbsent(schemeId, k -> new TreeMap<>())
		                 .computeIfAbsent(majorComponentId, k -> new TreeMap<>())
		                 .computeIfAbsent(subComponentId, k -> new TreeMap<>())
		                 .computeIfAbsent(activityMasterId, k -> new TreeMap<>())
		                 .computeIfAbsent(activityMasterDetailsId, k -> new ArrayList<>())
		                 .add(model);
		}

		return costingReportUtill.downloadCostingReportPdf(planYear,groupedByFields,stateName,recurNonrecurFinAmtTot,stateTentive);
	}
}
