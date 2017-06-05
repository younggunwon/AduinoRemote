package younggun.aduinoremote.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import younggun.aduinoremote.DBHelper;
import younggun.aduinoremote.MyAdapter;
import younggun.aduinoremote.R;
import younggun.aduinoremote.model.SettingData;

public class RobotSettingFragment extends Fragment {

    DBHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_robotsetting, container, false);
        dbHelper = new DBHelper(getActivity(), "Remote.db", null, 1);
        ArrayList<String> dbList = dbHelper.getValue("ROBOT");
        ArrayList<SettingData> settingList = new ArrayList<SettingData>();
        final MyAdapter myAdapter = new MyAdapter(getActivity(), settingList);
        ListView lv_setting_robot = (ListView)v.findViewById(R.id.lv_setting_robot);
        lv_setting_robot.setAdapter(myAdapter);

        settingList.add(new SettingData(R.drawable.up, dbList.get(0)));
        settingList.add(new SettingData(R.drawable.down, dbList.get(1)));
        settingList.add(new SettingData(R.drawable.left, dbList.get(2)));
        settingList.add(new SettingData(R.drawable.right, dbList.get(3)));
        settingList.add(new SettingData(R.drawable.a, dbList.get(4)));
        settingList.add(new SettingData(R.drawable.s, dbList.get(5)));
        settingList.add(new SettingData(R.drawable.f, dbList.get(6)));
        settingList.add(new SettingData(R.drawable.g, dbList.get(7)));
        myAdapter.notifyDataSetChanged();



        v.findViewById(R.id.bt_save_robot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] name = {"outputUp","outputDown","outputLeft","outputRight","outputa","outputs","outputf","outputg"};
                ArrayList<SettingData> List = myAdapter.get_settingList();
                for(int i = 0; i < 8; i++) {
                    dbHelper.update("ROBOT", name[i], List.get(i).getOutput());
                    Log.e("String", List.get(i).getOutput());
                }
                getActivity().getFragmentManager().beginTransaction().remove(RobotSettingFragment.this).commit();
                getActivity().getFragmentManager().popBackStack();
            }
        });
        v.findViewById(R.id.bt_cancel_robot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().remove(RobotSettingFragment.this).commit();
                getActivity().getFragmentManager().popBackStack();
            }
        });

        return v;
    }
}
