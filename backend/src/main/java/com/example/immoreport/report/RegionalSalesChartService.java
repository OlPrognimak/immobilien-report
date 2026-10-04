package com.example.immoreport.report;

import com.example.immoreport.sales.RegionalSalesPoint;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegionalSalesChartService {

    private static final int WIDTH = 510;
    private static final int HEIGHT = 430;

    private static final Color[] COMPANY_COLORS = {
            Color.decode("#315470"),
            Color.decode("#C17C2E")
    };

    private static final double AXIS_LABEL_ANGLE =
            Math.PI / 4.0; // 45 degrees

    private static final double Y_AXIS_TICK_UNIT = 500_000.0;

    public Element createChartSvg(
            List<RegionalSalesPoint> regionalSales) {

        JFreeChart chart;

        try {
            DefaultCategoryDataset dataset = dataset(regionalSales);
            chart = chart(dataset);
        } catch (RuntimeException exception) {
            throw new ReportGenerationException(
                    "Could not create regional sales chart dataset or chart",
                    exception
            );
        }

        return svg(chart);
    }

    private DefaultCategoryDataset dataset(
            List<RegionalSalesPoint> regionalSales) {

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        for (RegionalSalesPoint point : regionalSales) {
            dataset.addValue(
                    point.amount(),
                    point.companyName(),
                    point.region()
            );
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

        configureChart(chart);

        return chart;
    }

    private void configureChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);

        chart.getTitle().setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 13)
        );

        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(
                    new Font(Font.SANS_SERIF, Font.PLAIN, 9)
            );
        }

        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Color.decode("#CBD5DC"));

        configureXAxis(plot);
        configureYAxis(plot);
        configureRenderer(plot);
    }

    private void configureXAxis(CategoryPlot plot) {
        CategoryAxis xAxis = plot.getDomainAxis();

        // Rotate region names by 45 degrees.
        xAxis.setCategoryLabelPositions(
                CategoryLabelPositions.UP_45
        );

        xAxis.setLabelFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 10)
        );

        xAxis.setTickLabelFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 10)
        );

        xAxis.setCategoryLabelPositionOffset(8);
        xAxis.setMaximumCategoryLabelWidthRatio(0.9f);
    }

    private void configureYAxis(CategoryPlot plot) {
        /*
         * Important: create and install the custom axis.
         * The previous code only declared RotatedNumberAxis,
         * but continued using the default NumberAxis.
         */
        RotatedNumberAxis yAxis = new RotatedNumberAxis(
                "Sales (€)",
                AXIS_LABEL_ANGLE
        );

        yAxis.setLabelFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 10)
        );

        yAxis.setTickLabelFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 10)
        );

        yAxis.setNumberFormatOverride(
                new DecimalFormat("€ #,##0")
        );

        yAxis.setAutoRangeIncludesZero(true);
        yAxis.setTickUnit(new NumberTickUnit(Y_AXIS_TICK_UNIT));

        // Install RotatedNumberAxis into the plot.
        plot.setRangeAxis(yAxis);
    }

    private void configureRenderer(CategoryPlot plot) {
        BarRenderer renderer =
                (BarRenderer) plot.getRenderer();

        renderer.setBarPainter(new StandardBarPainter());
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        for (int index = 0;
             index < COMPANY_COLORS.length;
             index++) {

            renderer.setSeriesPaint(
                    index,
                    COMPANY_COLORS[index]
            );
        }
    }

    private Element svg(JFreeChart chart) {
        try {
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .getDOMImplementation()
                    .createDocument(
                            "http://www.w3.org/2000/svg",
                            "svg",
                            null
                    );

            SVGGraphics2D graphics =
                    new SVGGraphics2D(document);

            graphics.setSVGCanvasSize(
                    new Dimension(WIDTH, HEIGHT)
            );

            chart.draw(
                    graphics,
                    new Rectangle2D.Double(
                            0,
                            0,
                            WIDTH,
                            HEIGHT
                    )
            );

            Element root = graphics.getRoot();

            root.setAttribute(
                    "viewBox",
                    "0 0 " + WIDTH + " " + HEIGHT
            );

            root.setAttribute(
                    "width",
                    Integer.toString(WIDTH)
            );

            root.setAttribute(
                    "height",
                    Integer.toString(HEIGHT)
            );

            return root;
        } catch (ParserConfigurationException
                 | RuntimeException exception) {

            throw new ReportGenerationException(
                    "Could not generate regional sales chart SVG",
                    exception
            );
        }
    }

    /**
     * Custom NumberAxis supporting an arbitrary tick-label angle.
     *
     * The standard NumberAxis supports only horizontal labels
     * or vertical labels rotated by 90 degrees.
     */
    private static final class RotatedNumberAxis
            extends NumberAxis {

        private final double tickLabelAngle;

        private RotatedNumberAxis(
                String label,
                double tickLabelAngle) {

            super(label);
            this.tickLabelAngle = tickLabelAngle;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        protected List refreshTicksVertical(
                Graphics2D graphics,
                Rectangle2D dataArea,
                RectangleEdge edge) {

            /*
             * Let NumberAxis calculate the range, tick values,
             * automatic tick units and formatted labels.
             */
            List originalTicks = super.refreshTicksVertical(
                    graphics,
                    dataArea,
                    edge
            );

            List rotatedTicks =
                    new ArrayList(originalTicks.size());

            for (Object value : originalTicks) {
                if (value instanceof NumberTick numberTick
                        && numberTick.getText() != null
                        && !numberTick.getText().isBlank()) {

                    rotatedTicks.add(
                            rotateTick(numberTick, edge)
                    );
                } else {
                    // Preserve unlabeled minor ticks.
                    rotatedTicks.add(value);
                }
            }

            return rotatedTicks;
        }

        private NumberTick rotateTick(
                NumberTick original,
                RectangleEdge edge) {

            boolean leftAxis =
                    edge == RectangleEdge.LEFT;

            TextAnchor anchor = leftAxis
                    ? TextAnchor.CENTER_RIGHT
                    : TextAnchor.CENTER_LEFT;

            /*
             * For an axis on the left, a negative angle
             * produces an upward 45-degree rotation.
             */
            double angle = leftAxis
                    ? -tickLabelAngle
                    : tickLabelAngle;

            return new NumberTick(
                    original.getTickType(),
                    original.getValue(),
                    original.getText(),
                    anchor,
                    anchor,
                    angle
            );
        }
    }
}
