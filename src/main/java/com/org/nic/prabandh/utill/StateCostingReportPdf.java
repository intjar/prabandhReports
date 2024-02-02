package com.org.nic.prabandh.utill;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.org.nic.prabandh.bean.MajorComponentProposal;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.bean.RecurringNonRecurring;
import com.org.nic.prabandh.bean.Spillover;
import com.org.nic.prabandh.constant.Constants;
import com.org.nic.prabandh.model.MastStatesTentative;

@Component
public class StateCostingReportPdf {
	DecimalFormat df = new DecimalFormat("0.00000");
	DecimalFormat dfWithoutZero = new DecimalFormat("#.##");
	DecimalFormat dfWithTwoDig = new DecimalFormat("0.00");
	
	SimpleDateFormat sdf = new SimpleDateFormat(Constants.META_DATA_DATE_FORMAT);

	// DecimalFormat df = new DecimalFormat("0.0000");
	void addUdiseLogo(Document document) {
		ImageData imageData;
		Image image = null;
		try {
			String filePath = ResourceUtils.getFile("classpath:udise-nic.png").getPath();
			imageData = ImageDataFactory.create(filePath);
			image = new Image(imageData);
			image.setFixedPosition(340, 10);
			document.add(image);
		} catch (MalformedURLException | FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public byte[] inputStreamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}

	@SuppressWarnings("resource")
	public byte[] addFooterAndPageNumbers(byte[] pdf, String regionName, String planYear) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));

		PdfWriter writer = new PdfWriter(baos);
		PdfDocument pdfDoc = new PdfDocument(reader, writer);
		int numberOfPages = pdfDoc.getNumberOfPages();
		// PdfFont font = PdfFontFactory.createFont();
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		for (int i = 1; i <= numberOfPages; i++) {
			PdfPage page = pdfDoc.getPage(i);
			Rectangle pageSize = page.getPageSize();
			PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

			String pageInfo = "Page no " + Integer.toString(pdfDoc.getPageNumber(page)) + " of " + numberOfPages;
			String formattedDate = sdf.format(new Date());
			
			new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(font).setFontSize(9).showTextAligned(pageInfo, pageSize.getWidth() / 2, 20, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
			/*.showTextAligned("Generated on " + formattedDate, 403, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0).setFontColor(new DeviceRgb(165,42,42))
			.showTextAligned("https://prabandh.education.gov.in", 403, 15, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0).setFontColor(new DeviceRgb(165,42,42));*/

			new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(font).setFontSize(9).setFontColor(new DeviceRgb(12, 49, 99))
					.showTextAligned("Generated on " + formattedDate, 650, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
					.showTextAligned("https://prabandh.education.gov.in", 650, 15, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);

			if (i > 1 && i<= 5) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10).setFontColor(new DeviceRgb(255, 0, 0))
				.showTextAligned("*All figures (In Lakhs)" , 700, 560, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			}
			
			if (i > 5) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10).setFontColor(new DeviceRgb(165, 42, 42))
				.showTextAligned("Budget Demand  - " + regionName, 37, 576, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
				.showTextAligned("F. Y. - " + planYear, 700, 576, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			}

			ImageData imageData;
			Image image = null;
			try {
				InputStream is = getClass().getResourceAsStream("/static/prabandh-nic.png");
				imageData = ImageDataFactory.create(inputStreamToByteArray(is));
				image = new Image(imageData);
				image.scaleAbsolute(200, 25);
				image.setFixedPosition(32, 6);
			} catch (Exception e) {
				e.printStackTrace();
			}

			PdfExtGState gstate = new PdfExtGState();
			PdfCanvas canvasImage = new PdfCanvas(page);
			canvasImage.saveState();
			canvasImage.setExtGState(gstate);
			try (Canvas canvas2 = new Canvas(canvasImage, pdfDoc, pageSize)) {
				canvas2.add(image);
			} catch (Exception e) {
				e.printStackTrace();
			}
			canvasImage.restoreState();
		}

		pdfDoc.close();
		return baos.toByteArray();
	}

	public ResponseEntity<?> downloadStateCostingReptPdf(String planYear, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> costingReportMap, String regionName,
			Optional<MastStatesTentative> stateTentive,
			List<MajorComponentProposal> majorComponentProposal,
			List<RecurringNonRecurring> statePlanList,List<RecurringNonRecurring> budgetRecurNonRecur2324,
			List<RecurringNonRecurring> expenditureRecurNonRecur2324, List<Spillover> spilloverList, List<MajorComponentProposal> majorCompoDetails) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PdfWriter write = new PdfWriter(byteArrayOutputStream);
		write.setSmartMode(true);

		PdfDocument pdfDoc = new PdfDocument(write);
		pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

		Document doc = new Document(pdfDoc);

		// first page paragraph----Start-----------
		Color paraFColor1 = new DeviceRgb(165, 42, 42);
		doc.add(CommonMethod.createParaGraphBold("Costing Sheet", 30f, 0f, 35, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("(Samagra Shiksha)", 0f, 0f, 40, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("of", 20f, 0f, 20, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold(regionName == null ? "" : regionName, 10f, 0f, 35, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold(planYear == null ? "" : planYear, 8f, 0f, 40, paraFColor1, null, TextAlignment.CENTER));

		doc.add(CommonMethod.createParaGraphBold("(Prepared by - " + regionName + ")", 10f, 0f, 15, new DeviceRgb(12, 49, 99), null, TextAlignment.CENTER));

		// first page paragraph---End------------

		Color paraFColor2 = new DeviceRgb(0, 0, 0);
		if (costingReportMap.size() > 1) {
			// 2nd page Summary at a Glance table----Start-----------
			doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			if (stateTentive != null && budgetRecurNonRecur2324 != null && expenditureRecurNonRecur2324 != null) {
				doc.add(CommonMethod.createParaGraphBold("Summary at a Glance", 0f, 0f, 12, paraFColor2, null, TextAlignment.CENTER));
				Table summaryGlance = getSummaryGlance(doc, planYear, stateTentive, budgetRecurNonRecur2324, expenditureRecurNonRecur2324);
				doc.add(summaryGlance);
			}

			if (budgetRecurNonRecur2324 != null && expenditureRecurNonRecur2324 != null) {
				doc.add(CommonMethod.createParaGraphBold("Budget Approved for F.Y. 2023-24 VS Anticipated Expenditure Details till 31st March 2024", 20f, 0f, 12, paraFColor2, null, TextAlignment.CENTER));
				Table tentativeProposed = getBudgetExpenditureBarChart(doc, planYear, budgetRecurNonRecur2324, expenditureRecurNonRecur2324);
				doc.add(tentativeProposed);
			}
			// 2nd page Summary at a Glance table----end-----------

			// 3rd page Expenditure Details table----Start-----------
			if (stateTentive != null && stateTentive.isPresent()) {
				doc.add(CommonMethod.createParaGraphBold("Tentative Outlay F.Y. 2024-25", 20f, 0f, 12, paraFColor2, null, TextAlignment.CENTER));
				Table tentativeProposed = getTentativeProposed(doc, stateTentive, planYear);
				doc.add(tentativeProposed);
			}

			if (spilloverList != null && spilloverList.size() > 0) {
				doc.add(CommonMethod.createParaGraphBold("Spillover", 30f, 0f, 12, paraFColor2, null, TextAlignment.CENTER));
				Table tableSpillover = getSpilloverTable(doc, planYear, spilloverList);
				doc.add(tableSpillover);
			}

			if (statePlanList != null && statePlanList.size() >0) {
				doc.add(CommonMethod.createParaGraphBold("State Plan (F.Y. " + planYear + ")", 30f, 0f, 12, paraFColor2, null, TextAlignment.CENTER));
				Table summaryGlance = getStatePlanFY(doc, planYear, stateTentive, statePlanList);
				doc.add(summaryGlance);
			}

			if (majorCompoDetails != null && majorCompoDetails.size() > 0) {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(CommonMethod.createParaGraphBold("Major Component wise Details", 30f, 0f, 15, paraFColor2, null, TextAlignment.CENTER));
				Table tableComponentDetails = getMajorCompoDetails(doc, majorCompoDetails, planYear);
				doc.add(tableComponentDetails);
			}
			// 3rd page Expenditure Details table----End-----------

			// 2nd page Tentative Proposed table----Start-----------

			if (majorComponentProposal != null && majorComponentProposal.size() > 0) {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(CommonMethod.createParaGraphBold("Major Component wise - State Plan (F.Y. " + planYear + ")", 20f, 0f, 12, paraFColor2, null, TextAlignment.CENTER));
				Table tentativeProposed = getMajorProposed(doc, majorComponentProposal, planYear);
				doc.add(tentativeProposed);
			}

			// 2nd page Expenditure Details table----End-----------

			// ----------------------------------------------------------------------------------

			// report data-----Start------------
			Table table = new Table(UnitValue.createPercentArray(new float[] { 1.5f, 1.5f, 1.5f, 3.2f, 1.0f, 1.0f, 1.0f }));
			table.setWidth(UnitValue.createPercentValue(100));
			for (Map.Entry<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> schemeEntry : costingReportMap.entrySet()) {
				Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue = schemeEntry.getValue();

				Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue555555 = costingReportMap.get(555555);
				Integer schemeKey = schemeEntry.getKey();

				if (schemeKey == 555555)
					break;

				Table tableDetailsReport = getTableReportData(doc, planYear, costingReportMap, schemeKey, schemeValue, schemeValue555555, (costingReportMap.size() - 1));
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(tableDetailsReport);
			}
			// report data-----end------------
		} else {
			doc.add(CommonMethod.createParaGraphBold("Data not avaliable", 50f, 0f, 25, paraFColor2, null, TextAlignment.CENTER));
		}

		doc.close();
		byteArrayOutputStream.close();

		byte[] bytes = byteArrayOutputStream.toByteArray();
		try {
			bytes = addFooterAndPageNumbers(bytes, regionName, planYear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=Costing Sheet for " + regionName + ".pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bytes);
	}

	
	
	
	
	
	private Table getMajorCompoDetails(Document doc, List<MajorComponentProposal> majorComponentProposal, String planYear) throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] {0.5f,2f  ,1f ,1f,1f ,1f,1f ,1f,1f ,1.1f,1f}));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();
		
		try {
			Color bgColorgTotal = new DeviceRgb(227,237,243);
			Color bgColorgTotalPercent = new DeviceRgb(230, 255, 230);
			Color bgColor = new DeviceRgb(37, 132, 198);
			
			CommonMethod.createDataCellTableHead(table, "SNo", 1, 3, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Major Component", 1, 3, 10f,bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Figures for F.Y. 2023-24", 9, 1, 10f,bgColor, TextAlignment.CENTER);
			
			CommonMethod.createDataCellTableHead(table, "Budget Approvals" , 3, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Expenditure till 31st March 2024" , 3, 1, 10f,bgColor, TextAlignment.CENTER);
			
			//CommonMethod.createDataCellTableHead(table, "Spillover (Cumulative)" , 2, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Expenditure in % against Approval" , 3, 1, 10f,bgColor, TextAlignment.CENTER);
			
			
			CommonMethod.createDataCellTableHead(table, "Recurring" , 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring" , 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f,bgColor, TextAlignment.CENTER);
			
			CommonMethod.createDataCellTableHead(table, "Recurring" , 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring" , 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f,bgColor, TextAlignment.CENTER);
			
			
			//CommonMethod.createDataCellTableHead(table, "Budget" , 1, 1, 10f,bgColor, TextAlignment.CENTER);
			//CommonMethod.createDataCellTableHead(table, "Expenditure" , 1, 1, 10f,bgColor, TextAlignment.CENTER);
			
			CommonMethod.createDataCellTableHead(table, "Recurring" , 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring" , 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total" , 1, 1, 10f,bgColor, TextAlignment.CENTER);

			
			int sno=1;float dataFontSize=9;
			for (MajorComponentProposal listObj : majorComponentProposal) {
				if(listObj.getMajorComponentName()!=null) {
					CommonMethod.createDataCellBold(table, sno+"", 1, 1, dataFontSize,null,TextAlignment.CENTER);
					

					if(!listObj.getMajorComponentName().equals("Total")) {
						CommonMethod.createDataCellBold(table, listObj.getMajorComponentName(), 1, 1,dataFontSize,null,TextAlignment.LEFT);
						
						CommonMethod.createDataCell(table, listObj.getApprovedBudgetRecurring()==null?"":df.format(listObj.getApprovedBudgetRecurring()), 1, 1, dataFontSize,null,TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getApprovedBudgetNonRecurring()==null?"":df.format(listObj.getApprovedBudgetNonRecurring()), 1, 1, dataFontSize,null,TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getTotApprovedBudget()==null?"":df.format(listObj.getTotApprovedBudget()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);

						CommonMethod.createDataCell(table, listObj.getExpenditureRecurring_31()==null?"":df.format(listObj.getExpenditureRecurring_31()), 1, 1, dataFontSize,null,TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getExpenditureNonRecurring_31()==null?"":df.format(listObj.getExpenditureNonRecurring_31()), 1, 1, dataFontSize,null,TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getTotExpenditure()==null?"":df.format(listObj.getTotExpenditure()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);
						
						//CommonMethod.createDataCell(table, listObj.getSpillOverApprovalBudget23()==null?"":df.format(listObj.getSpillOverApprovalBudget23()), 1, 1, dataFontSize,null,TextAlignment.RIGHT);
						//CommonMethod.createDataCell(table, listObj.getAnticipatedExpenditureSpillOver()==null?"":df.format(listObj.getAnticipatedExpenditureSpillOver()), 1, 1, dataFontSize,null,TextAlignment.RIGHT);
						
						
						CommonMethod.createDataCell(table, listObj.getApprovedBudgetRecurring()==0?dfWithTwoDig.format(0):dfWithTwoDig.format(listObj.getExpenditureRecurring_31()/listObj.getApprovedBudgetRecurring()*100), 1, 1, dataFontSize,bgColorgTotalPercent,TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getApprovedBudgetNonRecurring()==0?dfWithTwoDig.format(0):dfWithTwoDig.format(listObj.getExpenditureNonRecurring_31()/listObj.getApprovedBudgetNonRecurring()*100), 1, 1, dataFontSize,bgColorgTotalPercent,TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getTotApprovedBudget()==0?dfWithTwoDig.format(0):dfWithTwoDig.format(listObj.getTotExpenditure()/listObj.getTotApprovedBudget()*100), 1, 1, dataFontSize,bgColorgTotalPercent,TextAlignment.RIGHT);
					
					}else {
						CommonMethod.createDataCellBold(table, listObj.getMajorComponentName(), 1, 1,dataFontSize,bgColorgTotal,TextAlignment.LEFT);
						
						CommonMethod.createDataCellBold(table, listObj.getApprovedBudgetRecurring()==null?"":df.format(listObj.getApprovedBudgetRecurring()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getApprovedBudgetNonRecurring()==null?"":df.format(listObj.getApprovedBudgetNonRecurring()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getTotApprovedBudget()==null?"":df.format(listObj.getTotApprovedBudget()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);

						CommonMethod.createDataCellBold(table, listObj.getExpenditureRecurring_31()==null?"":df.format(listObj.getExpenditureRecurring_31()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getExpenditureNonRecurring_31()==null?"":df.format(listObj.getExpenditureNonRecurring_31()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getTotExpenditure()==null?"":df.format(listObj.getTotExpenditure()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);
						
						//CommonMethod.createDataCellBold(table, listObj.getSpillOverApprovalBudget23()==0?dfWithTwoDig.format(0):df.format(listObj.getSpillOverApprovalBudget23()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);
						//CommonMethod.createDataCellBold(table, listObj.getAnticipatedExpenditureSpillOver()==null?"":df.format(listObj.getAnticipatedExpenditureSpillOver()), 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);

						CommonMethod.createDataCellBold(table, listObj.getApprovedBudgetRecurring()==0?dfWithTwoDig.format(0):dfWithTwoDig.format(listObj.getExpenditureRecurring_31()/listObj.getApprovedBudgetRecurring()*100), 1, 1, dataFontSize,bgColorgTotalPercent,TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getApprovedBudgetNonRecurring()==0?dfWithTwoDig.format(0):dfWithTwoDig.format(listObj.getExpenditureNonRecurring_31()/listObj.getApprovedBudgetNonRecurring()*100), 1, 1, dataFontSize,bgColorgTotalPercent,TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getTotApprovedBudget()==0?dfWithTwoDig.format(0):dfWithTwoDig.format(listObj.getTotExpenditure()/listObj.getTotApprovedBudget()*100), 1, 1, dataFontSize,bgColorgTotalPercent,TextAlignment.RIGHT);
					}
					
					}
				sno++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return table;
			
		}
		return table;
	}

	

	private Table getSpilloverTable(Document doc, String planYear, List<Spillover> spilloverList) throws IOException {
		Table table = new Table(UnitValue.createPercentArray(new float[] { 0.5f,1.5f, 1f, 1f, 1f}));
		table.setWidth(UnitValue.createPercentValue(100));

		try {
			Color bgColor = new DeviceRgb(37, 132, 198);
			Color bgColorgTotal = new DeviceRgb(227, 237, 243);
			Color bgColorgSpillBal = new DeviceRgb(242, 230, 255);
			
			CommonMethod.createDataCellTableHead(table, "SNo", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Particulars ", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Spillover Approval ", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Spillover Expenditure ", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Spillover Balance", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			
			int sno = 1, dataFontSize = 9;
			Double totSpillApp=0d,totSpillExp=0d,totBalance = 0d;
			for (Spillover listObj : spilloverList) {
				if(listObj.getSchemeName() !=null) {
					
					if (listObj.getSpillOverApproval23() !=null) {
						totSpillApp +=listObj.getSpillOverApproval23();
					}if (listObj.getAnticipatedExpenditureSpillOver() !=null) {
						totSpillExp +=listObj.getAnticipatedExpenditureSpillOver();
					}
					
					Double balance=0d;
					if (listObj.getSpillOverApproval23() !=null && listObj.getAnticipatedExpenditureSpillOver() !=null) {
						balance =(listObj.getSpillOverApproval23()-listObj.getAnticipatedExpenditureSpillOver());
					}
					totBalance +=balance;
			
					CommonMethod.createDataCellBold(table, sno + "", 1, 1, dataFontSize, null, TextAlignment.CENTER);
					CommonMethod.createDataCellBold(table, listObj.getSchemeName(), 1, 1, dataFontSize, null, TextAlignment.LEFT);
					
					CommonMethod.createDataCell(table, listObj.getSpillOverApproval23()==null?"":df.format(listObj.getSpillOverApproval23()), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, listObj.getAnticipatedExpenditureSpillOver()==null?"":df.format(listObj.getAnticipatedExpenditureSpillOver()), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, df.format(balance), 1, 1, dataFontSize, bgColorgSpillBal, TextAlignment.RIGHT);
					
					sno++;
				}
			}
			
			CommonMethod.createDataCellBold(table, sno + "", 1, 1, dataFontSize, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Total", 1, 1, dataFontSize, null, TextAlignment.LEFT);

			CommonMethod.createDataCellBold(table, df.format(totSpillApp), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(totSpillExp), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(totBalance), 1, 1, dataFontSize, bgColorgSpillBal, TextAlignment.RIGHT);
			
		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}
		return table;
	}
	
	private Table getStatePlanFY(Document doc, String planYear, Optional<MastStatesTentative> stateTentive, List<RecurringNonRecurring> statePlanList) throws IOException {
		float[] columnWidths = { .5f, 2.3f, 1.5f, 1.5f, 1f };
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));

		try {
			Color bgColor = new DeviceRgb(37, 132, 198);
			CommonMethod.createDataCellTableHead(table, "SNo", 1, 2, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Particulars", 1, 2, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "State Plan F.Y. " + planYear, 3, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non-Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			Double elementaryRec = 0d, elementaryNonRec = 0d, elementaryRecNonRecTot = 0d;
			Double secondaryRec = 0d, secondaryNonRec = 0d, secondaryRecNonRecTot = 0d;
			Double teacherRec = 0d, teacherNonRec = 0d, teacherRecNonRecTot = 0d;
			for (RecurringNonRecurring obj : statePlanList) {
				if (obj.getSchemeId() != null && obj.getSchemeId() == 1) {
					if (obj.getRecuring() != null)
						elementaryRec = obj.getRecuring();
					if (obj.getNoRecuring() != null)
						elementaryNonRec = obj.getNoRecuring();
					if (obj.getTotal() != null)
						elementaryRecNonRecTot = obj.getTotal();
				}
				if (obj.getSchemeId() != null && obj.getSchemeId() == 2) {
					if (obj.getRecuring() != null)
						secondaryRec = obj.getRecuring();
					if (obj.getNoRecuring() != null)
						secondaryNonRec = obj.getNoRecuring();
					if (obj.getTotal() != null)
						secondaryRecNonRecTot = obj.getTotal();
				}
				if (obj.getSchemeId() != null && obj.getSchemeId() == 3) {
					if (obj.getRecuring() != null)
						teacherRec = obj.getRecuring();
					if (obj.getNoRecuring() != null)
						teacherNonRec = obj.getNoRecuring();
					if (obj.getTotal() != null)
						teacherRecNonRecTot = obj.getTotal();
				}
			}
			Double grantTotalRecur = (elementaryRec + secondaryRec + teacherRec);
			Double grantTotalNonrecur = (elementaryNonRec + secondaryNonRec + teacherNonRec);
			Double grantTotaltotalRecurNonrecur = grantTotalRecur + grantTotalNonrecur;

			
			Color bgColorgTotal = new DeviceRgb(227,237,243);
			Color bgColorgTotalPercent = new DeviceRgb(230, 255, 230);
			
			CommonMethod.createDataCellBold(table, "1", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Elementary Education", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCell(table, df.format(elementaryRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(elementaryNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(elementaryRecNonRecTot), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, "2", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Secondary Education", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCell(table, df.format(secondaryRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(secondaryNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(secondaryRecNonRecTot), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, "3", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Teacher Education", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCell(table, df.format(teacherRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(teacherNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(teacherRecNonRecTot), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, "4", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Grand Total", 1, 1, 9, bgColorgTotal, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, df.format(grantTotalRecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(grantTotalNonrecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(grantTotaltotalRecurNonrecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);

			Double parcentCentral = stateTentive.get().getCenterSharePercent();
			Double parcentState = (100 - stateTentive.get().getCenterSharePercent());

			
			CommonMethod.createDataCellBold(table, "5", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Central Share(" + parcentCentral + "%)", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, "", 2, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, dfWithTwoDig.format(grantTotaltotalRecurNonrecur * (parcentCentral / 100)), 1, 1, 9, bgColorgTotalPercent, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, "6", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "State Share(" + parcentState + "%)", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, "", 2, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, dfWithTwoDig.format(grantTotaltotalRecurNonrecur * (parcentState / 100)), 1, 1, 9, bgColorgTotalPercent, TextAlignment.RIGHT);

		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}

		return table;

	}

	private Table getBudgetExpenditureBarChart(Document doc, String planYear, List<RecurringNonRecurring> budgetRecurNonRecur2324, List<RecurringNonRecurring> expenditureRecurNonRecur2324) throws IOException {
		Table table = new Table(UnitValue.createPercentArray(new float[] {1f,1f}));
		
		try {
			Double budgetElementaryRec = 0d, budgetElementaryNonRec = 0d, budgetElementaryRecNonRecTot = 0d;
			Double budgetSecondaryRec = 0d, budgetSecondaryNonRec = 0d, budgetSecondaryRecNonRecTot = 0d;
			Double budgetTeacherRec = 0d, budgetTeacherNonRec = 0d, budgetTeacherRecNonRecTot = 0d;
			for (RecurringNonRecurring bObj : budgetRecurNonRecur2324) {
				if (bObj.getSchemeId() != null && bObj.getSchemeId() == 1) {
					if (bObj.getRecuring() != null)
						budgetElementaryRec = bObj.getRecuring();
					if (bObj.getNoRecuring() != null)
						budgetElementaryNonRec = bObj.getNoRecuring();
					if (bObj.getTotal() != null)
						budgetElementaryRecNonRecTot = bObj.getTotal();
				}
				if (bObj.getSchemeId() != null && bObj.getSchemeId() == 2) {
					if (bObj.getRecuring() != null)
						budgetSecondaryRec = bObj.getRecuring();
					if (bObj.getNoRecuring() != null)
						budgetSecondaryNonRec = bObj.getNoRecuring();
					if (bObj.getTotal() != null)
						budgetSecondaryRecNonRecTot = bObj.getTotal();
				}
				if (bObj.getSchemeId() != null && bObj.getSchemeId() == 3) {
					if (bObj.getRecuring() != null)
						budgetTeacherRec = bObj.getRecuring();
					if (bObj.getNoRecuring() != null)
						budgetTeacherNonRec = bObj.getNoRecuring();
					if (bObj.getTotal() != null)
						budgetTeacherRecNonRecTot = bObj.getTotal();
				}
			}
			/*		Double budgetGrantTotalRecur = (budgetElementaryRec + budgetSecondaryRec + budgetTeacherRec);
					Double budgetGrantTotalNonrecur = (budgetElementaryNonRec + budgetSecondaryNonRec + budgetTeacherNonRec);
					Double budgetGrantTotaltotalRecurNonrecur = budgetGrantTotalRecur + budgetGrantTotalNonrecur;*/
			
			
			Double expenditureElementaryRec = 0d, expenditureElementaryNonRec = 0d, expenditureElementaryRecNonRecTot = 0d;
			Double expenditureSecondaryRec = 0d, expenditureSecondaryNonRec = 0d, expenditureSecondaryRecNonRecTot = 0d;
			Double expenditureTeacherRec = 0d, expenditureTeacherNonRec = 0d, expenditureTeacherRecNonRecTot = 0d;
			for (RecurringNonRecurring eObj : expenditureRecurNonRecur2324) {
				if (eObj.getSchemeId() != null && eObj.getSchemeId() == 1) {
					if (eObj.getRecuring() != null)
						expenditureElementaryRec = eObj.getRecuring();
					if (eObj.getNoRecuring() != null)
						expenditureElementaryNonRec = eObj.getNoRecuring();
					if (eObj.getTotal() != null)
						expenditureElementaryRecNonRecTot = eObj.getTotal();
				}
				if (eObj.getSchemeId() != null && eObj.getSchemeId() == 2) {
					if (eObj.getRecuring() != null)
						expenditureSecondaryRec = eObj.getRecuring();
					if (eObj.getNoRecuring() != null)
						expenditureSecondaryNonRec = eObj.getNoRecuring();
					if (eObj.getTotal() != null)
						expenditureSecondaryRecNonRecTot = eObj.getTotal();
				}
				if (eObj.getSchemeId() != null && eObj.getSchemeId() == 3) {
					if (eObj.getRecuring() != null)
						expenditureTeacherRec = eObj.getRecuring();
					if (eObj.getNoRecuring() != null)
						expenditureTeacherNonRec = eObj.getNoRecuring();
					if (eObj.getTotal() != null)
						expenditureTeacherRecNonRecTot = eObj.getTotal();
				}
			}
			
			/*Double expenditureGrantTotalRecur = (expenditureElementaryRec + expenditureSecondaryRec + expenditureTeacherRec);
			Double expenditureGrantTotalNonrecur = (expenditureElementaryNonRec + expenditureSecondaryNonRec + expenditureTeacherNonRec);
			Double expenditureGrantTotaltotalRecurNonrecur = expenditureGrantTotalRecur + expenditureGrantTotalNonrecur;*/
			

			final DefaultCategoryDataset barDataBudExpendRecNonRec = new DefaultCategoryDataset();

			barDataBudExpendRecNonRec.addValue(budgetElementaryRec, "Recurring-Bud", "Elementary");
			barDataBudExpendRecNonRec.addValue(expenditureElementaryRec, "Recurring-Exp", "Elementary");
			barDataBudExpendRecNonRec.addValue(budgetElementaryNonRec, "NonRecurring-Bud", "Elementary");
			barDataBudExpendRecNonRec.addValue(expenditureElementaryNonRec, "NonRecurring-Exp", "Elementary");

			barDataBudExpendRecNonRec.addValue(budgetSecondaryRec, "Recurring-Bud", "Secondary");
			barDataBudExpendRecNonRec.addValue(expenditureSecondaryRec, "Recurring-Exp", "Secondary");
			barDataBudExpendRecNonRec.addValue(budgetSecondaryNonRec, "NonRecurring-Bud", "Secondary");
			barDataBudExpendRecNonRec.addValue(expenditureSecondaryNonRec, "NonRecurring-Exp", "Secondary");

			barDataBudExpendRecNonRec.addValue(budgetTeacherRec, "Recurring-Bud", "Teacher");
			barDataBudExpendRecNonRec.addValue(expenditureTeacherRec, "Recurring-Exp", "Teacher");
			barDataBudExpendRecNonRec.addValue(budgetTeacherNonRec, "NonRecurring-Bud", "Teacher");
			barDataBudExpendRecNonRec.addValue(expenditureTeacherNonRec, "NonRecurring-Exp", "Teacher");
			
			
			Object[] colorArr= {(java.awt.Color.GREEN),java.awt.Color.RED,java.awt.Color.cyan,java.awt.Color.MAGENTA};
			ImageData barImageDataBudget = DrawChartImage.generateMultiBarChart(barDataBudExpendRecNonRec,colorArr,4, "Budget(Recurring/Non-Recurring) vs Expenditure(Recurring/Non-Recurring)", "Particulars", "figures (In Lakhs)", 15, 12, 12);
			Image barChartImageBudget = new Image(barImageDataBudget);
			//barChartImageBudget.scaleAbsolute(250, 250);
			barChartImageBudget.setAutoScale(true);
			//barChartImage.setFixedPosition(310, 250);
			Cell cellBarLeft = new Cell(1,1);
			cellBarLeft.add(barChartImageBudget);
			table.addCell(cellBarLeft);
			
			

			final DefaultCategoryDataset barDataBudExpendTot = new DefaultCategoryDataset();
			barDataBudExpendTot.addValue(budgetElementaryRecNonRecTot, "Total-Bud", "Elementary");
			barDataBudExpendTot.addValue(expenditureElementaryRecNonRecTot, "Total-Exp", "Elementary");

			barDataBudExpendTot.addValue(budgetSecondaryRecNonRecTot, "Total-Bud", "Secondary");
			barDataBudExpendTot.addValue(expenditureSecondaryRecNonRecTot, "Total-Exp", "Secondary");

			barDataBudExpendTot.addValue(budgetTeacherRecNonRecTot, "Total-Bud", "Teacher");
			barDataBudExpendTot.addValue(expenditureTeacherRecNonRecTot, "Total-Exp", "Teacher");

			
			
			ImageData barImageDataExpen = DrawChartImage.generateMultiBarChart(barDataBudExpendTot,colorArr,3, "Budget(Total) vs Expenditure(Total)", "Particulars", "figures (In Lakhs)", 15, 12, 12);
			Image barChartImageExpen = new Image(barImageDataExpen);
			barChartImageExpen.setAutoScale(true);
			//barChartImageExpen.scaleAbsolute(250, 250);
			//barChartImage.setFixedPosition(310, 250);
			Cell cellBarRight = new Cell(1,1);
			cellBarRight.add(barChartImageExpen);
			table.addCell(cellBarRight);
		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}
		
		return table;
	}

	private Table getMajorProposed(Document doc, List<MajorComponentProposal> majorComponentProposal, String planYear) throws IOException {
		
		Table mainTable = new Table(UnitValue.createPercentArray(new float[] { 1f,1f }));
		Table table = new Table(UnitValue.createPercentArray(new float[] {.5f, 2f,1f,1f, 1.2f, 1f}));
		table.setWidth(UnitValue.createPercentValue(100));
		
		try {
			Color bgColorgTotal = new DeviceRgb(227,237,243);
			Color bgColorgTotalPercent = new DeviceRgb(230, 255, 230);
			

			Color bgColor = new DeviceRgb(37, 132, 198);
			CommonMethod.createDataCellTableHead(table, "SNo", 1, 2, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Major Component", 1, 2, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Proposal F.Y. "+planYear, 4, 1, 10f,bgColor, TextAlignment.CENTER);
			
			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring", 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "% of Total", 1, 1, 10f,bgColor, TextAlignment.CENTER);
			
			
			Double financialAmountTot=0d;
			for (MajorComponentProposal listObj : majorComponentProposal) {
				if(listObj.getMajorComponentName() !=null && listObj.getMajorComponentName().equals("Total")) {
					double financialAmount=0d;
					if(listObj.getFinancialAmount() !=null)
						financialAmount=listObj.getFinancialAmount();
					financialAmountTot=financialAmount;
				}
			}

			
			Map<String, Double> dataSet = new TreeMap<>();
			int sno=1,dataFontSize=9;
			Double recProposed=0.0d,nonRecProposed=0.0d,totProposed=0.0d;
			Double recProposedTot=0.0d,nonRecProposedTot=0.0d;
			for (MajorComponentProposal listObj : majorComponentProposal) {
				
				if(listObj.getRecuringNonrecuring() !=null && listObj.getRecuringNonrecuring().equals("R")) {
					recProposed=listObj.getFinancialAmount();
					recProposedTot=recProposedTot+recProposed;
				}
				else if(listObj.getRecuringNonrecuring() !=null && listObj.getRecuringNonrecuring().equals("NR")) {
					nonRecProposed=listObj.getFinancialAmount();
					nonRecProposedTot=nonRecProposedTot+nonRecProposed;
				}
				else if(listObj.getRecuringNonrecuring() !=null && listObj.getRecuringNonrecuring().equals("Total")) {
					totProposed=listObj.getFinancialAmount();
				}
				
				if (listObj.getMajorComponentName() != null) {
					if (!listObj.getMajorComponentName().equals("Total")) {
						if (listObj.getRecuringNonrecuring().equals("Total")) {
							CommonMethod.createDataCellBold(table, sno + "", 1, 1, dataFontSize, null, TextAlignment.CENTER);
							CommonMethod.createDataCellBold(table, listObj.getMajorComponentName(), 1, 1, dataFontSize, null, TextAlignment.LEFT);

							CommonMethod.createDataCell(table, df.format(recProposed), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, df.format(nonRecProposed), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, df.format(totProposed), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, financialAmountTot==0?"": dfWithTwoDig.format((totProposed == null ? 0 : totProposed) / financialAmountTot * 100), 1, 1, dataFontSize, bgColorgTotalPercent,
									TextAlignment.RIGHT);
							sno++;
							dataSet.put(listObj.getMajorComponentName(), Double.parseDouble(df.format(totProposed)));
							
							recProposed=0.0d;nonRecProposed=0.0d;totProposed=0.0d;
						}
					}

					if (listObj.getMajorComponentName().equals("Total")) {
						if (listObj.getRecuringNonrecuring().equals("Total")) {
							CommonMethod.createDataCellBold(table, sno + "", 1, 1, dataFontSize, null, TextAlignment.CENTER);
							CommonMethod.createDataCellBold(table, listObj.getMajorComponentName(), 1, 1, dataFontSize, null, TextAlignment.LEFT);

							CommonMethod.createDataCellBold(table, df.format(recProposedTot), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCellBold(table, df.format(nonRecProposedTot), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCellBold(table, df.format(financialAmountTot), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCellBold(table, "", 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);
						}
					}
				}

			}
			mainTable.addCell(table);
			ImageData pieImageData = DrawChartImage.generatePieChart(dataSet, "Component wise Proposal (figures (In Lakhs))", 18, 14, 12,30);
			Image pieChartimage = new Image(pieImageData);
			//pieChartimage.scaleAbsolute(250, 250);
			pieChartimage.setAutoScale(true);
			//pieChartimage.scaleToFit(100, 100);
			//pieChartimage.setFixedPosition(35, 550);
			
			Cell cellPie = new Cell(1,1);
			cellPie.add(pieChartimage);
			mainTable.addCell(cellPie);
		} catch (Exception e) {
			e.printStackTrace();
			return mainTable;
		}
		
		return mainTable;
	}

	
	
	private Table getSummaryGlance(Document doc,String planYear,
			Optional<MastStatesTentative> stateTentive,
			List<RecurringNonRecurring> budgetRecurNonRecur2324,
			List<RecurringNonRecurring> expenditureRecurNonRecur2324) throws IOException {

		float[] columnWidths = { .5f, 2.3f, 1.5f, 1.5f, 1f, 1.5f, 1.5f, 1f };
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));

		try {
			Color bgColor = new DeviceRgb(37, 132, 198);
			CommonMethod.createDataCellTableHead(table, "SNo", 1, 2, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Particulars", 1, 2, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Budget Approved for F.Y. 2023-24", 3, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Expenditure till 31st March 2024", 3, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non-Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non-Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			Double budgetElementaryRec = 0d, budgetElementaryNonRec = 0d, budgetElementaryRecNonRecTot = 0d;
			Double budgetSecondaryRec = 0d, budgetSecondaryNonRec = 0d, budgetSecondaryRecNonRecTot = 0d;
			Double budgetTeacherRec = 0d, budgetTeacherNonRec = 0d, budgetTeacherRecNonRecTot = 0d;
			for (RecurringNonRecurring bObj : budgetRecurNonRecur2324) {
				if (bObj.getSchemeId() != null && bObj.getSchemeId() == 1) {
					if (bObj.getRecuring() != null)
						budgetElementaryRec = bObj.getRecuring();
					if (bObj.getNoRecuring() != null)
						budgetElementaryNonRec = bObj.getNoRecuring();
					if (bObj.getTotal() != null)
						budgetElementaryRecNonRecTot = bObj.getTotal();
				}
				if (bObj.getSchemeId() != null && bObj.getSchemeId() == 2) {
					if (bObj.getRecuring() != null)
						budgetSecondaryRec = bObj.getRecuring();
					if (bObj.getNoRecuring() != null)
						budgetSecondaryNonRec = bObj.getNoRecuring();
					if (bObj.getTotal() != null)
						budgetSecondaryRecNonRecTot = bObj.getTotal();
				}
				if (bObj.getSchemeId() != null && bObj.getSchemeId() == 3) {
					if (bObj.getRecuring() != null)
						budgetTeacherRec = bObj.getRecuring();
					if (bObj.getNoRecuring() != null)
						budgetTeacherNonRec = bObj.getNoRecuring();
					if (bObj.getTotal() != null)
						budgetTeacherRecNonRecTot = bObj.getTotal();
				}
			}

			Double budgetGrantTotalRecur = (budgetElementaryRec + budgetSecondaryRec + budgetTeacherRec);
			Double budgetGrantTotalNonrecur = (budgetElementaryNonRec + budgetSecondaryNonRec + budgetTeacherNonRec);
			Double budgetGrantTotaltotalRecurNonrecur = budgetGrantTotalRecur + budgetGrantTotalNonrecur;

			Double expenditureElementaryRec = 0d, expenditureElementaryNonRec = 0d, expenditureElementaryRecNonRecTot = 0d;
			Double expenditureSecondaryRec = 0d, expenditureSecondaryNonRec = 0d, expenditureSecondaryRecNonRecTot = 0d;
			Double expenditureTeacherRec = 0d, expenditureTeacherNonRec = 0d, expenditureTeacherRecNonRecTot = 0d;
			for (RecurringNonRecurring eObj : expenditureRecurNonRecur2324) {
				if (eObj.getSchemeId() != null && eObj.getSchemeId() == 1) {
					if (eObj.getRecuring() != null)
						expenditureElementaryRec = eObj.getRecuring();
					if (eObj.getNoRecuring() != null)
						expenditureElementaryNonRec = eObj.getNoRecuring();
					if (eObj.getTotal() != null)
						expenditureElementaryRecNonRecTot = eObj.getTotal();
				}
				if (eObj.getSchemeId() != null && eObj.getSchemeId() == 2) {
					if (eObj.getRecuring() != null)
						expenditureSecondaryRec = eObj.getRecuring();
					if (eObj.getNoRecuring() != null)
						expenditureSecondaryNonRec = eObj.getNoRecuring();
					if (eObj.getTotal() != null)
						expenditureSecondaryRecNonRecTot = eObj.getTotal();
				}
				if (eObj.getSchemeId() != null && eObj.getSchemeId() == 3) {
					if (eObj.getRecuring() != null)
						expenditureTeacherRec = eObj.getRecuring();
					if (eObj.getNoRecuring() != null)
						expenditureTeacherNonRec = eObj.getNoRecuring();
					if (eObj.getTotal() != null)
						expenditureTeacherRecNonRecTot = eObj.getTotal();
				}
			}

			Color bgColorgTotal = new DeviceRgb(227, 237, 243);

			Double expenditureGrantTotalRecur = (expenditureElementaryRec + expenditureSecondaryRec + expenditureTeacherRec);
			Double expenditureGrantTotalNonrecur = (expenditureElementaryNonRec + expenditureSecondaryNonRec + expenditureTeacherNonRec);
			Double expenditureGrantTotaltotalRecurNonrecur = expenditureGrantTotalRecur + expenditureGrantTotalNonrecur;

			CommonMethod.createDataCellBold(table, "1", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Elementary Education", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCell(table, df.format(budgetElementaryRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(budgetElementaryNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(budgetElementaryRecNonRecTot), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureElementaryRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureElementaryNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureElementaryRecNonRecTot), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, "2", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Secondary Education", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCell(table, df.format(budgetSecondaryRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(budgetSecondaryNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(budgetSecondaryRecNonRecTot), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureSecondaryRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureSecondaryNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureSecondaryRecNonRecTot), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, "3", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Teacher Education", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCell(table, df.format(budgetTeacherRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(budgetTeacherNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(budgetTeacherRecNonRecTot), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureTeacherRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureTeacherNonRec), 1, 1, 9, null, TextAlignment.RIGHT);
			CommonMethod.createDataCell(table, df.format(expenditureTeacherRecNonRecTot), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, "4", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Grand Total", 1, 1, 9, bgColorgTotal, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, df.format(budgetGrantTotalRecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(budgetGrantTotalNonrecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(budgetGrantTotaltotalRecurNonrecur), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(expenditureGrantTotalRecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(expenditureGrantTotalNonrecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(expenditureGrantTotaltotalRecurNonrecur), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);
		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}
		return table;
	}


	private Table getTentativeProposed(Document doc, Optional<MastStatesTentative> stateTentive, String planYear) throws IOException {
		float[] columnWidths = { 2.5f, 1.5f, 2.5f, 1.5f, 1.5f, 1.5f};
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));
	
		try {
			if (stateTentive.get() != null) {
				Color bgColorgTotal = new DeviceRgb(227, 237, 243);

				Double parcentCentral = (stateTentive.get().getCenterSharePercent()!=null?stateTentive.get().getCenterSharePercent():0);
				Double reParcentState = (100 - (stateTentive.get().getCenterSharePercent() !=null?stateTentive.get().getCenterSharePercent():0));

				Color bgColor = new DeviceRgb(37, 132, 198);
				CommonMethod.createDataCellTableHead(table, "Central Share(" + parcentCentral + "%)", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellBold(table,stateTentive.get().getTentativeCentralShare()==null?"": df.format(stateTentive.get().getTentativeCentralShare()), 1, 1, 9, null, TextAlignment.RIGHT);

				CommonMethod.createDataCellTableHead(table, "State Share(" + reParcentState + "%)", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellBold(table, stateTentive.get().getTentativeStateShare()==null?"":df.format(stateTentive.get().getTentativeStateShare()), 1, 1, 9, null, TextAlignment.RIGHT);

				CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellBold(table, stateTentive.get().getTentativeTotalEstimates()==null?"":df.format(stateTentive.get().getTentativeTotalEstimates()), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}
		return table;
	}
	
	
	
	// ---CostingReportStateWise start here----------------------
	private Table getTableReportData(Document doc,String planYear, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> groupedByFiveAttributes, Integer schemeKey,
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue555555,int mainMapSize)
			throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] {1.4f,1.4f,1.4f,2.1f, 0.4f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1f}));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();
		
		
		try {
			float fHeader=8f,fData=7.5f,fGrandTotal=7.5f;
			if (schemeKey != 555555) {
				Color bgColorhead = new DeviceRgb(37, 132, 198);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Major Component", 1, 2, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Sub Component", 1, 2, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Activity", 1, 2, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Sub Activity", 1, 2, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "R/ NR", 1, 2, fHeader,bgColorhead, TextAlignment.CENTER);

				CommonMethod.createDataCellTableHeadEveryPage(table, "Budget Approved for F.Y. 2023-24 (In Lakhs)", 4, 1, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "State Plan F.Y. "+planYear, 3, 1, 10f,bgColorhead, TextAlignment.CENTER);
				
				
				
				
				CommonMethod.createDataCellTableHeadEveryPage(table, "Approved Budget", 1, 1, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Fresh Exp.", 1, 1, fHeader,bgColorhead, TextAlignment.CENTER);
				
				CommonMethod.createDataCellTableHeadEveryPage(table, "Spillover Cumu.", 1, 1, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Spillover Exp.", 1, 1, fHeader,bgColorhead, TextAlignment.CENTER);
				
				CommonMethod.createDataCellTableHeadEveryPage(table, "Phy Qty", 1, 1, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Unit Cost", 1, 1, fHeader,bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Amount (In Lakhs)", 1, 1, fHeader,bgColorhead, TextAlignment.CENTER);
				
				
				
			}

			int loopTimes = 1;
			if (schemeKey == mainMapSize)
				loopTimes = 2;

			for (int i = 1; i <= loopTimes; i++) {
				if (schemeKey == mainMapSize && i == 2) {
					schemeValue = schemeValue555555;
				}

				Boolean schemeFlag = true;
				int majaorSrNo = 0;
				for (Map.Entry<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> majorEntry : schemeValue.entrySet()) {
					Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>> majorValue = majorEntry.getValue();
					majaorSrNo++;

					// calculate only major component size-----start---------------
					int majorCompSize = 0;
					for (Map.Entry<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>> innerEntry : majorValue.entrySet()) {
						Map<Integer, Map<Integer, List<ProposedCosting>>> innermostValue = innerEntry.getValue();
						for (Map.Entry<Integer, Map<Integer, List<ProposedCosting>>> deepestEntry : innermostValue.entrySet()) {
							Map<Integer, List<ProposedCosting>> deepestValue = deepestEntry.getValue();
							for (Map.Entry<Integer, List<ProposedCosting>> finalEntry : deepestValue.entrySet()) {
								List<ProposedCosting> finalValueList = finalEntry.getValue();
								int listSize = finalValueList.size();
								majorCompSize = majorCompSize + listSize;
							}
						}
					}
					// calculate only major component size-----end---------------

					int subCompSrNo = 0;
					for (Map.Entry<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>> innerEntry : majorValue.entrySet()) {
						Map<Integer, Map<Integer, List<ProposedCosting>>> innermostValue = innerEntry.getValue();
						subCompSrNo++;

						// calculate only major sub component size-----start---------------
						int subCompSize = 0;
						for (Map.Entry<Integer, Map<Integer, List<ProposedCosting>>> deepestEntry : innermostValue.entrySet()) {
							Map<Integer, List<ProposedCosting>> deepestValue = deepestEntry.getValue();
							for (Map.Entry<Integer, List<ProposedCosting>> finalEntry : deepestValue.entrySet()) {
								List<ProposedCosting> finalValueList = finalEntry.getValue();
								int listSize = finalValueList.size();
								subCompSize = subCompSize + listSize;
							}
						}
						// calculate only major sub component end-----end---------------

						int activitySrNo = 0;
						for (Map.Entry<Integer, Map<Integer, List<ProposedCosting>>> deepestEntry : innermostValue.entrySet()) {
							Map<Integer, List<ProposedCosting>> deepestValue = deepestEntry.getValue();
							Integer deepestValue1 = deepestEntry.getKey();
							activitySrNo++;

							// calculate only activity size-----start---------------
							int activitySize = 0;
							for (Map.Entry<Integer, List<ProposedCosting>> finalEntry : deepestValue.entrySet()) {
								List<ProposedCosting> finalValueList = finalEntry.getValue();
								int listSize = finalValueList.size();
								activitySize = activitySize + listSize;
							}
							// calculate only activity size-----end---------------

							int subActivitySrNo = 0;
							for (Map.Entry<Integer, List<ProposedCosting>> finalEntry : deepestValue.entrySet()) {

								List<ProposedCosting> finalValueList = finalEntry.getValue();

								for (ProposedCosting listObj : finalValueList) {
									if(listObj.getSchemeId() !=null) {
										if (listObj.getSchemeId() != 555555) {
											if (schemeFlag) {
												String schemeName = "Schem Name : " + listObj.getSchemeId().toString() + " - " + (listObj.getSchemeName() == null ? "" : listObj.getSchemeName());
												CommonMethod.createDataCellBold(table, schemeName, 12, 1, 11,null,TextAlignment.LEFT);
												schemeFlag = false;
											}
											if (majorCompSize != 0 && listObj.getMajorComponentId() != 666666) {
												String majorComponentName = (majaorSrNo) + " - " + (listObj.getMajorComponentName() == null ? "" : listObj.getMajorComponentName());
												CommonMethod.createDataCell(table, majorComponentName, 1, majorCompSize,fData,null,TextAlignment.LEFT);
											}
											majorCompSize = 0;

											if (subCompSize != 0 && listObj.getSubComponentId() != 777777 && listObj.getMajorComponentId() != 666666) {
												String subComponentName = (majaorSrNo) + "." + subCompSrNo + " - " + (listObj.getSubComponentName() == null ? "" : listObj.getSubComponentName());
												CommonMethod.createDataCell(table, subComponentName, 1, subCompSize,fData,null,TextAlignment.LEFT);
											}
											subCompSize = 0;

											if (activitySize != 0 && listObj.getActivityMasterId() != 888888 && listObj.getSubComponentId() != 777777 && listObj.getMajorComponentId() != 666666) {
												String activityMasterName = (majaorSrNo) + "." + subCompSrNo + "." + activitySrNo + " - "
														+ (listObj.getActivityMasterName() == null ? "" : listObj.getActivityMasterName());
												CommonMethod.createDataCell(table, activityMasterName, 1, activitySize,fData,null,TextAlignment.LEFT);
											}
											activitySize = 0;

										}

										Boolean isTotal = false;
										if (listObj.getSchemeId() == 555555) {
											Color bgColorgGrandTotalText =new DeviceRgb(227,237,243);
											CommonMethod.createDataCellBold(table, "Grand Total of All Scheme ", 5, 1, fGrandTotal,bgColorgGrandTotalText,TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getMajorComponentId() == 666666) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getSchemeName(), 5, 1,fData,null,TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getSubComponentId() == 777777) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getMajorComponentName(), 4, 1,fData,null,TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getActivityMasterId() == 888888) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getSubComponentName(), 3, 1,fData,null,TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getActivityMasterDetailsId() == 999999) {
											CommonMethod.createDataCellBold(table, "Sub Total", 2, 1,fData,null,TextAlignment.RIGHT);
											isTotal = true;
										} else {
											subActivitySrNo++;
											CommonMethod.createDataCell(table,(subActivitySrNo) + "-" + (listObj.getActivityMasterDetailName() == null ? "" : listObj.getActivityMasterDetailName()), 1, 1,fData,null,TextAlignment.LEFT);
										}

										if (isTotal) {

											Color bgColorgTotal = null;
											if (listObj.getSchemeId() == 555555) {
												bgColorgTotal = new DeviceRgb(227, 237, 243);
											}
											CommonMethod.createDataCellBold(table, listObj.getTotApprovedBudget() == null ? "" : listObj.getTotApprovedBudget() == 0.00000 ? "" : df.format(listObj.getTotApprovedBudget()) + "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);

											CommonMethod.createDataCellBold(table, listObj.getTotExpenditure() == null ? "" : listObj.getTotExpenditure() == null ? "" : listObj.getTotExpenditure() == 0 ? "" : df.format(listObj.getTotExpenditure()) + "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);

											CommonMethod.createDataCellBold(table, listObj.getTotExpenditure() == null ? "" : listObj.getSpillOverApprovalBudget23() == null ? "" : listObj.getSpillOverApprovalBudget23() == 0 ? "" : df.format(listObj.getSpillOverApprovalBudget23()) + "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, listObj.getTotExpenditure() == null ? "" : listObj.getAnticipatedExpenditureSpillOver() == null ? "" : listObj.getAnticipatedExpenditureSpillOver() == 0 ? "" : df.format(listObj.getAnticipatedExpenditureSpillOver()) + "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);

											CommonMethod.createDataCellBold(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, listObj.getFinancialAmount() == null ? "" : listObj.getFinancialAmount() == 0 ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);

										} else {
											CommonMethod.createDataCell(table, listObj.getRecuringNonrecuring().equals("NA") ? "" : listObj.getRecuringNonrecuring() + "", 1, 1, fData,null,TextAlignment.CENTER);
											CommonMethod.createDataCell(table,  listObj.getTotApprovedBudget() == null ?  "" :listObj.getTotApprovedBudget()==0 ? "": df.format(listObj.getTotApprovedBudget()) + "", 1, 1, fData,null,TextAlignment.RIGHT);
											
											CommonMethod.createDataCell(table, listObj.getTotExpenditure() == null ? "" : listObj.getTotExpenditure()==0?"": df.format(listObj.getTotExpenditure())+"", 1, 1, fData,null,TextAlignment.RIGHT);
											
											CommonMethod.createDataCell(table, listObj.getTotExpenditure() == null ? "" : listObj.getSpillOverApprovalBudget23()==null?"": listObj.getSpillOverApprovalBudget23()==0 ?"": df.format(listObj.getSpillOverApprovalBudget23())+"", 1, 1, fData,null,TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getTotExpenditure() == null ? "" : listObj.getAnticipatedExpenditureSpillOver()==null?"": listObj.getAnticipatedExpenditureSpillOver()==0?"": df.format(listObj.getAnticipatedExpenditureSpillOver())+"", 1, 1, fData,null,TextAlignment.RIGHT);
											
											CommonMethod.createDataCell(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1, fData,null,TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getUnitCost() == null ? "" : listObj.getUnitCost() == 0 ? "" : df.format(listObj.getUnitCost()), 1, 1, fData,null,TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getFinancialAmount() == null ? "" : listObj.getFinancialAmount() == 0 ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, fData,null,TextAlignment.RIGHT);
										}
									}
								}

							}
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}
		
		return table;
	}
	
	

}
