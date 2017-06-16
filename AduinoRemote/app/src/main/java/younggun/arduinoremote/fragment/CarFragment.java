package younggun.arduinoremote.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import younggun.arduinoremote.DBHelper;
import younggun.aduinoremote.R;
import younggun.arduinoremote.TouchThread;

/**
 * Created by 219 on 2017-06-05.
 */

public class CarFragment extends RobotFragment {

    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("CarFragment","start");
        v = inflater.inflate(R.layout.fragment_car, container, false);
        if(list == null) {
            init();
            Log.e("carFragment","startConnect");
            startConnect(this, false);
            DBHelper dbHelper = new DBHelper(getActivity(), "Remote.db", null, 1);
            list = dbHelper.getValue("CAR");
        }
        return v;
    }

    @Override
    void init() {
        ImageView iv_up = (ImageView)v.findViewById(R.id.iv_up);
        ImageView iv_down = (ImageView)v.findViewById(R.id.iv_down);
        ImageView iv_left = (ImageView)v.findViewById(R.id.iv_left);
        ImageView iv_right = (ImageView)v.findViewById(R.id.iv_right);
        iv_up.setOnTouchListener(this);
        iv_down.setOnTouchListener(this);
        iv_left.setOnTouchListener(this);
        iv_right.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String s;

        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _isBtDown = true;
                switch(view.getId()) {
                    case R.id.iv_up:
                        s = list.get(0);
                        break;
                    case R.id.iv_down:
                        s = list.get(1);
                        break;
                    case R.id.iv_left:
                        s = list.get(2);
                        break;
                    case R.id.iv_right:
                        s = list.get(3);
                        break;
                    default:
                        s = "what";
                }
                touchThread = new TouchThread(s, handler, connectedThread);
                touchThread.setIsBtn(_isBtDown);
                touchThread.start();
                break;
            case MotionEvent.ACTION_UP:
                _isBtDown = false;
                touchThread.setIsBtn(_isBtDown);
                break;
        }
        return true;
    }
}
