package younggun.arduinoremote;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by LOVE on 2017-05-27.
 */

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler _handler;
    private boolean useRead;
    private ArrayList<String> filter;

    public ConnectedThread(BluetoothSocket socket, Handler $hanler, boolean $useRead) {
        Log.e("connectedThread", "make");
        mmSocket = socket;
        _handler = $hanler;
        useRead = $useRead;
        if (mmSocket.isConnected()) {
            Log.e("connected", "sucess");
        } else {
            Log.e("connected", "fail");
        }
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        Message msg = new Message();
        msg.what = 2;
        _handler.sendMessage(msg);
    }

    public ConnectedThread(BluetoothSocket socket, Handler $hanler, boolean $useRead, ArrayList<String> $filter) {
        Log.e("connectedThread", "make");
        mmSocket = socket;
        _handler = $hanler;
        useRead = $useRead;
        filter = $filter;
        if (mmSocket.isConnected()) {
            Log.e("connected", "sucess");
        } else {
            Log.e("connected", "fail");
        }
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        Message msg = new Message();
        msg.what = 2;
        _handler.sendMessage(msg);
    }

    public void setFilter(ArrayList<String> $list) {
        filter = $list;
    }

    public void run() {
        Log.e("connectedThread", "start");
        while (useRead) {
            try {
                if(mmInStream.available() > 0) {
                    String s = "";
                    String sv = "";
                    sleep(100);
                    while(mmInStream.available() > 0) {
                        char c = (char) mmInStream.read();
                        s += c;
                    }
                    while(mmInStream.available() == 0) { };
                    Message msg = _handler.obtainMessage();
                    if (s.equals(filter.get(6))) {
                        msg.what = 10;
                    } else if (s.equals(filter.get(7))) {
                        msg.what = 11;
                    } else if (s.equals(filter.get(8))) {
                        msg.what = 12;
                    } else if (s.equals(filter.get(9))) {
                        msg.what = 13;
                    } else if (s.equals(filter.get(10))) {
                        msg.what = 14;
                    } else if (s.equals(filter.get(11))) {
                        msg.what = 15;
                    } else {
                        msg.what = 100;
                        sv = "";
                    }
                    if(msg.what != 100) {
                        sleep(50);
                        while(mmInStream.available() > 0) {
                            char c = (char) mmInStream.read();
                            sv += c;
                        }
                        Log.e("take", s + "/" + sv);
                        msg.obj = sv;
                        _handler.sendMessage(msg);
                    } else {
                        Log.e("fail", s);
                    }
                }
            } catch (IOException e) {
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        Log.e("write", "write");
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}