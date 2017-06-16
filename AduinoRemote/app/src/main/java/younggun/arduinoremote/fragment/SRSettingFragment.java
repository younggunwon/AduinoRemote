package younggun.arduinoremote.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringDef;
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

import younggun.aduinoremote.R;
import younggun.arduinoremote.DBHelper;
import younggun.arduinoremote.model.SettingData;

/**
 * Created by 219 on 2017-06-16.
 */

public class SRSettingFragment extends Fragment {

    DBHelper dbHelper;
    ArrayList<StringData> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_srsetting, container, false);
        dbHelper = new DBHelper(getActivity(), "Remote.db", null, 1);
        ArrayList<String> dbList = dbHelper.getValue("SR");
        dataList = new ArrayList<StringData>();
        for(int i = 0; i < 24; i++) {
            dataList.add(new StringData(dbList.get(i)));
        }
        final MyAdapter myAdapter = new MyAdapter(getActivity(), dataList);
        ListView lv_setting_robot = (ListView)v.findViewById(R.id.lv_setting_sr);
        lv_setting_robot.setAdapter(myAdapter);



        v.findViewById(R.id.bt_save_sr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] name = {"inputName0","inputName1","inputName2","inputName3","inputName4","inputName5",
                        "inputValue0","inputValue1","inputValue2","inputValue3","inputValue4","inputValue5",
                        "buttonName0","buttonName1","buttonName2","buttonName3","buttonName4","buttonName5",
                        "buttonValue0","buttonValue1","buttonValue2","buttonValue3","buttonValue4","buttonValue5"};
                for(int i = 0; i < name.length; i++) {
                    dbHelper.update("SR", name[i], dataList.get(i).getData());
                    Log.e("String", dataList.get(i).getData());
                }
                getActivity().getFragmentManager().beginTransaction().remove(SRSettingFragment.this).commit();
                getActivity().getFragmentManager().popBackStack();
            }
        });
        v.findViewById(R.id.bt_cancel_sr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().remove(SRSettingFragment.this).commit();
                getActivity().getFragmentManager().popBackStack();
            }
        });

        return v;
    }

    public class MyAdapter extends BaseAdapter {

        ArrayList<StringData> _settingList = new ArrayList<StringData>();
        Context _context;


        public MyAdapter(Context $context, ArrayList<StringData> $settingList) {
            _context = $context;
            _settingList = $settingList;
        }

        @Override
        public int getCount() {
            return _settingList.size()/2;
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
                returnView = layoutInflater.inflate(R.layout.item_sr, null);
            }
            EditText et_input0 = (EditText)returnView.findViewById(R.id.et_input0);
            EditText et_input1 = (EditText)returnView.findViewById(R.id.et_input1);
            int a = 0;
            if(position > 5) {
                a = 6;
            }
            et_input0.setText(_settingList.get(position+a).getData());
            et_input0.addTextChangedListener(new myWatcher(et_input0,_settingList,position));
            et_input1.setText(_settingList.get(position+6+a).getData());
            et_input1.addTextChangedListener(new myWatcher(et_input1,_settingList,position));

            return returnView;
        }

        public ArrayList<StringData> get_settingList() {
            return _settingList;
        }
    }

    class myWatcher implements TextWatcher {

        EditText _et;
        ArrayList<StringData> _list;
        int _position;

        myWatcher(EditText $et, ArrayList<StringData> $list, int $position) {
            _et = $et;
            _list = $list;
            _position = $position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(_et.getId() == R.id.et_input0) {
                _list.get(_position*2).setData(_et.getText().toString());
            } else if(_et.getId() == R.id.et_input1) {
                _list.get(_position*2+1).setData(_et.getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    class StringData {
        private String data;

        StringData(String s) {
            data = s;
        }

        String getData() {
            return data;
        }

        void setData(String s) {
            data = s;
        }
    }
}
