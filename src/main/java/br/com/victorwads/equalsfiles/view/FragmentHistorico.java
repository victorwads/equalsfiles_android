package br.com.victorwads.equalsfiles.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.victorwads.equalsfiles.R;
import br.com.victorwads.equalsfiles.dao.Conexao;
import br.com.victorwads.equalsfiles.view.util.Dialogos;

/**
 * Created by victo on 07/07/2017.
 */

public class FragmentHistorico extends Fragment{
	View view;

	public static void copyFile(File src, File dst) throws IOException{
		FileInputStream var2 = new FileInputStream(src);
		FileOutputStream var3 = new FileOutputStream(dst);
		byte[] var4 = new byte[1024];

		int var5;
		while((var5 = var2.read(var4)) > 0){
			var3.write(var4, 0, var5);
		}

		var2.close();
		var3.close();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		view = inflater.inflate(R.layout.fragment_historico, container, false);

		final File s = new File(Conexao.BD_FILE);
		final File t = new File(Environment.getExternalStorageDirectory() + File.separator + s.getName());

		Dialogos.pergunta(getContext(), "Export/Import", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				try{
					copyFile(s, t);
					Log.e(getClass().getName(), "\n-\nCopy: " + s.getAbsolutePath() + "\nto: " + t.getAbsolutePath());
				}catch(IOException e){
					e.printStackTrace(System.err);
				}
			}
		}, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				try{
					copyFile(t, s);
					Log.e(getClass().getName(), "\n-\nCopy: " + t.getAbsolutePath() + "\nto: " + s.getAbsolutePath());
				}catch(IOException e){
					e.printStackTrace(System.err);
				}
			}
		});

		return view;
	}

}
