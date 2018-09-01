package com.example.hp.muscle.helper.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.hp.muscle.helper.ChartDemoFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Handler;

/**
 * Created by hp on 2016/7/28.
 */
public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;
    private ArrayBlockingQueue<Byte> queue;
    private static final String NAME = "BluetoothChat";

    private static final UUID MY_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mAdapter;
    private final android.os.Handler mHandler;
    private ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;

    private BluetoothDevice device;
    private int mState;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private byte[] order_data = new byte[7];
    public static float voltage = 0;



    public BluetoothService(Context context, android.os.Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        queue = new ArrayBlockingQueue<Byte>(1024);
        //命令帧初始化
        order_data[0] = 0x68;
        order_data[1] = 0x67;
        order_data[2] = 0x02;
        order_data[3] = 0x01;
        order_data[4] = 0x01;
        //send 0D0A after the data
        order_data[5] = 0x0D;
        order_data[6] = 0x0A;
    }

    private synchronized void setState(int state) {
        if (D)
            Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        // return a message from the global message pool
        mHandler.obtainMessage(AppFragment.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void connect(BluetoothDevice device) {
        if (D)
            Log.d(TAG, "connect to: " + device);
        this.device = device;
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device, final String socketType) {
        if (D)
            Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity


        Message msg = mHandler.obtainMessage(AppFragment.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(AppFragment.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (D)
            Log.e(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }


    public synchronized void startFiftyTransmition() {
        if (mConnectedThread != null) {
            order_data[3] = 0x01;
            order_data[4] = 0x01;
            mConnectedThread.write(order_data);
        }
    }

    public synchronized void stopTransmition() {
        Log.e(TAG, "stopTransmition: stop" );
        if (mConnectedThread != null) {
            order_data[3] = 0x11;
            mConnectedThread.write(order_data);
        }
    }


    public synchronized void startSixtyFrequency() {
        if (mConnectedThread != null) {
            order_data[3] = 0x01;
            order_data[4] = 0x11;
            mConnectedThread.write(order_data);

        }
    }

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(AppFragment.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(AppFragment.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(AppFragment.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(AppFragment.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);


    }


    public static void startConnection() {
        mConnectedThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType
                            + " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType
                        + " socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int count;
            // Keep listening to the InputStream while connected
            new ProcessThread().start();
            while (true) {
                try {
                    count = mmInStream.read(buffer);
                    for (int i = 0; i < count; i++) {
                        try {
                            queue.put(buffer[i]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            Log.e(TAG, "write: byte" );

            try {
                mmOutStream.write(buffer);
                Log.d(TAG, "send message to stm32");
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(AppFragment.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }


        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    //处理数据线程
    class ProcessThread extends Thread {

        public void run() {
            try {
                while (!Thread.interrupted()) {
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
                    Log.d(TAG, "length: " + header3);
                    int code;
                    short a = 0, b = 0;
                    short c = 0;
                    for (int i = 0; i < header3 / 2; i++) {
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
                    Log.d(TAG, "queue:" + queue);
                    Log.d(TAG, "code:" + code);
                    if (code == 0xff) {
                        // Send the obtained bytes to the UI Activity
                        mHandler.obtainMessage(AppFragment.MESSAGE_READ, header3 / 2, -1, data).sendToTarget();
                        mHandler.obtainMessage(AppFragment.MESSAGE_SIGNAL, -1, -1, signal).sendToTarget();
                    }
                    Log.d(TAG, "data of all" + header1 + header2 + header3 + header4 + c + signal + code);

                }
            } catch (InterruptedException e) {

            }
        }
    }

}