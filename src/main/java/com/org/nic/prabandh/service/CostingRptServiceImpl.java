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
import com.org.nic.prabandh.model.MastStatesTentative;
import com.org.nic.prabandh.model.MasterDistricts;
import com.org.nic.prabandh.model.MstStateModel;
import com.org.nic.prabandh.repository.CostingRptRepository;
import com.org.nic.prabandh.repository.DistrictsMasrerRepository;
import com.org.nic.prabandh.repository.MastStatesTentativeRepository;
import com.org.nic.prabandh.repository.StateCostingRptRepository;
import com.org.nic.prabandh.repository.StateMasterRepository;
import com.org.nic.prabandh.utill.DistrictCostingReportPdf;
import com.org.nic.prabandh.utill.StateCostingReportPdf;
import com.org.nic.prabandh.utill.CostingReportPdf;

@Service
public class CostingRptServiceImpl implements CostingRptService {

	@Autowired
	CostingReportPdf costingReportPdf;
	
	@Autowired
	DistrictCostingReportPdf districtCostingReportPdf;
	
	@Autowired
	StateCostingReportPdf stateCostingReportPdf;

	@Autowired
	StateMasterRepository stateMasterRepository;
	
	@Autowired
	DistrictsMasrerRepository districtsMasrerRepository;

	@Autowired
	MastStatesTentativeRepository mastStatesTentativeRepository;

	@Autowired
	CostingRptRepository costingRptRepository;
	
	@Autowired
	StateCostingRptRepository stateCostingRptRepository;

	@Override
	public ResponseEntity<?> downloadCostingReport(Integer regionId, Integer regionType, String planYear) throws IOException {

		String regionName = null;

		List<ProposedCosting> dataList =new ArrayList<>();
		Optional<MastStatesTentative> stateTentive = java.util.Optional.empty();
		List<MajorComponentProposal> majorComponentProposal =null;
		List<RecurringNonRecurring> recurringNonRecurring =null;
		List<RecurringNonRecurring> budgetRecurNonRecur2324 =null;
		List<RecurringNonRecurring> expenditureRecurNonRecur2324 =null;

		switch(regionType) {
		   case 1:
			   Optional<MstStateModel> stateMaster = stateMasterRepository.findById(regionId);
				if (stateMaster.isPresent()) {
					MstStateModel mstStateModel = stateMaster.get();
					regionName = mstStateModel.getStateName() != null ? mstStateModel.getStateName() : "";
				}
				stateTentive = mastStatesTentativeRepository.findById(regionId);
				dataList = costingRptRepository.findAllByStateAndPlanYear(regionId, planYear);
				majorComponentProposal=costingRptRepository.findMajorComponentProposal(regionId, planYear);
				recurringNonRecurring=costingRptRepository.findRecurringNonRecurring(regionId, planYear);
				budgetRecurNonRecur2324=costingRptRepository.findBudgetRecurringNonRecurring2324(regionId);
				expenditureRecurNonRecur2324=costingRptRepository.findExpexpenditureRecurNonRecur2324(regionId);
		       break;
		   case 2:
			   Optional<MasterDistricts> districtMaster = districtsMasrerRepository.findById(regionId);
				if (districtMaster.isPresent()) {
					MasterDistricts masterDistricts= districtMaster.get();
					regionName = masterDistricts.getDistrictName() != null ? masterDistricts.getDistrictName() : "";
				}
				dataList = costingRptRepository.findAllByDistrictAndPlanYear(regionId, planYear);
		       break;
		   default:

		}

		
		List<ProposedCosting> listObj = new ArrayList<>();
		for (ProposedCosting obj : dataList) {
			if (obj != null && obj.getPhysicalQuantity() != null && obj.getUnitCost() != null && obj.getFinancialAmount() != null && obj.getPhysicalQuantity() != 0 && obj.getUnitCost() != 0 && obj.getFinancialAmount() != 0) {
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

		if(regionType==1)
			return costingReportPdf.downloadCostingReportPdf(planYear, groupedByFields, regionName,stateTentive,majorComponentProposal,recurringNonRecurring,budgetRecurNonRecur2324,expenditureRecurNonRecur2324);
		else if(regionType==2)
			return districtCostingReportPdf.downloadCostingReportPdf(planYear, groupedByFields, regionName);
		else
			return null;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	//sample 
	@Override
	public ResponseEntity<?> downloadStateCostingReport(Integer regionId, String planYear) throws IOException {


		String regionName = null;
		Optional<MastStatesTentative> stateTentive = java.util.Optional.empty();
		List<MajorComponentProposal> majorComponentProposal =null;
		List<RecurringNonRecurring> recurringNonRecurring =null;
		List<RecurringNonRecurring> budgetRecurNonRecur2324 =null;
		List<RecurringNonRecurring> expenditureRecurNonRecur2324 =null;
		
		Optional<MstStateModel> stateMaster = stateMasterRepository.findById(regionId);
		if (stateMaster.isPresent()) {
			MstStateModel mstStateModel = stateMaster.get();
			regionName = mstStateModel.getStateName() != null ? mstStateModel.getStateName() : "";
		}
		stateTentive = mastStatesTentativeRepository.findById(regionId);
		List<ProposedCosting> dataList = stateCostingRptRepository.findAllByStateAndPlanYear(regionId, planYear);
		majorComponentProposal=costingRptRepository.findMajorComponentProposalForStateRecommendation(regionId, planYear);
		recurringNonRecurring=costingRptRepository.findRecurringNonRecurring(regionId, planYear);
		budgetRecurNonRecur2324=costingRptRepository.findBudgetRecurringNonRecurring2324(regionId);
		expenditureRecurNonRecur2324=costingRptRepository.findExpexpenditureRecurNonRecur2324(regionId);

		List<ProposedCosting> listObj = new ArrayList<>();
		for (ProposedCosting obj : dataList) {
			if (obj != null && obj.getPhysicalQuantity() != null && obj.getUnitCost() != null && obj.getFinancialAmount() != null && obj.getPhysicalQuantity() != 0 && obj.getUnitCost() != 0 && obj.getFinancialAmount() != 0) {
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
		
		
		//return goaCostingReportPdf.downloadCostingReportPdf(planYear, groupedByFields, stateName, recurNonrecurFinAmtTot, stateTentive);
		
		//if(listObj !=null && listObj.size()>0)
			return stateCostingReportPdf.downloadCostingReportPdf(planYear, groupedByFields, regionName ,stateTentive,recurringNonRecurring,budgetRecurNonRecur2324,expenditureRecurNonRecur2324,majorComponentProposal);
		//else
			//return null;
		/*else if(regionType==2)
			return districtCostingReportPdf.downloadCostingReportPdf(planYear, groupedByFields, regionName);
		else
			return null;*/

	}
}
