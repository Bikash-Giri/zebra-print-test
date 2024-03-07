package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.OutputStream
import java.lang.IllegalArgumentException
import java.util.UUID

class ZebraPrinter(
    private val activity: AppCompatActivity,
    private val PRINTER_MAC_ADDRESS: String
) {
    fun printQRCode(printData: String?) {
        PrintTask().execute(printData)
    }

    private inner class PrintTask : AsyncTask<String?, Void?, Void?>() {

        override fun doInBackground(vararg contents: String?): Void? {
            val printerDevice:BluetoothDevice
            val printData = contents[0]

            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

            var socket: BluetoothSocket? = null
            var outputStream: OutputStream? = null
            try {
             printerDevice = bluetoothAdapter.getRemoteDevice(PRINTER_MAC_ADDRESS)
                socket = printerDevice.createRfcommSocketToServiceRecord(SERIAL_UUID)
                socket.connect()
                if (printData != "") {
                    outputStream = socket.outputStream
                    outputStream.write(printData!!.toByteArray())
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle the IOException here
            } catch (e: SecurityException) {
                e.printStackTrace()
                // Handle the SecurityException here
            } catch (e:IllegalArgumentException){
                e.printStackTrace()
            }
            finally {
                try {
                    if (outputStream != null) {
                        println("outputSteam is not null")
                        outputStream.close()
                    }
                    if (socket != null) {
                        println("socket is not null")
                        socket.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }
    }

    companion object {
        // Bluetooth device MAC address of the Zebra printer
        //    private static final String PRINTER_MAC_ADDRESS = "84:C6:92:51:D5:C0"; // Example MAC address
        // UUID for the Serial Port Profile (SPP)
        private val SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}
