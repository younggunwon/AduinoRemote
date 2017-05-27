package younggun.aduinoremote;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class RobotSettingFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_robotsetting, container, false);
        ArrayList<SettingRobot> settingList = new ArrayList<SettingRobot>();
        MyAdapter myAdapter = new MyAdapter(getActivity(), settingList);
        ListView lv_setting_robot = (ListView)v.findViewById(R.id.lv_setting_robot);
        lv_setting_robot.setAdapter(myAdapter);

        settingList.add(new SettingRobot(R.drawable.a, "up"));
        settingList.add(new SettingRobot(R.drawable.a, "up"));
        settingList.add(new SettingRobot(R.drawable.a, "up"));
        myAdapter.notifyDataSetChanged();

        return v;
    }

    class MyAdapter extends BaseAdapter {

        ArrayList<SettingRobot> _settingList = new ArrayList<SettingRobot>();
        ArrayList<EditText> textList = new ArrayList<EditText>();
        Context _context;


        public MyAdapter(Context $context, ArrayList<SettingRobot> $settingList) {
            _context = $context;
            _settingList = $settingList;
        }

        @Override
        public int getCount() {
            return _settingList.size();
        }

        @Override
        public Object getItem(int i) {
            return _settingList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View returnView;
            if(view != null) {
                returnView = view;
            } else {
                LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                returnView = (LinearLayout)layoutInflater.inflate(R.layout.item_robot, null);
            }
            ((ImageView)returnView.findViewById(R.id.iv_button)).setImageResource(_settingList.get(position).getImg());
            textList.add((EditText)returnView.findViewById(R.id.et_input));
            return returnView;
        }

    }

    class SettingRobot {
        private int img;
        private String input;

        SettingRobot(int $img, String $input) {
            img = $img;
            input = $input;
        }

        public int getImg() {
            return img;
        }

        public String getInput() {
            return input;
        }
    }
}
