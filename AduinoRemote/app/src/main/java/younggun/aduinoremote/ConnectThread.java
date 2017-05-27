package younggun.aduinoremote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import java.io.IOException;

import static younggun.aduinoremote.MainFragment.MY_UUID;

/**
 * Created by LOVE on 2017-05-27.
 */

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    ConnectedThread connectedThread;
    Handler _handler;
    OnMakeListener onMakeListener;

    public ConnectThread(BluetoothDevice device, Handler $handler) {
        Log.e("connectThread","make");
        Log.e("connectThread",device.getAddress());
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        _handler = $handler;

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
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)]
        if(mmSocket.isConnected()) {
            Log.e("connect","sucess");
        } else {
            Log.e("connect","fail");
        }

        ConnectedThread connectedThread = new ConnectedThread(mmSocket, _handler);
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

    void setOnMakeListener(OnMakeListener $onMakeListener) {
        onMakeListener = $onMakeListener;
    }

    public interface OnMakeListener {
        public void onMake(ConnectedThread $connectedThread);
    }
}


