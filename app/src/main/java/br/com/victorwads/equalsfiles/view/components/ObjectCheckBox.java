package br.com.victorwads.equalsfiles.view.components;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by victo on 20/07/2017.
 */

public class ObjectCheckBox<T> extends androidx.appcompat.widget.AppCompatCheckBox{
	public T object;

	public ObjectCheckBox(Context context){
		super(context);
	}

	public ObjectCheckBox(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	public ObjectCheckBox(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
	}
}
