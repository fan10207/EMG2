package com.example.hp.muscle.helper.bluetooth;

import com.example.hp.muscle.helper.ChartDemoFragment;

/**
 * Created by hp on 2016/7/30.
 */
public class Filter {
    private float b[] = {0.3913f, -0.7827f, 0.3913f};
    private float a[] = {0.3695f, -0.1958f};
    //save X[k-i] and Y[k-i],x是当前输入,X[0]是x的前一个输入X(k-1),X[1]存放X(k-2)
    private float Xk[] = new float[4];
    private float Yk[] = new float[4];
    //用来计算16点EMG绝对值平均值
    private float Uk[] = new float[2];
    private float envelope[] = new float[3];
    public float c[] = new float[10];
    private int EMG_Streng[] = new int[128];
    private int signal;
    private float[] voltage = new float[2];
    private int last_signal = 0;
    public int count1 = 0;

    /*
     *	digital filter
     * */
    public float filter(float x){
        //y(k) = 0.3913 * u(k) -0.7827 * u(k-2) + 0.3913 * u(k-4) + 0.3695 * y(k-2) -0.1958 * y(k-4)
        float y = b[0] * x + b[1] * Xk[1] + b[2] * Xk[3] + a[0] * Yk[1] + a[1] * Yk[3];
        Xk[3] = Xk[2];//数组更新
        Xk[2] = Xk[1];
        Xk[1] = Xk[0];
        Xk[0] = x;

        Yk[3] = Yk[2];
        Yk[2] = Yk[1];
        Yk[1] = Yk[0];
        Yk[0] = y;
        return y;
    }

    /**
     * get envelop
     **/
    public float getEnvelope(float x){
        //int y = (envelope[0]>>2) + (envelope[1]>>2) + (envelope[2]>>3) + (x>>3) + (Uk[0]>>3) + (Uk[1]>>3);
        float y = ((float)(0.35 * envelope[0])) + (float)(0.25*envelope[1]) + (float)(0.125*envelope[2])
                + (float)(0.125 * x) + (float)(0.1 * Uk[0]) +(float)(0.05* Uk[1]);
        envelope[2] = envelope[1];
        envelope[1] = envelope[0];
        envelope[0] = y;

        Uk[1] = Uk[0];
        Uk[0] = x;
        return y;
    }

    /**
     *	求连续16点的和,存于数组中
     **/
    public void getSum(float a[]) {
        int i = 0;
        int j = 16;
        while (i+j < 56){
            float sum=0;
            for (int k = i; k < i+20; k++)
            {
                sum += Math.abs(a[k]);
            }
            c[i/4]=sum;
            i += 4;

        }
    }

    /**
     * 过零率
     */
    public int zeroPass(float x){
        //for (int i = 0; i < 112; i++) {
        if ((last_signal == 0) && (voltage[1] > voltage[0]) && (voltage[1] > x) &&
                (voltage[1] > 0.25 * ( 0.5 + ChartDemoFragment.scale ) * ChartDemoFragment.threshold)) {
            last_signal = 1;
        }
        if ((last_signal == 1) && (voltage[0] > voltage[1]) && (x > voltage[1]) && (0 > voltage[1])) {
            last_signal = 0;
            count1++;
        }
        voltage[0] = voltage[1];
        voltage[1] = x;
        //}
        return count1;
    }


    /**
     * 求阀值
     */
    public float getThreshold(float[] count){
        float noiceSrms = 0;
        float variance = 0;
        int L = 500;
        for (int i = 0; i < L; i++) {
            noiceSrms += count[i] / L;

        }
        for (int i = 0; i < 500; i++) {
            variance += ((Math.abs(count[i]) - noiceSrms) * (Math.abs(count[i]) - noiceSrms) / (L - 1));
        }
        variance = (float) Math.sqrt(variance);

        return noiceSrms + 5 * variance;
    }



}
