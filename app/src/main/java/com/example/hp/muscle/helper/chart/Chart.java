package com.example.hp.muscle.helper.chart;

import android.graphics.Color;
import android.graphics.Paint;


import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Date;

/**
 * Created by hp on 2016/8/30.
 */
public class Chart {
    protected XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    protected XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    protected long addX;
    protected float addY;
    /**
     * 数据
     */
    protected Date[] xCache = new Date[500];
    /**
     * 数据
     */
    protected float[] yCache = new float[500];
    protected int pointNum;
    protected GraphicalView chartView;
    protected static final int TIMESERIES = 1;
    protected static final int LINESERIES = 0;

    /**
     * 有两种chart，一种是横坐标为时间轴一直在变，另一种是横坐标的点数固定
     *
     * @param pointNum 横坐标的数量
     */
    public Chart(int pointNum) {
        this.pointNum = pointNum;
    }


    /**
     * 更新曲线框,默认的为时间序列曲线
     * 如果为其他序列，需要覆盖这个方法
     *
     * @param seriesNum 曲线编号
     * @param voltage   数值
     */
    public void updateSeries(int seriesNum, float voltage) {
        //设定长度为30
        TimeSeries series = (TimeSeries) dataset.getSeriesAt(seriesNum);
        int length = series.getItemCount();
        if (length >= pointNum) {
            length = pointNum;
        }
        addX = new Date().getTime();
        addY = voltage;
        //保存之前的值
        for (int i = 0; i < length; i++) {
            xCache[i] = new Date((long) series.getX(i));
            yCache[i] = (float) series.getY(i);
        }
        series.clear();
        series.add(addX, addY);
        for (int i = 0; i < length; i++)
            series.add(xCache[i], yCache[i]);
        //在数据集中添加新的点集
        dataset.removeSeries(seriesNum);
        dataset.addSeries(seriesNum, series);
        //曲线更新
        chartView.invalidate();
    }

    /**
     * 设定曲线框的样式
     *
     * @param title  标题
     * @param xTitle 横坐标
     * @param yTitle 纵坐标
     * @param yMin   纵坐标最小值
     * @param yMax   纵坐标最大值
     */
    public void setRenderer(String title, String xTitle, String yTitle, int yMin, int yMax) {

        //外面图形框的设置
        renderer.setChartTitle(title);//标题
        renderer.setChartTitleTextSize(15);//标题字体大小
        renderer.setXTitle(xTitle);    //x轴说明
        renderer.setYTitle(yTitle);
        // renderer.setBackgroundColor(Color.WHITE);
        //renderer.setApplyBackgroundColor(true);
        renderer.setAxisTitleTextSize(15);//坐标轴字体大小
        renderer.setLegendTextSize(15);    //图例字体大小
        renderer.setShowLegend(true);   //显示图例
        // renderer.setBackgroundColor(Color.GRAY);
        renderer.setXLabels(15);//横轴字体
        renderer.setYLabels(15);
        renderer.setAxesColor(Color.LTGRAY);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(false, false);
        renderer.setPanLimits(new double[]{-10, 20, -10, 40});
        renderer.setZoomLimits(new double[]{-10, 20, -10, 40});
        renderer.setMargins(new int[]{50, 40, 40, 0});//top,left,bottom,right

        renderer.setMarginsColor(Color.GRAY);
        renderer.setShowGrid(true);
        renderer.setYAxisMax(yMax);//设置Y轴范围
        renderer.setYAxisMin(yMin);
        renderer.setXAxisMax(400);
        renderer.setXAxisMin(0);

        renderer.setInScroll(false);  //调整大小

    }

    /**
     * 添加一条曲线
     *
     * @param datasetTitle 曲线标题
     * @param seriesType   曲线类型，0是横坐标固定的曲线，1是时间曲线
     * @param lineColor    曲线颜色
     * @param lineWidth    曲线线宽
     * @param style        点的风格
     */
    public void AddSeriesToRender(String datasetTitle, int seriesType, int lineColor, int lineWidth, PointStyle style) {
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(lineColor);
        r.setChartValuesTextSize(1);
        r.setChartValuesSpacing(1);
        r.setPointStyle(style);
        r.setFillPoints(false);
        r.setLineWidth(lineWidth);
        renderer.addSeriesRenderer(r);
        addDataSet(datasetTitle, seriesType);
    }


    /**
     * 增加时间序列
     *
     * @param dataSetTitle 曲线标题
     */
    private void addDataSet(String dataSetTitle, int seriesType) {
        switch (seriesType) {
            case LINESERIES:
                XYValueSeries series = new XYValueSeries(dataSetTitle);
                dataset.addSeries(series);
                break;
            case TIMESERIES:
                TimeSeries serie = new TimeSeries(dataSetTitle);
                dataset.addSeries(serie);
                break;
            default:
                break;
        }

    }

}
