package com.org.nic.prabandh.utill;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
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
import com.itextpdf.layout.borders.Border;
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
import com.org.nic.prabandh.constant.Constants;
import com.org.nic.prabandh.model.MastStatesTentative;

@Component
public class StateCostingReportPdf{

	DecimalFormat df = new DecimalFormat("0.00000");
	SimpleDateFormat sdf = new SimpleDateFormat(Constants.META_DATA_DATE_FORMAT);
	String formattedDate = sdf.format(new Date());

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

			new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(font).setFontSize(9).showTextAligned(pageInfo, pageSize.getWidth() / 2, 20, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
			/*.showTextAligned("Generated on " + formattedDate, 403, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0).setFontColor(new DeviceRgb(165,42,42))
			.showTextAligned("https://prabandh.education.gov.in", 403, 15, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0).setFontColor(new DeviceRgb(165,42,42));*/

			new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(font).setFontSize(9).setFontColor(new DeviceRgb(12, 49, 99))
					.showTextAligned("Generated on " + formattedDate, 640, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
					.showTextAligned("https://prabandh.education.gov.in", 640, 15, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);

			/*if (i == 2 || i == 3) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10).setFontColor(new DeviceRgb(255, 0, 0))
				.showTextAligned("*All figures (In Lakhs)" , 455, 790, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			}*/
			
			if (i > 1) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10).setFontColor(new DeviceRgb(165, 42, 42))
				.showTextAligned("Budget Demand  - " + regionName, 37, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
				.showTextAligned("F. Y. - " + planYear, 725, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
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

	public ResponseEntity<?> downloadCostingReportPdf(String planYear,
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> costingReportMap,
			String regionName, Optional<MastStatesTentative> stateTentive,
			List<RecurringNonRecurring> recurringNonRecurring, List<RecurringNonRecurring> budgetRecurNonRecur2324,
			List<RecurringNonRecurring> expenditureRecurNonRecur2324,
			List<MajorComponentProposal> majorComponentProposal) throws IOException {
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PdfWriter write = new PdfWriter(byteArrayOutputStream);
		write.setSmartMode(true);

		PdfDocument pdfDoc = new PdfDocument(write);
		pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

		Document doc = new Document(pdfDoc);

		// first page paragraph----Start-----------
	//	doc.add(CommonMethod.createHeadingParaGraph("State Costing Sheet Recommendation", 50f, 0f, 35, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph("Costing Sheet", 30f, 0f, 35, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph("(Samagra Shiksha)", 0f, 0f, 40, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph("of", 20f, 0f, 20, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph(regionName, 10f, 0f, 35, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph(planYear, 8f, 0f, 40, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph("Recommended", 8f, 0f, 15, new DeviceRgb(12, 49, 99)));
		doc.add(CommonMethod.createHeadingParaGraph("by", 1f, 0f, 15, new DeviceRgb(12, 49, 99)));
		doc.add(CommonMethod.createHeadingParaGraph("Dept. Of School Education & Literacy", 1f, 0f, 15, new DeviceRgb(12, 49, 99)));
		doc.add(CommonMethod.createHeadingParaGraph("Govt. Of India", 1f, 0f, 15, new DeviceRgb(12, 49, 99)));
		// first page paragraph---End------------

		
		
		
		
		
		if (costingReportMap.size() > 1) {
			
			// 2nd page Summary at a Glance table----Start-----------
			doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			if(stateTentive !=null && stateTentive.isPresent()) {
				doc.add(CommonMethod.createHeadingParaGraph("Summary at a Glance", 30f, 0f, 15, new DeviceRgb(0, 0, 0)));
				Table summaryGlance = getSummaryGlance(doc,planYear, stateTentive,recurringNonRecurring,budgetRecurNonRecur2324);
				doc.add(summaryGlance);
			}
			// 2nd page Summary at a Glance table----end-----------

			// 2nd page Expenditure Details table----Start-----------
			if(expenditureRecurNonRecur2324 !=null && expenditureRecurNonRecur2324.size()>0 ) {
				doc.add(CommonMethod.createHeadingParaGraph("Anticipated Expenditure Details till 31st March 2024", 20f, 0f, 15, new DeviceRgb(0, 0, 0)));
				Table expenditureDetails = getExpenditureDetails(doc,planYear, expenditureRecurNonRecur2324);
				doc.add(expenditureDetails);
			}
			// 2nd page Expenditure Details table----End-----------
			
			// 2nd page Tentative Proposed  table----Start-----------
			if(stateTentive !=null && stateTentive.isPresent()) {
				doc.add(CommonMethod.createHeadingParaGraph("Tentative Proposed Release F.Y. 2024-25", 60f, 0f, 15, new DeviceRgb(0, 0, 0)));
				Table tentativeProposed = getTentativeProposed (doc, stateTentive,planYear);
				doc.add(tentativeProposed);
			}
			
			if(majorComponentProposal !=null && majorComponentProposal.size()>0) {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(CommonMethod.createHeadingParaGraph("Major Component wise Proposal", 20f, 0f, 15, new DeviceRgb(0, 0, 0)));
				Table tentativeProposed = getMajorProposed(doc, majorComponentProposal,planYear);
				doc.add(tentativeProposed);
			}
			
			if(budgetRecurNonRecur2324 !=null && expenditureRecurNonRecur2324 !=null ) {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(CommonMethod.createHeadingParaGraph("Budget Approved for F.Y. 2023-24 VS Anticipated Expenditure Details till 31st March 2024", 20f, 0f, 10, new DeviceRgb(0, 0, 0)));
				Table tentativeProposed = getBudgetExpenditureBarChart(doc,planYear, budgetRecurNonRecur2324,expenditureRecurNonRecur2324);
				doc.add(tentativeProposed);
			}
			// 2nd page Expenditure Details table----End-----------
			
			//----------------------------------------------------------------------------------
			
			
			// report data-----Start------------
			//Table table = new Table(UnitValue.createPercentArray(new float[] { 1.5f, 1.5f, 1.5f, 3.2f, 1.0f, 1.0f, 1.0f }));
			//table.setWidth(UnitValue.createPercentValue(100));
			for (Map.Entry<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> schemeEntry : costingReportMap.entrySet()) {
				Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue = schemeEntry.getValue();

				Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue555555 = costingReportMap.get(555555);
				Integer schemeKey = schemeEntry.getKey();

				if (schemeKey == 555555)
					break;

				Table costingReportStateWise = getCostingReportStateWise(doc, costingReportMap, schemeKey, schemeValue, schemeValue555555);
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(costingReportStateWise);
			}
			// report data-----end------------
		
		} else {
			doc.add(CommonMethod.createHeadingParaGraph("Data not avaliable", 50f, 0f, 25, new DeviceRgb(0, 0, 0)));
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


	private Table getBudgetExpenditureBarChart(Document doc, String planYear, List<RecurringNonRecurring> budgetRecurNonRecur2324, List<RecurringNonRecurring> expenditureRecurNonRecur2324) throws IOException {
		Table table = new Table(UnitValue.createPercentArray(new float[] {1f,1f}));
		
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
		
		ImageData barImageDataBudget = DrawChartImage.generateBarChart(barDataBudExpendRecNonRec,4, "Budget(Recurring/Non-Recurring) vs Expenditure(Recurring/Non-Recurring)", "Particulars", "figures (In Lakhs)", 17, 14, 12);
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

		ImageData barImageDataExpen = DrawChartImage.generateBarChart(barDataBudExpendTot,3, "Budget(Total) vs Expenditure(Total)", "Particulars", "figures (In Lakhs)", 17, 14, 12);
		Image barChartImageExpen = new Image(barImageDataExpen);
		barChartImageExpen.setAutoScale(true);
		//barChartImageExpen.scaleAbsolute(250, 250);
		//barChartImage.setFixedPosition(310, 250);
		Cell cellBarRight = new Cell(1,1);
		cellBarRight.add(barChartImageExpen);
		table.addCell(cellBarRight);
		
		return table;
	}

	private Table getMajorProposed(Document doc, List<MajorComponentProposal> majorComponentProposal, String planYear) throws IOException {
		
		Table mainTable1 = new Table(UnitValue.createPercentArray(new float[] {2, 1.5f ,1.5f}));
		Table table1 = new Table(UnitValue.createPercentArray(new float[] {1}));
		Table table2 = new Table(UnitValue.createPercentArray(new float[] {1}));
		mainTable1.setWidth(UnitValue.createPercentValue(100));
		mainTable1.setFixedLayout();
		Table table = new Table(UnitValue.createPercentArray(new float[] {.5f, 2, 1 ,1}));

		CommonMethod.createDataCellBoldWithBackGroundColor(table, "SNo", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Major Component", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Figures for F.Y."+planYear, 2, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Proposed by State" , 1, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Recommended by DoSEL", 1, 1, 10f, TextAlignment.CENTER);
		
		Map<String, Double> dataSet = new TreeMap<>();
		Map<String, Double> recommendationFincdataSet = new TreeMap<>();
		int sno=1;Double financialAmountTot= 0d,recommendationFinancialAmountTot = 0d;
		for (MajorComponentProposal listObj : majorComponentProposal) {
			
			double financialAmount=0d;
			double recommendationFinancialAmount=0d;
			if(listObj.getFinancialAmount() !=null)
				financialAmount=listObj.getFinancialAmount();
			    financialAmountTot=financialAmountTot+financialAmount;
			    recommendationFinancialAmount = listObj.getMajorComponentIdWithoutScheme();
			    recommendationFinancialAmountTot = recommendationFinancialAmountTot+recommendationFinancialAmount;
			    
			
			if(listObj.getMajorComponentName()!=null) {
				CommonMethod.createDataCellBoldCenter(table, sno+"", 1, 1, 9);
				CommonMethod.createDataCellBoldLeft(table, listObj.getMajorComponentName(), 1, 1, 9);
				CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(financialAmount), 1, 1, 9);
				CommonMethod.createDataCellCategoryWithBorderRight(table,df.format(recommendationFinancialAmount), 1, 1, 9);
				dataSet.put(listObj.getMajorComponentName(), Double.parseDouble(df.format(financialAmount)));
				recommendationFincdataSet.put(listObj.getMajorComponentName(), Double.parseDouble(df.format(recommendationFinancialAmount)));
			}
		
			
			sno++;
		}
		CommonMethod.createDataCellBoldCenter(table, sno+"", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Total", 1, 1, 9);
		CommonMethod.createDataCellBoldRight(table, df.format(financialAmountTot), 1, 1, 9);
		CommonMethod.createDataCellBoldRight(table, df.format(recommendationFinancialAmountTot), 1, 1, 9);
		mainTable1.addCell(table);


		ImageData pieImageData = DrawChartImage.generatePieChart(dataSet, "State Proposal (Figures In Lakhs)", 18, 14, 18,30);
		Image pieChartimage = new Image(pieImageData);
	//	pieChartimage.scaleAbsolute(100, 100);
		//pieChartimage.setAutoScale(true);
	  //  pieChartimage.scaleToFit(100, 100);

		  
		pieChartimage.setFixedPosition(348, 240);
		pieChartimage.setWidth(230);
		
		Cell cellPie = new Cell(1, 1);
		cellPie.add(pieChartimage);
	//	table1.addCell(cellPie);
	
		mainTable1.addCell(cellPie);
		
		ImageData pieImageData1 = DrawChartImage.generatePieChart(recommendationFincdataSet, "DoSEL Recommendations (Figures In Lakhs)", 18, 14, 18,30);
		Image pieChartimage1 = new Image(pieImageData1);
		//pieChartimage.scaleAbsolute(100, 100);
	//	pieChartimage.setAutoScale(true);
	//	pieChartimage.scaleToFit(100, 100);

		  
		pieChartimage1.setFixedPosition(582, 240);
		pieChartimage1.setWidth(230);
		
		Cell cellPie1 = new Cell(1, 1);
		cellPie1.add(pieChartimage1);
		mainTable1.addCell(cellPie1);
		//table2.addCell(cellPie1);
	//	table2.setBorder(Border.NO_BORDER);
	//	mainTable1.addCell(table1);
	//	mainTable1.addCell(table2);
	//	mainTable1.addCell(Table2);
		
		return mainTable1;
	}

	private Table getSummaryGlance(Document doc,String planYear,
			Optional<MastStatesTentative> stateTentive,
			List<RecurringNonRecurring> recurringNonRecurring,
			List<RecurringNonRecurring> budgetRecurNonRecur2324) throws IOException {
		
		
		float[] columnWidths = { .5f, 2.3f, 1.5f, 1.5f,1f, 1.5f, 1.5f, 1f };
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));
		
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "SNo", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Particulars", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "State Plan F.Y. "+planYear, 2, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Total", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Budget Approved for F.Y. 2023-24", 2, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Total", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Recurring", 1, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Non-Recurring", 1, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Recurring", 1, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Non-Recurring", 1, 1, 10f, TextAlignment.CENTER);
		

		Double elementaryRec = 0d, elementaryNonRec = 0d, elementaryRecNonRecTot = 0d;
		Double secondaryRec = 0d, secondaryNonRec = 0d, secondaryRecNonRecTot = 0d;
		Double teacherRec = 0d, teacherNonRec = 0d, teacherRecNonRecTot = 0d;
		for (RecurringNonRecurring obj : recurringNonRecurring) {
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
		

		CommonMethod.createDataCellBoldCenter(table, "1", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Elementary Education", 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(elementaryRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(elementaryNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(elementaryRecNonRecTot), 1, 1, 10);
		
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(budgetElementaryRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(budgetElementaryNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(budgetElementaryRecNonRecTot), 1, 1, 10);
		
		CommonMethod.createDataCellBoldCenter(table, "2", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Secondary Education", 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(secondaryRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(secondaryNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(secondaryRecNonRecTot), 1, 1, 10);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(budgetSecondaryRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(budgetSecondaryNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(budgetSecondaryRecNonRecTot), 1, 1, 10);
		
		
		CommonMethod.createDataCellBoldCenter(table, "3", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Teacher Education", 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(teacherRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(teacherNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(teacherRecNonRecTot), 1, 1, 10);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(budgetTeacherRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(budgetTeacherNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(budgetTeacherRecNonRecTot), 1, 1, 10);
		
		CommonMethod.createDataCellBoldCenter(table, "4", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Grand Total", 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(grantTotalRecur), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(grantTotalNonrecur), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(grantTotaltotalRecurNonrecur), 1, 1, 10);
		
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(budgetGrantTotalRecur), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(budgetGrantTotalNonrecur), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(budgetGrantTotaltotalRecurNonrecur), 1, 1, 10);
		
		Double parcentCentral = stateTentive.get().getCenterSharePercent();
		Double parcentState = (100 - stateTentive.get().getCenterSharePercent());
		
		CommonMethod.createDataCellBoldCenter(table, "5", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Central Share("+parcentCentral+"%)", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "", 2, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(grantTotaltotalRecurNonrecur * (parcentCentral / 100)), 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "", 2, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(budgetGrantTotaltotalRecurNonrecur * (parcentCentral / 100)), 1, 1, 9);
		
		CommonMethod.createDataCellBoldCenter(table, "6", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "State Share("+parcentState+"%)", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "", 2, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(grantTotaltotalRecurNonrecur * (parcentState / 100)), 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "", 2, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(budgetGrantTotaltotalRecurNonrecur * (parcentState / 100)), 1, 1, 9);
	
		return table;
	}

	

	private Table getExpenditureDetails(Document doc, String planYear,List<RecurringNonRecurring> expenditureRecurNonRecur2324) throws IOException {

		float[] columnWidths = { 0.5f, 1.5f, 1.5f, 1.5f, 1.5f};
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));
		
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "SNo", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Particulars", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Expenditure till 31st March 2024", 2, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Total", 1, 2, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Recurring", 1, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Non-Recurring", 1, 1, 10f, TextAlignment.CENTER);


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
		
		Double expenditureGrantTotalRecur = (expenditureElementaryRec + expenditureSecondaryRec + expenditureTeacherRec);
		Double expenditureGrantTotalNonrecur = (expenditureElementaryNonRec + expenditureSecondaryNonRec + expenditureTeacherNonRec);
		Double expenditureGrantTotaltotalRecurNonrecur = expenditureGrantTotalRecur + expenditureGrantTotalNonrecur;
		
		
		CommonMethod.createDataCellBoldCenter(table, "1", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Elementary Education", 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(expenditureElementaryRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(expenditureElementaryNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(expenditureElementaryRecNonRecTot), 1, 1, 10);

		
		CommonMethod.createDataCellBoldCenter(table, "2", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Secondary Education", 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(expenditureSecondaryRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(expenditureSecondaryNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(expenditureSecondaryRecNonRecTot), 1, 1, 10);
		
		
		CommonMethod.createDataCellBoldCenter(table, "3", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Teacher Education", 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(expenditureTeacherRec), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(expenditureTeacherNonRec), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(expenditureTeacherRecNonRecTot), 1, 1, 10);
		
		
		CommonMethod.createDataCellBoldCenter(table, "4", 1, 1, 9);
		CommonMethod.createDataCellBoldLeft(table, "Grand Total", 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(expenditureGrantTotalRecur), 1, 1, 9);
		CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(expenditureGrantTotalNonrecur), 1, 1, 9);
		CommonMethod.createDataCellTotalWithBorderRight(table, df.format(expenditureGrantTotaltotalRecurNonrecur), 1, 1, 10);

		return table;
	}
	
	private Table getTentativeProposed(Document doc, Optional<MastStatesTentative> stateTentive, String planYear) throws IOException {
		float[] columnWidths = { 2.5f, 1.5f, 2.5f, 1.5f, 1.5f, 1.5f};
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));
	
		Double parcentCentral = stateTentive.get().getCenterSharePercent();
		Double reParcentState = (100 - stateTentive.get().getCenterSharePercent());
		
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Central Share("+parcentCentral+"%)", 1, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldRight(table, df.format(stateTentive.get().getTentativeCentralShare()), 1, 1, 9);
		
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "State Share("+reParcentState+"%)", 1, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldRight(table, df.format(stateTentive.get().getTentativeStateShare()), 1, 1, 9);
		
		CommonMethod.createDataCellBoldWithBackGroundColor(table, "Total", 1, 1, 10f, TextAlignment.CENTER);
		CommonMethod.createDataCellBoldRight(table, df.format(stateTentive.get().getTentativeTotalEstimates()), 1, 1, 9);

		return table;
	}
	
	
	
	// ---CostingReportStateWise start here----------------------
	private Table getCostingReportStateWise(Document doc, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> groupedByFiveAttributes, Integer schemeKey,
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue555555)
			throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] {1.5f, 1.5f, 1.5f, 3.2f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,1.0f }));
		//Table table = new Table(UnitValue.createPercentArray(new float[] {1.5f, 1.5f, 1.5f, 3.2f, 1.0f, 1.0f, 1.0f, 1.0f }));
		table.setWidth(UnitValue.createPercentValue(100));

		if (schemeKey != 555555) {
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Major Component", 1, 2, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Sub Component", 1, 2, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Activity", 1, 2, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Sub Activity", 1, 2, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Proposed by State", 3, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Recommended by DoSEL", 3, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Coordinator Remarks", 1, 2, 10f, TextAlignment.CENTER);
			
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Physical Quantity", 1, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Unit Cost", 1, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Financial Amount (In Lakhs)", 1, 1, 10f, TextAlignment.CENTER);
			
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Physical Quantity", 1, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Unit Cost", 1, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Financial Amount (In Lakhs)", 1, 1, 10f, TextAlignment.CENTER);
			
	
		}

		int loopTimes = 1;
		if (schemeKey == 3)
			loopTimes = 2;

		for (int i = 1; i <= loopTimes; i++) {
			if (schemeKey == 3 && i == 2) {
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

								if (listObj.getSchemeId() != 555555) {
									if (schemeFlag) {
										String schemeName = "Schem Name : " + listObj.getSchemeId().toString() + " - " + (listObj.getSchemeName() == null ? "" : listObj.getSchemeName());
										CommonMethod.createDataCellBoldLeft(table, schemeName, 11, 1, 10);
										schemeFlag = false;
									}
									if (majorCompSize != 0 && listObj.getMajorComponentId() != 666666) {
										String majorComponentName = (majaorSrNo) + " - " + (listObj.getMajorComponentName() == null ? "" : listObj.getMajorComponentName());
										CommonMethod.createDataCellCategoryWithBorderLeft(table, majorComponentName, 1, majorCompSize);
									}
									majorCompSize = 0;

									if (subCompSize != 0 && listObj.getSubComponentId() != 777777 && listObj.getMajorComponentId() != 666666) {
										String subComponentName = (majaorSrNo) + "." + subCompSrNo + " - " + (listObj.getSubComponentName() == null ? "" : listObj.getSubComponentName());
										CommonMethod.createDataCellCategoryWithBorderLeft(table, subComponentName, 1, subCompSize);
									}
									subCompSize = 0;

									if (activitySize != 0 && listObj.getActivityMasterId() != 888888 && listObj.getSubComponentId() != 777777 && listObj.getMajorComponentId() != 666666) {
										String activityMasterName = (majaorSrNo) + "." + subCompSrNo + "." + activitySrNo + " - "
												+ (listObj.getActivityMasterName() == null ? "" : listObj.getActivityMasterName());
										CommonMethod.createDataCellCategoryWithBorderLeft(table, activityMasterName, 1, activitySize);
									}
									activitySize = 0;

								}

								Boolean isTotal = false;
								if (listObj.getSchemeId() == 555555) {
									CommonMethod.createDataCellTotalWithBorderRight(table, "Grand Total of All Scheme : ", 4, 1, 11);
									CommonMethod.createDataCellTotalWithBorderRight(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1, 11);
									CommonMethod.createDataCellTotalWithBorderRight(table, "", 1, 1, 11);
									CommonMethod.createDataCellTotalWithBorderRight(table, listObj.getFinancialAmount() == null ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, 11);
									
									CommonMethod.createDataCellTotalWithBorderRight(table, listObj.getProposedPhysicalQuantity()+"", 1, 1,9);
									CommonMethod.createDataCellTotalWithBorderRight(table, df.format(listObj.getProposedUnitCost()), 1, 1,9);
									CommonMethod.createDataCellTotalWithBorderRight(table, df.format(listObj.getProposedFinancialAmount()), 1, 1,9);
									CommonMethod.createDataCellTotalWithBorderRight(table, listObj.getCoordinatorRemarks(), 1, 1,9);
									isTotal = true;
								} else if (listObj.getMajorComponentId() == 666666) {
									CommonMethod.createDataCellBoldRight(table, "Total of " + listObj.getSchemeName(), 4, 1,9);
									isTotal = true;
								} else if (listObj.getSubComponentId() == 777777) {
									CommonMethod.createDataCellBoldRight(table, "Total of " + listObj.getMajorComponentName(), 3, 1,9);
									isTotal = true;
								} else if (listObj.getActivityMasterId() == 888888) {
									CommonMethod.createDataCellBoldRight(table, "Total of " + listObj.getSubComponentName(), 2, 1,9);
									isTotal = true;
								} else if (listObj.getActivityMasterDetailsId() == 999999) {
									CommonMethod.createDataCellBoldRight(table, "Sub Total", 1, 1,9);
									isTotal = true;
								} else {
									subActivitySrNo++;
									CommonMethod.createDataCellCategoryWithBorderLeft(table,(subActivitySrNo) + "-" + (listObj.getActivityMasterDetailName() == null ? "" : listObj.getActivityMasterDetailName()), 1, 1);
								}

								if (isTotal) {
									if (listObj.getSchemeId() != 555555) {
										CommonMethod.createDataCellBoldRight(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1,9);
										CommonMethod.createDataCellBoldRight(table, "", 1, 1,9);
										CommonMethod.createDataCellBoldRight(table, listObj.getFinancialAmount() == null ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1,9);
										
										CommonMethod.createDataCellBoldRight(table, df.format(listObj.getProposedPhysicalQuantity()), 1, 1,9);
									//	CommonMethod.createDataCellBoldRight(table, df.format(listObj.getProposedUnitCost()), 1, 1,9);
										CommonMethod.createDataCellBoldRight(table, "", 1, 1,9);
										CommonMethod.createDataCellBoldRight(table, df.format(listObj.getProposedFinancialAmount()), 1, 1,9);
										//CommonMethod.createDataCellBoldRight(table, listObj.getCoordinatorRemarks(), 1, 1,9);
										CommonMethod.createDataCellBoldRight(table, "", 1, 1,9);
									}
								} else {
									CommonMethod.createDataCellCategoryWithBorderRight(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1, 9);
									CommonMethod.createDataCellCategoryWithBorderRight(table, listObj.getUnitCost() == null ? "" : df.format(listObj.getUnitCost()) + "", 1, 1, 9);
									CommonMethod.createDataCellCategoryWithBorderRight(table, listObj.getFinancialAmount() == null ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, 9);
									
									CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(listObj.getProposedPhysicalQuantity()), 1, 1,9);
									CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(listObj.getProposedUnitCost()), 1, 1,9);
									CommonMethod.createDataCellCategoryWithBorderRight(table, df.format(listObj.getProposedFinancialAmount()), 1, 1,9);
									CommonMethod.createDataCellCategoryWithBorderRight(table, listObj.getCoordinatorRemarks(), 1, 1,9);
								}

							}

						}
					}
				}

			}
		}
		return table;
	}
	
	


}

