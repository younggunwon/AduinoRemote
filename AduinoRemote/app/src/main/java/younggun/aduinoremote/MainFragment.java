package younggun.aduinoremote;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by LOVE on 2017-05-23.
 */

public class MainFragment extends Fragment implements DialogInterface.OnDismissListener {

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    View v;
    BluetoothAdapter mBluetoothAdapter;
    ListView lv_find;
    ArrayAdapter<String> mArrayAdapter;

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
           getActivity().unregisterReceiver(mReceiver);
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
               BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(s.substring(s.length() - 17));
               SelectDialog dialog = new SelectDialog(getActivity(), device);
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

   void startRemote(int code, BluetoothDevice device) {
       Fragment fragment;
       switch (code) {
           case 0 :fragment = new RobotFragment(); break;
           case 1 :fragment = new RobotFragment(); break;
           case 2 :fragment = new RobotFragment(); break;
           case 3 :fragment = new RobotFragment(); break;
           default:fragment = new RobotFragment(); break;
       }
       new ConnectThread(code, device).start();
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

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        int code;

        public ConnectThread(int code, BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            this.code = code;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            new ConnectedThread(code, mmSocket).start();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        int code;

        public ConnectedThread(int code, BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.code = code;
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    //code에 따른 처리방식 입력
                    write(new String("abc").getBytes());
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        SelectDialog dialog = (SelectDialog)dialogInterface;
        startRemote(dialog.getRemote(), dialog.getDevice());
    }

    private class SelectDialog extends Dialog implements View.OnClickListener {

        private OnDismissListener _listener;
        private int remote;
        private BluetoothDevice _device;

        public SelectDialog(@NonNull Context context, BluetoothDevice $device) {
            super(context);
            _device = $device;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_select);
            findViewById(R.id.iv_select_robot).setOnClickListener(this);
            findViewById(R.id.iv_select_car).setOnClickListener(this);
            findViewById(R.id.iv_select_button).setOnClickListener(this);
            findViewById(R.id.iv_select_chat).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch(v.getId()) {
                case R.id.iv_select_robot :
                    remote = 0;
                    break;
                case R.id.iv_select_car :
                    remote = 1;
                    break;
                case R.id.iv_select_button :
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

        public BluetoothDevice getDevice() {
            return _device;
        }
    }



}
