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
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.constant.Constants;

@Component
public class DistrictCostingReportPdf {
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
					.showTextAligned("Generated on " + formattedDate, 403, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
					.showTextAligned("https://prabandh.education.gov.in", 403, 15, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);

			if (i > 1) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10).setFontColor(new DeviceRgb(165, 42, 42))
				.showTextAligned("Budget Demand  - " + regionName, 37, 820, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0)
				.showTextAligned("F. Y. - " + planYear, 475, 820, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
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

	public ResponseEntity<?> downloadCostingReportPdf(String planYear, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> costingReportMap, String regionName) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PdfWriter write = new PdfWriter(byteArrayOutputStream);
		write.setSmartMode(true);

		PdfDocument pdfDoc = new PdfDocument(write);
		pdfDoc.setDefaultPageSize(PageSize.A4);

		Document doc = new Document(pdfDoc);

		// first page paragraph----Start-----------
		doc.add(CommonMethod.createHeadingParaGraph("Costing Sheet", 100f, 0f, 35, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph("(Samagra Shiksha)", 10f, 0f, 40, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph("of", 25f, 0f, 20, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph(regionName, 25f, 0f, 35, new DeviceRgb(165, 42, 42)));
		doc.add(CommonMethod.createHeadingParaGraph(planYear, 25f, 0f, 40, new DeviceRgb(165, 42, 42)));
		//doc.add(CommonMethod.createHeadingParaGraph("(Prepared by - " + regionName + ")", 50f, 0f, 15, new DeviceRgb(12, 49, 99)));
		doc.add(CommonMethod.createHeadingParaGraph("(Recommended by Dept. Of School Education & Literacy Govt. Of India)", 50f, 0f, 15, new DeviceRgb(12, 49, 99)));
		
		
		// first page paragraph---End------------

		if (costingReportMap.size() > 1) {
			// report data-----Start------------

			Table table = new Table(UnitValue.createPercentArray(new float[] { 1.5f, 1.5f, 1.5f, 3.2f, 1.0f, 1.0f, 1.0f }));
			table.setWidth(UnitValue.createPercentValue(100));

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
		} else {
			// doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			doc.add(CommonMethod.createHeadingParaGraph("Data not avaliable", 50f, 0f, 25, new DeviceRgb(0, 0, 0)));
		}
		// report data-----end------------

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

	private Table getCostingReportStateWise(Document doc, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> groupedByFiveAttributes, Integer schemeKey,
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue555555)
			throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] { 1.5f, 1.5f, 1.5f, 3.2f, 1.0f, 1.0f, 1.0f }));
		table.setWidth(UnitValue.createPercentValue(100));

		if (schemeKey != 555555) {
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Major Component", 1, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Sub Component", 1, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Activity", 1, 1, 10f, TextAlignment.CENTER);
			CommonMethod.createDataCelltableHeaderWithBgBlue(table, "Sub Activity", 1, 1, 10f, TextAlignment.CENTER);
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
										CommonMethod.createDataCellBoldLeft(table, schemeName, 7, 1, 10);
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
									CommonMethod.createDataCellCategoryWithBorderLeft(table,
											(subActivitySrNo) + "-" + (listObj.getActivityMasterDetailName() == null ? "" : listObj.getActivityMasterDetailName()), 1, 1);
								}

								if (isTotal) {
									if (listObj.getSchemeId() != 555555) {
										CommonMethod.createDataCellBoldRight(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1,9);
										CommonMethod.createDataCellBoldRight(table, "", 1, 1,9);
										CommonMethod.createDataCellBoldRight(table, listObj.getFinancialAmount() == null ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1,9);
									}
								} else {
									CommonMethod.createDataCellCategoryWithBorderRight(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1, 9);
									CommonMethod.createDataCellCategoryWithBorderRight(table, listObj.getUnitCost() == null ? "" : df.format(listObj.getUnitCost()) + "", 1, 1, 9);
									CommonMethod.createDataCellCategoryWithBorderRight(table, listObj.getFinancialAmount() == null ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, 9);
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
