package younggun.aduinoremote.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;
import java.util.UUID;

import younggun.aduinoremote.R;

/**
 * Created by LOVE on 2017-05-23.
 */

public class MainFragment extends Fragment implements DialogInterface.OnDismissListener {

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    View v;
    BluetoothAdapter mBluetoothAdapter;
    ListView lv_find;
    ArrayAdapter<String> mArrayAdapter;
    OnRemoteStartListener _startRemoteListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        return v;
   }

   void getPairedDevice() {
       Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
       if (pairedDevices.size() > 0) {
           for (BluetoothDevice device : pairedDevices) {
               mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
           }
       }
   }

   void findDevice() {
       if(mBluetoothAdapter.isDiscovering()) {
           mBluetoothAdapter.cancelDiscovery();
       }
       IntentFilter filter = new IntentFilter();
       filter.addAction(BluetoothDevice.ACTION_FOUND);
       filter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
       filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
       filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
       mBluetoothAdapter.startDiscovery();
       getActivity().registerReceiver(mReceiver, filter);
   }


   void init() {
       mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       mArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);
       lv_find = (ListView)v.findViewById(R.id.lv_find);
       lv_find.setAdapter(mArrayAdapter);
       lv_find.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               String s = ((TextView)view).getText().toString();
               SelectDialog dialog = new SelectDialog(getActivity(), s.substring(s.length() - 17));
               dialog.setOnDismissListener(MainFragment.this);
               dialog.show();
           }
       });
       FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);

       fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //주변 기기 검색
               mArrayAdapter.clear();
               getPairedDevice();
               findDevice();
           }
       });
   }

   void startRemote(int $code, String $address) {
       Fragment fragment;
       switch ($code) {
           case 0 :fragment = new RobotFragment(); break;
           case 1 :fragment = new CarFragment(); break;
           case 2 :fragment = new VariousFragment(); break;
           case 3 :fragment = new RobotFragment(); break;
           default:fragment = new RobotFragment(); break;
       }
       if(_startRemoteListener == null) { } else {
           _startRemoteListener.onRemoteStart($code);
       }
       Bundle bundle = new Bundle();
       bundle.putString("ADDRESS", $address);
       Log.e("address",$address);
       bundle.putInt("CODE", $code);
       fragment.setArguments(bundle);
       FragmentManager fm = getFragmentManager();
       FragmentTransaction ft = fm.beginTransaction();
       ft.replace(R.id.layout_main, fragment);
       ft.addToBackStack(null);
       ft.commit();
   }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_CLASS_CHANGED.equals(action)) { //|| BluetoothDevice.ACTION_NAME_CHANGED.equals(action)
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for(int i = 0; i < mArrayAdapter.getCount(); i++) {
                    String s = mArrayAdapter.getItem(i);
                    if(!device.getAddress().equals(s.substring(s.length()-17))) {
                        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if(mArrayAdapter.isEmpty()) {
                    mArrayAdapter.add("연결 가능한 기기가 없습니다.");
                }
                getActivity().unregisterReceiver(mReceiver);
            }
        }
    };

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        SelectDialog dialog = (SelectDialog)dialogInterface;
        startRemote(dialog.getRemote(), dialog.getAddress());
    }

    private class SelectDialog extends Dialog implements View.OnClickListener {

        private OnDismissListener _listener;
        private int remote;
        private String _address;

        public SelectDialog(@NonNull Context context, String $address) {
            super(context);
            _address = $address;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_select);
            findViewById(R.id.iv_select_robot).setOnClickListener(this);
            findViewById(R.id.iv_select_car).setOnClickListener(this);
            findViewById(R.id.iv_select_various).setOnClickListener(this);
            findViewById(R.id.iv_select_chat).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch(view.getId()) {
                case R.id.iv_select_robot :
                    remote = 0;
                    break;
                case R.id.iv_select_car :
                    remote = 1;
                    break;
                case R.id.iv_select_various :
                    remote = 2;
                    break;
                case R.id.iv_select_chat :
                    remote = 3;
                    break;
            }
            if(_listener == null) { } else {
                _listener.onDismiss(SelectDialog.this);
            }
            dismiss();
        }

        public void setOnDismissListener(OnDismissListener $listener) {
            _listener = $listener;
        }

        public int getRemote() {
            return remote;
        }

        public String getAddress() {
            return _address;
        }
    }

    public interface OnRemoteStartListener {
        public void onRemoteStart(int $code);
    }

    public void setOnRemoteStartListener(OnRemoteStartListener $listener) {
        _startRemoteListener = $listener;
    }

}
