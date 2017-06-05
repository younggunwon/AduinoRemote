package younggun.aduinoremote.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import younggun.aduinoremote.ConnectThread;
import younggun.aduinoremote.ConnectedThread;
import younggun.aduinoremote.DBHelper;
import younggun.aduinoremote.R;

/**
 * Created by LOVE on 2017-05-25.
 */

public class RobotFragment extends Fragment implements ConnectThread.OnMakeListener,View.OnTouchListener{

    ConnectedThread connectedThread = null;

    boolean _isBtDown;
    Handler handler;
    ArrayList<String> list;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_robot, container, false);
        if(list == null) {
            init(v);
            startConnect(this, false);
            DBHelper dbHelper = new DBHelper(getActivity(), "Remote.db", null, 1);
            list = dbHelper.getValue("ROBOT");
        }
        return v;
    }

    void startConnect(final Fragment f, boolean useRead) {
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
                        getActivity().getFragmentManager().beginTransaction().remove(f).commit();
                        getActivity().getFragmentManager().popBackStack();
                        Toast.makeText(getActivity(), "연결 실패, 기기 전원 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (msg.what == 2) { //연결 성공
                        Toast.makeText(getActivity(), "연결 성공!", Toast.LENGTH_SHORT).show();
                    } else if (msg.what == 3) { //연결 에러
                        Toast.makeText(getActivity(), "연결중입니다. 잠시 후에 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        drawValue(msg.what, (String)msg.obj);
                    }
                }
            }
        };

        mBluetoothAdapter.cancelDiscovery();
        ConnectThread ct = new ConnectThread(mDevice, handler, useRead);
        ct.setOnMakeListener(this);
        ct.start();
    }

    void drawValue(int code, String s) {
        switch (code) {
            case 10:
                ((TextView)v.findViewById(R.id.tv_speed)).setText(s + "km/h");
                break;
            case 11:
                ((TextView)v.findViewById(R.id.tv_temper)).setText(s + "℃");
                break;
            case 12:
                ((TextView)v.findViewById(R.id.tv_humi)).setText(s + "%");
                break;
            case 13:
                ((TextView)v.findViewById(R.id.tv_left)).setText(s + "cm");
                break;
            case 14:
                ((TextView)v.findViewById(R.id.tv_right)).setText(s + "cm");
                break;
        }
    }

    void init(View v) {
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
                        s = list.get(0);
                        break;
                    case R.id.ib_down:
                        s = list.get(1);
                        break;
                    case R.id.ib_left:
                        s = list.get(2);
                        break;
                    case R.id.ib_right:
                        s = list.get(3);
                        break;
                    case R.id.iv_a:
                        s = list.get(4);
                        break;
                    case R.id.iv_s:
                        s = list.get(5);
                        break;
                    case R.id.iv_f:
                        s = list.get(6);
                        break;
                    case R.id.iv_g:
                        s = list.get(7);
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
        return true;
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
