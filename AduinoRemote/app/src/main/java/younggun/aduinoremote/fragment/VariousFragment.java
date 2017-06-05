package younggun.aduinoremote.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import younggun.aduinoremote.DBHelper;
import younggun.aduinoremote.R;

/**
 * Created by LOVE on 2017-06-05.
 */

public class VariousFragment extends RobotFragment implements View.OnClickListener{

    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("Various","start");
        v = inflater.inflate(R.layout.fragment_various, container, false);
        if(list == null) {
            init(v);
            Log.e("VariousFragment","startConnect");
            startConnect(this, true);
            DBHelper dbHelper = new DBHelper(getActivity(), "Remote.db", null, 1);
            list = dbHelper.getValue("VARIOUS");
        }
        return v;
    }

    @Override
    void init(View v) {
        ((Switch)v.findViewById(R.id.switch_use)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_use:
                String s;
                if(((Switch)view).isChecked()) {
                    s = "use";
                } else {
                    s = "nouse";
                }
                connectedThread.write(s.getBytes());
                break;
        }
    }

    /*
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
                break;
            case MotionEvent.ACTION_UP:
                _isBtDown = false;
                break;
        }
        return true;
    }
    */
}
