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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.org.nic.prabandh.bean.AnnexureDetailsList;
import com.org.nic.prabandh.bean.AnnexureSchemeDetails;
import com.org.nic.prabandh.bean.ProposedCosting;
import com.org.nic.prabandh.bean.SpillOverReportDto;
import com.org.nic.prabandh.constant.Constants;
import com.org.nic.prabandh.utill.CommonMethod;

@Component
public class AnnexureReptPdf {

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

			new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(font).setFontSize(9).setFontColor(new DeviceRgb(12, 49, 99)).showTextAligned("Generated on " + formattedDate, 640, 28, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0).showTextAligned("https://prabandh.education.gov.in", 640, 15, TextAlignment.LEFT,
					VerticalAlignment.MIDDLE, 0);

			if (i > 1) {
				new Canvas(pdfCanvas, pdfDoc, pageSize).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(9).setFontColor(new DeviceRgb(165, 42, 42)).showTextAligned("Annexure  - " + regionName, 37, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0).showTextAligned("F. Y. - " + planYear,
						725, 570, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
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

	public ResponseEntity<?> downloadAnnexureReptPdf(String planYear, String regionName, List<AnnexureSchemeDetails> dataDetails, Map<Integer, List<AnnexureDetailsList>> dataListMap) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PdfWriter write = new PdfWriter(byteArrayOutputStream);
		write.setSmartMode(true);

		PdfDocument pdfDoc = new PdfDocument(write);
		pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

		Document doc = new Document(pdfDoc);

		// first page paragraph----Start-----------
		Color paraFColor1 = new DeviceRgb(165, 42, 42);
		doc.add(CommonMethod.createParaGraphBold("Annexure Details Sheet", 30f, 0f, 35, paraFColor1, null, TextAlignment.CENTER));
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
		if (dataDetails.size() > 0) {
			// report data-----Start------------
			doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			for (AnnexureSchemeDetails detailsObj : dataDetails) {
				
				Table tableSchemeDetails = getTableSchemeDetails(doc, planYear, regionName, detailsObj);
				doc.add(tableSchemeDetails);

				Table tableDetailsReport = getTableReportData(doc, planYear, regionName, detailsObj.getActivity_master_details_id(),detailsObj.getActivity_master_details_name(), dataListMap);
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

	private Table getTableSchemeDetails(Document doc, String planYear, String regionName, AnnexureSchemeDetails detailsObj) {
		float[] tableWidth = { 0.32f, 0.8f, 0.25f, 1.0f, 0.6f, 1.0f, 0.3f, 1.5f };
		Table table = new Table(UnitValue.createPercentArray(tableWidth));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();
		try {
			float fHeader = 8.3f, fData = 8f;
			Color bgColorhead = new DeviceRgb(230, 247, 255);

			table.addCell(CommonMethod.createCell("", 10, 1, 9).setBorder(null).setPaddingBottom(15));

			table.addCell(CommonMethod.createCellBold("Scheme :", 1, 1, fHeader).setBorderRight(null).setBackgroundColor(bgColorhead));
			table.addCell(CommonMethod.createCellBold(detailsObj.getScheme_name(), 1, 1, fHeader).setBorderLeft(null).setBackgroundColor(bgColorhead));

			table.addCell(CommonMethod.createCellBold("Major :", 1, 1, fHeader).setBorderRight(null).setBackgroundColor(bgColorhead));
			table.addCell(CommonMethod.createCellBold(detailsObj.getMajor_component_name(), 1, 1, fHeader).setBorderLeft(null).setBackgroundColor(bgColorhead));

			table.addCell(CommonMethod.createCellBold("Sub Component :", 1, 1, fHeader).setBorderRight(null).setBackgroundColor(bgColorhead));
			table.addCell(CommonMethod.createCellBold(detailsObj.getSub_component_name(), 1, 1, fHeader).setBorderLeft(null).setBackgroundColor(bgColorhead));

			table.addCell(CommonMethod.createCellBold("Activity :", 1, 1, fHeader).setBorderRight(null).setBackgroundColor(bgColorhead));
			table.addCell(CommonMethod.createCellBold(detailsObj.getActivity_master_name(), 1, 1, fHeader).setBorderLeft(null).setBackgroundColor(bgColorhead));

		} catch (Exception e) {
			e.printStackTrace();
			return table;
		}
		return table;
	}

	
	private Table getTableReportData(Document doc, String planYear, String regionName, Integer activity_master_details_id, String details_name, Map<Integer, List<AnnexureDetailsList>> dataListMap) {
		float[] tableWidth={1.0f, 1.0f, 0.7f,  2.0f, 1.5f, 0.8f};
		Table table = new Table(UnitValue.createPercentArray(tableWidth));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();
		try {
			float fHeader = 8.3f, fData = 8f;

			List<AnnexureDetailsList> detailsList = dataListMap.get(activity_master_details_id);
			if (detailsList != null && detailsList.size() > 0) {

				Color bgColor = new DeviceRgb(37, 132, 198);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Activity Master Details Name", 2, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Udise Sch Code", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "School Name", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				
				CommonMethod.createDataCellTableHeadEveryPage(table, "District Name", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				CommonMethod.createDataCellTableHeadEveryPage(table, "Quantity", 1, 1, 10f, bgColor, TextAlignment.CENTER);
				

				bgColor = null; //.setFontColor(new DeviceRgb(77, 0, 38)
				table.setKeepTogether(true);
				table.addCell(CommonMethod.createCellBold(details_name, 2, detailsList.size(), 10).setFontColor(new DeviceRgb(77, 0, 38)));
				for (AnnexureDetailsList listObj : detailsList) {
					CommonMethod.createDataCell(table, listObj.getUdise_sch_code() == null ? "" : listObj.getUdise_sch_code() + "", 1, 1, fData, bgColor, TextAlignment.CENTER);
					CommonMethod.createDataCell(table, listObj.getSchool_name() == null ? "" : listObj.getSchool_name() + "", 1, 1, fData, bgColor, TextAlignment.LEFT);
					CommonMethod.createDataCell(table, listObj.getDistrict_name() == null ? "" : listObj.getDistrict_name() + "", 1, 1, fData, bgColor, TextAlignment.LEFT);

					CommonMethod.createDataCell(table, listObj.getQuantity() == null ? "" : listObj.getQuantity() == 0 ? "" : listObj.getQuantity() + "", 1, 1, fData, bgColor, TextAlignment.RIGHT);
					
				}
			} else {
				table.addCell(CommonMethod.createCellBold(details_name, 2, 1, 10).setFontColor(new DeviceRgb(77, 0, 38)));
				table.addCell(CommonMethod.createCellBold("N/A", tableWidth.length-2, 1, 10).setTextAlignment(TextAlignment.CENTER));
			}

		}
			 catch (Exception e) {
			e.printStackTrace();
			return table;
		}
		return table;
	}
	

}
