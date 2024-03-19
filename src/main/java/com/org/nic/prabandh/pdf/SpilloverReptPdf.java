package com.org.nic.prabandh.pdf;

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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.org.nic.prabandh.bean.SpillOverReportDto;
import com.org.nic.prabandh.constant.Constants;
import com.org.nic.prabandh.utill.CommonMethod;

@Component
public class SpilloverReptPdf {

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
					.showTextAligned("Generated on " + formattedDate, 640, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
					.showTextAligned("https://prabandh.education.gov.in", 640, 15, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			
			if (i > 1) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(9).setFontColor(new DeviceRgb(165, 42, 42))
				.showTextAligned("Spill Over  - " + regionName, 37, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
				.showTextAligned("F. Y. - " + planYear, 725, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
			}
			/*if (i > 1 && i <= 6) {
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
			}*/

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

	public ResponseEntity<?> downloadSpilloverReptPdf(String planYear,String regionName,
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<SpillOverReportDto>>>>> groupedByFields) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PdfWriter write = new PdfWriter(byteArrayOutputStream);
		write.setSmartMode(true);

		PdfDocument pdfDoc = new PdfDocument(write);
		pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

		Document doc = new Document(pdfDoc);

		// first page paragraph----Start-----------
		Color paraFColor1 = new DeviceRgb(165, 42, 42);
		doc.add(CommonMethod.createParaGraphBold("Spill Over Details Sheet", 30f, 0f, 35, paraFColor1, null, TextAlignment.CENTER));
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
		if (groupedByFields.size() > 1) {
			// report data-----Start------------
			int majaorSrNo = 0;
			for (Map.Entry<Integer, Map<Integer, Map<Integer, Map<Integer, List<SpillOverReportDto>>>>> schemeEntry : groupedByFields.entrySet()) {
				Map<Integer,  Map<Integer, Map<Integer, List<SpillOverReportDto>>>> majorValue = schemeEntry.getValue();

				Map<Integer,  Map<Integer, Map<Integer, List<SpillOverReportDto>>>> majorValue666666 = groupedByFields.get(666666);
				Integer majorKey = schemeEntry.getKey();

				if (majorKey == 666666)
					break;

				majaorSrNo++;
				Table tableDetailsReport = getTableReportData(doc, groupedByFields, majorKey,majaorSrNo, majorValue, majorValue666666, (groupedByFields.size() - 1));
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
			bytes = addFooterAndPageNumbers(bytes, regionName, planYear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=Spill Over Report for " + regionName + ".pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bytes);
	}

	
	
	
	
	// ---Main Report start here----------------------
	private Table getTableReportData(Document doc, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<SpillOverReportDto>>>>> groupedByFields, Integer majorKey,
			int majaorSrNo, Map<Integer, Map<Integer, Map<Integer, List<SpillOverReportDto>>>> majorValue, Map<Integer, Map<Integer, Map<Integer, List<SpillOverReportDto>>>> majorValue666666, int mainMapSize)
			throws IOException {

		float[] tableWidth={2.0f, 2.0f, 2.5f,  0.8f, 1.2f, 0.9f, 0.8f, 1.2f, 0.8f, 0.8f, 0.8f, 1.2f};
		Table table = new Table(UnitValue.createPercentArray(tableWidth));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();

		try {
			float fHeader = 8.3f, fData = 8f;
			if (majorKey != 666666) {
				Color bgColorhead = new DeviceRgb(37, 132, 198);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Sub Component", 1, 3, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Activity", 1, 3, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Sub Activity", 1, 3, fHeader, bgColorhead, TextAlignment.CENTER);

				CommonMethod.createDataCellTableHeadEveryPage(table, "Budget Approved(Cummulative)", 2, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Cummulative Progress (Since Inception)", 3, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Spill Over", 4, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				
				CommonMethod.createDataCellTableHeadEveryPage(table, "Physical", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Financial", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Physical", 2, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Financial", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Physical", 3, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Financial ", 1, 2, fHeader, bgColorhead, TextAlignment.CENTER);
				
				

				CommonMethod.createDataCellTableHeadEveryPage(table, "Complete", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "In-progress", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "In-progress", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Not Started", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Total", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
			}

			int loopTimes = 1;
			if (majaorSrNo == mainMapSize)
				loopTimes = 2;

			for (int i = 1; i <= loopTimes; i++) {
				if (majaorSrNo == mainMapSize && i == 2) {
					majorValue = majorValue666666;
				}

				Boolean majorFlag = true;
				int subCompSrNo = 0;
				
				
				
				
				
				
				
				
				
				for (Map.Entry<Integer, Map<Integer, Map<Integer, List<SpillOverReportDto>>>> innerEntry : majorValue.entrySet()) {
					Map<Integer, Map<Integer, List<SpillOverReportDto>>> innermostValue = innerEntry.getValue();
					subCompSrNo++;

					// calculate only major sub component size-----start---------------
					int subCompSize = 0;
					for (Map.Entry<Integer, Map<Integer, List<SpillOverReportDto>>> deepestEntry : innermostValue.entrySet()) {
						Map<Integer, List<SpillOverReportDto>> deepestValue = deepestEntry.getValue();
						for (Map.Entry<Integer, List<SpillOverReportDto>> finalEntry : deepestValue.entrySet()) {
							List<SpillOverReportDto> finalValueList = finalEntry.getValue();
							int listSize = finalValueList.size();
							subCompSize = subCompSize + listSize;
						}
					}
					// calculate only major sub component end-----end---------------

					int activitySrNo = 0;
					for (Map.Entry<Integer, Map<Integer, List<SpillOverReportDto>>> deepestEntry : innermostValue.entrySet()) {
						Map<Integer, List<SpillOverReportDto>> deepestValue = deepestEntry.getValue();
						Integer deepestValue1 = deepestEntry.getKey();
						activitySrNo++;

						// calculate only activity size-----start---------------
						int activitySize = 0;
						for (Map.Entry<Integer, List<SpillOverReportDto>> finalEntry : deepestValue.entrySet()) {
							List<SpillOverReportDto> finalValueList = finalEntry.getValue();
							int listSize = finalValueList.size();
							activitySize = activitySize + listSize;
						}
						// calculate only activity size-----end---------------

						int subActivitySrNo = 0;
						for (Map.Entry<Integer, List<SpillOverReportDto>> finalEntry : deepestValue.entrySet()) {

							List<SpillOverReportDto> finalValueList = finalEntry.getValue();

							for (SpillOverReportDto listObj : finalValueList) {
								if (listObj.getMajor_component_id() != null) {
									if (listObj.getMajor_component_id() != 666666) {
										if (majorFlag) {
											String majorName = "Major Name : " + majaorSrNo + "-" + (listObj.getMajor_component_name() == null ? "" : listObj.getMajor_component_name());
											CommonMethod.createDataCellBold(table, majorName, tableWidth.length, 1, 10, null, TextAlignment.LEFT);
											majorFlag = false;
										}
										

										if (subCompSize != 0 && listObj.getSub_component_id() != 777777 && listObj.getMajor_component_id() != 666666) {
											//String subComponentName = subCompSrNo + "-" + (listObj.getSub_component_name() == null ? "" : listObj.getSub_component_name());
											//CommonMethod.createDataCell(table, subComponentName, 1, subCompSize, fData, null, TextAlignment.LEFT);
											Table tableInn = new Table(UnitValue.createPercentArray(new float[]{0.1f, 6f}));
											tableInn.addCell(CommonMethod.createCellBold(subCompSrNo+"", 1, 1, fData).setBorder(null).setFontColor(new DeviceRgb(120, 12, 44)).setPaddingLeft(0).setPaddingRight(1));
											tableInn.addCell(CommonMethod.createCell((listObj.getSub_component_name() == null ? "" : listObj.getSub_component_name()), 1, 1, fData).setBorder(null));
											table.addCell(new Cell(subCompSize,1).add(tableInn).setBorder(new SolidBorder(new DeviceRgb(111,107,107), 0.5f)));
										}
										subCompSize = 0;

										if (activitySize != 0 && listObj.getActivity_master_id() != 888888 && listObj.getSub_component_id() != 777777 && listObj.getMajor_component_id() != 666666) {
											//String activityMasterName = subCompSrNo + "." + activitySrNo + " - " + (listObj.getActivity_master_name() == null ? "" : listObj.getActivity_master_name());
											//CommonMethod.createDataCell(table, activityMasterName, 1, activitySize, fData, null, TextAlignment.LEFT);
											
											Table tableInn = new Table(UnitValue.createPercentArray(new float[]{0.1f, 6f}));
											tableInn.addCell(CommonMethod.createCellBold(subCompSrNo + "." + activitySrNo, 1, 1, fData).setBorder(null).setFontColor(new DeviceRgb(120, 12, 44)).setPaddingLeft(0).setPaddingRight(1));
											tableInn.addCell(CommonMethod.createCell((listObj.getActivity_master_name() == null ? "" : listObj.getActivity_master_name()), 1, 1, fData).setBorder(null));
											table.addCell(new Cell(activitySize,1).add(tableInn).setBorder(new SolidBorder(new DeviceRgb(111,107,107), 0.5f)));
										}
										activitySize = 0;

									}

									Boolean isTotal = false;
									if (listObj.getMajor_component_id() == 666666) {
										Color bgColorgTotal = new DeviceRgb(227, 237, 243);
										CommonMethod.createDataCellBold(table, "Grand Total of All Major ", 3, 1, fData, bgColorgTotal, TextAlignment.RIGHT);
										isTotal = true;
									} else if (listObj.getSub_component_id() == 777777) {
										CommonMethod.createDataCellBold(table, "Total of " + listObj.getMajor_component_name(), 3, 1, fData, null, TextAlignment.RIGHT);
										isTotal = true;
									} else if (listObj.getActivity_master_id() == 888888) {
										CommonMethod.createDataCellBold(table, "Total of " + listObj.getSub_component_name(), 2, 1, fData, null, TextAlignment.RIGHT);
										isTotal = true;
									} else if (listObj.getActivity_master_details_id() == 999999) {
										CommonMethod.createDataCellBold(table, "Sub Total", 1, 1, fData, null, TextAlignment.RIGHT);
										isTotal = true;
									} else {
										subActivitySrNo++;
										//CommonMethod.createDataCell(table, (subActivitySrNo) + "-" + (listObj.getActivity_master_details_name() == null ? "" : listObj.getActivity_master_details_name()), 1, 1, fData, null, TextAlignment.LEFT);
									
										Table tableInn = new Table(UnitValue.createPercentArray(new float[]{0.1f, 6f}));
										tableInn.addCell(CommonMethod.createCellBold(subActivitySrNo+"", 1, 1, fData).setBorder(null).setFontColor(new DeviceRgb(120, 12, 44)).setPaddingLeft(0).setPaddingRight(1));
										tableInn.addCell(CommonMethod.createCell((listObj.getActivity_master_details_name() == null ? "" : listObj.getActivity_master_details_name()), 1, 1, fData).setBorder(null));
										table.addCell(new Cell(1,1).add(tableInn).setBorder(new SolidBorder(new DeviceRgb(111,107,107), 0.5f)));
									}

									if (isTotal) {
										Color bgColor = null;
										if (listObj.getMajor_component_id() == 666666) {
											bgColor = new DeviceRgb(227, 237, 243);
											fData = 8f;
										}

										CommonMethod.createDataCellBold(table, listObj.getTotal_physical_budget_approved() == null ? "0" : listObj.getTotal_physical_budget_approved() == 0 ? "" : dfWithoutZero.format(listObj.getTotal_physical_budget_approved()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCellBold(table, listObj.getTotal_financial_budget_approved() == null ? "0" : listObj.getTotal_financial_budget_approved() == 0 ? "" : df.format(listObj.getTotal_financial_budget_approved()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										
										CommonMethod.createDataCellBold(table, listObj.getPhysical_quantity_progress_complete_inception() == null ? "0"  : dfWithoutZero.format(listObj.getPhysical_quantity_progress_complete_inception()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCellBold(table, listObj.getPhysical_quantity_progress_progress_inception() == null ? "0" : listObj.getPhysical_quantity_progress_progress_inception() == 0 ? "" : dfWithoutZero.format(listObj.getPhysical_quantity_progress_progress_inception()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCellBold(table, listObj.getFinancial_amount_progress_inception() == null ? "0" : df.format(listObj.getFinancial_amount_progress_inception()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										
										CommonMethod.createDataCellBold(table, listObj.getPhysical_quantity_progress_progress_inception() == null ? "0": dfWithoutZero.format(listObj.getPhysical_quantity_progress_progress_inception()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCellBold(table, listObj.getPhysical_quantity_not_started() == null ? "0" :  dfWithoutZero.format(listObj.getPhysical_quantity_not_started()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										
										CommonMethod.createDataCellBold(table, listObj.getPhysical_quantity_spill_over() == null ? "0" : dfWithoutZero.format(listObj.getPhysical_quantity_spill_over()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCellBold(table, listObj.getFinancial_amount_spill_over() == null ? "0" : df.format(listObj.getFinancial_amount_spill_over()), 1, 1, fData, bgColor, TextAlignment.RIGHT);

										

									} else {

										Color bgColor = null;
										/*if (listObj.getTotal_physical_budget_approved() != null && listObj.getTotal_financial_budget_approved() != null && listObj.getTotal_physical_budget_approved() > 0 && listObj.getTotal_financial_budget_approved() < listObj.getTotal_financial_budget_approved()) {
											bgColor = new DeviceRgb(255, 255, 153);// Yellow color
										} else if (listObj.getTotal_financial_budget_approved() != null && listObj.getTotal_financial_budget_approved() != null && listObj.getTotal_financial_budget_approved() > 0 && listObj.getTotal_financial_budget_approved() == 0) {
											bgColor = new DeviceRgb(255, 204, 204);// Red color
										}*/

										
										CommonMethod.createDataCell(table, listObj.getTotal_physical_budget_approved() == null ? "0"  : dfWithoutZero.format(listObj.getTotal_physical_budget_approved()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCell(table, listObj.getTotal_financial_budget_approved() == null ? "0" : df.format(listObj.getTotal_financial_budget_approved()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										
										CommonMethod.createDataCell(table, listObj.getPhysical_quantity_progress_complete_inception() == null ? "0" :  dfWithoutZero.format(listObj.getPhysical_quantity_progress_complete_inception()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCell(table, listObj.getPhysical_quantity_progress_progress_inception() == null ? "0" :  dfWithoutZero.format(listObj.getPhysical_quantity_progress_progress_inception()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCell(table, listObj.getFinancial_amount_progress_inception() == null ? "0" : df.format(listObj.getFinancial_amount_progress_inception()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										
										CommonMethod.createDataCell(table, listObj.getPhysical_quantity_progress_progress_inception() == null ? "0" :  dfWithoutZero.format(listObj.getPhysical_quantity_progress_progress_inception()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCell(table, listObj.getPhysical_quantity_not_started() == null ? "0" : dfWithoutZero.format(listObj.getPhysical_quantity_not_started()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										
										CommonMethod.createDataCell(table, listObj.getPhysical_quantity_spill_over() == null ? "0" : dfWithoutZero.format(listObj.getPhysical_quantity_spill_over()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										CommonMethod.createDataCell(table, listObj.getFinancial_amount_spill_over() == null ? "0" : df.format(listObj.getFinancial_amount_spill_over()), 1, 1, fData, bgColor, TextAlignment.RIGHT);
										
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
