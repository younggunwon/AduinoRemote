package younggun.aduinoremote;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainFragment.OnRemoteStartListener {

    Toolbar toolbar;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Fragment fragment;
            switch (code) {
                case 0 :fragment = new RobotSettingFragment(); break;
                case 1 :fragment = new RobotSettingFragment(); break;
                case 2 :fragment = new RobotSettingFragment(); break;
                case 3 :fragment = new RobotSettingFragment(); break;
                default:fragment = new RobotSettingFragment(); break;
            }

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.layout_main, fragment);
            ft.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    void init() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "이 기기는 블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 100);
            }
        }
        setDatabase();

        MainFragment mainFragment = new MainFragment();
        mainFragment.setOnRemoteStartListener(this);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.layout_main, mainFragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100 && resultCode == RESULT_CANCELED) {
            Toast.makeText(MainActivity.this, "블루투스를 사용해야 합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onRemoteStart(int $code) {
        code = $code;
        toolbar.getMenu().findItem(R.id.action_settings).setVisible(true);
    }

    private void setDatabase() {
        DBHelper dbHelper = new DBHelper(MainActivity.this, "Remote.db", null, 1);
        ArrayList<String> dbList = dbHelper.getValue("ROBOT");
        if(dbList.isEmpty()) {
            dbHelper.insert("ROBOT", "outputUp", "u");
            dbHelper.insert("ROBOT", "outputDown", "d");
            dbHelper.insert("ROBOT", "outputLeft", "l");
            dbHelper.insert("ROBOT", "outputRight", "r");
            dbHelper.insert("ROBOT", "outputa", "a");
            dbHelper.insert("ROBOT", "outputs", "s");
            dbHelper.insert("ROBOT", "outputf", "f");
            dbHelper.insert("ROBOT", "outputg", "g");

            dbHelper.insert("CAR", "outputUp", "u");
            dbHelper.insert("CAR", "outputDown", "d");
            dbHelper.insert("CAR", "outputLeft", "l");
            dbHelper.insert("CAR", "outputRight", "r");

            dbHelper.insert("VARIOUS", "inputText0", "받은값1");
            dbHelper.insert("VARIOUS", "inputText1", "받은값2");
            dbHelper.insert("VARIOUS", "inputText2", "받은값3");

            dbHelper.insert("VARIOUS", "buttonName0", "버튼1");
            dbHelper.insert("VARIOUS", "buttonName1", "버튼2");
            dbHelper.insert("VARIOUS", "buttonName2", "버튼3");
            dbHelper.insert("VARIOUS", "buttonName3", "버튼4");
            dbHelper.insert("VARIOUS", "buttonName4", "버튼5");
            dbHelper.insert("VARIOUS", "buttonName5", "버튼6");
        }
    }
}
