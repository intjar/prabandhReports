package com.org.nic.prabandh.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.org.nic.prabandh.bean.AnnexureDetailsList;
import com.org.nic.prabandh.bean.AnnexureSchemeDetails;
import com.org.nic.prabandh.bean.MajorComponentProposal;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.bean.RecurringNonRecurring;
import com.org.nic.prabandh.bean.SpillOverReportDto;
import com.org.nic.prabandh.bean.Spillover;
import com.org.nic.prabandh.model.MastStatesTentative;
import com.org.nic.prabandh.model.MasterDistricts;
import com.org.nic.prabandh.model.MstStateModel;
import com.org.nic.prabandh.pdf.AnnexureReptPdf;
import com.org.nic.prabandh.pdf.DistrictCostingReportPdf;
import com.org.nic.prabandh.pdf.DraftPABDetailsReptPdf;
import com.org.nic.prabandh.pdf.RecommendationDeatilsReptPdf;
import com.org.nic.prabandh.pdf.RecommendationReptPdf;
import com.org.nic.prabandh.pdf.SpilloverReptPdf;
import com.org.nic.prabandh.pdf.StateCostingReportPdf;
import com.org.nic.prabandh.repository.AnnexureRepository;
import com.org.nic.prabandh.repository.DistrictsMasrerRepository;
import com.org.nic.prabandh.repository.MastStatesTentativeRepository;
import com.org.nic.prabandh.repository.RecommendationRptRepository;
import com.org.nic.prabandh.repository.SillOverRptRepository;
import com.org.nic.prabandh.repository.StateDistCostRptRepository;
import com.org.nic.prabandh.repository.StateMasterRepository;

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
	SpilloverReptPdf spilloverReptPdf;
	
	@Autowired
	DraftPABDetailsReptPdf draftPABDetailsReptPdf;
	
	@Autowired
	AnnexureReptPdf annexureReptPdf;
	
	
	
	
	
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
	
	@Autowired
	SillOverRptRepository sillOverRptRepository;
	
	@Autowired
	AnnexureRepository annexureRepository;

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

		String regionName = "";
		Optional<MstStateModel> stateMaster = stateMasterRepository.findById(regionId);
		if (stateMaster.isPresent()) {
			MstStateModel mstStateModel = stateMaster.get();
			regionName = mstStateModel.getStateName() != null ? mstStateModel.getStateName() : "";
		}
		

		 Integer isDraft =0;
		 List<Object[]> isDraftList = recommendationRptRepository.findIsDraftOrNot(regionId);
        if (!isDraftList.isEmpty()) {
            Object[] firstRow = isDraftList.get(0);
            if (firstRow.length > 0) {
                isDraft = ((Short) firstRow[0]).intValue();
            }
        }
		

		List<ProposedCosting> dataList = recommendationRptRepository.findAllByStateAndPlanYear(regionId, planYear);
        Optional<MastStatesTentative> stateTentive = mastStatesTentativeRepository.findById(regionId);
        List<MajorComponentProposal> majorComponentProposal = recommendationRptRepository.findMajorComponentProposalForStateRecommendation(regionId, planYear);
        List<RecurringNonRecurring> statePlanList = recommendationRptRepository.findStatePlanList(regionId, planYear);
        List<RecurringNonRecurring> budgetRecurNonRecur2324 = recommendationRptRepository.findBudgetRecurringNonRecurring2324(regionId);
        List<RecurringNonRecurring> expenditureRecurNonRecur2324 = recommendationRptRepository.findExpexpenditureRecurNonRecur2324(regionId);
        List<RecurringNonRecurring> recommendationList = recommendationRptRepository.getRecommendationList(regionId, planYear);
        List<Spillover> spilloverList = recommendationRptRepository.getSpilloverListList(regionId);
        List<MajorComponentProposal> majorComponentStatePlan = recommendationRptRepository.findMajorComponentStatePlan(regionId, planYear);
        
        

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
		
		
		ResponseEntity<?> response = null;
		if(isDetails) {
			response= recommendationDeatilsReptPdf.downloadRecommendationDetailsReptPdf(isDraft,planYear, groupedByFields, regionName, stateTentive, statePlanList, budgetRecurNonRecur2324, expenditureRecurNonRecur2324,
					majorComponentProposal, recommendationList, spilloverList,majorComponentStatePlan);
		}else if(!isDetails) {
			response= recommendationReptPdf.downloadRecommendationReptPdf(isDraft,planYear, groupedByFields, regionName, stateTentive, statePlanList, budgetRecurNonRecur2324, expenditureRecurNonRecur2324,
					majorComponentProposal, recommendationList, spilloverList,majorComponentStatePlan);
		}
		
		return response;
		
	
	}
	
	
	@Override
	public ResponseEntity<?> downloadDraftPABReport(Integer regionId, String planYear) throws IOException {
System.out.println(regionId+", "+planYear);
		
		String regionName = "";
		Optional<MstStateModel> stateMaster = stateMasterRepository.findById(regionId);
		if (stateMaster.isPresent()) {
			MstStateModel mstStateModel = stateMaster.get();
			regionName = mstStateModel.getStateName() != null ? mstStateModel.getStateName() : "";
		}
		

		 Integer isDraft =0;
		 List<Object[]> isDraftList = recommendationRptRepository.findIsDraftOrNot(regionId);
        if (!isDraftList.isEmpty()) {
            Object[] firstRow = isDraftList.get(0);
            if (firstRow.length > 0) {
                isDraft = ((Short) firstRow[0]).intValue();
            }
        }
		

		List<ProposedCosting> dataList = recommendationRptRepository.findAllDaftPABByStateAndPlanYear(regionId, planYear);
        Optional<MastStatesTentative> stateTentive = mastStatesTentativeRepository.findById(regionId);
        List<MajorComponentProposal> majorComponentProposal = recommendationRptRepository.findMajorComponentProposalForStateRecommendation(regionId, planYear);
        List<RecurringNonRecurring> statePlanList = recommendationRptRepository.findStatePlanList(regionId, planYear);
        List<RecurringNonRecurring> budgetRecurNonRecur2324 = recommendationRptRepository.findBudgetRecurringNonRecurring2324(regionId);
        List<RecurringNonRecurring> expenditureRecurNonRecur2324 = recommendationRptRepository.findExpexpenditureRecurNonRecur2324(regionId);
        List<RecurringNonRecurring> recommendationList = recommendationRptRepository.getRecommendationList(regionId, planYear);
        List<Spillover> spilloverList = recommendationRptRepository.getSpilloverListList(regionId);
        List<MajorComponentProposal> majorComponentStatePlan = recommendationRptRepository.findMajorComponentStatePlan(regionId, planYear);
        
        

		List<ProposedCosting> listObj = new ArrayList<>();
		for (ProposedCosting obj : dataList) {
			/*int isAllZero = 0;
			if (obj.getProposedFinancialAmount() == null || obj.getProposedFinancialAmount() == 0) {
				isAllZero++;
			}
			if (obj.getFinancialAmount() == null || obj.getFinancialAmount() == 0) {
				isAllZero++;
			}
			if (obj.getTotApprovedBudget() == null || obj.getTotApprovedBudget() == 0) {
				isAllZero++;
			}
			if (obj.getTotExpenditure() == null || obj.getTotExpenditure() == 0) {
				isAllZero++;
			}
			if (obj.getSpillOverApprovalBudget23() == null || obj.getSpillOverApprovalBudget23() == 0) {
				isAllZero++;
			}
			if (obj.getAnticipatedExpenditureSpillOver() == null || obj.getAnticipatedExpenditureSpillOver() == 0) {
				isAllZero++;
			}
			if (isAllZero != 6)*/
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
		
		
		ResponseEntity<?> response = null;
			response= draftPABDetailsReptPdf.downloadDraftPABDetailsReptPdf(isDraft,planYear, groupedByFields, regionName, stateTentive, statePlanList, budgetRecurNonRecur2324, expenditureRecurNonRecur2324,
					majorComponentProposal, recommendationList, spilloverList,majorComponentStatePlan);

		return response;
		
	
	}














	@Override
	public ResponseEntity<?> downloadSpilloverReport(Integer regionId, String planYear) throws IOException {
		ResponseEntity<?> response = null;
		
		String regionName = null;
		Optional<MstStateModel> stateMaster = stateMasterRepository.findById(regionId);
		if (stateMaster.isPresent()) {
			MstStateModel mstStateModel = stateMaster.get();
			regionName = mstStateModel.getStateName() != null ? mstStateModel.getStateName() : "";
		}
		List<SpillOverReportDto> dataList = sillOverRptRepository.findAllByState(regionId);
		
		List<SpillOverReportDto> listObj = new ArrayList<>();
		for (SpillOverReportDto obj : dataList) {
			int isAllZero=0;
				if (obj.getTotal_physical_budget_approved() == null || obj.getTotal_physical_budget_approved()== 0){
					isAllZero++;
				}if (obj.getTotal_financial_budget_approved() == null || obj.getTotal_financial_budget_approved()== 0){
					isAllZero++;
				}if (obj.getFinancial_amount_progress_inception() == null || obj.getFinancial_amount_progress_inception()== 0 ){
					isAllZero++;
				}if (obj.getFinancial_amount_spill_over() == null || obj.getFinancial_amount_spill_over()== 0){
					isAllZero++;
				}
				if(isAllZero !=46)
					listObj.add(obj);
		}
		
		
		Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<SpillOverReportDto>>>>> groupedByFields = new TreeMap<>();
		for (SpillOverReportDto model : listObj) {
			int majorComponentId = model.getMajor_component_id();
			int subComponentId = model.getSub_component_id();
			int activityMasterId = model.getActivity_master_id();
			int activityMasterDetailsId = model.getActivity_master_details_id();

			groupedByFields.computeIfAbsent(
					majorComponentId, k -> new TreeMap<>())
				.computeIfAbsent(subComponentId, k -> new TreeMap<>())
				.computeIfAbsent(activityMasterId, k -> new TreeMap<>())
				.computeIfAbsent(activityMasterDetailsId, k -> new ArrayList<>()).add(model);
		}
		
		response= spilloverReptPdf.downloadSpilloverReptPdf(planYear,regionName, groupedByFields);
		
		return response;
	}




	@Override
	public ResponseEntity<?> downloadAnnexureReport(Integer regionId, String planYear) throws IOException {
		String regionName = null;
		Optional<MstStateModel> stateMaster = stateMasterRepository.findById(regionId);
		if (stateMaster.isPresent()) {
			MstStateModel mstStateModel = stateMaster.get();
			regionName = mstStateModel.getStateName() != null ? mstStateModel.getStateName() : "";
		}
		List<AnnexureSchemeDetails> dataDetails = annexureRepository.findDetailsByStateAndYear(regionId,planYear);
		
		
		List<AnnexureDetailsList> dataList = annexureRepository.findDetailsListByStateAndYear(regionId,planYear);
		Map<Integer, List<AnnexureDetailsList>> dataListMap = dataList.stream()
			    .collect(Collectors.groupingBy(AnnexureDetailsList::getActivity_master_details_id));
		
		ResponseEntity<?> response= annexureReptPdf.downloadAnnexureReptPdf(planYear,regionName, dataDetails,dataListMap);
		
		return response;
	}
	
	
	
}
