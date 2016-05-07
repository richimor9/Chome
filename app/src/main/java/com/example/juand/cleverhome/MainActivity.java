package com.example.juand.cleverhome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;


import java.util.Set;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    Button btnShow;
    ListView deviceList;

    //Bluetooth
    private BluetoothAdapter cellBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("bluetooth");
        tabSpec.setContent(R.id.bluetoothTab);
        tabSpec.setIndicator("Bluetooth");
        tabHost.addTab(tabSpec);

        btnShow = (Button)findViewById(R.id.showButton);
        deviceList = (ListView)findViewById(R.id.deviceList);

        cellBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(cellBluetooth == null)
        {
            //Muestra un mensaje -> No hay dispositivos bluetooth
            Toast.makeText(getApplicationContext(), "Adaptador Bluetooth No Disponible",Toast.LENGTH_LONG).show();
            finish();
        }
        else if(!cellBluetooth.isEnabled())
        {
            //Pregunta si deseas activar bluetooth
            Intent turnBtnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBtnOn,1);
        }

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {pairedDevicesList();
            }
        });

        tabSpec = tabHost.newTabSpec("ethernet");
        tabSpec.setContent(R.id.ethernetTab);
        tabSpec.setIndicator("Ethernet");
        tabHost.addTab(tabSpec);
    }
    private void pairedDevicesList()
    {
        pairedDevices = cellBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size() > 0)
        {
            for (BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No se encontraron dispositivos...", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(cellListClickListener);
    }
    private AdapterView.OnItemClickListener cellListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?>av,View v, int arg2,long arg3)
        {
            String info = ((TextView)v).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent i = new Intent(MainActivity.this,ledControl.class);

            i.putExtra(EXTRA_ADDRESS,address);
            startActivity(i);
        }
    };
}
