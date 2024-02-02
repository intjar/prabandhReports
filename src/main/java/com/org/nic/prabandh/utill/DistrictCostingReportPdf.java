package com.org.nic.prabandh.utill;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.constant.Constants;

@Component
public class DistrictCostingReportPdf {
	DecimalFormat df = new DecimalFormat("0.00000");

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

		SimpleDateFormat sdf = new SimpleDateFormat(Constants.META_DATA_DATE_FORMAT);
		String formattedDate = sdf.format(new Date());

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

	public ResponseEntity<?> downloadDistrictCostingReptPdf(String planYear, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> costingReportMap,
			String regionName) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PdfWriter write = new PdfWriter(byteArrayOutputStream);
		write.setSmartMode(true);

		PdfDocument pdfDoc = new PdfDocument(write);
		pdfDoc.setDefaultPageSize(PageSize.A4);

		Document doc = new Document(pdfDoc);
		Color paraFColor1 = new DeviceRgb(165, 42, 42);
		// first page paragraph----Start-----------
		doc.add(CommonMethod.createParaGraphBold("Costing Sheet", 100f, 0f, 35, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("(Samagra Shiksha)", 10f, 0f, 40, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold("of", 25f, 0f, 20, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold(regionName == null ? "" : regionName, 25f, 0f, 35, paraFColor1, null, TextAlignment.CENTER));
		doc.add(CommonMethod.createParaGraphBold(planYear == null ? "" : planYear, 25f, 0f, 40, paraFColor1, null, TextAlignment.CENTER));

		doc.add(CommonMethod.createParaGraphBold("(Recommended by Dept. Of School Education & Literacy Govt. Of India)", 50f, 0f, 15, new DeviceRgb(12, 49, 99), null, TextAlignment.CENTER));
		// first page paragraph---End------------
		
		
		Color paraFColor2 = new DeviceRgb(0, 0, 0);
		if (costingReportMap.size() > 1) {
			
			
			
			
			
			
			
			
			
			
			////////////////////////////////////////////////////////////////////////////////
			Table tableChart = new Table(UnitValue.createPercentArray(new float[] { 1f }));
			tableChart.setWidth(UnitValue.createPercentValue(100));
			tableChart.setFixedLayout();
			try {
				doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				DefaultCategoryDataset dataset = new DefaultCategoryDataset();
				dataset.addValue(10, "Series 1", "Category 1");
				dataset.addValue(20, "Series 2", "Category 1");
				dataset.addValue(30, "Series 3", "Category 1");
				
				dataset.addValue(15, "Series 1", "Category 2");
				dataset.addValue(25, "Series 2", "Category 2");
				dataset.addValue(35, "Series 3", "Category 2");
				
				dataset.addValue(15, "Series 1", "Category 3");
				dataset.addValue(25, "Series 2", "Category 3");
				dataset.addValue(35, "Series 3", "Category 3");
				
				Object[] colorArr= {(java.awt.Color.GREEN),java.awt.Color.RED,java.awt.Color.cyan,java.awt.Color.MAGENTA};
				ImageData imageData = DrawChartImage.generateStackedBarChart(dataset,colorArr, "My Title", "Horizontal Axis", "Vertical Axis", 20, 16, 14);
				Image sChartimage = new Image(imageData);
				sChartimage.setAutoScale(true);
				sChartimage.setHorizontalAlignment(HorizontalAlignment.CENTER);
				tableChart.addCell(new Cell(1, 1).add(sChartimage));
				
				
				Map<String, Double> datasetT = new LinkedHashMap<>();
				datasetT.put("Series 1", 10.0);
				datasetT.put("Series 2", 20.0);
				datasetT.put("Series 3", 30.0);
				datasetT.put("Series 4", 30.0);
				datasetT.put("Series 5", 30.0);

				Object[] colorArrSBar= {new java.awt.Color(0, 92, 153)};
				ImageData barImageDataBudget = DrawChartImage.generateSingleBarChart(datasetT,colorArrSBar, 4, "Budget(Recurring/Non-Recurring) vs Expenditure(Recurring/Non-Recurring)", "Particulars", "figures (In Lakhs)", 15, 12, 12);
				Image barChartImageBudget = new Image(barImageDataBudget);
				barChartImageBudget.setAutoScale(true);
				barChartImageBudget.setHorizontalAlignment(HorizontalAlignment.CENTER);
				tableChart.addCell(new Cell(1, 1).add(barChartImageBudget));
				
				doc.add(CommonMethod.createParaGraphBold("Summary data", 0f, 0f, 12, paraFColor2, null, TextAlignment.CENTER));
				Table table = new Table(UnitValue.createPercentArray(new float[] { .5f,1.5f,1f, 1.5f,1f, 1.5f,1f, 1.5f,1f,.5f,}));
				table.setWidth(UnitValue.createPercentValue(100));

				
				
				
				
				int dataSize=15;
		        Color[] colorArray1 = CommonMethod.genDarkToLightColor(new java.awt.Color(102, 34, 0), dataSize);
		        Color[] colorArray2 = CommonMethod.genDarkToLightColor(new java.awt.Color(134, 45, 89), dataSize);
		        Color[] colorArray3 = CommonMethod.genDarkToLightColor(new java.awt.Color(26, 83, 255), dataSize);
		        Color[] colorArray4 = CommonMethod.genDarkToLightColor(new java.awt.Color(82, 122, 122), dataSize);
				
		        table.addCell(CommonMethod.createCellBold("Title Summary", 10, 1, 15).setTextAlignment(TextAlignment.CENTER).setBorder(null).setMinHeight(40).setUnderline());
		        for (int i = 0; i < dataSize; i++) {
		        	
		        	Color fColor=DeviceRgb.WHITE;
		        	if(i>(dataSize/3))
		        		fColor=DeviceRgb.BLACK;
		        	
		        	if(i==0)
		        		table.addCell(new Cell(dataSize,1).add(new Paragraph("All Figures in lakhs").setRotationAngle(Math.PI / 2)).setBorder(null).setVerticalAlignment(VerticalAlignment.MIDDLE));
					
		        	table.addCell(CommonMethod.createCellBold("Series-"+i, 1, 1, 9).setTextAlignment(TextAlignment.RIGHT).setBorder(null).setMinHeight(20));
					table.addCell(CommonMethod.createCellBold(""+(dataSize-i), 1, 1, 9).setBackgroundColor(colorArray1[i]).setTextAlignment(TextAlignment.RIGHT).setBorder(null).setFontColor(fColor));

					table.addCell(CommonMethod.createCellBold("Series-"+i, 1, 1, 9).setTextAlignment(TextAlignment.RIGHT).setBorder(null));
					table.addCell(CommonMethod.createCellBold(""+(dataSize-i), 1, 1, 9).setBackgroundColor(colorArray2[i]).setTextAlignment(TextAlignment.RIGHT).setBorder(null).setFontColor(fColor));
					
					table.addCell(CommonMethod.createCellBold("Series-"+i, 1, 1, 9).setTextAlignment(TextAlignment.RIGHT).setBorder(null));
					table.addCell(CommonMethod.createCellBold(""+(dataSize-i), 1, 1, 9).setBackgroundColor(colorArray3[i]).setTextAlignment(TextAlignment.RIGHT).setBorder(null).setFontColor(fColor));

					table.addCell(CommonMethod.createCellBold("Series-"+i, 1, 1, 9).setTextAlignment(TextAlignment.RIGHT).setBorder(null));
					table.addCell(CommonMethod.createCellBold(""+(dataSize-i), 1, 1, 9).setBackgroundColor(colorArray4[i]).setTextAlignment(TextAlignment.RIGHT).setBorder(null).setFontColor(fColor));
					
					table.addCell(CommonMethod.createCellBold("", 1, 1, 9).setTextAlignment(TextAlignment.RIGHT).setBorder(null));
		        }
				tableChart.addCell(new Cell(1, 1).add(table));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			doc.add(tableChart);
			////////////////////////////////////////////////////////////////////////////////
			
			
			
			
			
			
			
			// report data-----Start------------
			Table table = new Table(UnitValue.createPercentArray(new float[] { 1.5f, 1.5f, 1.5f, 3.2f, 1.0f, 1.0f, 1.0f }));
			table.setWidth(UnitValue.createPercentValue(100));

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
		} else {
			// doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			doc.add(CommonMethod.createParaGraphBold("Data not avaliable", 50f, 0f, 25, paraFColor2, null, TextAlignment.CENTER));
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
	

	private Table getTableReportData(Document doc, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>>> groupedByFiveAttributes, Integer schemeKey,
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<ProposedCosting>>>>> schemeValue555555, int mainMapSize)
			throws IOException {

		Table table = new Table(UnitValue.createPercentArray(new float[] { 1.5f, 1.5f, 1.5f, 3.2f, 1.0f, 1.0f, 1.0f }));
		table.setWidth(UnitValue.createPercentValue(100));

		try {
			int fHeader = 9, fData = 8, fGrandTotal = 9;
			if (schemeKey != 555555) {
				Color bgColorhead = new DeviceRgb(37, 132, 198);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Major Component", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Sub Component", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Activity", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Sub Activity", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Physical Quantity", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Unit Cost", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Financial Amount (In Lakhs)", 1, 1, fHeader, bgColorhead, TextAlignment.CENTER);
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
												CommonMethod.createDataCellBold(table, schemeName, 7, 1, 11, null, TextAlignment.LEFT);
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
											CommonMethod.createDataCellBold(table, "Grand Total of All Scheme : ", 4, 1, fGrandTotal, bgColorgTotal, TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getMajorComponentId() == 666666) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getSchemeName(), 4, 1, fData, null, TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getSubComponentId() == 777777) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getMajorComponentName(), 3, 1, fData, null, TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getActivityMasterId() == 888888) {
											CommonMethod.createDataCellBold(table, "Total of " + listObj.getSubComponentName(), 2, 1, fData, null, TextAlignment.RIGHT);
											isTotal = true;
										} else if (listObj.getActivityMasterDetailsId() == 999999) {
											CommonMethod.createDataCellBold(table, "Sub Total", 1, 1, fData, null, TextAlignment.RIGHT);
											isTotal = true;
										} else {
											subActivitySrNo++;
											CommonMethod.createDataCell(table,
													(subActivitySrNo) + "-" + (listObj.getActivityMasterDetailName() == null ? "" : listObj.getActivityMasterDetailName()), 1, 1, fData, null, TextAlignment.LEFT);
										}

										if (isTotal) {
											Color bgColorgTotal = null;
											if (listObj.getSchemeId() == 555555)
												bgColorgTotal = new DeviceRgb(227, 237, 243);

											CommonMethod.createDataCellBold(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, "", 1, 1, 9, bgColorgTotal, TextAlignment.RIGHT);
											CommonMethod.createDataCellBold(table, listObj.getFinancialAmount() == null ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, fData, bgColorgTotal, TextAlignment.RIGHT);

										} else {
											CommonMethod.createDataCell(table, listObj.getPhysicalQuantity() == null ? "" : listObj.getPhysicalQuantity() + "", 1, 1, fData, null, TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getUnitCost() == null ? "" : df.format(listObj.getUnitCost()) + "", 1, 1, fData, null, TextAlignment.RIGHT);
											CommonMethod.createDataCell(table, listObj.getFinancialAmount() == null ? "" : df.format(listObj.getFinancialAmount()) + "", 1, 1, fData, null, TextAlignment.RIGHT);
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
