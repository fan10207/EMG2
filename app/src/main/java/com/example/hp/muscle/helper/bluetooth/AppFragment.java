package com.example.hp.muscle.helper.bluetooth;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * Created by hp on 2016/7/30.
 */
public class AppFragment extends Fragment {
    public static final String TAG = "bluetooth";
    // Debugging
    public static final boolean D = true;
    // Debugging
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_TEXT = 6;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final int MESSAGE_SIGNAL = 7;
    // Name of the connected device
    public String mConnectedDeviceName = null;
    protected BluetoothService mChatService = null;
    //TCP/IP connection
 //   public static SocketConnection socketConnection;

    protected static Handler mHandler;
    protected Context context;
    protected static Random rand;
  //  protected static Gson gson;
 //   private MedicLog log;
    private static ExecutorService executor;
    //保存一些设置
    protected static SharedPreferences settings;
    protected String server;
    protected int port;
    //是否已经登录
    public volatile boolean isLogined = false;
    //允许向服务器上传用户数据
    public volatile boolean allowedToSend = true;
    //所有的医疗记录都将发送给server帐号，服务器端将拦截所有发送给server的消息，并
    //为每一个用户创建一个表单保存相应的数据
    protected static final String target = "server";
    //设置页面的选项
    //静态块初始化
    static {
        rand = new Random();
    //    gson = new Gson();
        //executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        settings = context.getSharedPreferences("setting", 0);
        server = settings.getString("serverIp", "202.38.214.241");
        port = Integer.parseInt(settings.getString("port", "9000"));
//        socketConnection = new SocketConnection(server, port);
 //       log = new MedicLog();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onCreateView: BTdestroy" );

        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();

    }
    protected void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public BluetoothService getmChatService() {
        return mChatService;
    }





    /**
     * 返回基本的设置
     * @return
     */
    public static SharedPreferences getSettings(){
        return settings;
    }

}

