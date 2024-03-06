package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ZebraPrinter {

    // Bluetooth device MAC address of the Zebra printer
    private static final String PRINTER_MAC_ADDRESS = "84:C6:92:51:D5:C0"; // Example MAC address

    // UUID for the Serial Port Profile (SPP)
    private static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1001;


    private final AppCompatActivity activity;
    public ZebraPrinter(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void printQRCode(String printData) {

        new PrintTask().execute(printData);
    }

    private class PrintTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String ...contents) {
           String printData = contents[0];

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice printerDevice = bluetoothAdapter.getRemoteDevice(PRINTER_MAC_ADDRESS);
            BluetoothSocket socket = null;
            OutputStream outputStream = null;

            try {
                socket = printerDevice.createRfcommSocketToServiceRecord(SERIAL_UUID);
                socket.connect();
                outputStream = socket.getOutputStream();
                outputStream.write(printData.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the IOException here
            } catch (SecurityException e) {
                e.printStackTrace();
                // Handle the SecurityException here
            } finally {
                try {
                    if (outputStream != null) {
                        System.out.println("outputSteam is not null");

                        outputStream.close();
                    }
                    if (socket != null) {
                        System.out.println("socket is not null");
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
