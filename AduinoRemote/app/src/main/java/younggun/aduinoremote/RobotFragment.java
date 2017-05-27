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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by LOVE on 2017-05-25.
 */

public class RobotFragment extends Fragment implements View.OnClickListener,ConnectThread.OnMakeListener{

    ConnectedThread connectedThread = null;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_robot, container, false);
        init();
        startConnect();
        return v;
    }

    void startConnect() {
        Log.e("RobotFragment","setConnect");
        Bundle bundle = getArguments();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice mDevice = mBluetoothAdapter.getRemoteDevice(bundle.getString("ADDRESS"));
        Log.e("address",bundle.getString("ADDRESS"));
        Log.e("getAddress",mDevice.getAddress());
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0) { //메인UI 변경내용
                    //textView.setText((String)msg.getObj);
                } else if(msg.what == 1) { //연결 에러
                    getActivity().getFragmentManager().beginTransaction().remove(RobotFragment.this).commit();
                    getActivity().getFragmentManager().popBackStack();
                    Toast.makeText(getActivity(), "연결 실패, 기기 전원 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else if(msg.what == 2) { //연결 성공
                    Toast.makeText(getActivity(), "연결 성공!", Toast.LENGTH_SHORT).show();
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
        ImageView iv_s = (ImageView)v.findViewById(R.id.iv_a);
        ImageView iv_f = (ImageView)v.findViewById(R.id.iv_a);
        ImageView iv_g = (ImageView)v.findViewById(R.id.iv_a);
        ib_up.setOnClickListener(this);
        ib_down.setOnClickListener(this);
        ib_left.setOnClickListener(this);
        ib_right.setOnClickListener(this);
        iv_a.setOnClickListener(this);
        iv_s.setOnClickListener(this);
        iv_f.setOnClickListener(this);
        iv_g.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.e("RobotFragment","onClick");
        String s;
        switch(view.getId()) {
            case R.id.ib_up:
                s = "up";
                break;
            case R.id.ib_down:
                s = "down";
                break;
            case R.id.ib_left:
                s = "left";
                break;
            case R.id.ib_right:
                s = "right";
                break;
            case R.id.iv_a:
                s = "a";
                break;
            case R.id.iv_s:
                s = "a";
                break;
            case R.id.iv_f:
                s = "a";
                break;
            case R.id.iv_g:
                s = "a";
                break;
            default:
                s = "what";
                break;
        }
        try {
            connectedThread.write(s.getBytes());
        } catch(NullPointerException e) {
            Toast.makeText(getActivity(), "연결중입니다. 5초 후에 다시 시도하세요.", Toast.LENGTH_SHORT).show();
        }
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
}
