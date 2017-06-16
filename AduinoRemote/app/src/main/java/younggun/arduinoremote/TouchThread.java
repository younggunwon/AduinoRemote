package younggun.arduinoremote;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by 219 on 2017-06-16.
 */

public class TouchThread extends Thread
{
    private String _s;
    private Handler _handler;
    private boolean _isBtDown;
    private ConnectedThread _connectedThread;

    public TouchThread(String $s, Handler $handler, ConnectedThread $connectedThread) {
        _s = $s;
        _handler = $handler;
        _connectedThread = $connectedThread;
    }

    @Override
    public void run()
    {
        Log.e("touchEvent","start");
        super.run();

        while (_isBtDown)
        {
            try {
                _connectedThread.write(_s.getBytes());
            } catch(NullPointerException e) {
                Message msg = new Message();
                msg.what = 3;
                _handler.sendMessage(msg);
            }
            try
            {
                Thread.sleep(200);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setIsBtn(boolean $isBtDown) {
        _isBtDown = $isBtDown;
    }
}
