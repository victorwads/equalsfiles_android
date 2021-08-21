package br.com.victorwads.equalsfiles.util

import androidx.fragment.app.FragmentActivity
import br.com.victorwads.equalsfiles.util.FileUtil
import android.app.Activity
import android.os.Build.VERSION
import android.os.Build
import androidx.core.content.ContextCompat
import android.Manifest.permission
import android.content.pm.PackageManager
import android.content.Intent
import androidx.core.app.ActivityCompat
import br.com.victorwads.equalsfiles.R
import android.content.ActivityNotFoundException
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import java.io.File
import java.lang.Exception

/**
 * Created by victor on 05/07/2017.
 */
object FileUtil {

    const val PERMISSION_REQUEST_CODE = 515

    @JvmStatic
	fun showFolderChooser(activity: FragmentActivity, RESULT_CODE: Int) {
        showFileChooser(activity, RESULT_CODE, true, null)
    }

    private fun checkPermission(activity: Activity): Boolean {
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(activity, permission.READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(activity, permission.WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(activity: Activity) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", activity.applicationContext.packageName))
                activity.startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                activity.startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission.READ_EXTERNAL_STORAGE,permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showFileChooser(
        activity: FragmentActivity,
        RESULT_CODE: Int,
        getFolder: Boolean,
        fileType: String?
    ) {
        if (!checkPermission(activity))
            requestPermission(activity)

        val intent = Intent()
        if (getFolder) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                intent.action = Intent.ACTION_OPEN_DOCUMENT_TREE
            } else if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.action = Intent.ACTION_OPEN_DOCUMENT_TREE
            } else {
                intent.action = "com.estrongs.action.PICK_DIRECTORY"
                val uri = Uri.parse(Environment.getExternalStorageDirectory().path)
                intent.putExtra(
                    "com.estrongs.intent.extra.TITLE",
                    activity.getString(R.string.lbl_select_dir)
                )
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
            }
        } else {
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = fileType ?: "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        }
        try {
            if (intent.resolveActivityInfo(activity.packageManager, 0) == null) {
                throw ActivityNotFoundException("")
            }
            activity.startActivityForResult(
                Intent.createChooser(intent, "Select a File to Upload"),
                RESULT_CODE
            )
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(activity, "Please install ES File Explorer App.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @JvmStatic
	fun getFileFromChoser(activity: FragmentActivity, data: Intent): File? {
        var uri = data.data
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uri = try {
                DocumentFile.fromTreeUri(activity, uri!!)!!.uri
            } catch (e: Exception) {
                DocumentFile.fromSingleUri(activity, uri!!)!!.uri
            }
            getRealFileFromURI(activity, uri)
        } else {
            File(uri!!.path)
        }
    }

    private fun getRealFileFromURI(activity: FragmentActivity, uri: Uri?): File? {
        var filePath: String? = null
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            filePath = if ("primary".equals(split[0], ignoreCase = true)) {
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + if (split.size > 1) split[1] else ""
            } else {
                "/storage/" + split[0] + "/" + if (split.size > 1) split[1] else ""
            }
        } else if (isDownloadsDocument(uri)) {
            // DownloadsProvider
            val id = DocumentsContract.getDocumentId(uri)
            //final Uri contentUri = ContentUris.withAppendedId(
            // Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = activity.contentResolver.query(uri!!, projection, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    filePath = cursor.getString(index)
                    cursor.close()
                }
            } finally {
                cursor?.close()
            }
        } else if (DocumentsContract.isDocumentUri(activity, uri)) {
            // MediaProvider
            val column = arrayOf(MediaStore.Images.Media.DATA)
            val wholeID = DocumentsContract.getDocumentId(uri)

            // Split at colon, use second item in the array
            val ids = wholeID.split(":").toTypedArray()
            val id = if (ids.size > 1) ids[1] else ids[0]

            // where id is equal to
            val sel = MediaStore.Images.Media._ID + "=?"
            val cursor = activity.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column,
                sel,
                arrayOf(id),
                null
            )
            if (cursor != null) {
                val columnIndex = cursor.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } else {
            val proj = arrayOf(MediaStore.Audio.Media.DATA)
            val cursor = activity.contentResolver.query(uri!!, proj, null, null, null)
            if (cursor != null) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                if (cursor.moveToFirst()) filePath = cursor.getString(column_index)
                cursor.close()
            }
        }
        return if (filePath == null) null else File(filePath)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri?): Boolean {
        return "com.android.externalstorage.documents" == uri!!.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri?): Boolean {
        return "com.android.providers.downloads.documents" == uri!!.authority
    }
}