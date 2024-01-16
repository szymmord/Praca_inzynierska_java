package com.example.projekt_inzynierski;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private TextView textView1, textView2, textView3, textView4, btReadings, btDevices;
    private Button connectToDeviceButton, seachDevicesButton, clearValuesButton;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler handler;
    private BluetoothDevice arduinoBTModule;
    private String arduinoBTDeviceAddress = "E4:D2:4C:A7:3B:C6"; // Replace with your Bluetooth device address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);

        connectToDeviceButton = findViewById(R.id.connectToDeviceButton);
        seachDevicesButton = findViewById(R.id.seachDevicesButton);
        clearValuesButton = findViewById(R.id.clearValuesButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        textView1.setText((String) msg.obj);
                        break;
                    case 1:
                        textView2.setText((String) msg.obj);
                        break;
                    case 2:
                        textView3.setText((String) msg.obj);
                        break;
                    case 3:
                        textView4.setText((String) msg.obj);
                        break;
                }
                return true;
            }
        });

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!bluetoothAdapter.isEnabled()) {
            // Włączanie Bluetooth, jeśli jest wyłączony
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, 1);
        } else {
            connectBluetooth();
        }

        seachDevicesButton.setOnClickListener(view -> searchBluetoothDevices());
        clearValuesButton.setOnClickListener(view -> clearValues());
        connectToDeviceButton.setOnClickListener(view -> connectToDevice());
    }

    private void connectBluetooth() {
        arduinoBTModule = bluetoothAdapter.getRemoteDevice(arduinoBTDeviceAddress);
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothSocket.connect();
            Toast.makeText(this, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
            ReadThread readThread = new ReadThread(bluetoothSocket.getInputStream());
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchBluetoothDevices() {
        btDevices.setText(""); // Clear previous device list
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                btDevices.append(deviceName + " || " + deviceHardwareAddress + "\n");

                if (deviceName.equals("SIEMANKO")) {
                    arduinoBTDeviceAddress = deviceHardwareAddress;
                    arduinoBTModule = device;
                    connectToDeviceButton.setEnabled(true);
                }
            }
        }
    }

    private void clearValues() {
        textView1.setText("");
        textView2.setText("");
        textView3.setText("");
        textView4.setText("");
        btReadings.setText("");
    }

    private void connectToDevice() {
        btReadings.setText("");
        if (arduinoBTModule != null) {
            ConnectThread connectThread = new ConnectThread(arduinoBTModule, MY_UUID, handler);
            connectThread.run();

            if (connectThread.getMmSocket().isConnected()) {
                ConnectedThread connectedThread = new ConnectedThread(connectThread.getMmSocket());
                connectedThread.run();
                if (connectedThread.getValueRead() != null) {
                    btReadings.setText(connectedThread.getValueRead());
                }
                connectedThread.cancel();
            }
            connectThread.cancel();
        }
    }

    private class ReadThread extends Thread {
        private InputStream inputStream;

        ReadThread(InputStream stream) {
            inputStream = stream;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String data = new String(buffer, 0, bytes);
                    String[] lines = data.split("\n");
                    for (int i = 0; i < lines.length; i++) {
                        Message message = handler.obtainMessage(i, lines[i]);
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
