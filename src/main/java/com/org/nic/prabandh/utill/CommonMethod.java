package com.org.nic.prabandh.utill;

import java.io.IOException;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

public class CommonMethod {

	public static void createDataCellCategoryWithBorderCeneter(Table table, String content, int colspan, int rowspan) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(11));
		cell.setTextAlignment(TextAlignment.CENTER);
		Border borderColor = new SolidBorder(new DeviceRgb(111,107,107), 0.5f);
		cell.setBorder(borderColor);
		table.addCell(cell);
	}

	public static void createHeaderCellTableCenter(Table table, String content, int colspan, int rowspan) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(10));
		Color bgColour = new DeviceRgb(179, 212, 217);
		cell.setBackgroundColor(bgColour);
		cell.setTextAlignment(TextAlignment.CENTER); 
		Border borderColor = new SolidBorder(new DeviceRgb(111,107,107), 0.5f);
		cell.setBorder(borderColor);
		table.addCell(cell);
	}

	public static void createDataCellCategoryWithBorderLeft(Table table, String content, int colspan, int rowspan) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(9f));
		cell.setTextAlignment(TextAlignment.LEFT);
		Border borderColor = new SolidBorder(new DeviceRgb(111,107,107), 0.5f);
		cell.setBorder(borderColor);
		table.addCell(cell);

	}

	public static void createDataCellCategoryWithBorderRight(Table table, String content, int colspan, int rowspan,int fontSize) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(fontSize));
		cell.setTextAlignment(TextAlignment.RIGHT);
		Border borderColor = new SolidBorder(new DeviceRgb(111,107,107), 0.5f);
		cell.setBorder(borderColor);
		table.addCell(cell);
	}
	
	public static void createDataCellTotalWithBorderRight(Table table, String content, int colspan, int rowspan,int fontSize) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(fontSize));
		cell.setTextAlignment(TextAlignment.RIGHT);
		Border borderColor = new SolidBorder(new DeviceRgb(111,107,107), 0.5f);
		cell.setBorder(borderColor);
		Color bgColor = new DeviceRgb(227,237,243);
		cell.setBackgroundColor(bgColor);
		Color fColor = new DeviceRgb(51, 51, 51);
		cell.setFontColor(fColor);
		table.addCell(cell);
	}

	public static void createDataCellBoldWithBackGroundColor(Table table, String content, int colspan, int rowspan,float fontSIze,TextAlignment textAlignment) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(fontSIze));
		cell.setMinHeight(14);
		cell.setTextAlignment(textAlignment);
		cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
		
		//Color color = new DeviceRgb(247, 246, 231);
		Color bgColor = new DeviceRgb(37, 132, 198);
		cell.setBackgroundColor(bgColor);
		
		Color fcolor = new DeviceRgb(255,255,255);
		cell.setFontColor(fcolor);

		Border borderColor = new SolidBorder(new DeviceRgb(239, 239, 239), 1);
		cell.setBorder(borderColor);
		table.addCell(cell);

	}
	
	public static void createDataCelltableHeaderWithBgBlue(Table table, String content, int colspan, int rowspan,float fontSIze,TextAlignment textAlignment) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(fontSIze));
		cell.setMinHeight(14);
		cell.setTextAlignment(textAlignment);
		cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
		
		//Color color = new DeviceRgb(247, 246, 231);
		Color bgColor = new DeviceRgb(37, 132, 198);
		cell.setBackgroundColor(bgColor);
		
		Color fcolor = new DeviceRgb(255,255,255);
		cell.setFontColor(fcolor);

		Border borderColor = new SolidBorder(new DeviceRgb(239, 239, 239), 1);
		cell.setBorder(borderColor);
		table.addHeaderCell(cell);

	}

	public static void createDataCellBoldLeft(Table table, String content, int colspan, int rowspan,float fontSize) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(fontSize));
		cell.setMinHeight(14);
		cell.setTextAlignment(TextAlignment.LEFT);
		cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
		Border borderColor = new SolidBorder(new DeviceRgb(111,107,107), 0.5f);
		cell.setBorder(borderColor);
		table.addCell(cell);
	}
	public static void createDataCellBoldCenter(Table table, String content, int colspan, int rowspan,float fontSize) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(fontSize));
		cell.setMinHeight(14);
		cell.setTextAlignment(TextAlignment.CENTER);
		cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
		Border borderColor = new SolidBorder(new DeviceRgb(111,107,107), 0.5f);
		cell.setBorder(borderColor);
		table.addCell(cell);
	}

	public static void createDataCellBoldRight(Table table, String content, int colspan, int rowspan, float fontSize) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		Cell cell = new Cell(rowspan, colspan).add(new Paragraph(content).setFont(font).setFontSize(fontSize));
		cell.setMinHeight(14);
		cell.setTextAlignment(TextAlignment.RIGHT);
		cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
		Border borderColor = new SolidBorder(new DeviceRgb(111,107,107), 0.5f);
		cell.setBorder(borderColor);
		table.addCell(cell);

	}

	public static Paragraph createHeadingParaGraph(String text, float paddingTop, float paddingBottom, int fontSize,Color fColor) throws IOException {
		Paragraph para = new Paragraph(text);
		para.setPaddingTop(paddingTop);
		para.setPaddingBottom(paddingBottom);
		para.setFontSize(fontSize);
		para.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
		para.setTextAlignment(TextAlignment.CENTER);
		para.setFontColor(fColor);
		return para;
	}

}
