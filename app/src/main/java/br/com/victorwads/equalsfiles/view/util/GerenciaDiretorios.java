/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.victorwads.equalsfiles.view.util;

// <editor-fold defaultstate="collapsed" desc="Imports">

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.victorwads.equalsfiles.R;
import br.com.victorwads.equalsfiles.model.Diretorio;
// </editor-fold>

/**
 * @author victo
 */
public class GerenciaDiretorios{

	// <editor-fold defaultstate="collapsed" desc="Classes">
	private class DiretorioFilho extends Diretorio{

		public DiretorioFilho(String patch){
			super(patch);
		}

		public DiretorioFilho(Diretorio d){
			super(d.toString());
		}

		@Override
		public boolean equals(Object obj){
			if(this == obj){
				return true;
			}else if(obj == null || getClass() != obj.getClass()){
				return false;
			}
			final DiretorioFilho other = (DiretorioFilho) obj;
			return toString().indexOf(other.toString()) == 0 || other.toString().indexOf(this.toString()) == 0;
		}

		@Override
		public int hashCode(){
			int hash = 3;
			return hash;
		}
	}
	// </editor-fold>

	private Context context;
	private ListView view;
	private final ArrayAdapter adapter;
	private final ArrayList<DiretorioFilho> DIRETORIOS = new ArrayList();

	/**
	 * @param context
	 * @param view
	 */
	public GerenciaDiretorios(Context context, ListView view){
		this.context = context;
		this.view = view;
		adapter = new ArrayAdapter(context, R.layout.item_simple_list, DIRETORIOS);
		view.setAdapter(adapter);
		view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, final int position, long id){
				final Diretorio item = DIRETORIOS.get(position);
				view.animate().setDuration(500).translationX(view.getWidth()).withEndAction(new Runnable(){
					@Override
					public void run(){
						DIRETORIOS.remove(item);
						reloadView();
						view.setTranslationX(0);
					}
				});
			}
		});
	}

	public void addAll(String[] all){
		for(String diretorio : all){
			DIRETORIOS.add(new DiretorioFilho(diretorio));
		}
		adapter.notifyDataSetChanged();
	}

	public void addAll(Diretorio[] all){
		for(Diretorio diretorio : all){
			DIRETORIOS.add(new DiretorioFilho(diretorio));
		}
		adapter.notifyDataSetChanged();
	}

	private void reloadView(){
		adapter.notifyDataSetChanged();
	}

	// <editor-fold defaultstate="collapsed" desc="Methodos">
//	public void userAddDiretorio(){
//		Diretorio a = Dialogos.selecionarPasta(context);
//		if(a != null){
//			addDiretorio(a);
//		}
//	}

	public boolean addDiretorio(Diretorio diretorio){
		DiretorioFilho caminho = new DiretorioFilho(diretorio);
		if(DIRETORIOS.indexOf(caminho) != -1){
			Dialogos.erro(context, context.getString(R.string.msg_duplicate_dir));
			return false;
		}
		DIRETORIOS.add(caminho);
		reloadView();
		return true;
	}

//	public void removeDiretorio(){
//		if(listDiretorios.getSelectedIndices().length > 0){
//			DiretorioFilho[] selecteds = new DiretorioFilho[listDiretorios.getSelectedIndices().length];
//			int c = 0;
//			for(int i : listDiretorios.getSelectedIndices()){
//				selecteds[c] = (DiretorioFilho) DIRETORIOS.getElementAt(i);
//				c++;
//			}
//			for(DiretorioFilho d : selecteds){
//				DIRETORIOS.removeElement(d);
//			}
//			listDiretorios.ensureIndexIsVisible(0);
//			btnRemover.setEnabled(false);
//		}
//	}

	public void clear(){
		DIRETORIOS.clear();
		reloadView();
	}

	public Diretorio[] getDiretorios(){
		Diretorio[] diretorios = new Diretorio[DIRETORIOS.size()];
		for(int i = 0; i < DIRETORIOS.size(); i++){
			diretorios[i] = (Diretorio) DIRETORIOS.get(i);
		}
		return diretorios;
	}

	public int size(){
		return DIRETORIOS.size();
	}
	// </editor-fold>

}
