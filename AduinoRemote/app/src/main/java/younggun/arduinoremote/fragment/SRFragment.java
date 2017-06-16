package younggun.arduinoremote.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import younggun.aduinoremote.R;
import younggun.arduinoremote.ConnectedThread;
import younggun.arduinoremote.DBHelper;
import younggun.arduinoremote.TouchThread;

/**
 * Created by 219 on 2017-06-16.
 */

public class SRFragment extends RobotFragment {

    TextView tv_inputName[] = new TextView[6];
    TextView tv_inputValue[] = new TextView[6];
    Button bt_output[] = new Button[6];
    Button bt_outputString;
    EditText et_output;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("SRFragment","start");
        DBHelper dbHelper = new DBHelper(getActivity(), "Remote.db", null, 1);
        list = dbHelper.getValue("SR");
        if(v == null) {
            v = inflater.inflate(R.layout.fragment_sr, container, false);
            Log.e("SRFragment","startConnect");
            startConnect(this, true, list);
        } else {
            connectedThread.setFilter(list);
        }
        init();
        return v;
    }

    @Override
    void init() {

        Integer nameResource[] = {R.id.tv_inputName0,R.id.tv_inputName1,R.id.tv_inputName2,R.id.tv_inputName3,R.id.tv_inputName4,R.id.tv_inputName5};
        Integer valueResource[] = {R.id.tv_inputValue0,R.id.tv_inputValue1,R.id.tv_inputValue2,R.id.tv_inputValue3,R.id.tv_inputValue4,R.id.tv_inputValue5};
        Integer buttonResource[] = {R.id.bt_output0,R.id.bt_output1,R.id.bt_output2,R.id.bt_output3,R.id.bt_output4,R.id.bt_output5};
        for(int i = 0; i < 6; i++) {
            tv_inputName[i] = (TextView)v.findViewById(nameResource[i]);
            tv_inputValue[i] = (TextView)v.findViewById(valueResource[i]);
            bt_output[i] = (Button)v.findViewById(buttonResource[i]);
            tv_inputName[i].setOnTouchListener(this);
            tv_inputValue[i].setOnTouchListener(this);
            bt_output[i].setOnTouchListener(this);
            Log.e("setText", list.get(0+i));
            tv_inputName[i].setText(list.get(0+i));
            tv_inputValue[i].setText(list.get(6+i));
            bt_output[i].setText(list.get(12+i));
        }
        bt_outputString = (Button)v.findViewById(R.id.bt_outputString);
        et_output = (EditText)v.findViewById(R.id.et_output);
        bt_outputString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_output.getText().toString().equals("")) {
                    connectedThread.write(et_output.getText().toString().getBytes());
                    et_output.setText("");
                } else {
                    Toast.makeText(getActivity(), "데이터를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String s;

        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _isBtDown = true;
                switch(view.getId()) {
                    case R.id.bt_output0:
                        s = list.get(18);
                        break;
                    case R.id.bt_output1:
                        s = list.get(19);
                        break;
                    case R.id.bt_output2:
                        s = list.get(20);
                        break;
                    case R.id.bt_output3:
                        s = list.get(21);
                        break;
                    case R.id.bt_output4:
                        s = list.get(22);
                        break;
                    case R.id.bt_output5:
                        s = list.get(23);
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

    @Override
    void drawValue(int code, String s) {
        Log.e("drawValue", Integer.toString(code) + "/" + s);
        try {

        } catch (Exception e) {
            return;
        }
        switch (code) {
            case 10:
                ((TextView)v.findViewById(R.id.tv_inputValue0)).setText(s);
                break;
            case 11:
                ((TextView)v.findViewById(R.id.tv_inputValue1)).setText(s);
                break;
            case 12:
                ((TextView)v.findViewById(R.id.tv_inputValue2)).setText(s);
                break;
            case 13:
                ((TextView)v.findViewById(R.id.tv_inputValue3)).setText(s);
                break;
            case 14:
                ((TextView)v.findViewById(R.id.tv_inputValue4)).setText(s);
                break;
            case 15:
                ((TextView)v.findViewById(R.id.tv_inputValue5)).setText(s);
                break;
        }
    }
}
