package br.com.victorwads.equalsfiles.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import br.com.victorwads.equalsfiles.R;
import br.com.victorwads.equalsfiles.controller.Resultados;
import br.com.victorwads.equalsfiles.model.Arquivo;
import br.com.victorwads.equalsfiles.model.Diretorio;
import br.com.victorwads.equalsfiles.model.Resultado;
import br.com.victorwads.equalsfiles.view.components.ObjectCheckBox;
import br.com.victorwads.equalsfiles.view.util.DateTime;
import br.com.victorwads.equalsfiles.view.util.Dialogos;
import br.com.victorwads.equalsfiles.view.util.ResultadoAdapter;

import static br.com.victorwads.equalsfiles.controller.CacheMD5.humamSize;

/**
 * Created by victo on 09/07/2017.
 */

public class FragmentResultado extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener{

	private View view;
	private LinearLayout box;
	private TextView txtAnim, txtDuplicatas, txtTamanhoDuplicatas;
	private FloatingActionButton btnAction;
	private LayoutInflater layoutInflater;
	private Resultado resultado;
	private String extensao = null;
	private String[] ordem;
	private final ArrayList<String> exts = new ArrayList<>();
	private final ArrayList<Arquivo> toDelete = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		return view = inflater.inflate(R.layout.fragment_resultado, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
		resultado = Resultados.byId(getArguments().getInt(ResultadoAdapter.BUNLDE_ID));
		initComponents();
	}

	private void initComponents(){
		layoutInflater = getActivity().getLayoutInflater();

		//Init Vars
		btnAction = (FloatingActionButton) view.findViewById(R.id.btn_excluir);
		txtAnim = (TextView) view.findViewById(R.id.txt_anim);
		txtTamanhoDuplicatas = (TextView) view.findViewById(R.id.txt_duplicatas);
		txtDuplicatas = (TextView) view.findViewById(R.id.txt_duplicatas_tamanho);
		box = (LinearLayout) view.findViewById(R.id.box);

		//Local Vars
		TextView txtArquivos = (TextView) view.findViewById(R.id.txt_arquivo);
		TextView txtTamanho = (TextView) view.findViewById(R.id.txt_tamanho);
		TextView txtDuracao = (TextView) view.findViewById(R.id.txt_duracao);

		//Carregar informações na View
		txtArquivos.setText(Integer.toString(resultado.getQuantidadeArquivos()));
		txtTamanho.setText(humamSize(resultado.getTamanhoArquivos()));
		txtDuracao.setText(DateTime.getDurationStringFromSeconds(resultado.getDuracao()));

		//Ação de excluir
		btnAction.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				long total = 0;
				for(Arquivo s : toDelete){
					total += s.getSize();
				}
				Dialogos.pergunta(getContext(), "Excluir arquivos selecionados, " + humamSize(total) + "?", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						excluirAll();
					}
				}, null);
			}
		});

		ordem = resultado.getSortedHashs();
		exts.clear();

		long max = 0, maxInt = 0;
		for(String key : ordem){
			Arquivo first = resultado.get(key).get(0);

			if(max < first.getSize()){
				maxInt = (int) (first.getSize() / 1024);
				max = first.getSize();
			}
			if(exts.indexOf(first.extencao) == -1){
				exts.add(first.extencao);
			}
		}

		//Lista de Extenções
		Collections.sort(exts);

		ArrayAdapter<String> ad = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, exts);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner cmbEstensoes = (Spinner) view.findViewById(R.id.cmb_estensoes);
		cmbEstensoes.setAdapter(ad);
		cmbEstensoes.setOnItemSelectedListener(this);
		//Set First Item
		if(exts.size() != 0){
			int i = exts.size() > 1 ? 1 : 0;
			cmbEstensoes.setSelection(i);
			extensao = exts.get(i);
		}

		attData();
	}

	private void attData(){
		toDelete.clear();
		box.removeAllViews();

		int quantidade = 0;
		long totalToFree = 0;
		for(String key : ordem){
			ArrayList<Arquivo> arquivos = resultado.get(key);
			if(arquivos == null){
				continue;
			}
			Arquivo first = arquivos.get(0);
			if((extensao != null && !first.extencao.equals(extensao))){//|| first.getSize() < minSize || first.getSize() > maxSize) {
				continue;
			}
			quantidade += arquivos.size() - 1;
			totalToFree += first.getSize() * (arquivos.size() - 1);

			View newView = layoutInflater.inflate(R.layout.item_card_resultado_md5, null, false);
			LinearLayout cardView = (LinearLayout) newView.findViewById(R.id.box_arquivos);
			TextView l1 = (TextView) newView.findViewById(R.id.txt_nome_arquivo);
			TextView l2 = (TextView) newView.findViewById(R.id.txt_quantidade);
			TextView l3 = (TextView) newView.findViewById(R.id.txt_tamanho);
			l1.setText(first.nome);
			l2.setText(humamSize(first.getSize()));
			l3.setText(humamSize(first.getSize() * arquivos.size()));

			boolean flag = false;
			ObjectCheckBox<Arquivo> checkbox;
			for(Arquivo f : arquivos){
				checkbox = new ObjectCheckBox<>(getContext());
				checkbox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				checkbox.setText(f.getFullName());
				checkbox.setOnClickListener(this);
				checkbox.object = f;
				cardView.addView(checkbox);
				if(flag){
					checkbox.setChecked(true);
					f.setToExclude(true);
					toDelete.add(f);
				}
				flag = true;
			}

			box.addView(newView);
		}

		txtDuplicatas.setText(humamSize(totalToFree));
		txtTamanhoDuplicatas.setText(Integer.toString(quantidade));
		popUp();
	}

	private void excluirAll(){
		for(Arquivo s : toDelete){
			new File(s.getFullName()).delete();
			resultado.removeFile(s);
		}
		attData();
	}

	private void popUp(){
		txtAnim.clearAnimation();
		txtAnim.setScaleX(0);
		txtAnim.setScaleY(0);
		txtAnim.setText(Integer.toString(toDelete.size()));
		btnAction.animate().setDuration(200).scaleX(0).scaleY(0).start();
		txtAnim.animate().setDuration(300).scaleX(1.2f).scaleY(1.2f).withEndAction(new Runnable(){
			@Override
			public void run(){
				if(toDelete.size() > 0)
					btnAction.animate().setDuration(200).scaleX(1).scaleY(1).setStartDelay(400).start();
				txtAnim.animate().setDuration(300).scaleX(0).scaleY(0).translationY(0).setStartDelay(300).start();
			}
		});
	}

	@Override
	public void onClick(View v){
		ObjectCheckBox<Arquivo> c = (ObjectCheckBox<Arquivo>) v;
		if(c.isChecked()){
			toDelete.add(c.object);
		}else{
			toDelete.remove(c.object);
		}
		popUp();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
		extensao = exts.get(position);
		attData();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent){

	}
}
