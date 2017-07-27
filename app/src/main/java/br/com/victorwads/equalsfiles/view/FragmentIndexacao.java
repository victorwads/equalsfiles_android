package br.com.victorwads.equalsfiles.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.victorwads.equalsfiles.R;

/**
 * Created by victo on 07/07/2017.
 */

public class FragmentIndexacao extends Fragment{
	View view;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		view = inflater.inflate(R.layout.fragment_indexacao, container, false);
		return view;
	}

}
