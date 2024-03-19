package com.org.nic.prabandh.utill;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CenterTextMode;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.UnitType;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
		Font titleFont = new Font("Helvetica-Bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);
		title.setPadding(paddingTop, 0, 0, 0);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("Helvetica-Bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);
		
		@SuppressWarnings("rawtypes")
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(pieBufferedImage, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		ImageData pieImageData = ImageDataFactory.create(imageInByte);
		
		return pieImageData;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static ImageData generateDonutChart(Map<String, Double> data, String centerTotal, String titleText, int titleSize, int legendSize, int labelSize,int paddingTop) throws IOException {

		@SuppressWarnings("rawtypes")
		DefaultPieDataset dataset = new DefaultPieDataset();
		data.entrySet().stream().forEach(e -> dataset.setValue(e.getKey(), e.getValue()));

		JFreeChart chart = ChartFactory.createRingChart(titleText, dataset, true, false, false);

		TextTitle title = chart.getTitle();
		Font titleFont = new Font("Helvetica-Bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);
		title.setPadding(paddingTop, 0, 0, 0);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("Helvetica-Bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);

		RingPlot rp = (RingPlot) chart.getPlot();
		rp.setBackgroundPaint(Color.WHITE);
		rp.setSectionDepth(0.5);
		rp.setSeparatorsVisible(false);
		rp.setLabelBackgroundPaint(Color.WHITE);
		rp.setLabelOutlinePaint(Color.WHITE);
		rp.setLabelGenerator(new StandardPieSectionLabelGenerator("{1}"));
		rp.setSimpleLabelOffset(new RectangleInsets(UnitType.RELATIVE, 0.09, 0.09, 0.09, 0.09));
		//rp.setSimpleLabels(true); // To avoid lines for labels
		rp.setCenterTextMode(CenterTextMode.FIXED);
		rp.setCenterText(centerTotal);

		rp.setCenterTextFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));

		
		@SuppressWarnings("rawtypes")
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));
		plot.setLabelBackgroundPaint(Color.WHITE);
		plot.setLabelOutlinePaint(null);
		plot.setLabelShadowPaint(null);
		
		//piePlot.setSimpleLabels(true);
		plot.setCircular(true);
		plot.setBackgroundPaint(null);
		plot.setShadowPaint(null);
		plot.setOutlineVisible(false);
		
		BufferedImage pieBufferedImage = chart.createBufferedImage(500, 500);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(pieBufferedImage, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		ImageData pieImageData = ImageDataFactory.create(imageInByte);

		return pieImageData;
	}
	
	
	
	
	public static ImageData generateSingleBarChart(Map<String, Double> dataset,Object[] colorArr,int noOfBar, String titleText, String verticleText, String horizontalText, int titleSize, int legendSize, int labelSize)
			throws IOException {

		DefaultCategoryDataset data = new DefaultCategoryDataset();
	    for (Map.Entry<String, Double> entry : dataset.entrySet()) {
	        data.addValue(entry.getValue(), "Category", entry.getKey());
	    }
	    
		JFreeChart chart = ChartFactory.createBarChart(titleText, verticleText, horizontalText, data, PlotOrientation.VERTICAL, true, true, false);
		
		
		TextTitle title = chart.getTitle();
		Font titleFont = new Font("Helvetica-Bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("Helvetica-Bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);
		
		
		CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
		CategoryAxis categoryAxis = (CategoryAxis) categoryPlot.getDomainAxis();
		categoryAxis.setTickLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));
		categoryAxis.setLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));
		//categoryPlot.setOutlineVisible(false);
		
		Plot plot = chart.getPlot();
		plot.setBackgroundPaint(null);
		plot.setOutlineVisible(false);
		
		
		BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		for (int i = 0; i < colorArr.length; i++) {
			Color color=(Color) colorArr[i];
			renderer.setSeriesPaint(i, color); 
		}

		renderer.setItemLabelAnchorOffset(25.0);
		renderer.setSeriesVisibleInLegend(0, false);

		//categoryPlot.getRangeAxis().setUpperMargin(0.10);
		final ValueAxis rangeAxis = categoryPlot.getRangeAxis();
		rangeAxis.setUpperMargin(0.24);
		rangeAxis.setVisible(false);
		
		//rangeAxis.setLowerMargin(-0.12);
		//rangeAxis.setLowerBound(-10);

		//ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT);
		ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE11, TextAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, -Math.PI / 2);
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance());
		Font seriesItemLabelFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
		for (int i = 0; i < noOfBar; i++) {
		   renderer.setSeriesItemLabelGenerator(i, generator);
		   renderer.setSeriesItemLabelsVisible(i, true);
		   renderer.setSeriesPositiveItemLabelPosition(i, position);
		   renderer.setSeriesItemLabelFont(i, seriesItemLabelFont, true);
		   //renderer.setItemMargin(0.3);
		}

		CategoryAxis domainAxis = categoryPlot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		Font verticalAxisFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
		//Font verticalAxisFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
		NumberAxis verticalAxis = (NumberAxis) categoryPlot.getRangeAxis();
		verticalAxis.setTickLabelFont(verticalAxisFont);
		verticalAxis.setLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));
		domainAxis.setLabel(null);
		
		

		BufferedImage pieBufferedImage = chart.createBufferedImage(500, 500);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(pieBufferedImage, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		ImageData pieImageData = ImageDataFactory.create(imageInByte);

		return pieImageData;
	}

	
	
	public static ImageData generateMultiBarChart(DefaultCategoryDataset dataset,Object[] colorArr,int noOfBar, String titleText, String verticleText, String horizontalText, int titleSize, int legendSize, int labelSize)
			throws IOException {

		JFreeChart chart = ChartFactory.createBarChart(titleText, verticleText, horizontalText, dataset, PlotOrientation.VERTICAL, true, true, false);
		
		
		TextTitle title = chart.getTitle();
		Font titleFont = new Font("Helvetica-Bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("Helvetica-Bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);
		
		
		CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
		CategoryAxis categoryAxis = (CategoryAxis) categoryPlot.getDomainAxis();
		categoryAxis.setTickLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));
		categoryAxis.setLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));
		//categoryPlot.setOutlineVisible(false);
		
		Plot plot = chart.getPlot();
		plot.setBackgroundPaint(null);

		BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		for (int i = 0; i < colorArr.length; i++) {
			Color color=(Color) colorArr[i];
			renderer.setSeriesPaint(i, color); 
		}
		renderer.setItemLabelAnchorOffset(25.0);

		//categoryPlot.getRangeAxis().setUpperMargin(0.10);
		final ValueAxis rangeAxis = categoryPlot.getRangeAxis();
		rangeAxis.setUpperMargin(0.24);
		//rangeAxis.setLowerMargin(-0.12);
		//rangeAxis.setLowerBound(-10);

		//ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT);
		ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE11, TextAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, -Math.PI / 2);
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance());
		Font seriesItemLabelFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
		for (int i = 0; i < noOfBar; i++) {
		   renderer.setSeriesItemLabelGenerator(i, generator);
		   renderer.setSeriesItemLabelsVisible(i, true);
		   renderer.setSeriesPositiveItemLabelPosition(i, position);
		   renderer.setSeriesItemLabelFont(i, seriesItemLabelFont, true);
		   //renderer.setItemMargin(0.3);
		}

		CategoryAxis domainAxis = categoryPlot.getDomainAxis();
		//domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		Font verticalAxisFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
		//Font verticalAxisFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
		NumberAxis verticalAxis = (NumberAxis) categoryPlot.getRangeAxis();
		verticalAxis.setTickLabelFont(verticalAxisFont);
		verticalAxis.setLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));

		BufferedImage pieBufferedImage = chart.createBufferedImage(500, 500);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(pieBufferedImage, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		ImageData pieImageData = ImageDataFactory.create(imageInByte);

		return pieImageData;
	}



	
	public static ImageData generateStackedBarChart(DefaultCategoryDataset dataset,Object[] colorArr, String titleText, String verticalText, String horizontalText, int titleSize, int legendSize, int labelSize) throws Exception {
	    // Create a chart using JFreeChart
	    JFreeChart chart = ChartFactory.createStackedBarChart( titleText, horizontalText, verticalText, dataset, PlotOrientation.VERTICAL, true, true, false);

	    TextTitle title = chart.getTitle();
		Font titleFont = new Font("Serif", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);

		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);
		legend.setFrame(BlockBorder.NONE);
		Font legendFont = new Font("Serif", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);
		
		Plot plot = chart.getPlot();
		plot.setBackgroundPaint(null);

	    chart.getCategoryPlot().getDomainAxis().setLabelFont(new Font("Serif", Font.BOLD, labelSize));
	    chart.getCategoryPlot().getRangeAxis().setLabelFont(new Font("Serif", Font.BOLD, labelSize));
	    CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
		CategoryAxis categoryAxis = (CategoryAxis) categoryPlot.getDomainAxis();
		categoryAxis.setTickLabelFont(new Font("Serif", Font.BOLD, labelSize));
		categoryAxis.setLabelFont(new Font("Serif", Font.BOLD, labelSize));
	    

	    
	    
		BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		for (int i = 0; i < colorArr.length; i++) {
			Color color=(Color) colorArr[i];
			renderer.setSeriesPaint(i, color); 
		}
	    
	    
		/*BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
		renderer.setSeriesPaint(0, new Color(0, 92, 153)); 
		renderer.setSeriesPaint(1, Color.RED);
		renderer.setSeriesPaint(2, Color.cyan);
		renderer.setSeriesPaint(3, Color.MAGENTA);
		*/
		renderer.setItemLabelAnchorOffset(25.0);
	    
	    
	    
		
		ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE11, TextAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, Math.PI);
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance());
		Font seriesItemLabelFont = new Font("Helvetica-Bold", Font.PLAIN, (labelSize-2));
		for (int i = 0; i < 3; i++) {
		   renderer.setSeriesItemLabelGenerator(i, generator);
		   renderer.setSeriesItemLabelsVisible(i, true);
		   //renderer.setSeriesPositiveItemLabelPosition(i, position);
		   renderer.setSeriesItemLabelFont(i, seriesItemLabelFont, true);
		   //renderer.setItemMargin(0.3);
		}

		CategoryAxis domainAxis = categoryPlot.getDomainAxis();
		//domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		Font verticalAxisFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
		//Font verticalAxisFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
		NumberAxis verticalAxis = (NumberAxis) categoryPlot.getRangeAxis();
		verticalAxis.setTickLabelFont(verticalAxisFont);
		verticalAxis.setLabelFont(new Font("Helvetica-Bold", Font.BOLD, labelSize));
		
		
		
		
		
		
		
		
		
		
	    BufferedImage image = chart.createBufferedImage(600, 400);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(image, "png", baos);
	    baos.flush();
	    byte[] imageInByte = baos.toByteArray();
	    baos.close();

	    // Return the image data
	    return ImageDataFactory.create(imageInByte);
	}

	
	
	
	
	public static ImageData generateSpiderWebChart(DefaultCategoryDataset dataset, Object[] colorArr, String titleText, int titleSize, int legendSize, int labelSize) throws IOException {

		//SpiderWebPlot spiderWebPlot = new SpiderWebPlot(dataset);
		
		SpiderWebPlot spiderWebPlot = customizePlot(dataset);
		
	    //spiderWebPlot.setStartAngle(54);
	    //spiderWebPlot.setInteriorGap(0.30);
	    
	    spiderWebPlot.setStartAngle(Math.PI / 4); // Start angle in radians
	    spiderWebPlot.setInteriorGap(0.40); // Interior gap
	    
	    //spiderWebPlot.setMaxValue(300);
	    //spiderWebPlot.setMaxValue(14297);
	    
	    spiderWebPlot.setToolTipGenerator(new StandardCategoryToolTipGenerator());
	    spiderWebPlot.setLabelFont(new Font("Arial", Font.BOLD, labelSize));

	    JFreeChart chart = new JFreeChart(titleText, JFreeChart.DEFAULT_TITLE_FONT, spiderWebPlot, true);
	    
	    chart.setTitle(new TextTitle(titleText, new Font("Serif", Font.BOLD, titleSize)));
	    chart.setBackgroundPaint(null);

	    LegendTitle legend = chart.getLegend();
	    legend.setFrame(BlockBorder.NONE);
	    Font legendFont = new Font("Serif", Font.BOLD, legendSize);
	    legend.setItemFont(legendFont);

	    for (int i = 0; i < colorArr.length; i++) {
	        spiderWebPlot.setSeriesPaint(i, (Color) colorArr[i]);
	
	    }

	    ChartPanel chartPanel = new ChartPanel(chart, false);
	    chartPanel.setPreferredSize(new Dimension(1500, 1270));

	    

	    CategoryItemLabelGenerator generator = new CategoryItemLabelGenerator() {
	        @Override
	        public String generateLabel(CategoryDataset dataset, int row, int column) {
	            Number value = dataset.getValue(row, column);
	            if (value != null) {
	                return value.toString();
	            } else {
	                return "";
	            }
	        }

	        @Override
	        public String generateRowLabel(CategoryDataset dataset, int row) {
	            return dataset.getRowKey(row).toString();
	        }

	        @Override
	        public String generateColumnLabel(CategoryDataset dataset, int column) {
	            return dataset.getColumnKey(column).toString();
	        }
	    };
	    
	    spiderWebPlot.setLabelGenerator(generator);

	    //-----------------------------------------------------------------
	    
	    
	    
	    // Convert JFreeChart to Image
	    BufferedImage bufferedImage = chart.createBufferedImage(800, 600);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(bufferedImage, "png", baos);
	    byte[] bytes = baos.toByteArray();

	    // Convert bytes to ImageData using iText7
	    ImageData imageData = ImageDataFactory.create(bytes);

	    return imageData;
	}

	
	public static ImageData generatePolarChart(DefaultCategoryDataset dataset, Object[] colorArr, String titleText, int titleSize, int legendSize, int labelSize) throws IOException {
		List<Double> listBug = new ArrayList<>();
		List<Double> listExp = new ArrayList<>();
		for (int i = 0; i < dataset.getColumnCount(); i++) {
			Comparable columnKey = dataset.getColumnKey(i);
			for (int j = 0; j < dataset.getRowCount(); j++) {
				Comparable rowKey = dataset.getRowKey(j);
				double value = dataset.getValue(rowKey, columnKey).doubleValue();

				if (rowKey.equals("Budget"))
					listBug.add(value);
				if (rowKey.equals("Expenditure"))
					listExp.add(value);
				
				// System.out.println("Row Key: " + rowKey + ", Column Key: " + columnKey + ", Value: " + value);
			}
		}

		Double divide = (360.0 / listBug.size());
		XYSeriesCollection xyDataset = new XYSeriesCollection();

		XYSeries series1 = new XYSeries("Budget", false, false);
		int i = 0;
		for (double az = 0.0; az < 360.0; az += divide) {
			final double radius = listBug.get(i);
			series1.add(az, radius);
			i++;
		}
		xyDataset.addSeries(series1);

		XYSeries series2 = new XYSeries("Expenditure", false, false);
		int j = 0;
		for (double az = 0.0; az < 360.0; az += divide) {
			final double radius = listExp.get(j);
			series2.add(az, radius);
			j++;
		}
		xyDataset.addSeries(series2);

		

		
		JFreeChart chart = ChartFactory.createPolarChart(titleText, xyDataset, true, true, false);
		PolarPlot polarPlot = (PolarPlot) chart.getPlot();

		// Customize the plot
		polarPlot.setBackgroundPaint(Color.WHITE);
		polarPlot.setOutlineVisible(false);
		polarPlot.setAngleGridlinePaint(Color.BLACK);
		polarPlot.setRadiusGridlinePaint(Color.BLACK);

		
		DefaultPolarItemRenderer renderer = new DefaultPolarItemRenderer();
		
		for (int k = 0; k < colorArr.length; k++) {
			renderer.setSeriesPaint(k, (Color) colorArr[k]);
			renderer.setSeriesItemLabelsVisible(k, true);
		}
		polarPlot.setRenderer(renderer);
		

		// Convert JFreeChart to Image
		BufferedImage chartImage = chart.createBufferedImage(500, 500);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(chartImage, "png", baos);
		byte[] bytes = baos.toByteArray();

		// Convert bytes to ImageData using iText7
		ImageData imageData = ImageDataFactory.create(bytes);

		return imageData;
	}

	public static ImageData generateMultiLineChart(DefaultCategoryDataset dataset, Object[] colors, String titleText, int titleSize, int legendSize, int labelSize) throws IOException {
		JFreeChart chart = ChartFactory.createLineChart(
				titleText,"Categories","Values",dataset,PlotOrientation.VERTICAL,true,true,false
		);

		// Get the plot and cast it to a CategoryPlot
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		


		// Get the renderer and cast it to a LineAndShapeRenderer
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

		// Set the series paint and fill paint
		for (int i = 0; i < colors.length; i++) {
			renderer.setSeriesPaint(i, (Color)colors[i]);
			renderer.setSeriesFillPaint(i, (Color)colors[i]);
			renderer.setSeriesOutlinePaint(i, (Color)colors[i]);
			
			 renderer.setSeriesStroke(i, new BasicStroke(4.0f));
		}
		
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		
		//plot.setOutlinePaint(Color.BLUE);
	    //plot.setOutlineStroke(new BasicStroke(2.0f));
		    
		// Customize plot appearance
		plot.setBackgroundPaint(Color.WHITE);
		
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setOutlinePaint(Color.BLACK);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		
		   
		// Set the title and font size
		chart.getTitle().setFont(chart.getTitle().getFont().deriveFont((float) titleSize));

		// Set the legend font size
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(legend.getItemFont().deriveFont((float) legendSize));

		// Set the label font size
		plot.getDomainAxis().setLabelFont(plot.getDomainAxis().getLabelFont().deriveFont((float) labelSize));
		plot.getRangeAxis().setLabelFont(plot.getRangeAxis().getLabelFont().deriveFont((float) labelSize));

		// Convert JFreeChart to Image
		BufferedImage chartImage = chart.createBufferedImage(800, 600);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(chartImage, "png", baos);
		byte[] bytes = baos.toByteArray();
		ImageData imageData = ImageDataFactory.create(bytes);
		return imageData;

	}
	
	

    public static ImageData generateMultiAreaChart(DefaultCategoryDataset dataset, Color[] colorArr, String titleText, int titleSize, int legendSize, int labelSize) throws IOException {
        JFreeChart chart = ChartFactory.createStackedAreaChart(titleText,"","",dataset,PlotOrientation.VERTICAL,true,true,false);

        TextTitle title = chart.getTitle();
		Font titleFont = new Font("Helvetica-Bold", title.getFont().getStyle(), titleSize);
		title.setFont(titleFont);

		LegendTitle legend = chart.getLegend();
		Font legendFont = new Font("Helvetica-Bold", Font.BOLD, legendSize);
		legend.setItemFont(legendFont);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
       
        plot.setOutlinePaint(null);
        plot.setAxisOffset(new RectangleInsets(0.0,   0.0,  0.0,   0.0));
        
        
        Font seriesFont = new Font("Helvetica-Bold", Font.BOLD, labelSize);
        
        //ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE11, TextAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, -Math.PI / 2);
        CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance());
        StackedAreaRenderer renderer = (StackedAreaRenderer) plot.getRenderer();
        for (int i = 0; i < colorArr.length; i++) {
        	renderer.setSeriesItemLabelGenerator(i, generator);
  		   	renderer.setSeriesItemLabelsVisible(i, true);
  		   	renderer.setSeriesItemLabelFont(i, null);
  		   
			renderer.setSeriesPaint(i, colorArr[i]);
			renderer.setSeriesOutlinePaint(i, Color.black, true);
			renderer.setSeriesStroke(i, new BasicStroke(4.0f));
			renderer.setSeriesItemLabelFont(i,seriesFont);
			if(i==0) {
				renderer.setSeriesItemLabelPaint(i, Color.red);
				//renderer.setSeriesPositiveItemLabelPosition(i, position);
				
			}
		}
        
        //renderer.setItemLabelAnchorOffset(25.0);

        
        CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setTickLabelFont(new Font("Serif", Font.PLAIN, labelSize));
		domainAxis.setLabelFont(new Font("Serif", Font.PLAIN, (labelSize+1)));
		
		
		ValueAxis rangeAxis=plot.getRangeAxis();
		rangeAxis.setTickLabelFont(new Font("Serif", Font.PLAIN, labelSize));
		rangeAxis.setLabelFont(new Font("Serif", Font.PLAIN, (labelSize+1)));
		//rangeAxis.setUpperMargin(0.14);
		rangeAxis.setVisible(false);
		

		
		
     // Convert JFreeChart to Image
 		BufferedImage chartImage = chart.createBufferedImage(800, 580);
 		ByteArrayOutputStream baos = new ByteArrayOutputStream();
 		ImageIO.write(chartImage, "png", baos);
 		byte[] bytes = baos.toByteArray();
 		ImageData imageData = ImageDataFactory.create(bytes);
 		return imageData;
    }
	
    public static SpiderWebPlot customizePlot(CategoryDataset dataset) {

		@SuppressWarnings("serial")
		final SpiderWebPlot plot = new SpiderWebPlot(dataset) {
			// put this many labels on each axis.
			private int ticks = DEFAULT_TICKS;
			private static final int DEFAULT_TICKS = 3;
			private NumberFormat format = NumberFormat.getInstance();
			// constant for creating perpendicular tick marks.
			private static final double PERPENDICULAR = 90;
			// the size of a tick mark, as a percentage of the entire line length.
			private static final double TICK_SCALE = 0.0050;
			// the gap between the axis line and the numeric label itself.
			private int valueLabelGap = DEFAULT_GAP;
			private static final int DEFAULT_GAP = -15;
			// the threshold used for determining if something is "on" the axis
			private static final double THRESHOLD = 15;

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected void drawLabel(final Graphics2D g2, final Rectangle2D plotArea, final double value, final int cat, final double startAngle, final double extent) {
				super.drawLabel(g2, plotArea, value, cat, startAngle, extent);
				final FontRenderContext frc = g2.getFontRenderContext();
				final double[] transformed = new double[2];
				final double[] transformer = new double[2];
				final Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0, Arc2D.OPEN);
				for (int i = 1; i <= ticks; i++) {

					final Point2D point1 = arc1.getEndPoint();

					final double deltaX = plotArea.getCenterX();
					final double deltaY = plotArea.getCenterY();
					double labelX = point1.getX() - deltaX;
					double labelY = point1.getY() - deltaY;

					final double scale = ((double) i / (double) ticks);
					final AffineTransform tx = AffineTransform.getScaleInstance(scale, scale);
					// for getting the tick mark start points.
					final AffineTransform pointTrans = AffineTransform.getScaleInstance(scale + TICK_SCALE, scale + TICK_SCALE);
					transformer[0] = labelX;
					transformer[1] = labelY;
					pointTrans.transform(transformer, 0, transformed, 0, 1);
					final double pointX = transformed[0] + deltaX;
					final double pointY = transformed[1] + deltaY;
					tx.transform(transformer, 0, transformed, 0, 1);
					labelX = transformed[0] + deltaX;
					labelY = transformed[1] + deltaY;

					double rotated = (PERPENDICULAR);

					AffineTransform rotateTrans = AffineTransform.getRotateInstance(Math.toRadians(rotated), labelX,labelY);
					transformer[0] = pointX;
					transformer[1] = pointY;
					rotateTrans.transform(transformer, 0, transformed, 0, 1);
					final double x1 = transformed[0];
					final double y1 = transformed[1];

					rotated = (-PERPENDICULAR);
					rotateTrans = AffineTransform.getRotateInstance(Math.toRadians(rotated), labelX, labelY);

					rotateTrans.transform(transformer, 0, transformed, 0, 1);

					final Composite saveComposite = g2.getComposite();
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

					g2.draw(new Line2D.Double(transformed[0], transformed[1], x1, y1));

					if (startAngle == this.getStartAngle()) {
						final String label = format.format(((double) i / (double) ticks) * this.getMaxValue());
						final Rectangle2D labelBounds = getLabelFont().getStringBounds(label, frc);

						final LineMetrics lm = getLabelFont().getLineMetrics(label, frc);
						final double ascent = lm.getAscent();

						// move based on quadrant.
						if (Math.abs(labelX - plotArea.getCenterX()) < THRESHOLD) {
							// on Y Axis, label to right.
							labelX += valueLabelGap;
							// center vertically.
							labelY += ascent / (float) 2;
						} else if (Math.abs(labelY - plotArea.getCenterY()) < THRESHOLD) {
							// on X Axis, label underneath.
							labelY += valueLabelGap;
						} else if (labelX >= plotArea.getCenterX()) {
							if (labelY < plotArea.getCenterY()) {
								// quadrant 1
								labelX += valueLabelGap;
								labelY += valueLabelGap;
							} else {
								// quadrant 2
								labelX -= valueLabelGap;
								labelY += valueLabelGap;
							}
						} else {
							if (labelY > plotArea.getCenterY()) {
								// quadrant 3
								labelX -= valueLabelGap;
								labelY -= valueLabelGap;
							} else {
								// quadrant 4
								labelX += valueLabelGap;
								labelY -= valueLabelGap;
							}
						}
						g2.setPaint(getLabelPaint());
						g2.setFont(getLabelFont());
						g2.drawString(label, (float) labelX, (float) labelY);
					}
					g2.setComposite(saveComposite);
				}
			}

			/**
			 * sets the number of tick marks on this spider chart.
			 * 
			 * @param ticks the new number of tickmarks.
			 */
			public void setTicks(final int ticks) {
				this.ticks = ticks;
			}

			/**
			 * sets the numberformat for the tick labels on this spider chart.
			 * 
			 * @param format the new number format object.
			 */
			public void setFormat(final NumberFormat format) {
				this.format = format;
			}

		};
        return plot;
    }
	
    
    
    
    
}


