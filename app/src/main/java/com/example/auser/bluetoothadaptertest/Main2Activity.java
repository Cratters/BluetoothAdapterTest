package com.example.auser.bluetoothadaptertest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Handler;

public class Main2Activity extends AppCompatActivity {

    TextView textView;
    Button clear,send;
    EditText editText;
    Context context;
    BluetoothAdapter btAdapter;
    String remoteDeviceInfo;
    String remoteMacAddress;
    private final static String TAG = "EDIT_BT";
    BluetoothDevice device;
    BluetoothChatService mChatSetvice;
    StringBuilder mOutStringBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViews();
        setListeners();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent = getIntent();
        remoteDeviceInfo = intent.getStringExtra("remoteDvice");
        //mChatSetvice = new BluetoothChatService(context,mHandler);
        mOutStringBuffer = new StringBuilder("");


        if(remoteDeviceInfo != null) {
            Log.d(TAG,"Client mode");
            String deviceMsg = remoteDeviceInfo.substring(10);
            textView.append("Connecting to remote BT device: \n" + deviceMsg + "\n\n");
            remoteMacAddress = remoteDeviceInfo.substring(remoteDeviceInfo.length()-17);
            Log.d(TAG,remoteMacAddress);
            device = btAdapter.getRemoteDevice(remoteMacAddress);
            mChatSetvice.connect(device, true);
        }

    }

    void findViews() {
        context = this;
        textView = (TextView) findViewById(R.id.textView);
        clear = (Button) findViewById(R.id.clear);
        send = (Button) findViewById(R.id.send);
        editText = (EditText) findViewById(R.id.editText);
    }

    void setListeners() {
        clear.setOnClickListener(new listener());
        send.setOnClickListener(new listener());
        editText.setOnEditorActionListener(editTextListener);
    }

    private TextView.OnEditorActionListener editTextListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                String msg = textView.getText().toString();
                textView.append(">>" + msg + "\n");
                sendMsgToBT(msg);
            }
            return true;
        }
    };

    private class listener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.clear:
                    textView.setText("");
                    Toast.makeText(context, "Clean display", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.send:
                    textView.append(">>"+ editText.getText() +"\n");
                    String msg = editText.getText().toString();
                    sendMsgToBT(msg);
                    editText.setText("");
                    break;
            }
        }
    }

    private void sendMsgToBT(String msg) {

    }


    // The Handler that gets information back from the BluetoothChatService
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.what) {
//                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    //   dataTextView.append(">>  : " + writeMessage + "\n");   //display on TextView
//                    break;
//                case Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    dataTextView.append("remote : " + readMessage + "\n");   //display on TextView
//
//                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    Toast.makeText(context, "Connected to "
//                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    Toast.makeText(context, msg.getData().getString(Constants.TOAST),
//                            Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
}
