package br.com.victorwads.equalsfiles.view.util;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.victorwads.equalsfiles.R;
import br.com.victorwads.equalsfiles.model.Resultado;
import br.com.victorwads.equalsfiles.view.MenuApp;

import static br.com.victorwads.equalsfiles.controller.CacheMD5.humamSize;

/**
 * Created by victo on 09/07/2017.
 */

public class ResultadoAdapter extends ProcessAdapter{

	private final Resultado[] resultados;

	public ResultadoAdapter(Resultado[] resultados){
		this.resultados = resultados;
	}

	@Override
	public int getItemCount(){
		return resultados.length;
	}

	@Override
	public Card onCreateViewHolder(ViewGroup viewGroup, int i){
		return new Card(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_resultado, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(ProcessAdapter.Card card, int i){
		Resultado r = resultados[i];
		((Card) card).setId(r.getId());
		card.txtDuplicatas.setText(new DateTime(r.getData()).toString());
		card.txtDuracao.setText(DateTime.getDurationStringFromSeconds(r.getDuracao()));
		card.txtArquivos.setText(Integer.toString(r.getQuantidadeArquivos()));
		card.txtTamanho.setText(humamSize(r.getTamanhoArquivos()));
	}

	public final static String BUNLDE_ID = "bdid";
	public class Card extends ProcessAdapter.Card{
		private int id;

		public Card(final View itemView){
			super(itemView);
			btnFechar.setOnClickListener(null);
			btnAction.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					MenuApp act = (MenuApp) itemView.getContext();
					Bundle extras = new Bundle();
					extras.putInt(BUNLDE_ID,id);
					act.changeFragment(R.layout.fragment_resultado, extras);
				}
			});
		}

		public void setId(int id){
			this.id = id;
		}
	}

}
