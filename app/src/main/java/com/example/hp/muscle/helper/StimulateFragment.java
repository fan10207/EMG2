package com.example.hp.muscle.helper;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.muscle.R;
import com.example.hp.muscle.helper.bluetooth.AppFragment;
import com.example.hp.muscle.helper.bluetooth.BluetoothService;
import com.example.hp.muscle.helper.bluetooth.DeviceListActivity;
import com.example.hp.muscle.helper.bluetooth.Filter;
import com.example.hp.muscle.helper.chart.Chart;
import com.example.hp.muscle.helper.chart.ChartView;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.hp.muscle.helper.ChartDemoFragment.REQUEST_CONNECT_DEVICE;
import static com.example.hp.muscle.helper.ChartDemoFragment.REQUEST_ENABLE_BT;


/**
 * Created by hp on 2016/7/21.
 */
public class StimulateFragment extends AppFragment implements View.OnClickListener {

    private String TAG = "Stimulate";
    private int label = 288;
    public static float threshold = 0;
    private float[] noicedata;
    private int counta=0;
    private Filter filter;
    private float maxData = 0f;
    private float mData = 0f;
    private int frameNum = 288;
    private TextView TextPlatform, TextTreat, TextRest, TextFrequency;
    private TextView TextRise, TextStrength, TextFall, TextPulsewidth;
    private int PlatformNum = 10, TreatNum = 30, RestNum = 10, FrequencyNum = 30; //平台时间、治疗时间、休息时间、刺激频率
    private int RiseNum = 10, StrengthNum = 10, FallNum = 10, PulsewidthNum = 100; //上升时间、刺激强度、下降时间、脉冲宽度
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private BluetoothAdapter mBluetoothAdapter;
    @InjectView(R.id.output1)
    ChartView output1;
    @InjectView(R.id.output2)
    ChartView output2;
    @InjectView(R.id.b_connect)
    Button b_connect;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.output, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Initialization(); //控件实例化及设置监听事件
        sharedPreferences = getActivity().getSharedPreferences("address", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new MyHandler(this);
        mChatService = new BluetoothService(context, mHandler);
    }


    private void Initialization() {
        //平台时间
        filter = new Filter();
        noicedata = new float[500];
        output1.setPOINT_NUM(output1.getPOINT_NUM() / 56*10);
        output2.setPOINT_NUM(output2.getPOINT_NUM() / 56*10);
        ImageButton Platformtime_up = (ImageButton) getActivity().findViewById(R.id.platformtime_up);
        TextPlatform = (TextView) getActivity().findViewById(R.id.platformtime_num);
        ImageButton Platformtime_down = (ImageButton) getActivity().findViewById(R.id.platformtime_down);
        Platformtime_up.setOnClickListener(this); //监听点击事件
        Platformtime_down.setOnClickListener(this);
        //治疗时间
        ImageButton Treattime_up = (ImageButton) getActivity().findViewById(R.id.treattime_up);
        TextTreat = (TextView) getActivity().findViewById(R.id.treattime_num);
        ImageButton Treattime_down = (ImageButton) getActivity().findViewById(R.id.treattime_down);
        Treattime_up.setOnClickListener(this);
        Treattime_down.setOnClickListener(this);
        //休息时间
        ImageButton Resttime_up = (ImageButton) getActivity().findViewById(R.id.resttime_up);
        TextRest = (TextView) getActivity().findViewById(R.id.resttime_num);
        ImageButton Resttime_down = (ImageButton) getActivity().findViewById(R.id.resttime_down);
        Resttime_up.setOnClickListener(this);
        Resttime_down.setOnClickListener(this);
        //刺激频率
        ImageButton Frequency_up = (ImageButton) getActivity().findViewById(R.id.frequency_up);
        TextFrequency = (TextView) getActivity().findViewById(R.id.frequency_num);
        ImageButton Frequency_down = (ImageButton) getActivity().findViewById(R.id.frequency_down);
        Frequency_up.setOnClickListener(this);
        Frequency_down.setOnClickListener(this);
        //上升时间
        ImageButton Risetime_up = (ImageButton) getActivity().findViewById(R.id.risetime_up);
        TextRise = (TextView) getActivity().findViewById(R.id.risetime_num);
        ImageButton Risetime_down = (ImageButton) getActivity().findViewById(R.id.risetime_down);
        Risetime_up.setOnClickListener(this);
        Risetime_down.setOnClickListener(this);
        //刺激强度
        ImageButton Strength_up = (ImageButton) getActivity().findViewById(R.id.strength_up);
        TextStrength = (TextView) getActivity().findViewById(R.id.strength_num);
        ImageButton Strength_down = (ImageButton) getActivity().findViewById(R.id.strength_down);
        Strength_up.setOnClickListener(this);
        Strength_down.setOnClickListener(this);
        //下降时间
        ImageButton Falltime_up = (ImageButton) getActivity().findViewById(R.id.falltime_up);
        TextFall = (TextView) getActivity().findViewById(R.id.falltime_num);
        ImageButton Falltime_down = (ImageButton) getActivity().findViewById(R.id.falltime_down);
        Falltime_up.setOnClickListener(this);
        Falltime_down.setOnClickListener(this);
        //脉冲宽度
        ImageButton Pulsewidth_up = (ImageButton) getActivity().findViewById(R.id.pulsewidth_up);
        TextPulsewidth = (TextView) getActivity().findViewById(R.id.pulsewidth_num);
        ImageButton Pulsewidth_down = (ImageButton) getActivity().findViewById(R.id.pulsewidth_down);
        Pulsewidth_up.setOnClickListener(this);
        Pulsewidth_down.setOnClickListener(this);
        b_connect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_connect:
                mChatService = new BluetoothService(context, mHandler);
                Intent serverIntent = new Intent(context, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                break;
            case R.id.platformtime_up:
                if (PlatformNum < 30) {
                    PlatformNum++;
                }
                TextPlatform.setText(PlatformNum + " 分钟");
                break;
            case R.id.platformtime_down:
                if (PlatformNum > 1) {
                    PlatformNum--;
                }
                TextPlatform.setText(PlatformNum + " 分钟");
                break;
            case R.id.treattime_up:
                if (TreatNum < 30) {
                    TreatNum += 5;
                } else if (TreatNum == 60) {
                    TreatNum = 120;
                } else if (TreatNum == 30) {
                    TreatNum = 60;
                }
                TextTreat.setText(TreatNum + " 分钟");
                break;
            case R.id.treattime_down:
                if (TreatNum == 60) {
                    TreatNum = 30;
                } else if (TreatNum == 120) {
                    TreatNum = 60;
                } else if (TreatNum > 5) {
                    TreatNum -= 5;
                }
                TextTreat.setText(TreatNum + " 分钟");
                break;
            case R.id.resttime_up:
                if (RestNum < 30) {
                    RestNum++;
                }
                TextRest.setText(RestNum + " 分钟");
                break;
            case R.id.resttime_down:
                if (RestNum > 1) {
                    RestNum--;
                }
                TextRest.setText(RestNum + " 分钟");
                break;
            case R.id.frequency_up:
                if (FrequencyNum < 50) {
                    FrequencyNum += 5;
                }
                TextFrequency.setText(FrequencyNum + " 赫兹");
                break;
            case R.id.frequency_down:
                if (FrequencyNum > 30) {
                    FrequencyNum -= 5;
                }
                TextFrequency.setText(FrequencyNum + " 赫兹");
                break;
            case R.id.risetime_up:
                if (RiseNum < 60) {
                    RiseNum++;
                }
                TextRise.setText(RiseNum + " 秒");
                break;
            case R.id.risetime_down:
                if (RiseNum > 1) {
                    RiseNum--;
                }
                TextRise.setText(RiseNum + " 秒");
                break;
            case R.id.strength_up:
                if (StrengthNum < 30) {
                    StrengthNum++;
                }
                TextStrength.setText(StrengthNum + " 单位");
                break;
            case R.id.strength_down:
                if (StrengthNum > 0) {
                    StrengthNum--;
                }
                TextStrength.setText(StrengthNum + " 单位");
                break;
            case R.id.falltime_up:
                if (FallNum < 60) {
                    FallNum++;
                }
                TextFall.setText(FallNum + " 秒");
                break;
            case R.id.falltime_down:
                if (FallNum > 1) {
                    FallNum--;
                }
                TextFall.setText(FallNum + " 秒");
                break;
            case R.id.pulsewidth_up:
                if (PulsewidthNum < 500) {
                    PulsewidthNum += 50;
                }
                TextPulsewidth.setText(PulsewidthNum + " 微妙");
                break;
            case R.id.pulsewidth_down:
                if (PulsewidthNum > 100) {
                    PulsewidthNum -= 50;
                }
                TextPulsewidth.setText(PulsewidthNum + " 微妙");
                break;
            default:
                break;
        }
    }

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

                int constb = 4;
                float[] readBufOne = new float[msg.arg1 / constb];
                float[] envelope = new float[10];
                float[] squarewave = new float[10];
                float envelopeSum = 0f;
                //根据蓝牙传过来的数据进行处理
                switch (msg.what) {
                    case MESSAGE_READ:
                        float[] readBuf = (float[]) msg.obj;
                        int count = msg.arg1;
                        int cnt = 0;
                        for (int i = 0; i < count; i++) {
                            readBuf[i] = readBuf[i];
                            if (i % constb == 0) {
                                readBufOne[i / constb] = readBuf[i]; //出现 数组越界？
                            }
                            if (mData < readBuf[i]) {
                                mData = readBuf[i];
                            }
                            cnt = filter.zeroPass(readBuf[i]);//过零率 数目 用于防止心电触发
                        }
                        filter.count1 = 0;
                        if (frameNum > 0) {
                            maxData = mData;
                            frameNum--;
                        } else {
                            output1.setAmplitude(2 * maxData);
                        }
                        filter.getSum(readBufOne);
                        for (int j = 0; j < 10; j++) {
                            envelope[j] = filter.getEnvelope(filter.c[j]);
                                    envelopeSum += envelope[j];
                        }

                        if (label > 0) {
                            if (counta < 500) {
                                for (int j=0;j<10;j++) {
                                    noicedata[counta] = envelope[j];
                                    counta++;
                                }
                            }

                            label--;
                            if (label == 238) {
                                threshold = filter.getThreshold(noicedata);
                            }
                        } else {
                            for (int a=0;a<10;a++) {
                                if (envelope[a] > threshold) {
                                    squarewave[a] = 1000;
                                } else {
                                    squarewave[a]=0;
                                }
                            }
                        }
                        output1.updateFloats(envelope);
                        output2.updateFloats(squarewave);

                        break;
                    case MESSAGE_STATE_CHANGE:
                        if (D)
                            Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1) {
                            case BluetoothService.STATE_CONNECTED:

                                break;
                            case BluetoothService.STATE_CONNECTING:

                                break;
                            case BluetoothService.STATE_LISTEN:
                            case BluetoothService.STATE_NONE:

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
//
                        break;
                    default:
                        break;
                }
            }
        }
    }

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

}
