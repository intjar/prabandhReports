package com.org.nic.prabandh.utill;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CenterTextMode;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.UnitType;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;

public class DrawChartImage {

	
	@SuppressWarnings("unchecked")
	public static ImageData generatePieChart(Map<String,Double> data,String titleText,int titleSize,int legendSize,int labelSize,int paddingTop) throws IOException{
		
		@SuppressWarnings("rawtypes")
		DefaultPieDataset dataSet = new DefaultPieDataset();
		data.entrySet().stream().forEach(e -> dataSet.setValue(e.getKey(), e.getValue()));
	       
		JFreeChart chart = ChartFactory.createPieChart(titleText, dataSet, true, true, false);
	
		
		TextTitle title = chart.getTitle();
		Font titleFont = new Font("helvetica_bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);
		title.setPadding(paddingTop, 0, 0, 0);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("helvetica_bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);
		
		@SuppressWarnings("rawtypes")
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("helvetica_bold", 0, labelSize));
		plot.setLabelBackgroundPaint(Color.WHITE);
		plot.setLabelOutlinePaint(null);
		plot.setLabelShadowPaint(null);
		
		//piePlot.setSimpleLabels(true);
		plot.setCircular(true);
		plot.setBackgroundPaint(null);
		plot.setShadowPaint(null);
		plot.setOutlineVisible(false);

		//StandardPieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator( "{0}: {1} ({2})", NumberFormat.getInstance(), NumberFormat.getPercentInstance());
		StandardPieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator( "{1}", NumberFormat.getInstance(), NumberFormat.getPercentInstance());
		plot.setLabelGenerator(labelGenerator);
		
		/*pieChart.setBackgroundPaint(Color.yellow);
		pieChart.setBackgroundImageAlpha(0.5f);*/
		BufferedImage pieBufferedImage = chart.createBufferedImage(500, 500);
		ByteArrayOutputStream pieBaos = new ByteArrayOutputStream();
		ImageIO.write(pieBufferedImage, "png", pieBaos);
		pieBaos.flush();
		byte[] imageInByte = pieBaos.toByteArray();
		ImageData pieImageData = ImageDataFactory.create(imageInByte);
		
		return pieImageData;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static ImageData generateDonutChart(Map<String, Double> data, String titleText, int titleSize, int legendSize, int labelSize) throws IOException {

		@SuppressWarnings("rawtypes")
		DefaultPieDataset dataset = new DefaultPieDataset();
		data.entrySet().stream().forEach(e -> dataset.setValue(e.getKey(), e.getValue()));

		JFreeChart chart = ChartFactory.createRingChart(titleText, dataset, true, false, false);

		TextTitle title = chart.getTitle();
		Font titleFont = new Font("helvetica_bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("helvetica_bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);

		RingPlot rp = (RingPlot) chart.getPlot();
		rp.setBackgroundPaint(Color.WHITE);
		rp.setSectionDepth(0.5);
		rp.setSeparatorsVisible(false);
		rp.setLabelBackgroundPaint(Color.WHITE);
		rp.setLabelOutlinePaint(Color.WHITE);
		rp.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1}"));
		rp.setSimpleLabelOffset(new RectangleInsets(UnitType.RELATIVE, 0.09, 0.09, 0.09, 0.09));
		rp.setSimpleLabels(true); // To avoid lines for labels
		rp.setCenterTextMode(CenterTextMode.FIXED);
		rp.setCenterText("$ Center Total");

		BufferedImage pieBufferedImage = chart.createBufferedImage(500, 500);
		ByteArrayOutputStream pieBaos = new ByteArrayOutputStream();
		ImageIO.write(pieBufferedImage, "png", pieBaos);
		pieBaos.flush();
		byte[] imageInByte = pieBaos.toByteArray();
		ImageData pieImageData = ImageDataFactory.create(imageInByte);

		return pieImageData;
	}
	
	
	
	public static ImageData generateBarChart(DefaultCategoryDataset dataset,int noOfBar, String titleText, String verticleText, String horizontalText, int titleSize, int legendSize, int labelSize)
			throws IOException {

		JFreeChart chart = ChartFactory.createBarChart(titleText, verticleText, horizontalText, dataset, PlotOrientation.VERTICAL, true, true, false);
		
		
		TextTitle title = chart.getTitle();
		Font titleFont = new Font("helvetica_bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("helvetica_bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);
		
		
		CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
		CategoryAxis categoryAxis = (CategoryAxis) categoryPlot.getDomainAxis();
		categoryAxis.setTickLabelFont(new Font("helvetica_bold", Font.BOLD, 14));
		categoryAxis.setLabelFont(new Font("helvetica_bold", Font.BOLD, labelSize));
		//categoryPlot.setOutlineVisible(false);
		
		BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		renderer.setSeriesPaint(0, Color.GREEN); 
		renderer.setSeriesPaint(1, Color.RED);
		renderer.setSeriesPaint(2, Color.cyan);
		renderer.setSeriesPaint(3, Color.MAGENTA);
		renderer.setItemLabelAnchorOffset(18.0);

		//categoryPlot.getRangeAxis().setUpperMargin(0.10);
		final ValueAxis rangeAxis = categoryPlot.getRangeAxis();
		rangeAxis.setUpperMargin(0.24);
		//rangeAxis.setLowerMargin(-0.12);
		//rangeAxis.setLowerBound(-10);

		//ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT);
		ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE11, TextAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, -Math.PI / 2);
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance());
		Font seriesItemLabelFont = new Font("helvetica_bold", Font.BOLD, 12);
		for (int i = 0; i < noOfBar; i++) {
		   renderer.setSeriesItemLabelGenerator(i, generator);
		   renderer.setSeriesItemLabelsVisible(i, true);
		   renderer.setSeriesPositiveItemLabelPosition(i, position);
		   renderer.setSeriesItemLabelFont(i, seriesItemLabelFont, true);
		   //renderer.setItemMargin(0.3);
		}

		CategoryAxis domainAxis = categoryPlot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		Font verticalAxisFont = new Font("helvetica_bold", Font.BOLD, 14);
		NumberAxis verticalAxis = (NumberAxis) categoryPlot.getRangeAxis();
		verticalAxis.setTickLabelFont(verticalAxisFont);
		verticalAxis.setLabelFont(new Font("helvetica_bold", Font.BOLD, labelSize));
		
		Plot plot = chart.getPlot();
		plot.setBackgroundPaint(null);


		
		BufferedImage pieBufferedImage = chart.createBufferedImage(500, 500);
		ByteArrayOutputStream pieBaos = new ByteArrayOutputStream();
		ImageIO.write(pieBufferedImage, "png", pieBaos);
		pieBaos.flush();
		byte[] imageInByte = pieBaos.toByteArray();
		ImageData pieImageData = ImageDataFactory.create(imageInByte);

		return pieImageData;
	}



	public static ImageData generateLineChart(DefaultCategoryDataset lineDataset, String string, String string2, String string3, int titleSize, int legendSize, int labelSize) {
		
		JFreeChart chart = ChartFactory.createLineChart( string, string2, string3, lineDataset);
		
		
		TextTitle title = chart.getTitle();
		Font titleFont = new Font("helvetica_bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("helvetica_bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);
		
		return null;
	}
	
	
}
