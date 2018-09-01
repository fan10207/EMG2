package com.example.hp.muscle.helper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.hp.muscle.R;
import com.example.hp.muscle.helper.bluetooth.AppFragment;
import com.example.hp.muscle.helper.bluetooth.BluetoothService;
import com.example.hp.muscle.helper.bluetooth.DeviceListActivity;
import com.example.hp.muscle.helper.chart.ChartView;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * Created by hp on 2016/7/18.
 */
public class ChartDemoFragment extends AppFragment implements View.OnClickListener {

    //定义4个波形显示区域
    private ChartView mDataOneChart;
    private ChartView mDataTwoChart;
    private ChartView mDataThreeChart;
    private ChartView mDataFourChart;
    //存储需要显示的数据，线程安全的队列

    //放大倍数，初始值为1
    public static float scale = 1.2f;

    //ADC 精度,0.805664mv
    public static final float AD = 0.805664f;

    //存通道数据


    private int frameNum = 288;
    private float maxData = 0f;
    private float mData = 0f;
    private float maxData1 = 0f;
    private float maxData2 = 0f;
    private float maxData3 = 0f;
    private float mData1 = 0f;
    private float mData2 = 0f;
    private float mData3 = 0f;


    public static float threshold = 2000f;
    private Writer writerOne;//用于写肌电数据

    private int constb = 4;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_CONNECT_DEVICE = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private SerialService mSerialService;

    //蓝牙传过来的数据
    public float[] valueabledata;
    //含50Hz工频陷波的滤波器参数
    private double[] b1 = {0.1621, 0.328, -0.1468, -0.6253, -0.1468, 0.328, 0.1621};
    private double[] a1 = {1.134, -0.1204, 0.3858, -0.3186, -0.1138, -0.02828};
    //肌电包络滤波器
    private double[] b = {0.35, 0.25, 0.125};


    private float[] datachannel1;//用于存选取数据进行频谱分析




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //显示布局
        View view = inflater.inflate(R.layout.datacapture, container, false);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //显示波形的区域
        mDataOneChart = (ChartView) getActivity().findViewById(R.id.chartChannel1);
        mDataTwoChart = (ChartView) getActivity().findViewById(R.id.chartChannel2);
        mDataThreeChart = (ChartView) getActivity().findViewById(R.id.chartChannel3);
        mDataFourChart = (ChartView) getActivity().findViewById(R.id.chartChannel4);
        sharedPreferences = getActivity().getSharedPreferences("address", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //定义蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //定义3个按键
        Button scan_bluetooth = (Button) getActivity().findViewById(R.id.scan_bluetooth);
        Button button_start = (Button) getActivity().findViewById(R.id.start_caculate);
        Button button_stopdrawing = (Button) getActivity().findViewById(R.id.stop_drawing);
        Button button_serial = (Button) getActivity().findViewById(R.id.button_serial);
        button_stopdrawing.setOnClickListener(this);
        scan_bluetooth.setOnClickListener(this);
        button_start.setOnClickListener(this);
        button_serial.setOnClickListener(this);

//        //定义写文件的路径(存储蓝牙传过来的肌电信号)
        File sd = Environment.getExternalStorageDirectory();
        String p = sd.getPath() + "/signal";
        File f = new File(p);
        if (!f.exists())
            f.mkdir();
        String path = "/mnt/sdcard/signal/" + new Date().getTime() + ".txt";
        //建立写数据的对象(存储蓝牙传过来的肌电信号)
        final File fileOne = new File(path);
        try {
            writerOne = new FileWriter(fileOne, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //开启线程，等待蓝牙通信
        mHandler = new MyHandler(this);
        mChatService = new BluetoothService(context, mHandler);
        mSerialService = new SerialService(mHandler);
    }
    //按键相应事件
    public void onClick(View view) {
        switch (view.getId()) {

            //搜索蓝牙
            case R.id.scan_bluetooth:
                cleanBuffer();
                mChatService = new BluetoothService(context, mHandler);
                mDataOneChart.cleanChart();
                mDataTwoChart.cleanChart();
                mDataThreeChart.cleanChart();
                mDataFourChart.cleanChart();
                Intent serverIntent = new Intent(context, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                break;

            //开始计算肌电信号的起始结束值
            case R.id.start_caculate:
                //停止蓝牙传输和清除波形
                if (mDataOneChart.getMdrawstate() == 1) {
                    datachannel1 = new float[mDataOneChart.returnChosenData().length];
                    for (int i = 0; i < datachannel1.length; i++) {
                        datachannel1[i] = mDataOneChart.returnChosenData()[i];
                    }
                    float AEMG = 0,
                            RMS = 0;
                    if (datachannel1 != null) {
                        for (int i = 0; i < datachannel1.length; i++) {
                            AEMG += Math.abs(datachannel1[i]);
                            RMS += datachannel1[i] * datachannel1[i];
                        }
                        AEMG = AEMG / datachannel1.length;
                        RMS = (float) Math.sqrt(RMS / datachannel1.length);
                        Bundle bundle = new Bundle();
                        bundle.putFloatArray("buffer", datachannel1);
                        bundle.putFloat("AEMG", AEMG);
                        bundle.putFloat("RMS", RMS);
                        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                        FrequencyFragment freqFragment = new FrequencyFragment();
                        transaction.replace(R.id.right_layout, freqFragment);
                        freqFragment.setArguments(bundle);
                        transaction.commit();
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity(), "未设置起始点", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            //停止画图
            case R.id.stop_drawing:
                if (mChatService.getState() == BluetoothService.STATE_CONNECTED) {
                    mChatService.stop();
                    mDataOneChart.stopDrawing();
                    mDataTwoChart.stopDrawing();
                    mDataThreeChart.stopDrawing();
                    mDataFourChart.stopDrawing();
                } else {
                    Toast.makeText(getActivity(), "未开始连接设备", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_serial:
                try {
                    mSerialService.getSerialPort("/dev/ttymxc1", 230400);
                    Toast.makeText(getActivity(), "开启串口", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSerialService.startSerial();
                break;
        }
    }



    //线程自动接收蓝牙数组
    private class MyHandler extends Handler {
        private WeakReference<Fragment> weakRefFragment;
        /**
         * a
         * A constructor that gets a weak reference to the enclosing class. We
         * do this to avoid memory leaks during Java Garbage Collection.
         */
        public MyHandler(Fragment fragment) {
            weakRefFragment = new WeakReference<Fragment>(fragment);
        }

        //重写handleMessage方法，用于处理消息
        @Override
        public void handleMessage(Message msg) {
            {
                Log.e(TAG, "aaa start" );

                float[] readBufOne = new float[msg.arg1 / constb];
                float[] readBufTwo = new float[msg.arg1 / constb];
                float[] readBufThree = new float[msg.arg1 / constb];
                float[] readBufFour = new float[msg.arg1 / constb];

                //根据蓝牙传过来的数据进行处理
                switch (msg.what) {
                    case MESSAGE_READ:
                        float[] readBuf = (float[]) msg.obj;
                        int count = msg.arg1;
                        for (int i = 0; i < count; i++) {
                            readBuf[i] = readBuf[i];
                            try {
                                writerOne.append(readBuf[i] + "" + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (i % constb == 0) {
                                readBufOne[i / constb] = readBuf[i]; //出现 数组越界？
                                if (Math.abs(readBuf[i]) > mData) {
                                    mData = Math.abs(readBuf[i]);
                                }
                            } else if (i % constb == 1) {
                                readBufTwo[(i - 1) / constb] = readBuf[i];
                                if (Math.abs(readBuf[i]) > mData1) {
                                    mData1 = Math.abs(readBuf[i]);
                                }
                            } else if (i % constb == 2) {
                                readBufThree[(i - 2) / constb] = readBuf[i];
                                if (Math.abs(readBuf[i]) > mData2) {
                                    mData2 = Math.abs(readBuf[i]);
                                }
                            } else {
                                readBufFour[(i - 3) / constb] = readBuf[i];
                                if (Math.abs(readBuf[i]) > mData3) {
                                    mData3 = Math.abs(readBuf[i]);
                                }

                            }
                        }
                            if (frameNum > 0) {
                                maxData = mData;
                                maxData1 = mData1;
                                maxData2 = mData2;
                                maxData3 = mData3;
                                frameNum--;
                            } else {
                                mDataOneChart.setAmplitude(4 * maxData);
                                mDataTwoChart.setAmplitude(4 * maxData1);
                                mDataThreeChart.setAmplitude(4 * maxData2);
                                mDataFourChart.setAmplitude(4 * maxData3);
                            }
                        Log.e(TAG, "aaa start" );
                        mDataOneChart.updateFloats(readBufOne);
                        mDataTwoChart.updateFloats(readBufTwo);
                        mDataThreeChart.updateFloats(readBufThree);
                        mDataFourChart.updateFloats(readBufFour);
                        break;

                    case MESSAGE_STATE_CHANGE:
                        if (D)
                            Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1) {
                            //以下几个情况主要用于显示蓝牙状态的改变，因为取消了标题栏，所以没有添加状态变化的提示
                            case BluetoothService.STATE_CONNECTED:
                              /*  setStatus(getString(R.string.title_connected_to,
                                        mConnectedDeviceName));*/
                                break;
                            case BluetoothService.STATE_CONNECTING:
                            /*    setStatus(R.string.title_connecting);*/
                                break;
                            case BluetoothService.STATE_LISTEN:

                            case BluetoothService.STATE_NONE:
                            /*    setStatus(R.string.title_not_connected);*/
                                break;

                        }
                        break;
                    case MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        Toast.makeText(context,
                                "Connected to " + mConnectedDeviceName,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_TOAST:
                        Toast.makeText(context,
                                msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case MESSAGE_TEXT:
                        break;
                    default:
                        break;
                }
            }
        }
    }

//处理前面的开启DeviceListActivity的返回的结果
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Log.e(TAG, "bluetoothHandle: connect");
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    // setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(context, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void connectDevice(Intent data) {
        String address = data.getExtras().getString(
                DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        editor.putString("address", address);
        editor.commit();
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        getmChatService().connect(device);
    }


    public void ensureDiscorverable() {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "--chartFragment onDestroyView---");
        try {
            writerOne.flush();
            writerOne.close();
       } catch (IOException e) {
           e.printStackTrace();
        }
        mHandler.removeCallbacksAndMessages(null);

    }


    private void cleanBuffer() {
        maxData = 0f;
        mData = 0f;
        maxData1 = 0f;
        maxData2 = 0f;
        maxData3 = 0f;
        mData1 = 0f;
        mData2 = 0f;
        mData3 = 0;
        frameNum = 288;
    }

}
