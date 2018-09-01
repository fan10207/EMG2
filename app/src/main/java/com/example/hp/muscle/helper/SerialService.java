package com.example.hp.muscle.helper;


import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import com.example.hp.muscle.helper.ChartDemoFragment;
import com.example.hp.muscle.helper.bluetooth.AppFragment;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import android_serialport_api.SerialPort;

/**
 * Created by hp on 2016/12/16.
 */

public class SerialService {
    private String TAG = "SerialService";
    private final android.os.Handler mHandler;

    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    //第二个串口
    protected SerialPort mSerialPort2;
    protected OutputStream mOutputStream2;
    private InputStream mInputStream2;

    private ReadThread mReadThread;

    protected int Flag = 1;
    private ArrayBlockingQueue<Byte> queue;
    public static float voltage = 0;


    public SerialService(android.os.Handler handler) {
        mSerialPort = null;
        mOutputStream = null;
        mInputStream = null;
        mSerialPort2 = null;
        mOutputStream2 = null;
        mInputStream2 = null;
        queue = new ArrayBlockingQueue<Byte>(1024);
        mHandler = handler;
    }

    public class ReadThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ReadThread(InputStream In, OutputStream Out) {
            mmInStream = In;
            mmOutStream = Out;
        }

        @Override
        public void run() {
            int size;
            byte[] buffer = new byte[1024];
            new ProcessThread().start();
            while (true) {
                try {
                    size = mInputStream.read(buffer);

                    for (int i = 0; i < size; i++) {
                        try {
                            queue.put(buffer[i]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

        }
    }


    class ProcessThread extends Thread {

        public void run() {
            try {
                while (!Thread.interrupted()) {

                    Log.d(TAG, "queue1:" + queue);
                    //帧头header1和header2用来与stm32进行握手协议，header3代表数据帧的长度
                    int header1 = (queue.take()) & 0x000000ff;

                    while (header1 != 0x00000068) {
                        header1 = (queue.take()) & 0x000000ff;
                    }
                    Log.d(TAG, "header1: " + header1);
                    int header2 = queue.take();
                    Log.d(TAG, "header2: " + header2);
                    int header3 = ((queue.take()) & (0x000000ff)) * 4;//length of data
                    Log.d(TAG, "header3: " + header3);
                    int header4 = (queue.take()) & (0x000000ff);
                    Log.d(TAG, "header4: " + header4);//code
                    int signal;
                    float[] data = new float[(header3) / 2];
                    int code;
                    short a = 0, b = 0;
                    short c = 0;
                    for (int i = 0; i < data.length ; i++) {

                        //这里要进行位运算把byte转换成int，直接把byte赋值给int会进行符号位扩展
                        a = (short) ((short) queue.take() << 8);  //高八位
                        b = (short) ((queue.take()) & 0x00FF);   //取低八位
                        c = (short) (a | b);
                        voltage = (float) c * ChartDemoFragment.AD;
                        data[i] = voltage;
                    }
                    Log.d(TAG, "c_value" + c);
                    Log.d(TAG, "voltage:" + voltage);
                    Log.d(TAG, "b_value" + b);
                    Log.d(TAG, "a " + a);
                    signal = (queue.take()) & 0x000000ff;
                    code = (queue.take()) & 0x000000ff;
                    Log.d(TAG, "queue2:" + queue);
                    Log.d(TAG, "code:" + code);
                    if (code == 0xff) {
                        Log.e(TAG, "aaa start" );

                        // Send the obtained bytes to the UI Activity
                        mHandler.obtainMessage(AppFragment.MESSAGE_READ, data.length, -1, data).sendToTarget();
                        mHandler.obtainMessage(AppFragment.MESSAGE_SIGNAL, -1, -1, signal).sendToTarget();
                    }
                    Log.d(TAG, "data of all" + header1 + header2 + header3 + header4 + c + signal + code);

                }
            } catch (InterruptedException e) {

            }
        }
    }


    public void getSerialPort(String path, int baudrate) throws SecurityException, IOException {
        if ((path.length() == 0) || (baudrate == -1)) {
            throw new InvalidParameterException();
        }

		/* Open the serial port */
        mSerialPort = new SerialPort(new File(path), baudrate, 0);
        mInputStream = mSerialPort.getInputStream();
        mOutputStream = mSerialPort.getOutputStream();

    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    public void startSerial() {
        mReadThread = new ReadThread(mInputStream, mOutputStream);
        mReadThread.start();

    }


}
