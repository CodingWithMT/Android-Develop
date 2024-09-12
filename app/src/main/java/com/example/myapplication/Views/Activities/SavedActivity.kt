package com.example.myapplication.Views.Activities

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.Models.MediaModel
import com.example.myapplication.Utils.getSavedStatus
import com.example.myapplication.Views.Adapters.SavedAdapter
import com.example.myapplication.databinding.ActivitySavedBinding

class SavedActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedBinding

    private var list = ArrayList<MediaModel>()
    private var adapter = SavedAdapter()

    val directoryPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .toString() + "/WA Status Saver"
    val dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        .toString() + "/WA Status Saver"

    private val REQUEST_CODE_PERMISSIONS_SAVE = 102
    // private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        adapter = SavedAdapter()


        // val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/WAStatusSaver"

        //debugLog("Fetching directoryPath: $dirPath")

        if (allPermissionsGranted()) {
            requestPermissions()
        }
        binding.recyclerViewSaved.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewSaved.adapter = adapter
        //getSavedStatus(directoryPath)
        /*adapter = SavedAdapter(list)
        binding.recyclerViewSaved.adapter = adapter*/

        if (list.isEmpty()) {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this, "Data Found in the List == ${list.size}", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestPermissions() {
        if (allPermissionsGranted()) {
            list = getSavedStatus(dirPath)
            adapter.setData(list)
            //setupRecyclerView(this.list)
           // binding.recyclerViewSaved.adapter = adapter

        } else {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                REQUEST_CODE_PERMISSIONS_SAVE
            )
        }
    }

    private fun setupRecyclerView(imageList: ArrayList<MediaModel>) {
        //val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        binding.recyclerViewSaved.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewSaved.adapter = SavedAdapter()
    }

    private fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS_SAVE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                //getSavedStatus(directoryPath)
                 list = getSavedStatus(dirPath)
                adapter.setData(list)
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                // Optionally, you can direct the user to the app settings
                finish()
            }
        }
    }


    private val REQUEST_PERMISSION_CODE_SAVED = 1001

    /*    private fun checkAndRequestPermissions() {
            // Check if the required permissions are already granted
            val permissionsToRequest = mutableListOf<String>()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ permissions
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add( android.Manifest.permission.READ_MEDIA_IMAGES)
                }
                if (ContextCompat.checkSelfPermission(this,  android.Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add( android.Manifest.permission.READ_MEDIA_VIDEO)
                }
            } else {
                // Android 12 and below
                if (ContextCompat.checkSelfPermission(this,  android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add( android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            if (permissionsToRequest.isNotEmpty()) {
                // Request the permissions
                ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_PERMISSION_CODE_SAVED)
            } else {
                // Permissions already granted, proceed with fetching media files
                fetchMediaFiles()
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode == REQUEST_PERMISSION_CODE_SAVED) {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // All permissions granted, proceed with fetching media files
                    fetchMediaFiles()
                } else {
                    // Permissions denied, show rationale or disable features
                    Toast.makeText(this, "Permissions are required to access media files.", Toast.LENGTH_SHORT).show()
                }
            }
        }*/

}

fun debugLog(message: String) {
    Log.d("show_Debug_data", message)
}