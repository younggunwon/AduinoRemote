package younggun.aduinoremote;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by LOVE on 2017-05-27.
 */

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    Handler _handler;
    boolean useRead;

    public ConnectedThread(BluetoothSocket socket,Handler $hanler ,boolean $useRead) {
        Log.e("connectedThread","make");
        mmSocket = socket;
        _handler = $hanler;
        useRead = $useRead;
        if(mmSocket.isConnected()) {
            Log.e("connected","sucess");
        } else {
            Log.e("connected","fail");
        }
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        Message msg = new Message();
        msg.what = 2;
        _handler.sendMessage(msg);
    }



    public void run() {
        Log.e("connectedThread","start");
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (useRead) {
            try {
                String s = "";
                while(mmInStream.available() > 0) {
                    char c = (char)mmInStream.read();
                    s += c;
                    try {
                        sleep(5);
                    } catch (Exception e) {

                    }
                }
                Message msg = _handler.obtainMessage();
                if(s.equals("speed")) {
                    while(mmInStream.available() > 0) {
                        char c = (char)mmInStream.read();
                        s += c;
                        try {
                            sleep(5);
                        } catch (Exception e) {

                        }
                    }
                    msg.what = 10;
                } else if(s.equals("temper")) {
                    while(mmInStream.available() > 0) {
                        char c = (char)mmInStream.read();
                        s += c;
                        try {
                            sleep(5);
                        } catch (Exception e) {

                        }
                    }
                    msg.what = 11;
                } else if(s.equals("humi")) {
                    while(mmInStream.available() > 0) {
                        char c = (char)mmInStream.read();
                        s += c;
                        try {
                            sleep(5);
                        } catch (Exception e) {

                        }
                    }
                    msg.what = 12;
                } else if(s.equals("left")) {
                    while(mmInStream.available() > 0) {
                        char c = (char)mmInStream.read();
                        s += c;
                        try {
                            sleep(5);
                        } catch (Exception e) {

                        }
                    }
                    msg.what = 13;
                } else if(s.equals("right")) {
                    while(mmInStream.available() > 0) {
                        char c = (char)mmInStream.read();
                        s += c;
                        try {
                            sleep(5);
                        } catch (Exception e) {

                        }
                    }
                    msg.what = 14;
                } else {
                    msg.what = 100;
                }
                Log.e("take",s);
                msg.obj = s;
                _handler.sendMessage(msg);
                write(s.getBytes());
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        Log.e("write","write");
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}