package com.example.myapplication.Views.Activities

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.example.myapplication.R
import com.example.myapplication.RunningService
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    var isImages = false

    override fun onCreate(savedInstanceState: Bundle?) {

        // Load the saved theme preference before setting the content view
        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Check the current theme mode and set the toggle button state
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        binding.toggleBtn.isChecked  = isDarkMode


         //sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        // Set the listener for the toggle button
        binding.toggleBtn.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPref.edit()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("dark_mode", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("dark_mode", false)
            }
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()

            // Restart the activity for the theme change to take effect
            recreate()
        }


        ///////////////////-Custom Dialog-//////////////////////////////////////////////////////////////////////////

        binding.showDialogBtn.setOnClickListener {
            //val message = "Are you sure you want to log out"
           // showCustomDialog(message)
            isImages = true
            val message = "Are you sure you want to show Images"
            showCustomDialog(message)
        }
        binding.showImagesBtn.setOnClickListener {
            //isImages = true
            //val message = "Are you sure you want to show Images"
            //showCustomDialog(message)
            val intent = Intent(this, SavedActivity::class.java)
            startActivity(intent)
        }

        ///////////////////////////////////-Service-//////////////////////////////////////////////////////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        binding.startServiceBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 34){
                Toast.makeText(this, "please wait for 34", Toast.LENGTH_SHORT).show()
            }else{
                Intent(this, RunningService::class.java).also {
                    it.action = RunningService.Action.START.toString()
                    startService(it)
                }
            }
        }
        binding.stopServiceBtn.setOnClickListener {
            Intent(this, RunningService::class.java).also {
                it.action = RunningService.Action.STOP.toString()
                startService(it)
            }
        }

////////////////////-Remote Config-////////////////////////////////////////////////////////////////////////////////
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val wishMsg = remoteConfig.getString("WISH_MSG")
                    val hourMsg = remoteConfig.getString("HOUR_SHOW") 
                    val wishText = findViewById<TextView>(R.id.id_wish_msg)
                    val imgView = findViewById<ImageView>(R.id.id_ima_V)
                    wishText.text = wishMsg
                    imgView.setImageResource(R.drawable.conversation_image)
                    Log.d(TAG, "Config params updated: $wishMsg")
                   showToastMessage("$wishMsg Fetch and activate succeeded")
                } else {
                    showCustomDialog("Fetch failed")
                }
            }

        ///////////////////////////////////////////////////////////////////////////////////////////

        }

    private fun showCustomDialog(message : String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage : TextView = dialog.findViewById(R.id.tvMessage)
        val yesBtb : Button = dialog.findViewById(R.id.yesBtn)
        val noBtn : Button = dialog.findViewById(R.id.noBtn)

        tvMessage.text = message

        if (isImages){
            yesBtb.setOnClickListener {
                showToastMessage("Please wait for show images")
                isImages = false
                val intent = Intent(this, ImagesActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }
        }else{
            yesBtb.setOnClickListener {
                showToastMessage("Click On Yes Btn of custom Dialog")
            }
        }

        noBtn.setOnClickListener {
            showToastMessage("Dialog Dismiss")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showToastMessage(text : String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}