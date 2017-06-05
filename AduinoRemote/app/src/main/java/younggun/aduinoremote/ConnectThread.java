package younggun.aduinoremote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;

import static younggun.aduinoremote.fragment.MainFragment.MY_UUID;

/**
 * Created by LOVE on 2017-05-27.
 */

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    ConnectedThread connectedThread;
    Handler _handler;
    OnMakeListener onMakeListener;
    boolean useRead;

    public ConnectThread(BluetoothDevice device, Handler $handler, boolean $useRead) {
        Log.e("connectThread","make");
        Log.e("connectThread",device.getAddress());
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        _handler = $handler;
        useRead = $useRead;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            //tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        Log.e("connectThread","start");
        // Cancel discovery because it will slow down the connection

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            Message msg = new Message();
            msg.what = 1;
            _handler.sendMessage(msg);
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        ConnectedThread connectedThread = new ConnectedThread(mmSocket, _handler, useRead);
        if(_handler == null) {
        } else {
            connectedThread.start();
        }
        if(onMakeListener == null) {} else {
            onMakeListener.onMake(connectedThread);
        }
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

    public void setOnMakeListener(OnMakeListener $onMakeListener) {
        onMakeListener = $onMakeListener;
    }

    public interface OnMakeListener {
        public void onMake(ConnectedThread $connectedThread);
    }
}


