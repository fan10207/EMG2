package com.example.hp.muscle.helper;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.hp.muscle.R;
import com.example.hp.muscle.helper.bluetooth.AppFragment;
import com.example.hp.muscle.helper.bluetooth.DeviceListActivity;
import com.example.hp.muscle.helper.bluetooth.SysApplication;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hp on 2016/7/28.
 */
public class ParameterFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener  {
    private Context context;
    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_CONNECT_DEVICE = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
private ChartDemoFragment CHART_FRAGMENT;

    public static final String TAG="bluetooth";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.e(TAG, "onCreateView: BTcreateview" );
        View view = inflater.inflate(R.layout.parameter, container, false);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return view;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        CHART_FRAGMENT=new ChartDemoFragment();
        sharedPreferences = getActivity().getSharedPreferences("address", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Button bluetooth = (Button) getActivity().findViewById(R.id.bluetooth);
        Button search_bluetooth = (Button) getActivity().findViewById(R.id.search_bluetooth);
        bluetooth.setOnClickListener(this);
        search_bluetooth.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetooth: {
                PopupMenu popup = new PopupMenu(getActivity(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_bluetooth, popup.getMenu());
                popup.setOnMenuItemClickListener(this);
                popup.show();

            }
            break;
            case R.id.search_bluetooth: {


            }
            break;

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {

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
        CHART_FRAGMENT.getmChatService().connect(device);
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_bluetooth:
                //打开蓝牙
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                } else
                    Toast.makeText(context, "bluetooth has already opened", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_network:
                //网络功能控制

                return true;
            case R.id.set_num:
                //设置显示点数

               // setDemoPointNum();
                return true;
            case R.id.menu_discoverable:
                //蓝牙可见性
                ensureDiscorverable();
                return true;
            case R.id.fifty_transmition:
              //  mChatService.startFiftyTransmition();
                return true;
            case R.id.stop_transmition:
              //  mChatService.stopTransmition();
                return true;
            case R.id.sixty_transmition:
              //  mChatService.startSixtyFrequency();
                return true;
            case R.id.flush:
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                int state = adapter.getState();
                if (state == BluetoothAdapter.STATE_ON) {
                    adapter.disable();
                }
             /*   if (mChatService != null) {
                    mChatService.stop();
                }*/


                return true;
            case R.id.menu_exit:
                SysApplication.exit();
                return true;
            default:
                break;
        }
        return false;
    }
    public void ensureDiscorverable() {
        Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

}
