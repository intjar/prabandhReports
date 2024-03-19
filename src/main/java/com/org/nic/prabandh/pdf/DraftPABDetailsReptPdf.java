package com.org.nic.prabandh.pdf;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.org.nic.prabandh.bean.MajorComponentProposal;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.bean.RecurringNonRecurring;
import com.org.nic.prabandh.bean.Spillover;
import com.org.nic.prabandh.constant.Constants;
import com.org.nic.prabandh.model.MastStatesTentative;
import com.org.nic.prabandh.utill.CommonMethod;
import com.org.nic.prabandh.utill.DrawChartImage;

@Component
public class DraftPABDetailsReptPdf {

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
	public byte[] addFooterAndPageNumbers(byte[] pdf, String regionName, String planYear, Integer isDraft) throws Exception {
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
					.showTextAligned("Generated on " + formattedDate, 640, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
					.showTextAligned("https://prabandh.education.gov.in", 640, 15, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);

			if (i > 1 && i <= 6) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10).setFontColor(new DeviceRgb(255, 0, 0))
						.showTextAligned("*All figures (In Lakhs)", 700, 560, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			}

			if (i > 6) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(8).setFontColor(new DeviceRgb(0, 0, 0))
						.add(CommonMethod.createParaGraphBold("", 0f, 0f, 10, new DeviceRgb(255, 128, 128), new DeviceRgb(255, 128, 128), TextAlignment.CENTER).setHeight(10f).setFixedPosition(385, 562, 10f).setBorder(new SolidBorder(DeviceRgb.BLACK, 0.2f)))
						.showTextAligned("No fund Recommended", 400, 567, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);

				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(8).setFontColor(new DeviceRgb(0, 0, 0))
						.add(CommonMethod.createParaGraphBold("", 0f, 0f, 10, new DeviceRgb(255, 255, 51), new DeviceRgb(255, 255, 51), TextAlignment.CENTER).setHeight(10f).setFixedPosition(500, 562, 10f).setBorder(new SolidBorder(DeviceRgb.BLACK, 0.1f)))
						.showTextAligned("Less fund Recommended", 515, 567, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);

				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(9).setFontColor(new DeviceRgb(165, 42, 42))
						.showTextAligned("Budget Demand  - " + regionName, 37, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
						.showTextAligned("F. Y. - " + planYear, 725, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			}

			/*Image image = null;
			Image imageDraft = null;
			try {
				image = new Image(ImageDataFactory.create(inputStreamToByteArray(getClass().getResourceAsStream("/static/prabandh-nic.png"))));
				image.scaleAbsolute(200, 25);
				image.setFixedPosition(32, 6);
				
				imageDraft = new Image(ImageDataFactory.create(inputStreamToByteArray(getClass().getResourceAsStream("/static/draft-water-mark.png"))));
				imageDraft.scaleAbsolute(520, 520);
				imageDraft.setFixedPosition(140, 60);
				
			} catch (Exception e) {
				e.printStackTrace();
			}*/


			PdfCanvas canvasImage = new PdfCanvas(page);
			canvasImage.saveState();
			canvasImage.setExtGState(new PdfExtGState());
			try (Canvas canvas = new Canvas(canvasImage, pdfDoc, pageSize)) {
				Image image = new Image(ImageDataFactory.create(inputStreamToByteArray(getClass().getResourceAsStream("/static/prabandh-nic.png"))));
				image.scaleAbsolute(200, 25);
				image.setFixedPosition(32, 6);
				canvas.add(image);
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				canvasImage.restoreState();
			}
			

			if(isDraft==0) {
				PdfCanvas canvasDraft = new PdfCanvas(page);
				canvasDraft.saveState();
				canvasDraft.setExtGState(new PdfExtGState().setFillOpacity(.7f));
				try (Canvas canvas = new Canvas(canvasDraft, pdfDoc, pageSize)) {
					Image imageDraft = new Image(ImageDataFactory.create(inputStreamToByteArray(getClass().getResourceAsStream("/static/draft-water-mark.png"))));
					imageDraft.scaleAbsolute(520, 520);
					imageDraft.setFixedPosition(140, 60);

					canvas.add(imageDraft);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					canvasDraft.restoreState();
				}
			}
			
			
		}

		pdfDoc.close();
		return baos.toByteArray();
	}
	
	
	@SuppressWarnings("resource")
	public byte[] addFooterAndPageNumbersDraftPAB(byte[] pdf, String regionName, String planYear, Integer isDraft) throws Exception {
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
					.showTextAligned("Generated on " + formattedDate, 640, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
					.showTextAligned("https://prabandh.education.gov.in", 640, 15, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);

			if (i > 1 && i <= 6) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10).setFontColor(new DeviceRgb(255, 0, 0))
						.showTextAligned("*All figures (In Lakhs)", 700, 560, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			}

			if (i > 6) {
//				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(8).setFontColor(new DeviceRgb(0, 0, 0))
//						.add(CommonMethod.createParaGraphBold("", 0f, 0f, 10, new DeviceRgb(255, 128, 128), new DeviceRgb(255, 128, 128), TextAlignment.CENTER).setHeight(10f).setFixedPosition(385, 562, 10f).setBorder(new SolidBorder(DeviceRgb.BLACK, 0.2f)))
//						.showTextAligned("No fund Recommended", 400, 567, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
//
//				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(8).setFontColor(new DeviceRgb(0, 0, 0))
//						.add(CommonMethod.createParaGraphBold("", 0f, 0f, 10, new DeviceRgb(255, 255, 51), new DeviceRgb(255, 255, 51), TextAlignment.CENTER).setHeight(10f).setFixedPosition(500, 562, 10f).setBorder(new SolidBorder(DeviceRgb.BLACK, 0.1f)))
//						.showTextAligned("Less fund Recommended", 515, 567, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);

				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(9).setFontColor(new DeviceRgb(165, 42, 42))
						.showTextAligned("Budget Demand  - " + regionName, 37, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
						.showTextAligned("F. Y. - " + planYear, 725, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			}

			/*Image image = null;
			Image imageDraft = null;
			try {
				image = new Image(ImageDataFactory.create(inputStreamToByteArray(getClass().getResourceAsStream("/static/prabandh-nic.png"))));
				image.scaleAbsolute(200, 25);
				image.setFixedPosition(32, 6);
				
				imageDraft = new Image(ImageDataFactory.create(inputStreamToByteArray(getClass().getResourceAsStream("/static/draft-water-mark.png"))));
				imageDraft.scaleAbsolute(520, 520);
				imageDraft.setFixedPosition(140, 60);
				
			} catch (Exception e) {
				e.printStackTrace();
			}*/


			PdfCanvas canvasImage = new PdfCanvas(page);
			canvasImage.saveState();
			canvasImage.setExtGState(new PdfExtGState());
			try (Canvas canvas = new Canvas(canvasImage, pdfDoc, pageSize)) {
				Image image = new Image(ImageDataFactory.create(inputStreamToByteArray(getClass().getResourceAsStream("/static/prabandh-nic.png"))));
				image.scaleAbsolute(200, 25);
				image.setFixedPosition(32, 6);
				canvas.add(image);
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				canvasImage.restoreState();
			}
			

			if(isDraft==0) {
				PdfCanvas canvasDraft = new PdfCanvas(page);
				canvasDraft.saveState();
				canvasDraft.setExtGState(new PdfExtGState().setFillOpacity(.7f));
				try (Canvas canvas = new Canvas(canvasDraft, pdfDoc, pageSize)) {
					Image imageDraft = new Image(ImageDataFactory.create(inputStreamToByteArray(getClass().getResourceAsStream("/static/draft-water-mark.png"))));
					imageDraft.scaleAbsolute(520, 520);
					imageDraft.setFixedPosition(140, 60);

					canvas.add(imageDraft);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					canvasDraft.restoreState();
				}
			}
			
			
		}

		pdfDoc.close();
		return baos.toByteArray();
	}

	public ResponseEntity<?> downloadDraftPABDetailsReptPdf(Integer isDraft, String planYear,
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> costingReportMap,
			String regionName, Optional<MastStatesTentative> stateTentive,
			List<RecurringNonRecurring> statePlanList, List<RecurringNonRecurring> budgetRecurNonRecur2324,
			List<RecurringNonRecurring> expenditureRecurNonRecur2324,
			List<MajorComponentProposal> majorComponentProposal,
			List<RecurringNonRecurring> recommendationList, List<Spillover> spilloverList, List<MajorComponentProposal> majorComponentStatePlan) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PdfWriter write = new PdfWriter(byteArrayOutputStream);
		write.setSmartMode(true);

		PdfDocument pdfDoc = new PdfDocument(write);
		pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

		Document doc = new Document(pdfDoc);

		// first page paragraph----Start-----------
		Color paraFColor1 = new DeviceRgb(165, 42, 42);
		doc.add(CommonMethod.createParaGraphBold("Draft PAB Details Sheet", 30f, 0f, 35, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("(Samagra Shiksha)", 0f, 0f, 40, new DeviceRgb(165, 42, 42), null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("of", 20f, 0f, 20, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold(regionName == null ? "" : regionName, 10f, 0f, 35, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold(planYear == null ? "" : planYear, 8f, 0f, 40, paraFColor1, null, TextAlignment.CENTER));

		Color paraFColor2 = new DeviceRgb(12, 49, 99);
		doc.add(CommonMethod.createParaGraphBold("Recommended", 8f, 0f, 15, paraFColor2, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("by", 1f, 0f, 15, paraFColor2, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("Dept. Of School Education & Literacy", 1f, 0f, 15, paraFColor2, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("Govt. Of India", 1f, 0f, 15, paraFColor2, null, TextAlignment.CENTER));
		// first page paragraph---End------------

		Color paraFColor3 = new DeviceRgb(0, 0, 0);
		if (costingReportMap.size() > 1) {

			// 2nd page Summary at a Glance table----Start-----------
			doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			if (expenditureRecurNonRecur2324 != null && budgetRecurNonRecur2324 != null && expenditureRecurNonRecur2324.size() > 0 && budgetRecurNonRecur2324.size() > 0) {

				doc.add(CommonMethod.createParaGraphBold("Summary at a Glance", 0f, 0f, 12, paraFColor3, null, TextAlignment.CENTER));
				Table summaryGlance = getSummaryGlance(doc, planYear, budgetRecurNonRecur2324, expenditureRecurNonRecur2324);
				doc.add(summaryGlance);

				doc.add(CommonMethod.createParaGraphBold("Budget Approved for F.Y. 2023-24 VS Anticipated Expenditure Details till 31st March 2024", 20f, 0f, 12, paraFColor3, null,
						TextAlignment.CENTER));
				Table table = getBudgetExpenditureBarChart(doc, planYear, budgetRecurNonRecur2324, expenditureRecurNonRecur2324);
				doc.add(table);
			}
			// 2nd page Summary at a Glance table----end-----------

			// 3rd page Tentative Proposed table----Start-----------
			if (stateTentive != null && stateTentive.isPresent()) {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(CommonMethod.createParaGraphBold("Tentative Outlay F.Y. 2024-25", 30f, 0f, 12, paraFColor3, null, TextAlignment.CENTER));
				Table tableTentative = getTentativeProposed(doc, stateTentive, planYear);
				doc.add(tableTentative);
			}

			if (spilloverList != null && spilloverList.size() > 0) {
				doc.add(CommonMethod.createParaGraphBold("Spillover", 30f, 0f, 12, paraFColor3, null, TextAlignment.CENTER));
				Table tableSpillover = getSpilloverTable(doc, planYear, spilloverList);
				doc.add(tableSpillover);
			}

			if (recommendationList != null && stateTentive != null && statePlanList != null) {
				doc.add(CommonMethod.createParaGraphBold("State Plan Vs Recommendation (F.Y. " + planYear + ")", 30f, 0f, 12, paraFColor3, null, TextAlignment.CENTER));
				Table tableRecommendation = getRecommendationTable(doc, planYear, recommendationList, stateTentive, statePlanList);
				doc.add(tableRecommendation);
			}
			// 3rd page Tentative Proposed table----End-----------

			// 4th page Tentative Proposed table----Start-----------
			if (majorComponentProposal != null && majorComponentProposal.size() > 0) {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(CommonMethod.createParaGraphBold("Major Component wise Details", 30f, 0f, 15, paraFColor3, null, TextAlignment.CENTER));
				Table tableComponentDetails = getMajorCompoDetails(doc, majorComponentProposal, planYear);
				doc.add(tableComponentDetails);
			}
			/*if (majorComponentProposal != null && majorComponentProposal.size() > 0) {
				Table tableComponentDetails = getAreaChartMajorCompoDetails(doc, majorComponentProposal, planYear);
				doc.add(tableComponentDetails);
			}*/
			// ---------------------------------------------------------

			// 5th page MajorComponentPercentDetails table----Start-----------
			if (majorComponentStatePlan != null && majorComponentStatePlan.size() > 0) {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(CommonMethod.createParaGraphBold("Major Component wise - State Plan (F.Y. " + planYear + ")", 30f, 0f, 15, paraFColor3, null, TextAlignment.CENTER));
				Table tableMajorPercent = getMajorComponentPercentDetails(doc, majorComponentStatePlan, planYear);
				doc.add(tableMajorPercent);
			}
			// ------------------------------------------------
			// 6th page MajorComponentChart table----Start-----------
			if (majorComponentProposal != null && majorComponentProposal.size() > 0) {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(CommonMethod.createParaGraphBold("Major Component wise Details", 30f, 0f, 15, paraFColor3, null, TextAlignment.CENTER));
				Table tableMajorChart = getMajorComponentChartDraftPAB(doc, majorComponentProposal, planYear);
				doc.add(tableMajorChart);
			}
			// ------------------------------------------------------------

			// -------------------------------------------------------------------------------------------

			// report data-----Start------------
			for (Map.Entry<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> schemeEntry : costingReportMap.entrySet()) {
				Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue = schemeEntry.getValue();

				Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue555555 = costingReportMap.get(555555);
				Integer schemeKey = schemeEntry.getKey();

				if (schemeKey == 555555)
					break;

				Table tableDetailsReport = getTableReportData(doc, costingReportMap, schemeKey, schemeValue, schemeValue555555, (costingReportMap.size() - 1));
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				doc.add(tableDetailsReport);
			}
			// report data-----end------------

		} else {
			doc.add(CommonMethod.createParaGraphBold("Data not avaliable", 50f, 0f, 25, paraFColor3, null, TextAlignment.CENTER));
		}

		doc.close();
		byteArrayOutputStream.close();

		byte[] bytes = byteArrayOutputStream.toByteArray();
		try {
			bytes = addFooterAndPageNumbersDraftPAB(bytes, regionName, planYear,isDraft);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=Draft PAB details Sheet Recommended for " + regionName + ".pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bytes);
	}


	
	
	
	private Table getMajorComponentChart(Document doc, List<MajorComponentProposal> majorComponentProposal, String planYear) throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] { 1f, 1f }));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();

		try {
			Double financialAmountTot = 0d, recommendationFinancialAmountTot = 0d;
			Map<String, Double> dataSet = new TreeMap<>();
			Map<String, Double> recommendationFincdataSet = new TreeMap<>();
			for (MajorComponentProposal listObj : majorComponentProposal) {
				double financialAmount = 0d;
				if (listObj.getFinancialAmount() != null)
					financialAmount = listObj.getFinancialAmount();

				double recommendationFinancialAmount = 0d;
				if (listObj.getRecommendedFinancialAmount() != null)
					recommendationFinancialAmount = listObj.getRecommendedFinancialAmount();

				if (listObj.getMajorComponentName() != null && !listObj.getMajorComponentName().equals("Total")) {
					dataSet.put(listObj.getMajorComponentName(), Double.parseDouble(df.format(financialAmount)));
					recommendationFincdataSet.put(listObj.getMajorComponentName(), Double.parseDouble(df.format(recommendationFinancialAmount)));
				} else if (listObj.getMajorComponentName() != null && listObj.getMajorComponentName().equals("Total")) {
					financialAmountTot = financialAmount;
					recommendationFinancialAmountTot = recommendationFinancialAmount;
				}
			}

			String centerTotalFA = dfWithoutZero.format(financialAmountTot);
			ImageData dImageData = DrawChartImage.generateDonutChart(dataSet, centerTotalFA, "State Proposal (Figures In Lakhs)", 19, 15, 11, 5);
			Image dChartimage = new Image(dImageData);
			dChartimage.setAutoScale(true);
			// dChartimage.scaleAbsolute(225, 225);
			// dChartimage.setMaxHeight(225);
			dChartimage.setHorizontalAlignment(HorizontalAlignment.CENTER);

			Cell celld = new Cell(1, 1);
			celld.add(dChartimage);
			table.addCell(celld);

			String centerTotalDS = dfWithoutZero.format(recommendationFinancialAmountTot);
			ImageData dImageData1 = DrawChartImage.generateDonutChart(recommendationFincdataSet, centerTotalDS, "DoSEL Recommendations (Figures In Lakhs)", 19, 15, 11, 5);
			Image dChartimage1 = new Image(dImageData1);
			dChartimage1.setAutoScale(true);
			// dChartimage1.scaleAbsolute(225, 225);
			// dChartimage1.setMaxHeight(225);
			dChartimage1.setHorizontalAlignment(HorizontalAlignment.CENTER);

			Cell cellPie1 = new Cell(1, 1);
			cellPie1.add(dChartimage1);
			table.addCell(cellPie1);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			return table;
		}

		return table;
	}
	
	private Table getMajorComponentChartDraftPAB(Document doc, List<MajorComponentProposal> majorComponentProposal, String planYear) throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] { 1f, 1f }));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();

		try {
			Double financialAmountTot = 0d, recommendationFinancialAmountTot = 0d;
			Map<String, Double> dataSet = new TreeMap<>();
			Map<String, Double> recommendationFincdataSet = new TreeMap<>();
			for (MajorComponentProposal listObj : majorComponentProposal) {
				double financialAmount = 0d;
				if (listObj.getFinancialAmount() != null)
					financialAmount = listObj.getFinancialAmount();

				double recommendationFinancialAmount = 0d;
				if (listObj.getRecommendedFinancialAmount() != null)
					recommendationFinancialAmount = listObj.getRecommendedFinancialAmount();

				if (listObj.getMajorComponentName() != null && !listObj.getMajorComponentName().equals("Total")) {
					dataSet.put(listObj.getMajorComponentName(), Double.parseDouble(df.format(financialAmount)));
					recommendationFincdataSet.put(listObj.getMajorComponentName(), Double.parseDouble(df.format(recommendationFinancialAmount)));
				} else if (listObj.getMajorComponentName() != null && listObj.getMajorComponentName().equals("Total")) {
					financialAmountTot = financialAmount;
					recommendationFinancialAmountTot = recommendationFinancialAmount;
				}
			}

			String centerTotalFA = dfWithoutZero.format(financialAmountTot);
			ImageData dImageData = DrawChartImage.generateDonutChart(dataSet, centerTotalFA, "State Proposal (Figures In Lakhs)", 19, 15, 11, 5);
			Image dChartimage = new Image(dImageData);
			dChartimage.setAutoScale(true);
			// dChartimage.scaleAbsolute(225, 225);
			// dChartimage.setMaxHeight(225);
			dChartimage.setHorizontalAlignment(HorizontalAlignment.CENTER);

			Cell celld = new Cell(1, 1);
			celld.add(dChartimage);
			table.addCell(celld);

			String centerTotalDS = dfWithoutZero.format(recommendationFinancialAmountTot);
			ImageData dImageData1 = DrawChartImage.generateDonutChart(recommendationFincdataSet, centerTotalDS, "DoSEL Recommendations (Figures In Lakhs)", 19, 15, 11, 5);
			Image dChartimage1 = new Image(dImageData1);
			dChartimage1.setAutoScale(true);
			// dChartimage1.scaleAbsolute(225, 225);
			// dChartimage1.setMaxHeight(225);
			dChartimage1.setHorizontalAlignment(HorizontalAlignment.CENTER);

			Cell cellPie1 = new Cell(1, 1);
			cellPie1.add(dChartimage1);
			table.addCell(cellPie1);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			return table;
		}

		return table;
	}


	private Table getMajorComponentPercentDetails(Document doc, List<MajorComponentProposal> majorComponentStatePlan, String planYear) throws IOException {
		Table table = new Table(UnitValue.createPercentArray(new float[] { 0.5f, 2f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f }));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();

		try {
			Color bgColorgTotal = new DeviceRgb(227, 237, 243);
			Color bgColorgTotalPercent = new DeviceRgb(230, 255, 230);
			Color bgColor = new DeviceRgb(37, 132, 198);

			CommonMethod.createDataCellTableHead(table, "SNo", 1, 3, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Major Component", 1, 3, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Figures for F.Y. " + planYear, 8, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Proposed by State", 4, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Recommended by DoSEL", 4, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "% of Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "% of Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			Double financialAmountTot = 0d, recommendationFinancialAmountTot = 0d;
			for (MajorComponentProposal listObj : majorComponentStatePlan) {
				if (listObj.getMajorComponentName().equals("Total")) {

					double financialAmount = 0d;
					if (listObj.getFinancialAmount() != null)
						financialAmount = listObj.getFinancialAmount();

					double recommendationFinancialAmount = 0d;
					if (listObj.getRecommendedFinancialAmount() != null)
						recommendationFinancialAmount = listObj.getRecommendedFinancialAmount();

					financialAmountTot = financialAmount;
					recommendationFinancialAmountTot = recommendationFinancialAmount;
				}

			}

			int sno = 1, dataFontSize = 9;

			Double recProposed = 0.0d, nonRecProposed = 0.0d, totProposed = 0.0d, recDoSEL = 0.0d, nonRecDoSEL = 0.0d, totDoSEL = 0.0d;
			Double recProposedTot = 0.0d, nonRecProposedTot = 0.0d, recDoSELTot = 0.0d, nonRecDoSELTot = 0.0d;
			for (MajorComponentProposal listObj : majorComponentStatePlan) {
				if (listObj.getMajorComponentName() != null) {

					if (listObj.getRecuringNonrecuring().equals("R")) {
						recProposed = listObj.getFinancialAmount();
						recDoSEL = listObj.getRecommendedFinancialAmount();

						recProposedTot = recProposedTot + recProposed;
						recDoSELTot = recDoSELTot + recDoSEL;
					} else if (listObj.getRecuringNonrecuring().equals("NR")) {
						nonRecProposed = listObj.getFinancialAmount();
						nonRecDoSEL = listObj.getRecommendedFinancialAmount();

						nonRecProposedTot = nonRecProposedTot + nonRecProposed;
						nonRecDoSELTot = nonRecDoSELTot + nonRecDoSEL;
					} else if (listObj.getRecuringNonrecuring().equals("Total")) {
						totProposed = listObj.getFinancialAmount();
						totDoSEL = listObj.getRecommendedFinancialAmount();
					}

					if (!listObj.getMajorComponentName().equals("Total")) {
						if (listObj.getRecuringNonrecuring().equals("Total")) {
							CommonMethod.createDataCellBold(table, sno + "", 1, 1, dataFontSize, null, TextAlignment.CENTER);
							CommonMethod.createDataCellBold(table, listObj.getMajorComponentName(), 1, 1, dataFontSize, null, TextAlignment.LEFT);

							CommonMethod.createDataCell(table, df.format(recProposed), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, df.format(nonRecProposed), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, df.format(totProposed), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, financialAmountTot==0?dfWithTwoDig.format(0):dfWithTwoDig.format((totProposed == null ? 0 : totProposed) / financialAmountTot * 100), 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);

							CommonMethod.createDataCell(table, df.format(recDoSEL), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, df.format(nonRecDoSEL), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, df.format(totDoSEL), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCell(table, recommendationFinancialAmountTot==0?dfWithTwoDig.format(0):dfWithTwoDig.format((totDoSEL == null ? 0 : totDoSEL) / recommendationFinancialAmountTot * 100), 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);
							sno++;

							recProposed = 0.0d; nonRecProposed = 0.0d; totProposed = 0.0d; recDoSEL = 0.0d; nonRecDoSEL = 0.0d;totDoSEL = 0.0d;
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

							CommonMethod.createDataCellBold(table, df.format(recDoSELTot), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCellBold(table, df.format(nonRecDoSELTot), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCellBold(table, df.format(recommendationFinancialAmountTot), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
							CommonMethod.createDataCellBold(table, "", 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);
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

	private Table getRecommendationTable(Document doc, String planYear, List<RecurringNonRecurring> recommendationList,
			Optional<MastStatesTentative> stateTentive, List<RecurringNonRecurring> statePlanList) throws IOException {

		float[] columnWidths = { .5f, 2.3f, 1.5f, 1.5f, 1f, 1.5f, 1.5f, 1f };
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));

		try {
			Color bgColor = new DeviceRgb(37, 132, 198);
			CommonMethod.createDataCellTableHead(table, "SNo", 1, 2, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Particulars", 1, 2, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "State Plan", 3, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Recommendation", 3, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non-Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

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

			Color bgColorgTotal = new DeviceRgb(227, 237, 243);
			Color bgColorgTotalPercent = new DeviceRgb(230, 255, 230);
			int sno = 1;
			Double totalRecuring = 0.0d, totalNoRecuring = 0.0d, totalRecNonRec = 0.0d;
			for (RecurringNonRecurring listObj : recommendationList) {
				CommonMethod.createDataCellBold(table, sno + "", 1, 1, 9, null, TextAlignment.CENTER);

				if (listObj.getSchemeName() != null && listObj.getSchemeName().equals("Elementary Education")) {
					CommonMethod.createDataCellBold(table, listObj.getSchemeName(), 1, 1, 9, null, TextAlignment.LEFT);

					CommonMethod.createDataCell(table, elementaryRec == null ? "" : df.format(elementaryRec) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, elementaryNonRec == null ? "" : df.format(elementaryNonRec) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, elementaryRecNonRecTot == null ? "" : df.format(elementaryRecNonRecTot) + "", 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

					CommonMethod.createDataCell(table, listObj.getRecuring() == null ? "" : df.format(listObj.getRecuring()) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, listObj.getNoRecuring() == null ? "" : df.format(listObj.getNoRecuring()) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, listObj.getTotal() == null ? "" : df.format(listObj.getTotal()) + "", 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

				}

				if (listObj.getSchemeName() != null && listObj.getSchemeName().equals("Secondary Education")) {
					CommonMethod.createDataCellBold(table, listObj.getSchemeName(), 1, 1, 9, null, TextAlignment.LEFT);
					CommonMethod.createDataCell(table, secondaryRec == null ? "" : df.format(secondaryRec) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, secondaryNonRec == null ? "" : df.format(secondaryNonRec) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, secondaryRecNonRecTot == null ? "" : df.format(secondaryRecNonRecTot) + "", 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

					CommonMethod.createDataCell(table, listObj.getRecuring() == null ? "" : df.format(listObj.getRecuring()) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, listObj.getNoRecuring() == null ? "" : df.format(listObj.getNoRecuring()) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, listObj.getTotal() == null ? "" : df.format(listObj.getTotal()) + "", 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);
				}

				if (listObj.getSchemeName() != null && listObj.getSchemeName().equals("Teacher Education")) {
					CommonMethod.createDataCellBold(table, listObj.getSchemeName(), 1, 1, 9, null, TextAlignment.LEFT);
					CommonMethod.createDataCell(table, teacherRec == null ? "" : df.format(teacherRec) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, teacherNonRec == null ? "" : df.format(teacherNonRec) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, teacherRecNonRecTot == null ? "" : df.format(teacherRecNonRecTot) + "", 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

					CommonMethod.createDataCell(table, listObj.getRecuring() == null ? "" : df.format(listObj.getRecuring()) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, listObj.getNoRecuring() == null ? "" : df.format(listObj.getNoRecuring()) + "", 1, 1, 9, null, TextAlignment.RIGHT);
					CommonMethod.createDataCell(table, listObj.getTotal() == null ? "" : df.format(listObj.getTotal()) + "", 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);
				}

				totalRecuring = totalRecuring + (listObj.getRecuring() != null ? listObj.getRecuring() : 0);
				totalNoRecuring = totalNoRecuring + (listObj.getNoRecuring() != null ? listObj.getNoRecuring() : 0);

				totalRecNonRec = totalRecNonRec + (listObj.getTotal() != null ? listObj.getTotal() : 0d);
				sno++;
			}

			CommonMethod.createDataCellBold(table, sno + "", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Grand Total", 1, 1, 9, bgColorgTotal, TextAlignment.LEFT);

			CommonMethod.createDataCellBold(table, df.format(grantTotalRecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(grantTotalNonrecur), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(grantTotaltotalRecurNonrecur), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, df.format(totalRecuring), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(totalNoRecuring), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, df.format(totalRecNonRec), 1, 1, 9f, bgColorgTotal, TextAlignment.RIGHT);

			Double parcentCentral = stateTentive.get().getCenterSharePercent();
			Double parcentState = (100 - stateTentive.get().getCenterSharePercent());

			CommonMethod.createDataCellBold(table, "5", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "Central Share(" + parcentCentral + "%)", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, "", 2, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, df.format(grantTotaltotalRecurNonrecur * (parcentCentral / 100)), 1, 1, 9f, bgColorgTotalPercent, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, "", 2, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, df.format(totalRecNonRec * (parcentCentral / 100)), 1, 1, 9f, bgColorgTotalPercent, TextAlignment.RIGHT);

			CommonMethod.createDataCellBold(table, "6", 1, 1, 9, null, TextAlignment.CENTER);
			CommonMethod.createDataCellBold(table, "State Share(" + parcentState + "%)", 1, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, "", 2, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, df.format(grantTotaltotalRecurNonrecur * (parcentState / 100)), 1, 1, 9f, bgColorgTotalPercent, TextAlignment.RIGHT);
			CommonMethod.createDataCellBold(table, "", 2, 1, 9, null, TextAlignment.LEFT);
			CommonMethod.createDataCellBold(table, df.format(totalRecNonRec * (parcentState / 100)), 1, 1, 9f, bgColorgTotalPercent, TextAlignment.RIGHT);
		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}
		return table;
	}

	private Table getBudgetExpenditureBarChart(Document doc, String planYear, List<RecurringNonRecurring> budgetRecurNonRecur2324, List<RecurringNonRecurring> expenditureRecurNonRecur2324) throws IOException {
		Table table = new Table(UnitValue.createPercentArray(new float[] { 1f, 1f }));
		table.setWidth(UnitValue.createPercentValue(100));

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
			ImageData barImageDataBudget = DrawChartImage.generateMultiBarChart(barDataBudExpendRecNonRec,colorArr, 4, "Budget(Recurring/Non-Recurring) vs Expenditure(Recurring/Non-Recurring)", "Particulars", "figures (In Lakhs)", 15, 12, 12);
			Image barChartImageBudget = new Image(barImageDataBudget);
			barChartImageBudget.setAutoScale(true);
			// barChartImageBudget.scaleAbsolute(700, 400);
			Cell cellBarLeft = new Cell(1, 1);
			cellBarLeft.add(barChartImageBudget);
			table.addCell(cellBarLeft);

			final DefaultCategoryDataset barDataBudExpendTot = new DefaultCategoryDataset();
			barDataBudExpendTot.addValue(budgetElementaryRecNonRecTot, "Total-Bud", "Elementary");
			barDataBudExpendTot.addValue(expenditureElementaryRecNonRecTot, "Total-Exp", "Elementary");

			barDataBudExpendTot.addValue(budgetSecondaryRecNonRecTot, "Total-Bud", "Secondary");
			barDataBudExpendTot.addValue(expenditureSecondaryRecNonRecTot, "Total-Exp", "Secondary");

			barDataBudExpendTot.addValue(budgetTeacherRecNonRecTot, "Total-Bud", "Teacher");
			barDataBudExpendTot.addValue(expenditureTeacherRecNonRecTot, "Total-Exp", "Teacher");

			ImageData barImageDataExpen = DrawChartImage.generateMultiBarChart(barDataBudExpendTot,colorArr, 3, "Budget(Total) vs Expenditure(Total)", "Particulars", "figures (In Lakhs)", 15, 12, 12);
			Image barChartImageExpen = new Image(barImageDataExpen);
			barChartImageExpen.setAutoScale(true);
			// barChartImageExpen.scaleAbsolute(700, 400);
			Cell cellBarRight = new Cell(1, 1);
			cellBarRight.add(barChartImageExpen);
			table.addCell(cellBarRight);

		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}

		return table;
	}

	private Table getMajorCompoDetails(Document doc, List<MajorComponentProposal> majorComponentProposal, String planYear) throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] { 0.5f, 2f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1.1f, 1f }));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();

		try {
			Color bgColorgTotal = new DeviceRgb(227, 237, 243);
			Color bgColorgTotalPercent = new DeviceRgb(230, 255, 230);
			Color bgColor = new DeviceRgb(37, 132, 198);

			CommonMethod.createDataCellTableHead(table, "SNo", 1, 3, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Major Component", 1, 3, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Figures for F.Y. 2023-24", 9, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Budget Approvals", 3, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Expenditure till 31st March 2024", 3, 1, 10f, bgColor, TextAlignment.CENTER);

			// CommonMethod.createDataCellTableHead(table, "Spillover (Cumulative)" , 2, 1,
			// 10f,bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Expenditure in % against Approval", 3, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			// CommonMethod.createDataCellTableHead(table, "Budget" , 1, 1, 10f,bgColor,
			// TextAlignment.CENTER);
			// CommonMethod.createDataCellTableHead(table, "Expenditure" , 1, 1,
			// 10f,bgColor, TextAlignment.CENTER);

			CommonMethod.createDataCellTableHead(table, "Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Non- Recurring", 1, 1, 10f, bgColor, TextAlignment.CENTER);
			CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);

			int sno = 1;
			float dataFontSize = 9;
			for (MajorComponentProposal listObj : majorComponentProposal) {
				if (listObj.getMajorComponentName() != null) {
					CommonMethod.createDataCellBold(table, sno + "", 1, 1, dataFontSize, null, TextAlignment.CENTER);

					if (!listObj.getMajorComponentName().equals("Total")) {
						CommonMethod.createDataCellBold(table, listObj.getMajorComponentName(), 1, 1, dataFontSize, null, TextAlignment.LEFT);

						CommonMethod.createDataCell(table, listObj.getApprovedBudgetRecurring() == null ? "" : df.format(listObj.getApprovedBudgetRecurring()), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getApprovedBudgetNonRecurring() == null ? "" : df.format(listObj.getApprovedBudgetNonRecurring()), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getTotApprovedBudget() == null ? "" : df.format(listObj.getTotApprovedBudget()), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);

						CommonMethod.createDataCell(table, listObj.getExpenditureRecurring_31() == null ? "" : df.format(listObj.getExpenditureRecurring_31()), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getExpenditureNonRecurring_31() == null ? "" : df.format(listObj.getExpenditureNonRecurring_31()), 1, 1, dataFontSize, null, TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getTotExpenditure() == null ? "" : df.format(listObj.getTotExpenditure()), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);

						// CommonMethod.createDataCell(table,
						// listObj.getSpillOverApprovalBudget23()==null?"":df.format(listObj.getSpillOverApprovalBudget23()),
						// 1, 1, dataFontSize,null,TextAlignment.RIGHT);
						// CommonMethod.createDataCell(table,
						// listObj.getAnticipatedExpenditureSpillOver()==null?"":df.format(listObj.getAnticipatedExpenditureSpillOver()),
						// 1, 1, dataFontSize,null,TextAlignment.RIGHT);

						CommonMethod.createDataCell(table, listObj.getApprovedBudgetRecurring() == 0 ? dfWithTwoDig.format(0) : dfWithTwoDig.format(listObj.getExpenditureRecurring_31() / listObj.getApprovedBudgetRecurring() * 100), 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getApprovedBudgetNonRecurring() == 0 ? dfWithTwoDig.format(0) : dfWithTwoDig.format(listObj.getExpenditureNonRecurring_31() / listObj.getApprovedBudgetNonRecurring() * 100), 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);
						CommonMethod.createDataCell(table, listObj.getTotApprovedBudget() == 0 ? dfWithTwoDig.format(0) : dfWithTwoDig.format(listObj.getTotExpenditure() / listObj.getTotApprovedBudget() * 100), 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);

					} else {
						CommonMethod.createDataCellBold(table, listObj.getMajorComponentName(), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.LEFT);

						CommonMethod.createDataCellBold(table, listObj.getApprovedBudgetRecurring() == null ? "" : df.format(listObj.getApprovedBudgetRecurring()), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getApprovedBudgetNonRecurring() == null ? "" : df.format(listObj.getApprovedBudgetNonRecurring()), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getTotApprovedBudget() == null ? "" : df.format(listObj.getTotApprovedBudget()), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);

						CommonMethod.createDataCellBold(table, listObj.getExpenditureRecurring_31() == null ? "" : df.format(listObj.getExpenditureRecurring_31()), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getExpenditureNonRecurring_31() == null ? "" : df.format(listObj.getExpenditureNonRecurring_31()), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getTotExpenditure() == null ? "" : df.format(listObj.getTotExpenditure()), 1, 1, dataFontSize, bgColorgTotal, TextAlignment.RIGHT);

						// CommonMethod.createDataCellBold(table,
						// listObj.getSpillOverApprovalBudget23()==0?dfWithTwoDig.format(0):df.format(listObj.getSpillOverApprovalBudget23()),
						// 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);
						// CommonMethod.createDataCellBold(table,
						// listObj.getAnticipatedExpenditureSpillOver()==null?"":df.format(listObj.getAnticipatedExpenditureSpillOver()),
						// 1, 1, dataFontSize,bgColorgTotal,TextAlignment.RIGHT);

						CommonMethod.createDataCellBold(table, listObj.getApprovedBudgetRecurring() == 0 ? dfWithTwoDig.format(0) : dfWithTwoDig.format(listObj.getExpenditureRecurring_31() / listObj.getApprovedBudgetRecurring() * 100), 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getApprovedBudgetNonRecurring() == 0 ? dfWithTwoDig.format(0) : dfWithTwoDig.format(listObj.getExpenditureNonRecurring_31() / listObj.getApprovedBudgetNonRecurring() * 100), 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);
						CommonMethod.createDataCellBold(table, listObj.getTotApprovedBudget() == 0 ? dfWithTwoDig.format(0) : dfWithTwoDig.format(listObj.getTotExpenditure() / listObj.getTotApprovedBudget() * 100), 1, 1, dataFontSize, bgColorgTotalPercent, TextAlignment.RIGHT);
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
	


	private Table getSummaryGlance(Document doc, String planYear,
			List<RecurringNonRecurring> budgetRecurNonRecur2324, List<RecurringNonRecurring> expenditureRecurNonRecur2324) throws IOException {

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
		float[] columnWidths = { 2.5f, 1.5f, 2.5f, 1.5f, 1.5f, 1.5f };
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));

		try {
			if (stateTentive.get() != null) {
				Double parcentCentral = (stateTentive.get().getCenterSharePercent() != null ? stateTentive.get().getCenterSharePercent() : 0);
				Double reParcentState = (100 - (stateTentive.get().getCenterSharePercent() != null ? stateTentive.get().getCenterSharePercent() : 0));

				Color bgColor = new DeviceRgb(37, 132, 198);
				Color bgColorgTotal = new DeviceRgb(227, 237, 243);

				CommonMethod.createDataCellTableHead(table, "Central Share(" + parcentCentral + "%)", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellBold(table, stateTentive.get().getTentativeCentralShare() == null ? "" : df.format(stateTentive.get().getTentativeCentralShare()), 1, 1, 9, null, TextAlignment.RIGHT);

				CommonMethod.createDataCellTableHead(table, "State Share(" + reParcentState + "%)", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellBold(table, stateTentive.get().getTentativeStateShare() == null ? "" : df.format(stateTentive.get().getTentativeStateShare()), 1, 1, 9, null, TextAlignment.RIGHT);

				CommonMethod.createDataCellTableHead(table, "Total", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellBold(table, stateTentive.get().getTentativeTotalEstimates() == null ? "" : df.format(stateTentive.get().getTentativeTotalEstimates()), 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}

		return table;
	}

	
	// ---getTableReportData start here----------------------
	private Table getTableReportData(Document doc, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> groupedByFiveAttributes, Integer schemeKey,
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue555555, int mainMapSize)
			throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] { 1.2f, 1.2f, 1.2f, 2.3f, 0.4f,1.0f, 1.0f, 1.0f, 0.8f, 0.7f, 1.0f, 2.6f }));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();

		try {
			float fHeader = 8.0f, fData = 6.5f;
			if (schemeKey != 555555) {
				Color bgColorhead = new DeviceRgb(37, 132, 198);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Major Component", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Sub Component", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Activity", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Sub Activity", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "R/ NR", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);

//					CommonMethod.createDataCellTableHeadEveryPage(table, "State Budget F.Y. 23-24 (in Lakhs)", 4, 1, fHeader, bgColorhead, TextAlignment.CENTER);

				CommonMethod.createDataCellTableHeadEveryPage(table, "Proposed by State", 3, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Recommended by DoSEL", 3, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Coordinator Remarks", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);

//					CommonMethod.createDataCellTableHeadEveryPage(table, "Approved Budget", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
//					CommonMethod.createDataCellTableHeadEveryPage(table, "Fresh Exp.", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
//					CommonMethod.createDataCellTableHeadEveryPage(table, "Spillover Cumu.", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
//					CommonMethod.createDataCellTableHeadEveryPage(table, "Spillover Exp.", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);

				CommonMethod.createDataCellTableHeadEveryPage(table, "Phy Qty", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Unit Cost", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Amount (In Lakhs)", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);

				CommonMethod.createDataCellTableHeadEveryPage(table, "Phy Qty", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Unit Cost", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Amount (In Lakhs)", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
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
									if (listObj.getSchemeId() != null) {
										if (listObj.getSchemeId() != 555555) {
											if (schemeFlag) {
												String schemeName = "Schem Name : " + listObj.getSchemeId().toString() + " - " + (listObj.getSchemeName() == null ? "" : listObj.getSchemeName());
												CommonMethod.createDataCellBold(table, schemeName, 16, 1, 10, null, TextAlignment.LEFT);
												schemeFlag = false;
											}
											if (majorCompSize != 0 && listObj.getMajorComponentId() != 666666) {
												String majorComponentName = (majaorSrNo) + " - " + (listObj.getMajorComponentName() == null ? "" : listObj.getMajorComponentName());
												CommonMethod.createDataCell(table, majorComponentName, 1, majorCompSize, fData, null, TextAlignment.LEFT);
											}
											majorCompSize = 0;

											if (subCompSize != 0 && listObj.getSubComponentId() != 777777 && listObj.getMajorComponentId() != 666666) {
												String subComponentName = (majaorSrNo) + "." + subCompSrNo + " - " + (listObj.getSubComponentName() == null ? "" : listObj.getSubComponentName());
												CommonMethod.createDataCell(table, subComponentName, 1, subCompSize, fData, null, TextAlignment.LEFT);
											}
											subCompSize = 0;

											if (activitySize != 0 && listObj.getActivityMasterId() != 888888 && listObj.getSubComponentId() != 777777 && listObj.getMajorComponentId() != 666666) {
												String activityMasterName = (majaorSrNo) + "." + subCompSrNo + "." + activitySrNo + " - "
														+ (listObj.getActivityMasterName() == null ? "" : listObj.getActivityMasterName());
												CommonMethod.createDataCell(table, activityMasterName, 1, activitySize, fData, null, TextAlignment.LEFT);
											}
											activitySize = 0;

										}

										Boolean isTotal = false;
										if (listObj.getSchemeId() == 555555) {
											Color bgColorgTotal = new DeviceRgb(227, 237, 243);
											CommonMethod.createDataCellBold(table, "Grand Total of All Scheme ", 5, 1, fData, bgColorgTotal, TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getMajorComponentId() == 666666) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getSchemeName(), 5, 1, fData, null, TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getSubComponentId() == 777777) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getMajorComponentName(), 4, 1, fData, null, TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getActivityMasterId() == 888888) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getSubComponentName(), 3, 1, fData, null, TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getActivityMasterDetailsId() == 999999) {
											CommonMethod.createDataCellBold(table, "Sub Total", 2, 1, fData, null, TextAlignment.RIGHT);
											isTotal = true;
										} else {
											subActivitySrNo++;
											CommonMethod.createDataCell(table, (subActivitySrNo) + "-" + (listObj.getActivityMasterDetailName() == null ? "" : listObj.getActivityMasterDetailName()), 1, 1, fData, null, TextAlignment.LEFT);
										}

										if (isTotal) {
											Color bgColor = null;
											if (listObj.getSchemeId() == 555555) {
												bgColor = new DeviceRgb(227, 237, 243);
												fData = 6f;
											} else
												fData = 6.5f;

											// CommonMethod.createDataCellBold(table,
											// listObj.getRecuringNonrecuring().equals("NA") ? "" :
											// listObj.getRecuringNonrecuring() + "", 1,
											// 1,fData,bgColor,TextAlignment.CENTER);
//												CommonMethod.createDataCellBold(table, listObj.getTotApprovedBudget() == null ? "" : listObj.getTotApprovedBudget() == 0 ? "" : df.format(listObj.getTotApprovedBudget()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
//												CommonMethod.createDataCellBold(table, listObj.getTotExpenditure() == null ? "" : listObj.getTotExpenditure() == 0 ? "" : df.format(listObj.getTotExpenditure()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
//												CommonMethod.createDataCellBold(table, listObj.getSpillOverApprovalBudget23() == null ? "" : listObj.getSpillOverApprovalBudget23() == 0 ? "" : df.format(listObj.getSpillOverApprovalBudget23()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
//												CommonMethod.createDataCellBold(table, listObj.getAnticipatedExpenditureSpillOver() == null ? "" : listObj.getAnticipatedExpenditureSpillOver() == 0 ? "" : df.format(listObj.getAnticipatedExpenditureSpillOver()), 1, 1, fData, bgColor, TextAlignment.RIGHT);

											CommonMethod.createDataCellBold(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() == 0 ? "" : listObj.getPhysicalQuantity() + "", 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, "", 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, listObj.getFinancialAmount() == null ? "" : listObj.getFinancialAmount() == 0 ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, fData, bgColor, TextAlignment.RIGHT);

											CommonMethod.createDataCellBold(table, listObj.getProposedPhysicalQuantity() == null ? "" : listObj.getProposedPhysicalQuantity() == 0 ? "" : dfWithoutZero.format(listObj.getProposedPhysicalQuantity()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, "", 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, listObj.getProposedFinancialAmount() == null ? "" : listObj.getProposedFinancialAmount() == 0 ? "" : df.format(listObj.getProposedFinancialAmount()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, "", 1, 1, fData, bgColor, TextAlignment.RIGHT);

										} else {

											Color bgColor = null;
											if (listObj.getProposedFinancialAmount() != null && listObj.getFinancialAmount() != null && listObj.getProposedFinancialAmount() > 0 && listObj.getProposedFinancialAmount() < listObj.getFinancialAmount()) {
												bgColor = new DeviceRgb(255, 255, 255);// Yellow color
											} else if (listObj.getProposedFinancialAmount() != null && listObj.getFinancialAmount() != null && listObj.getFinancialAmount() > 0 && listObj.getProposedFinancialAmount() == 0) {
												bgColor = new DeviceRgb(255, 204, 204);// Red color
											}

											CommonMethod.createDataCell(table, listObj.getRecuringNonrecuring() == null ? "" : listObj.getRecuringNonrecuring().equals("NA") ? "" : listObj.getRecuringNonrecuring(), 1, 1, fData, bgColor, TextAlignment.CENTER);
//												CommonMethod.createDataCell(table, listObj.getTotApprovedBudget() == null ? "" : listObj.getTotApprovedBudget() == 0 ? "" : df.format(listObj.getTotApprovedBudget()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
//												CommonMethod.createDataCell(table, listObj.getTotExpenditure() == null ? "" : listObj.getTotExpenditure() == 0 ? "" : df.format(listObj.getTotExpenditure()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
//												CommonMethod.createDataCell(table, listObj.getSpillOverApprovalBudget23() == null ? "" : listObj.getSpillOverApprovalBudget23() == 0 ? "" : df.format(listObj.getSpillOverApprovalBudget23()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
//												CommonMethod.createDataCell(table, listObj.getAnticipatedExpenditureSpillOver() == null ? "" : listObj.getAnticipatedExpenditureSpillOver() == 0 ? "" : df.format(listObj.getAnticipatedExpenditureSpillOver()), 1, 1, fData, bgColor, TextAlignment.RIGHT);

											CommonMethod.createDataCell(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() == 0 ? "" : listObj.getPhysicalQuantity() + "", 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getUnitCost() == null ? "" : listObj.getUnitCost() == 0 ? "" : df.format(listObj.getUnitCost()) + "", 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getFinancialAmount() == null ? "" : listObj.getFinancialAmount() == 0 ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, fData, bgColor, TextAlignment.RIGHT);

											CommonMethod.createDataCell(table, listObj.getProposedPhysicalQuantity() == null ? "" : listObj.getProposedPhysicalQuantity() == 0 ? "" : dfWithoutZero.format(listObj.getProposedPhysicalQuantity()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getProposedUnitCost() == null ? "" : listObj.getProposedUnitCost() == 0 ? "" : df.format(listObj.getProposedUnitCost()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getProposedFinancialAmount() == null ? "" : listObj.getProposedFinancialAmount() == 0 ? "" : df.format(listObj.getProposedFinancialAmount()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getCoordinatorRemarks() == null ? "" : listObj.getCoordinatorRemarks(), 1, 1, fData, bgColor, TextAlignment.LEFT);
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
