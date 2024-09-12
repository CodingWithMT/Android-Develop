package com.example.myapplication.Utils

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Models.MediaModel
import com.example.myapplication.Views.Activities.debugLog
import java.io.File

private val _savedMediaFiles = MutableLiveData<List<File>>()
val savedMediaFiles: LiveData<List<File>> get() = _savedMediaFiles

fun getSavedStatus(directoryPath: String): ArrayList<MediaModel> {
    val mediaDirectory = File(directoryPath)
    val mediaList = ArrayList<MediaModel>()
    debugLog("directoryPath Get: ${directoryPath}")

    debugLog("directory: ${mediaDirectory.path}")
    if (mediaDirectory.exists()) {
        debugLog("directory exist")
        val files = mediaDirectory.listFiles { file ->
            file.isFile && (file.extension == "jpg" || file.extension == "jpeg" || file.extension == "png" || file.extension == "mp4" || file.extension == "avi")
        }?.toList() ?: emptyList()

        debugLog("filesCount; ${files.size}")

        files.forEach { item ->
            mediaList.add(
                MediaModel(
                    path = item.absolutePath,
                    fileName = item.name,
                    mimeType = item.extension
                )
            )
        }

        return mediaList

    } else {
        debugLog("directory not exist")
    }

    //_savedMediaFiles.postValue(mediaList)
    return mediaList
}


fun Context.fetchSavedStatuses(directoryPath: String): ArrayList<MediaModel> {
    val mediaList = ArrayList<MediaModel>()

    // The directory where the statuses are saved
    //val relativePath = Environment.DIRECTORY_DOCUMENTS + "/" + "WA Status Saver"

    debugLog("Path == $directoryPath")

    // Define the projection (the columns you want to retrieve)
    val projection = arrayOf(
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.DATE_ADDED,
        MediaStore.MediaColumns.DATA // This provides the file path
    )

    // Define the selection criteria
    val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
    val selectionArgs = arrayOf("$directoryPath%")

    // Query the MediaStore to get the saved statuses
    val cursor = contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        projection,
        selection,
        selectionArgs,
        "${MediaStore.MediaColumns.DATE_ADDED} DESC" // Order by latest saved
    )

    cursor?.use {
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        while (cursor.moveToNext()) {
            val fileName = cursor.getString(nameColumn)
            val mimeType = cursor.getString(mimeTypeColumn)
            val filePath = cursor.getString(dataColumn)

            // Determine if the file is a video based on its MIME type
            val isVideo = mimeType.startsWith("video")

            // Create MediaModel object
            val mediaModel = MediaModel(
                path = filePath,
                fileName = fileName,
                mimeType = if (isVideo) "video" else "image"
            )

            // Add to the list
            mediaList.add(mediaModel)

        }
        debugLog("mediaList In fetch == ${mediaList.size}")

    }

    return mediaList
}
