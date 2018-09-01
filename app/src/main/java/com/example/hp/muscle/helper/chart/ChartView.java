package com.example.hp.muscle.helper.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by hp on 2016/7/18.
 */
public class ChartView extends SurfaceView {
    private static final String TAG = "ChartView";

    private int POINT_NUM = 16128;
    int cnt;
    //to draw line
    public static final int DRAW_MOVE = 0;
    public static final int SET_START_END = 2;
    public static final int FINISHED=1;
    public static final int UNFINISHED=0;
    private int mdrawstate = DRAW_MOVE;
    private float[] mPoints;
    private float[] bufferPoints;
    public float[] comPoints;
    public float[] chosenData;
    private float amplitude = 2000f; //纵坐标幅值
    private float envelopeAmp = 1.0f;
    //points num in the view
    private int count = 0;
    private Paint mForePaint = new Paint();
    private Paint mLabelPaint = new Paint();
    private Paint p = new Paint();
    private boolean mCycleColor = false;
    private float last[];
    private float envelopeYAxis = 1.0f;
    public float currentX = 0;
    public float currentY = 0;
    public float start_Coordinate = 0;
    public float end_Coordinate = 0;
    public float startPos = 0;
    public float endPos = 0;
    private int finishPoint=UNFINISHED;
    private int getCurrentX;
    private int xLength=6;
    SurfaceHolder sfh;
    private boolean stopUpdate = false;

    public ChartView(Context context) {
        this(context, null, false);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, false);
    }

    public ChartView(Context context, AttributeSet attrs, boolean mCycleColor) {
        super(context, attrs);
        this.mCycleColor = mCycleColor;
        sfh = getHolder();
        sfh.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                drawBack(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                stopUpdate = true;
            }
        });
        initChartView();
    }

    private void initChartView() {
        mPoints = new float[224];
        bufferPoints = new float[224];
        comPoints = new float[POINT_NUM];
        last = new float[2];
        mForePaint.setStrokeWidth(2f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.BLACK);
        p.setStrokeWidth(3f);
        p.setAntiAlias(true);
        mLabelPaint.setTextSize(10);
        mLabelPaint.setColor(Color.BLACK);
    }

    public void setForePaintColor(int color) {
        mForePaint.setColor(color);
    }

    public void setPOINT_NUM(int POINT_NUM) {
        this.POINT_NUM = POINT_NUM;
        xLength = (getWidth() * 56) / (POINT_NUM - 1) + 1;

    }

    public int getPOINT_NUM() {
        return POINT_NUM;
    }

    public void setEnvelopeYAxis(float envelopeYAxis) {
        this.envelopeYAxis = envelopeYAxis;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public float getAmplitude() {  return this.amplitude;}

    public void setEnvelopeAmp(float envelopeAmp) {
        this.envelopeAmp = envelopeAmp;
    }



    /**
     * update chart from floats data, set our max value as 2000, this value should be changed
     * when your input value is bigger than this value
     *
     * @param floats
     */
    public void updateFloats(float[] floats) {
        if (floats == null || floats.length == 0||stopUpdate==true)
            return;
        Log.e(TAG, "updateFloats: count"+count );

        xLength = (getWidth() * 56) / (POINT_NUM - 1) + 1;
        int frameNum=floats.length*4;
        if ((count + floats.length) > POINT_NUM) {
            comPoints = new float[POINT_NUM];
        }

        if (count >= POINT_NUM) {
            count = 0;
        }

        if (count == 0) {
            drawBack(sfh);
            drawBack(sfh);
            cnt = 0; //cnt是一个标志位
        } else {
            cnt = 1;
        }
        //处理断点的情况
        if (cnt == 1) {
            for (int i=0;i<220;i++) {
                bufferPoints[i] = mPoints[i + 4];
            }
            bufferPoints[220] = last[0];
            bufferPoints[221] = last[1];

            bufferPoints[222] = (getWidth() * (count) / (POINT_NUM - 1));
            bufferPoints[223]= getHeight() / 2 * envelopeAmp
                    - (floats[0] / amplitude * (getHeight() / 2)) * envelopeAmp;
        } else {
            for (int i=0;i<112;i++) {
                bufferPoints[2*i+1]=(getHeight()+1)/2;
                bufferPoints[2*i] = 0;
            }
        }
        getCurrentX = getWidth() * count / POINT_NUM;//获取绘制当前点的坐标
        if (getCurrentX ==0) {
            getCurrentX=getCurrentX+1;
        }


        //画点，两点连成一条直线
        for (int i = 0; i < floats.length - 1; i++) {
            comPoints[count] = floats[i];
            mPoints[count * 4 % frameNum] = (getWidth() * count / (POINT_NUM - 1));
            mPoints[(count * 4 + 1) % frameNum] = getHeight() / 2 * envelopeAmp
                    - (floats[i] / amplitude * (getHeight() / 2)) * envelopeAmp;
            mPoints[(count * 4 + 2) % frameNum] = (getWidth() * (count + 1) / (POINT_NUM - 1));
            mPoints[(count * 4 + 3) % frameNum] = getHeight() / 2 * envelopeAmp
                    - (floats[i + 1] / amplitude * (getHeight() /  2)) * envelopeAmp;

            ++count;
        }
        last[0] = (getWidth() * count / (POINT_NUM - 1));
        last[1] =getHeight() / 2 * envelopeAmp
                - (floats[floats.length - 1] / amplitude * (getHeight() / 2)) * envelopeAmp;
        count += 1;

        if (mCycleColor) {
            cycleColor();
        }

        Canvas canvas = sfh.lockCanvas(new Rect(getCurrentX-1, 0, getCurrentX + xLength+1, getHeight()));
        canvas.drawLines(bufferPoints,mForePaint);
        canvas.drawLines(mPoints, mForePaint);
        sfh.unlockCanvasAndPost(canvas);

    }

    public void drawLabel(Canvas canvas) {
        int delta =getHeight() / 10;
        for (int i = 0; i <= 10; i++) {
            canvas.drawText((float) (Math.round((amplitude - 0.2 * i * amplitude / envelopeYAxis) * 10)) / 10
                    + "", 0, delta * i, mLabelPaint);
        }
    }


    public void cleanChart() {
        Log.e(TAG, "cleanChart: clean");
        amplitude = 2000f;
        stopUpdate = false;
        drawBack(sfh);
        comPoints = new float[POINT_NUM];
        mPoints = new float[224];
        count = 0;

    }



    private float colorCounter = 0;

    private void cycleColor() {
        int r = (int) Math.floor(128 * (Math.sin(colorCounter + 3)));
        int g = (int) Math.floor(128 * (Math.sin(colorCounter + 1) + 1));
        int b = (int) Math.floor(128 * (Math.sin(colorCounter + 7) + 1));
        mForePaint.setColor(Color.argb(128, r, g, b));
        colorCounter += 0.03;
    }

    /**
     *
     * @param event
     * @return 用于取得选取起始点时触屏得到准确位置
     */

    public boolean onTouchEvent(MotionEvent event) {
        if (mdrawstate == SET_START_END) {

            //获取按下位置坐标
            this.currentX = event.getX();
            this.currentY = event.getY();
            if (start_Coordinate == 0 | end_Coordinate == 0) {
                if (start_Coordinate == 0) {
                    p.setColor(Color.GREEN);
                    startPos = currentX;
                    start_Coordinate = currentX / getWidth() * POINT_NUM;
                    Log.e(TAG, "onTouchEvent:currentX "+currentX );
                    Canvas canvas = sfh.lockCanvas();
                    canvas.drawLine(currentX, 0, currentX, getHeight(), p);
                    sfh.unlockCanvasAndPost(canvas);
                    sfh.lockCanvas(new Rect(0, 0, 0, 0));
                    sfh.unlockCanvasAndPost(canvas);

                } else if (end_Coordinate == 0 && ((currentX - startPos) > 20)) {
                    Log.e(TAG, "onTouchEvent:currentX2 "+currentX );
                    endPos = currentX;
                    end_Coordinate = currentX / getWidth() * POINT_NUM;
                    Canvas canvas = sfh.lockCanvas();
                    canvas.drawLine(startPos, 0, startPos, getHeight(), p);
                    p.setColor(Color.RED);
                    canvas.drawLine(currentX, 0, currentX, getHeight(), p);
                    sfh.unlockCanvasAndPost(canvas);
                    sfh.lockCanvas(new Rect(0, 0, 0, 0));
                    sfh.unlockCanvasAndPost(canvas);
                    finishPoint = FINISHED;
                }
            }
        }
        return true;
    }




    /**
     * 改变画图的状态的子函数
     * @param i
     */
    public void setDrawstate(int i) {
        mdrawstate = i;
    }

    public int getDrawstate() {
        return mdrawstate;
    }

    public void stopDrawing() {
        setDrawstate(SET_START_END);
        stopUpdate=true;
    }

    /**
     *
     * @return 返回有效肌电值
     */

    public float[] returnChosenData() {
        int length = (int) (end_Coordinate - start_Coordinate);
        chosenData = new float[length];
        for (int i = 0; i < length; i++) {
            chosenData[i] = comPoints[i+(int)start_Coordinate];

        }
        return chosenData;


    }

    public int getMdrawstate() {
        return finishPoint;
    }

    private void drawBack(SurfaceHolder surfaceHolder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(2);
        drawLabel(canvas);
       /* canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, p);*/
        canvas.drawLine(0, 0, 0, getHeight() - 10, p);
        surfaceHolder.unlockCanvasAndPost(canvas);
        //重新锁一次，使背景持久化
        surfaceHolder.lockCanvas(new Rect(0, 0, 0, 0));
        surfaceHolder.unlockCanvasAndPost(canvas);

    }


}