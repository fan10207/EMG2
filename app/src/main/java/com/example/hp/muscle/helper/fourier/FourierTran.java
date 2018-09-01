package com.example.hp.muscle.helper.fourier;

import android.provider.Settings;
import android.util.Log;

import org.jtransforms.fft.FloatFFT_1D;

/**
 * Created by hp on 2016/8/24.
 */
public class FourierTran {
    private int fs;//采样频率
    private int length;//数组长度
    private float[] data;//处理的数据及计算结果
    private FloatFFT_1D fft;//快速傅里叶变换实例
    private int fftLength;//傅里叶变换的点数
    private float[] result;//频谱数据，幅度谱
    private String TAG = "FFT";

    public FourierTran(int fs, int n, float[] array) {
        this.fs = fs;
        this.length = n;
        data=new float[array.length];
        System.arraycopy(array, 0, data, 0, array.length);
        fft = new FloatFFT_1D(length);
        fft.realForward(data);

    }

    public float[] callMagnitude() {
        if (length % 2 == 0) {
            int len = length / 2 + 1;
            fftLength = len - 1;
            result = new float[len];
            result[len - 1] = Math.abs(data[1]);
            result[0] = data[0];
            for (int i = 1; i < len - 1; i++) {
                result[i] = (float) Math.sqrt((Math.pow(data[2 * i], 2)) + Math.pow(data[2 * i + 1], 2));
            }
        } else {
            int len = (length + 1) / 2;
            fftLength = len - 1;
            result = new float[len];
            result[0] = data[0];
            for (int i = 1; i < len - 1; i++) {
                result[i] = (float) Math.sqrt(Math.pow(data[2 * i], 2) + Math.pow(data[2 * i + 1], 2));
            }
            result[len - 1] = (float) Math.sqrt(Math.pow(data[length - 1], 2) + Math.pow(data[1], 2));
        }
        return result;
    }

}
