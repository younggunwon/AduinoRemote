package younggun.aduinoremote;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;

/**
 * Created by LOVE on 2017-05-23.
 */

public class MainFragment extends Fragment {

    View v;
    ArrayAdapter mArrayAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        ListView lv_find = (ListView)v.findViewById(R.id.lv_find);
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        lv_find.setAdapter(mArrayAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //주변 기기 검색
                mArrayAdapter.clear();
                ((MainActivity)getActivity()).setDevice();
            }
        });
        return v;
   }

   void setPairedDevice(Set<BluetoothDevice> pairedDevices) {

       for (BluetoothDevice device : pairedDevices) {
           addDevice(device);
       }
   }

   void addDevice(BluetoothDevice device) {
       mArrayAdapter.add(device.getName() + "    " + device.getAddress());
   }

   void checkEmpty() {
       if(mArrayAdapter.isEmpty()) {
           mArrayAdapter.add("연결 가능한 기기가 없습니다.");
       }
   }
}
