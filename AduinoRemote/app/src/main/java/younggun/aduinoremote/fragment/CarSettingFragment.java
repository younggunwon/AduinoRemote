package younggun.aduinoremote.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import younggun.aduinoremote.R;

/**
 * Created by 219 on 2017-06-05.
 */

public class CarSettingFragment extends RobotSettingFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_carsetting, container, false);
        ListView lv_setting_car = (ListView)v.findViewById(R.id.lv_setting_car);
        return v;
    }
}
