package com.example.hp.muscle.helper;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hp.muscle.MainActivity;
import com.example.hp.muscle.R;
import com.example.hp.muscle.helper.chart.SpectrumChart;
import com.example.hp.muscle.helper.fourier.FourierTran;

/**
 * Created by hp on 2016/9/9.
 */
public class FrequencyFragment extends Fragment {
    private String TAG = "fft";
    private float[] data;//存傅里叶变换后的结果
    private LinearLayout SpectrumChart;//画图的布局
    private com.example.hp.muscle.helper.chart.SpectrumChart dataChart;//画图的类
    //相关指标
    private float mAEMGOne;
    private float mRMS;
    private FourierTran fourierTran;
    private float lzc;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.result, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            float[] buffer = new float[bundle.getFloatArray("buffer").length];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = bundle.getFloatArray("buffer")[i];
                //  Log.e(TAG, "data ["+i+"]"+data[i] );
            }
            lzc = lzc(buffer);
            fourierTran = new FourierTran(2000, buffer.length, buffer);
            data = new float[fourierTran.callMagnitude().length];
            data = fourierTran.callMagnitude();

            Log.e(TAG, "data length" + data.length);
        /*    for (int i = 0; i < data.length; i++) {
                data[i] = bundle.getFloatArray("buffer")[i];
                //  Log.e(TAG, "data ["+i+"]"+data[i] );
            }*/
            mAEMGOne = bundle.getFloat("AEMG");
            mRMS = bundle.getFloat("RMS");
        }
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        TextView AEMGONE = (TextView) getActivity().findViewById(R.id.Aa);
        TextView RMSONE = (TextView) getActivity().findViewById(R.id.Ab);
        TextView MF = (TextView) getActivity().findViewById(R.id.Ac);
        TextView MPF = (TextView) getActivity().findViewById(R.id.Ad);
        TextView LZP = (TextView) getActivity().findViewById(R.id.Ag);
        float[] MpMpf1 = callMedianFrequency();
        //显示相关指标
        MF.setText(String.valueOf(MpMpf1[0]));
        MPF.setText(String.valueOf(MpMpf1[1]));
        AEMGONE.setText(String.valueOf(mAEMGOne));
        RMSONE.setText(String.valueOf(mRMS));
        LZP.setText(String.valueOf(lzc));
        super.onActivityCreated(savedInstanceState);
        this.SpectrumChart = (LinearLayout) getActivity().findViewById(R.id.frequencyLayout);
       /* dataChart=new SpectrumChart(getActivity(),SpectrumChart,data.length);
        dataChart.drawSpectrum(1,data);

*/
        //求出要显示的最大值，确定画图标度
        float bb = 0;
        for (int i = 0; i < data.length; i++) {
            if (bb < data[i]) {
                bb = data[i];
            }
        }
        int a = (int) (bb / 1000 + 1) * 1000;
        dataChart = new SpectrumChart(getActivity(), SpectrumChart, data.length);
        dataChart.setRenderer("第1,2,3,4通道频谱图", "频率", "幅值", 0, a);
        dataChart.drawSpectrum(0, data);


    }

    /**
     *
     * @return 二维数组 对应两个频域指标MF MPF
     */

    public float[] callMedianFrequency() {
        float[] res = new float[2];
        float[] buffer = new float[data.length];
        float sum = 0;
        for (int i = 0; i < buffer.length/2; i++) {
            buffer[i] = data[i] * data[i];
            sum += buffer[i];
        }

        float midSum = 0;
        for (int i = 0; i < buffer.length/2; i++) {
            midSum += buffer[i];
            if (midSum >= sum / 2) {
                res[0] = i * 1000 / data.length;
                break;
            }
        }
        float spectrumSum = 0;
        for (int i = 0; i < buffer.length/2; i++) {
            spectrumSum += (i + 1) * buffer[i];
        }
        res[1] = (spectrumSum * 1000 / data.length) / sum;


        return res;
    }

    public float lzc(float[] data) {
        int[] zone = {0, 0};
        int[] x = new int[data.length];
       float[] result = mean(data);
        float datamean = result[0];
        float datamin = result[1];
        float datamax = result[2];
        float upmean = (datamax + datamean) / 2;
        float downmean = (datamean + datamin) / 2;
        if (data[0] < datamean) {
            x[0] = 0;
            if (data[0] < downmean) {
                zone[0] = 0;
            } else{
                zone[0] = 1;}
        } else {
            x[0] = 1;
            if (data[0] < upmean) {
                zone[0] = 2;
            } else {
                zone[0] = 3;
            }
        }
        for (int i = 1; i < data.length; i++) {
            if (data[i] < datamean) {
                if (data[i] < downmean) {
                    zone[1] = 0;
                } else zone[1] = 1;
            } else {
                if (data[i] < upmean) {
                    zone[1] = 2;
                } else zone[1] = 3;
            }
            if (zone[0] == zone[1]) {
                x[i] = x[i - 1];
            }
            else {
                if (data[i] < data[i - 1]) {
                    x[i]=0;
                }else x[i]=1;
            }
            zone[0] = zone[1];
        }

        int c=1;
        String S = String.valueOf(x[0]);
        String Q = new String();

        String SQ;
        String SQv;
        for (int i = 1; i < x.length; i++) {
            Q=Q.concat(""+x[i]);
            SQ=S.concat(Q);
            SQv = SQ.substring(0, SQ.length() - 1);
            if (!SQv.contains(Q)) {
                S=SQ;
                Q = new String();
                c++;
            }
        }
        c++;
        return  (float)(c*(Math.log(x.length)/(Math.log(2)*x.length)));



    }

    public float[] mean(float[] buffer) {
        float sum = 0;
        float min = 0;
        float max = 0;
        for (int i = 0; i < buffer.length; i++) {
            sum += buffer[i];
            if (buffer[i] < min) {
                min = buffer[i];
            }
            if (buffer[i] > max) {
                max = buffer[i];
            }
        }
        float[] result = new float[3];
        result[0] = sum / buffer.length;
        result[1] = min;
        result[2] = max;
        return result;
    }


}
