package br.com.victorwads.equalsfiles.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.File;

import br.com.victorwads.equalsfiles.R;
import br.com.victorwads.equalsfiles.model.Diretorio;
import br.com.victorwads.equalsfiles.util.FileUtil;
import br.com.victorwads.equalsfiles.view.services.ComparacaoService;
import br.com.victorwads.equalsfiles.view.util.Dialogos;
import br.com.victorwads.equalsfiles.view.util.GerenciaDiretorios;

import static android.app.Activity.RESULT_OK;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.STRING_ARRAY_DIRS;


/**
 * Created by victo on 07/07/2017.
 */

public class FragmentComparacao extends Fragment{

	public static final String KEY_DIRETORIOS = "diretorios";
	public static final String KEY_IS_NAME_SENSITIVE = "isNameSensitive";
	private final int GET_FOLDER_CODE = 2;
	private View view;
	private CheckBox checkNameSensitive;
	private FloatingActionButton btnIniciar;
	private GerenciaDiretorios gerenciador = null;

	public FragmentComparacao(){
		setArguments(new Bundle());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		view = inflater.inflate(R.layout.fragment_comparacao, container, false);
		gerenciador = new GerenciaDiretorios(getActivity(), (ListView) view.findViewById(R.id.diretorios_list_view));
		initComponents();
		loadDados();

		return view;
	}

	@Override
	public void onDestroy(){
		Bundle dados = getArguments();

		dados.putBoolean(KEY_IS_NAME_SENSITIVE, checkNameSensitive.isChecked());
		dados.putStringArray(KEY_DIRETORIOS, Diretorio.toArrayString(gerenciador.getDiretorios()));

		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK) return;
		if(requestCode == GET_FOLDER_CODE){
			try{
				File f = FileUtil.getFileFromChoser(getActivity(), data);
				if(f.canRead()){
					gerenciador.addDiretorio(new Diretorio(f.getAbsolutePath()));
				}else{
					Dialogos.erro(getActivity(), getString(R.string.msg_dir_cant_read));
				}
			}catch(Exception e){
			}
		}
	}

	private void loadDados(){
		Bundle dados = getArguments();
		if(dados.containsKey(KEY_IS_NAME_SENSITIVE))
			checkNameSensitive.setChecked(dados.getBoolean(KEY_IS_NAME_SENSITIVE));
		if(dados.containsKey(KEY_DIRETORIOS))
			gerenciador.addAll(dados.getStringArray(KEY_DIRETORIOS));
	}

	private void initComponents(){
		checkNameSensitive = (CheckBox) view.findViewById(R.id.check_name_sensitive);
		btnIniciar = (FloatingActionButton) view.findViewById(R.id.btn_iniciar);
		btnIniciar.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				start();
			}
		});

		Button btnAdd = (Button) view.findViewById(R.id.btn_adicionar);
		btnAdd.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				FileUtil.showFolderChooser(getActivity(), GET_FOLDER_CODE);
			}
		});
	}

	private void start(){
		if(gerenciador.size() == 0){
			Dialogos.erro(getActivity(), getString(R.string.msg_choose_dirs));
		}else{
			Intent dirs = new Intent(getActivity(), ComparacaoService.class);
			dirs.putExtra(STRING_ARRAY_DIRS, Diretorio.toArrayString(gerenciador.getDiretorios()));
			getActivity().startService(dirs);

			MenuApp act = (MenuApp) getActivity();
			act.changeFragment(R.id.menuResultados, null);
		}
	}

}
