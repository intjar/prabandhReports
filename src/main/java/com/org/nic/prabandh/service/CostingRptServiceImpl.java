package com.org.nic.prabandh.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.org.nic.prabandh.bean.MajorComponentProposal;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.bean.RecurringNonRecurring;
import com.org.nic.prabandh.bean.Spillover;
import com.org.nic.prabandh.model.MastStatesTentative;
import com.org.nic.prabandh.model.MasterDistricts;
import com.org.nic.prabandh.model.MstStateModel;
import com.org.nic.prabandh.repository.DistrictsMasrerRepository;
import com.org.nic.prabandh.repository.MastStatesTentativeRepository;
import com.org.nic.prabandh.repository.RecommendationRptRepository;
import com.org.nic.prabandh.repository.StateDistCostRptRepository;
import com.org.nic.prabandh.repository.StateMasterRepository;
import com.org.nic.prabandh.utill.DistrictCostingReportPdf;
import com.org.nic.prabandh.utill.RecommendationDeatilsReptPdf;
import com.org.nic.prabandh.utill.RecommendationReptPdf;
import com.org.nic.prabandh.utill.StateCostingReportPdf;

@Service
public class CostingRptServiceImpl implements CostingRptService {

	@Autowired
	StateCostingReportPdf stateCostingReportPdf;
	
	@Autowired
	DistrictCostingReportPdf districtCostingReportPdf;
	
	
	
	@Autowired
	RecommendationDeatilsReptPdf recommendationDeatilsReptPdf;
	
	@Autowired
	RecommendationReptPdf recommendationReptPdf;

	@Autowired
	StateMasterRepository stateMasterRepository;
	
	@Autowired
	DistrictsMasrerRepository districtsMasrerRepository;

	@Autowired
	MastStatesTentativeRepository mastStatesTentativeRepository;

	@Autowired
	StateDistCostRptRepository stateDistCostRptRepository;
	
	@Autowired
	RecommendationRptRepository recommendationRptRepository;

	@Override
	public ResponseEntity<?> downloadStateDistCostingReport(Integer regionId, Integer regionType, String planYear) throws IOException {

		ResponseEntity<?> response = null;
		String regionName = null;

		List<ProposedCosting> dataList =new ArrayList<>();
		Optional<MastStatesTentative> stateTentive = java.util.Optional.empty();
		List<MajorComponentProposal> majorComponentProposal =null;
		List<RecurringNonRecurring> statePlanList =null;
		List<RecurringNonRecurring> budgetRecurNonRecur2324 =null;
		List<RecurringNonRecurring> expenditureRecurNonRecur2324 =null;
		List<Spillover> spilloverList =null;
		
		List<MajorComponentProposal> majorCompoDetails =null;

		switch(regionType) {
		   case 1:
			   Optional<MstStateModel> stateMaster = stateMasterRepository.findById(regionId);
				if (stateMaster.isPresent()) {
					MstStateModel mstStateModel = stateMaster.get();
					regionName = mstStateModel.getStateName() != null ? mstStateModel.getStateName() : "";
				}
				stateTentive = mastStatesTentativeRepository.findById(regionId);
				dataList = stateDistCostRptRepository.findAllByStateAndPlanYear(regionId, planYear);
				majorComponentProposal=stateDistCostRptRepository.findMajorComponentProposal(regionId, planYear);
				
				statePlanList=stateDistCostRptRepository.findStatePlanList(regionId, planYear);
				budgetRecurNonRecur2324=stateDistCostRptRepository.findBudgetRecurringNonRecurring2324(regionId);
				expenditureRecurNonRecur2324=stateDistCostRptRepository.findExpexpenditureRecurNonRecur2324(regionId);
				spilloverList=stateDistCostRptRepository.getSpilloverListList(regionId);
				
				majorCompoDetails=stateDistCostRptRepository.findMajorComponentDetails(regionId, planYear);
				
		       break;
		   case 2:
			   Optional<MasterDistricts> districtMaster = districtsMasrerRepository.findById(regionId);
				if (districtMaster.isPresent()) {
					MasterDistricts masterDistricts= districtMaster.get();
					regionName = masterDistricts.getDistrictName() != null ? masterDistricts.getDistrictName() : "";
				}
				dataList = stateDistCostRptRepository.findAllByDistrictAndPlanYear(regionId, planYear);
		       break;
		   default:

		}

		
		List<ProposedCosting> listObj = new ArrayList<>();
		for (ProposedCosting obj : dataList) {
			
			int isAllZero=0;
			if (obj.getFinancialAmount() == null || obj.getFinancialAmount()== 0){
				isAllZero++;
			}if (obj.getTotApprovedBudget() == null || obj.getTotApprovedBudget()== 0 ){
				isAllZero++;
			}if (obj.getTotExpenditure() == null || obj.getTotExpenditure()== 0){
				isAllZero++;
			}if (obj.getSpillOverApprovalBudget23() == null || obj.getSpillOverApprovalBudget23()== 0){
				isAllZero++;
			}if (obj.getAnticipatedExpenditureSpillOver() == null || obj.getAnticipatedExpenditureSpillOver()== 0){
				isAllZero++;
			}
				
			if(isAllZero !=5)
				listObj.add(obj);
		}

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
				.computeIfAbsent(activityMasterDetailsId, k -> new ArrayList<>()).add(model);
		}

		if (regionType == 1) {
			response = stateCostingReportPdf.downloadStateCostingReptPdf(planYear, groupedByFields, regionName, stateTentive, majorComponentProposal, statePlanList, budgetRecurNonRecur2324,
					expenditureRecurNonRecur2324, spilloverList,majorCompoDetails);
		}else if (regionType == 2) {
			response = districtCostingReportPdf.downloadDistrictCostingReptPdf(planYear, groupedByFields, regionName);
		}

		return response;
	}

	
	
	
	
	
	
	
	
	
	
	
	 
	@Override
	public ResponseEntity<?> downloadRecommendationReport(Integer regionId, String planYear, boolean isDetails) throws IOException {

		ResponseEntity<?> response = null;
		String regionName = null;
		Optional<MastStatesTentative> stateTentive = java.util.Optional.empty();
		List<MajorComponentProposal> majorComponentProposal =null;
		List<RecurringNonRecurring> statePlanList =null;
		List<RecurringNonRecurring> budgetRecurNonRecur2324 =null;
		List<RecurringNonRecurring> expenditureRecurNonRecur2324 =null;
		
		List<RecurringNonRecurring> recommendationList =null;
		List<Spillover> spilloverList =null;
		List<MajorComponentProposal> majorComponentStatePlan =null;
		
		
		//List<RecurringNonRecurring> recommendationList =null;
		
		Optional<MstStateModel> stateMaster = stateMasterRepository.findById(regionId);
		if (stateMaster.isPresent()) {
			MstStateModel mstStateModel = stateMaster.get();
			regionName = mstStateModel.getStateName() != null ? mstStateModel.getStateName() : "";
		}
		stateTentive = mastStatesTentativeRepository.findById(regionId);
		List<ProposedCosting> dataList = recommendationRptRepository.findAllByStateAndPlanYear(regionId, planYear);
		majorComponentProposal=recommendationRptRepository.findMajorComponentProposalForStateRecommendation(regionId, planYear);
		statePlanList=recommendationRptRepository.findStatePlanList(regionId, planYear);
		budgetRecurNonRecur2324=recommendationRptRepository.findBudgetRecurringNonRecurring2324(regionId);
		expenditureRecurNonRecur2324=recommendationRptRepository.findExpexpenditureRecurNonRecur2324(regionId);
		
		recommendationList=recommendationRptRepository.getRecommendationList(regionId,planYear);
		spilloverList=recommendationRptRepository.getSpilloverListList(regionId);
		
		majorComponentStatePlan=recommendationRptRepository.findMajorComponentStatePlan(regionId, planYear);
		
		

		List<ProposedCosting> listObj = new ArrayList<>();
		for (ProposedCosting obj : dataList) {

			int isAllZero=0;
			if(isDetails) {
				if (obj.getProposedFinancialAmount() == null || obj.getProposedFinancialAmount()== 0){
					isAllZero++;
				}if (obj.getFinancialAmount() == null || obj.getFinancialAmount()== 0){
					isAllZero++;
				}if (obj.getTotApprovedBudget() == null || obj.getTotApprovedBudget()== 0 ){
					isAllZero++;
				}if (obj.getTotExpenditure() == null || obj.getTotExpenditure()== 0){
					isAllZero++;
				}if (obj.getSpillOverApprovalBudget23() == null || obj.getSpillOverApprovalBudget23()== 0){
					isAllZero++;
				}if (obj.getAnticipatedExpenditureSpillOver() == null || obj.getAnticipatedExpenditureSpillOver()== 0){
					isAllZero++;
				}
				if(isAllZero !=6)
					listObj.add(obj);
			}
			
			if(!isDetails){
				if (obj.getProposedFinancialAmount() == null || obj.getProposedFinancialAmount()== 0){
					isAllZero++;
				}if (obj.getFinancialAmount() == null || obj.getFinancialAmount()== 0){
					isAllZero++;
				}
				if(isAllZero !=2)
					listObj.add(obj);
			}
		}
		
		
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
				.computeIfAbsent(activityMasterDetailsId, k -> new ArrayList<>()).add(model);
		}
		
		
		if(isDetails) {
			response= recommendationDeatilsReptPdf.downloadRecommendationDetailsReptPdf(planYear, groupedByFields, regionName, stateTentive, statePlanList, budgetRecurNonRecur2324, expenditureRecurNonRecur2324,
					majorComponentProposal, recommendationList, spilloverList,majorComponentStatePlan);
		}else if(!isDetails) {
			response= recommendationReptPdf.downloadRecommendationReptPdf(planYear, groupedByFields, regionName, stateTentive, statePlanList, budgetRecurNonRecur2324, expenditureRecurNonRecur2324,
					majorComponentProposal, recommendationList, spilloverList,majorComponentStatePlan);
		}
		
		return response;
		
	
	}
}
