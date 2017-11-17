package com.example.auser.bluetoothadaptertest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.support.constraint.R.id.parent;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mAdapter;
    ArrayList<String> listName;
    ListView listView;
    Button scan_btn,discoverable_btn,edit_btn;
    Context context;
    String TAG="";
    private final static int REQUEST_ENABLE_BT = 2;
    Set<BluetoothDevice> device;
    boolean receiverFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
        listName = new ArrayList<String>();
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);
//        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        registerReceiver(mReceiver, filter);
        mAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mAdapter == null)
        {
            Toast.makeText(context, "There is no bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!mAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            device = mAdapter.getBondedDevices();
            if(device.size()> 0) {
                for (BluetoothDevice bluetoothDevice : device){
                    listName.add("Paired : " + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                }
                listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listName));
            }
        }
//        listName = new ArrayList<>();
//        adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, listName);
//        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(requestCode == RESULT_CANCELED){
                Toast.makeText(context, "Enabled BT failed", Toast.LENGTH_SHORT).show();
                finish();
            } else  if (requestCode == RESULT_OK) {
                device = mAdapter.getBondedDevices();
                if(device.size()> 0) {
                    for (BluetoothDevice bluetoothDevice : device){
                        listName.add("Paired :  " + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                    }
                    listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listName));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void findViews() {
        context = this;
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(null);
        scan_btn = (Button) findViewById(R.id.scan);
        discoverable_btn = (Button) findViewById(R.id.discoverable);
        edit_btn = (Button) findViewById(R.id.edit);
    }

    void setListeners() {
        scan_btn.setOnClickListener(new listener());
        discoverable_btn.setOnClickListener(new listener());
        edit_btn.setOnClickListener(new listener());
        listView.setOnItemClickListener (new MyItemClick());
    }

    private class listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.scan:
                    Log.d(TAG,"Search Click");
                    mAdapter.startDiscovery();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver, filter);
                    receiverFlag = true;
                    Toast.makeText(context, "Begin to scan", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.discoverable: //press Discoverable button to make BT module discoverable for 150sec
                    Log.d(TAG,"Discoverable Click");
                    Toast.makeText(context, "Begin to discoverable", Toast.LENGTH_SHORT).show();
                    Intent disIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    disIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,150);
                    startActivity(disIntent);
                    break;
                case R.id.edit:
                    Log.d(TAG,"Edit Click");
                    Intent newIntent = new Intent(context, Main2Activity.class);
                    startActivity(newIntent);
                    Toast.makeText(context, "Begin to edit", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if( action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listName.add("Found :   " + device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,listName));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cancelDiscovery();
        if(receiver != null) {
            if (receiverFlag) {
                unregisterReceiver(receiver);
            }
        }
    }

    private class MyItemClick implements android.widget.AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mAdapter.cancelDiscovery();
            String remoteDeviceName = (String) parent.getItemAtPosition(position);
            Intent newIntent = new Intent(context, Main2Activity.class);
            newIntent.putExtra("remoteDevice", remoteDeviceName);
            startActivity(newIntent);
        }
    }

    //    BroadcastReceiver mReceiver = new BroadcastReceiver() {
//
//
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            //找到設備
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//
//                BluetoothDevice device = intent
//                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    Log.v(TAG, "find device:" + device.getName()
//                            + device.getAddress());
//                }
//
//            }//執行更新清單的代碼
//
//        }
//
//    };

}
