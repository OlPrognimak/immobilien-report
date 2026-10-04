package com.example.immoreport.report;

import com.example.immoreport.sales.RegionalSalesPoint;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.List;

@Service
public class RegionalSalesChartService {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 520;
    private static final Color[] COMPANY_COLORS = {
            Color.decode("#315470"),
            Color.decode("#C17C2E")
    };

    public Element createChartSvg(List<RegionalSalesPoint> regionalSales) {
        JFreeChart chart;
        try {
            DefaultCategoryDataset dataset = dataset(regionalSales);
            chart = chart(dataset);
        } catch (RuntimeException exception) {
            throw new ReportGenerationException("Could not create regional sales chart dataset or chart", exception);
        }
        return svg(chart);
    }

    private DefaultCategoryDataset dataset(List<RegionalSalesPoint> regionalSales) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (RegionalSalesPoint point : regionalSales) {
            dataset.addValue(point.amount(), point.companyName(), point.region());
        }
        return dataset;
    }

    private JFreeChart chart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Sales by region",
                "Region",
                "Sales (€)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        chart.getLegend().setItemFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.decode("#CBD5DC"));
        plot.getDomainAxis().setLabelFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        plot.getDomainAxis().setTickLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        plot.getRangeAxis().setLabelFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        plot.getRangeAxis().setTickLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        if (plot.getRangeAxis() instanceof NumberAxis axis) {
            axis.setNumberFormatOverride(new java.text.DecimalFormat("€ #,##0"));
        }

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        for (int index = 0; index < COMPANY_COLORS.length; index++) {
            renderer.setSeriesPaint(index, COMPANY_COLORS[index]);
        }
        return chart;
    }

    private Element svg(JFreeChart chart) {
        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .getDOMImplementation()
                    .createDocument("http://www.w3.org/2000/svg", "svg", null);
            SVGGraphics2D graphics = new SVGGraphics2D(document);
            graphics.setSVGCanvasSize(new Dimension(WIDTH, HEIGHT));
            chart.draw(graphics, new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));
            Element root = graphics.getRoot();
            root.setAttribute("viewBox", "0 0 " + WIDTH + " " + HEIGHT);
            root.setAttribute("width", Integer.toString(WIDTH));
            root.setAttribute("height", Integer.toString(HEIGHT));
            return root;
        } catch (ParserConfigurationException | RuntimeException exception) {
            throw new ReportGenerationException("Could not generate regional sales chart SVG", exception);
        }
    }
}
