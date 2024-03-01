package com.example.myapplication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
//import com.example.myapplication.ZebraPrinter.PrintTask
import com.example.myapplication.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Example JSON data
        // Example JSON data
        binding.fab.setOnClickListener { view ->
            // Print QR code with JSON data
            // Print QR code with JSON data
            printQRCode()

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
                  printQRCode()
                    // All permissions granted, proceed with printing
                    // Call the printQRCode method here again or handle it as appropriate
                } else {
                    // Permissions denied, handle this case
                    // For example, display a message to the user indicating that permissions are required for printing
//                    Toast.makeText(
//                        this,
//                        "Bluetooth permissions are required for printing",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    // Request permissions again
//                    ActivityCompat.requestPermissions(
//                        this, arrayOf(
//                            Manifest.permission.BLUETOOTH,
//                            Manifest.permission.BLUETOOTH_ADMIN
//                        ), REQUEST_BLUETOOTH_PERMISSIONS
//                    )
                }
            } else {
                // Unexpected situation, grantResults length does not match requested permissions
                // Handle this case as needed
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun printQRCode() {

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
            val jsonData = JSONObject()
            try {
                jsonData.put("Khalti_ID", "9860239082")
                jsonData.put("name", "Abhiyan Stationary Private limited")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val zebraPrinter = ZebraPrinter(this);
            zebraPrinter.printQRCode(jsonData)
//            PrintTask().execute(jsonData)
        } else {
            // Request Bluetooth permission
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                ),REQUEST_BLUETOOTH_PERMISSIONS
            )
        }
    }

    private fun hasBluetoothPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val isFistCorrect = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
            val isSecondCorrect = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED
            return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_BLUETOOTH_PERMISSIONS,
            )
        }
        return true // Permissions are implicitly granted on versions below M


    }
}