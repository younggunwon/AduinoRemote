package younggun.aduinoremote;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by LOVE on 2017-05-25.
 */

public class RobotFragment extends Fragment implements ConnectThread.OnMakeListener,View.OnTouchListener{

    ConnectedThread connectedThread = null;
    View v;
    String outputData[] = new String[8];
    boolean _isBtDown;
    Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(v == null) {
            v = inflater.inflate(R.layout.fragment_robot, container, false);
            init();
            startConnect();
        }
        DBHelper dbHelper = new DBHelper(getActivity(), "Remote.db", null, 1);
        ArrayList<String> list = dbHelper.getValue("ROBOT");
        for(int i = 0; i < list.size(); i++) {
            outputData[i] = list.get(i);
        }
        return v;
    }

    void startConnect() {
        Log.e("RobotFragment","setConnect");
        Bundle bundle = getArguments();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice mDevice = mBluetoothAdapter.getRemoteDevice(bundle.getString("ADDRESS"));
        Log.e("address",bundle.getString("ADDRESS"));
        Log.e("getAddress",mDevice.getAddress());
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(!(getActivity() == null)) {
                    if (msg.what == 0) { //메인UI 변경내용
                        //textView.setText((String)msg.getObj);
                    } else if (msg.what == 1) { //연결 실패
                        getActivity().getFragmentManager().beginTransaction().remove(RobotFragment.this).commit();
                        getActivity().getFragmentManager().popBackStack();
                        Toast.makeText(getActivity(), "연결 실패, 기기 전원 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (msg.what == 2) { //연결 성공
                        Toast.makeText(getActivity(), "연결 성공!", Toast.LENGTH_SHORT).show();
                    } else if (msg.what == 3) { //연결 에러
                        Toast.makeText(getActivity(), "연결중입니다. 잠시 후에 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        mBluetoothAdapter.cancelDiscovery();
        ConnectThread ct = new ConnectThread(mDevice, handler);
        ct.setOnMakeListener(this);
        ct.start();
    }

    void init() {
        Log.e("RobotFragment","init");
        ImageButton ib_up = (ImageButton)v.findViewById(R.id.ib_up);
        ImageButton ib_down = (ImageButton)v.findViewById(R.id.ib_down);
        ImageButton ib_left = (ImageButton)v.findViewById(R.id.ib_left);
        ImageButton ib_right = (ImageButton)v.findViewById(R.id.ib_right);
        ImageView iv_a = (ImageView)v.findViewById(R.id.iv_a);
        ImageView iv_s = (ImageView)v.findViewById(R.id.iv_s);
        ImageView iv_f = (ImageView)v.findViewById(R.id.iv_f);
        ImageView iv_g = (ImageView)v.findViewById(R.id.iv_g);
        ib_up.setOnTouchListener(this);
        ib_down.setOnTouchListener(this);
        ib_left.setOnTouchListener(this);
        ib_right.setOnTouchListener(this);
        iv_a.setOnTouchListener(this);
        iv_s.setOnTouchListener(this);
        iv_f.setOnTouchListener(this);
        iv_g.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String s;
        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _isBtDown = true;
                switch(view.getId()) {
                    case R.id.ib_up:
                        s = outputData[0];
                        break;
                    case R.id.ib_down:
                        s = outputData[1];
                        break;
                    case R.id.ib_left:
                        s = outputData[2];
                        break;
                    case R.id.ib_right:
                        s = outputData[3];
                        break;
                    case R.id.iv_a:
                        s = outputData[4];
                        break;
                    case R.id.iv_s:
                        s = outputData[5];
                        break;
                    case R.id.iv_f:
                        s = outputData[6];
                        break;
                    case R.id.iv_g:
                        s = outputData[7];
                        break;
                    default:
                        s = "what";
                        break;
                }
                TouchThread touchThread = new TouchThread(s, handler);
                touchThread.start();
                break;
            case MotionEvent.ACTION_UP:
                _isBtDown = false;
                break;
        }
        return false;
    }

    @Override
    public void onMake(ConnectedThread $connectedThread) {
        connectedThread = $connectedThread;
    }

    @Override
    public void onDestroy() {
        if(connectedThread != null) { connectedThread.cancel();}
        super.onDestroy();
    }

    private class TouchThread extends Thread
    {
        String _s;
        Handler _handler;

        TouchThread(String $s, Handler $handler) {
            _s = $s;
            _handler = $handler;
        }

        @Override
        public void run()
        {
            super.run();

            while (_isBtDown)
            {
                try {
                    connectedThread.write(_s.getBytes());
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
    }
}
