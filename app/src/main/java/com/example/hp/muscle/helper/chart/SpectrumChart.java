package com.example.hp.muscle.helper.chart;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYValueSeries;

/**
 * Created by hp on 2016/9/9.
 */
public class SpectrumChart extends Chart {


    public SpectrumChart(Context context, LinearLayout layout, int pointNum) {
        super(pointNum);
        setRenderer("第1,2,3,4通道频谱图","频率","幅值",0,50000);
        renderer.setXAxisMax(pointNum);
        renderer.setXAxisMin(0);
        AddSeriesToRender("第1通道频谱", 0, Color.BLACK, 1, PointStyle.POINT);
        chartView = ChartFactory.getLineChartView(context, dataset, renderer);
        layout.addView(chartView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public void drawSpectrum(int seriesNum, float[] voltage) {
        XYValueSeries series=(XYValueSeries) dataset.getSeriesAt(seriesNum);
        series.clear();
        for(int i = 0; i <401*voltage.length/1000+1; i ++){
            series.add(i*1000/voltage.length, voltage[i]);
        }

        //在数据集中添加新的点集
        dataset.removeSeries(seriesNum);
        dataset.addSeries(seriesNum, series);
        //曲线更新
        chartView.invalidate();
    }
}
