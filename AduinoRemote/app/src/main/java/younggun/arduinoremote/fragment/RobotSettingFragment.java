package younggun.arduinoremote.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import younggun.arduinoremote.DBHelper;
import younggun.aduinoremote.R;
import younggun.arduinoremote.model.SettingData;

public class RobotSettingFragment extends Fragment {

    DBHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_robotsetting, container, false);
        dbHelper = new DBHelper(getActivity(), "Remote.db", null, 1);
        ArrayList<String> dbList = dbHelper.getValue("ROBOT");
        ArrayList<SettingData> settingList = new ArrayList<SettingData>();
        settingList.add(new SettingData(R.drawable.up, dbList.get(0)));
        settingList.add(new SettingData(R.drawable.down, dbList.get(1)));
        settingList.add(new SettingData(R.drawable.left, dbList.get(2)));
        settingList.add(new SettingData(R.drawable.right, dbList.get(3)));
        settingList.add(new SettingData(R.drawable.aa, dbList.get(4)));
        settingList.add(new SettingData(R.drawable.ss, dbList.get(5)));
        settingList.add(new SettingData(R.drawable.ff, dbList.get(6)));
        settingList.add(new SettingData(R.drawable.gg, dbList.get(7)));
        final MyAdapter myAdapter = new MyAdapter(getActivity(), settingList);
        ListView lv_setting_robot = (ListView)v.findViewById(R.id.lv_setting_robot);
        lv_setting_robot.setAdapter(myAdapter);



        v.findViewById(R.id.bt_save_robot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] name = {"outputUp","outputDown","outputLeft","outputRight","outputa","outputs","outputf","outputg"};
                ArrayList<SettingData> List = myAdapter.get_settingList();
                for(int i = 0; i < name.length; i++) {
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

    public class MyAdapter extends BaseAdapter {

        ArrayList<SettingData> _settingList = new ArrayList<SettingData>();
        Context _context;


        public MyAdapter(Context $context, ArrayList<SettingData> $settingList) {
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
        public View getView(final int position, View view, ViewGroup viewGroup) {
            View returnView;
            if(view != null) {
                returnView = view;
            } else {
                LayoutInflater layoutInflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                returnView = layoutInflater.inflate(R.layout.item_robot, null);
            }
            ((ImageView)returnView.findViewById(R.id.iv_button)).setImageResource(_settingList.get(position).getImg());
            EditText et_output = (EditText)returnView.findViewById(R.id.et_input);
            et_output.setText(_settingList.get(position).getOutput());
            et_output.addTextChangedListener(new myWatcher(et_output,_settingList,position));

            return returnView;
        }

        public ArrayList<SettingData> get_settingList() {
            return _settingList;
        }
    }

    class myWatcher implements TextWatcher {

        EditText _et;
        ArrayList<SettingData> _list;
        int _position;

        myWatcher(EditText $et, ArrayList<SettingData> $list, int $position) {
            _et = $et;
            _list = $list;
            _position = $position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            _list.get(_position).setOutput(_et.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
