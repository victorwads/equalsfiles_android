package br.com.victorwads.equalsfiles.util;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.provider.DocumentFile;
import android.widget.Toast;

import java.io.File;

import br.com.victorwads.equalsfiles.R;

/**
 * Created by victor on 05/07/2017.
 */

public class FileUtil{

	public static void showFolderChooser(FragmentActivity activity, int RESULT_CODE){
		showFileChooser(activity, RESULT_CODE, true, null);
	}

	public static void showFileChooser(FragmentActivity activity, int RESULT_CODE){
		showFileChooser(activity, RESULT_CODE, false, null);
	}

	public static void showFileChooser(FragmentActivity activity, String fileType, int RESULT_CODE){
		showFileChooser(activity, RESULT_CODE, false, fileType);
	}

	private static void showFileChooser(FragmentActivity activity, int RESULT_CODE, boolean getFolder, String fileType){
		//check for permission/
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
		}

		Intent intent = new Intent();
		if(getFolder){
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
				intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
			}else{
				intent.setAction("com.estrongs.action.PICK_DIRECTORY");
				Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
				intent.putExtra("com.estrongs.intent.extra.TITLE", activity.getString(R.string.lbl_select_dir));
				//intent.addCategory(Intent.CATEGORY_OPENABLE);
			}
		}else{
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setType(fileType == null ? "*/*" : fileType);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
		}

		try{
			if(intent.resolveActivityInfo(activity.getPackageManager(), 0) == null){
				throw new ActivityNotFoundException("");
			}
			activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), RESULT_CODE);
		}catch(android.content.ActivityNotFoundException ex){
			Toast.makeText(activity, "Please install ES File Explorer App.", Toast.LENGTH_SHORT).show();
		}
	}

	public static File getFileFromChoser(FragmentActivity activity, Intent data){
		Uri uri = data.getData();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			try{
				uri = DocumentFile.fromTreeUri(activity, uri).getUri();
			}catch(Exception e){
				uri = DocumentFile.fromSingleUri(activity, uri).getUri();
			}
			return getRealFileFromURI(activity, uri);
		}else{
			return new File(uri.getPath());
		}
	}

	private static File getRealFileFromURI(FragmentActivity activity, Uri uri){
		String filePath = null;
		// ExternalStorageProvider
		if(isExternalStorageDocument(uri)){
			final String docId = DocumentsContract.getDocumentId(uri);
			final String[] split = docId.split(":");

			if("primary".equalsIgnoreCase(split[0])){
				filePath = Environment.getExternalStorageDirectory() + "/" + (split.length > 1 ? split[1] : "");
			}else{
				filePath = "/storage/" + split[0] + "/" + (split.length > 1 ? split[1] : "");
			}

		}else if(isDownloadsDocument(uri)){
			// DownloadsProvider
			final String id = DocumentsContract.getDocumentId(uri);
			//final Uri contentUri = ContentUris.withAppendedId(
			// Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

			Cursor cursor = null;
			final String column = "_data";
			final String[] projection = {column};

			try{
				cursor = activity.getContentResolver().query(uri, projection, null, null, null);
				if(cursor != null && cursor.moveToFirst()){
					final int index = cursor.getColumnIndexOrThrow(column);
					filePath = cursor.getString(index);
					cursor.close();
				}
			}finally{
				if(cursor != null)
					cursor.close();
			}
		}else if(DocumentsContract.isDocumentUri(activity, uri)){
			// MediaProvider
			String[] column = {MediaStore.Images.Media.DATA};
			String wholeID = DocumentsContract.getDocumentId(uri);

			// Split at colon, use second item in the array
			String[] ids = wholeID.split(":");
			String id = ids.length > 1 ? ids[1] : ids[0];

			// where id is equal to
			String sel = MediaStore.Images.Media._ID + "=?";

			Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
			if(cursor != null){
				int columnIndex = cursor.getColumnIndex(column[0]);
				if(cursor.moveToFirst()){
					filePath = cursor.getString(columnIndex);
				}
				cursor.close();
			}
		}else{
			String[] proj = {MediaStore.Audio.Media.DATA};
			Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
			if(cursor != null){
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
				if(cursor.moveToFirst())
					filePath = cursor.getString(column_index);
				cursor.close();
			}
		}
		return filePath == null ? null : new File(filePath);
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	private static boolean isExternalStorageDocument(Uri uri){
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	private static boolean isDownloadsDocument(Uri uri){
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

}
