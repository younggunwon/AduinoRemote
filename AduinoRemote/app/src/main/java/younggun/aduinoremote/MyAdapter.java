package younggun.aduinoremote;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import younggun.aduinoremote.model.SettingData;

/**
 * Created by younggun on 2017-06-05.
 */

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