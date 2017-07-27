package br.com.victorwads.equalsfiles.view.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import br.com.victorwads.equalsfiles.R;
/**
 * Created by victo on 07/07/2017.
 */

public class Dialogos{


	public static void info(Context context,String s){
		new AlertDialog.Builder(context)
			.setTitle(s)
			.setIcon(R.drawable.ic_alert)
			.setPositiveButton("Ok", null).setNegativeButton(null, null).show();
	}
	public static void erro(Context context,String s){
		new AlertDialog.Builder(context)
			.setTitle(s)
			.setIcon(R.drawable.ic_alert)
			.setPositiveButton("Ok", null).setNegativeButton(null, null).show();
	}

	public static void pergunta(Context context, String s, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no){
		new AlertDialog.Builder(context)
			.setTitle(s)
			.setIcon(R.drawable.ic_alert)
			.setPositiveButton(context.getString(R.string.word_yes), yes).setNegativeButton(context.getString(R.string.word_no), no).show();
	}
}
