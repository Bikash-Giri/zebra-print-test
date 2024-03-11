package com.example.myapplication

//import com.example.myapplication.ZebraPrinter.PrintTask

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.khalti.pos.R
import com.khalti.pos.databinding.ActivityMainBinding

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1001
    private val PICK_FILE_REQUEST_CODE = 1002
    private var address: String = ""
    private lateinit  var addressView:TextView
    private lateinit  var editText: EditText
    private lateinit var preferences:MySharedPreferences
    private lateinit var zebraPrinter:ZebraPrinter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = MySharedPreferences(this)
        // Find the EditText view
        editText = binding.editText
        addressView = binding.address


        // Access the text entered by the user
        address = editText.text.toString()
        retrieveValue()
        ActivityCompat.requestPermissions(
            this,
            permissions(),
            1,
        )

        setSupportActionBar(binding.toolbar)

    }

    var storage_permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storage_permissions_33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO
    )

    fun permissions(): Array<String> {
        val p: Array<String>
        p = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storage_permissions_33
        } else {
            storage_permissions
        }
        return p
    }
    fun selectFile(view: View?) {

        checkBluetoothPermission(true)


    }

    fun saveAddress(view: View?) {
        preferences.saveData("address",editText.text.toString())
        retrieveValue(true)

    }
    fun retrieveValue(connectBluetooth: Boolean? = false) {
        val retrievedValue = preferences.getData("address")
        if (retrievedValue !=null && retrievedValue != ""){
            address = retrievedValue
            addressView.text =  "Saved Address :" + retrievedValue
            editText.setText(address)
        }
        if (connectBluetooth!!){

        checkBluetoothPermission(false)
        }

    }
    private fun checkBluetoothPermission(openFile:Boolean? = false){
        // Check if Bluetooth is supported on the device
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Handle the case where Bluetooth is not supported on the device
            Toast.makeText(
                this,
                "Bluetooth is not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
//       else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
//                REQUEST_BLUETOOTH_PERMISSIONS,
//            )
//        }
        // Check for Bluetooth permission before executing Bluetooth functionality
        if (hasBluetoothPermission()) {

               if (openFile!!){
                   chooseFile()
               }else{
                   printQRCode("")
               }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSIONS,
            )
            // Request Bluetooth permission

        }
        else{
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                ),REQUEST_BLUETOOTH_PERMISSIONS
            )
        }

    }

     private fun chooseFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("*/*") // Set MIME type to all files
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            try {
                try {
                    if (uri != null){
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            val reader = BufferedReader(InputStreamReader(inputStream))
                            val stringBuilder = StringBuilder()
                            var line: String?
                            var modifiedString = ""
                            while (reader.readLine().also { line = it } != null) {
                                stringBuilder.append(line!!).append("\r\n")
                            }
                            val fileContent = stringBuilder.toString()
                            printQRCode(fileContent)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle the exception
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        ) // Call super method

        // Check if the request code matches the Bluetooth permission request
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            // Ensure grantResults array is not empty and has the same length as requested permissions
            if (grantResults.size == permissions.size) {
                var allPermissionsGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    }
                }
                if (allPermissionsGranted) {
                        chooseFile()
                }
            } else {
                // Unexpected situation, grantResults length does not match requested permissions
                // Handle this case as needed
            }
        }
    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun printQRCode(printContent:String?) {

        // Check if Bluetooth is supported on the device
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Handle the case where Bluetooth is not supported on the device
            Toast.makeText(
                this,
                "Bluetooth is not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Check for Bluetooth permission before executing Bluetooth functionality
        if (hasBluetoothPermission()) {
            zebraPrinter = ZebraPrinter(this,address)
            zebraPrinter.printQRCode(printContent)
        } else {
            // Request Bluetooth permission
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,

                ),REQUEST_BLUETOOTH_PERMISSIONS
            )
        }
    }

    private fun hasBluetoothPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val isFistCorrect = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_SCAN)
            val isSecondCorrect = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT)
            return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
        }
       else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

            return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED)
        }
        return true // Permissions are implicitly granted on versions below M


    }
}