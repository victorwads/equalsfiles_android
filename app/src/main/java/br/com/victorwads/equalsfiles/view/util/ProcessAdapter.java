package br.com.victorwads.equalsfiles.view.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.victorwads.equalsfiles.R;
import br.com.victorwads.equalsfiles.view.services.ComparacaoService;

import static br.com.victorwads.equalsfiles.controller.CacheMD5.humamSize;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.BOOLEAN_FINISED;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.BOOLEAN_INDETERMINATE;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.INT_DUPLICATES;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.INT_DURATION;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.INT_FILES;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.INT_MAX;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.INT_PROGRESS;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.LONG_DUPLICATES_SIZE;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.LONG_FILES_SIZE;
import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.STRING_PROGRESS;

/**
 * Created by victo on 09/07/2017.
 */

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.Card>{

	private ArrayList<Card> cards = new ArrayList<>();
	private Runnable onChangeListenner = null;

	@Override
	public void onViewDetachedFromWindow(Card holder){
		super.onViewDetachedFromWindow(holder);
		cards.remove(holder);
	}

	@Override
	public Card onCreateViewHolder(ViewGroup viewGroup, int i){
		return new Card(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_processo, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(Card runningCard, int i){
		runningCard.setInstancia(ComparacaoService.getInstancia(i));
		populateCard(runningCard, runningCard.getInstancia().getExtras());
		for(Card card : cards){
			if(card != runningCard && card.getInstancia() != null && card.getInstancia().getId() == runningCard.getInstancia().getId()){
				cards.add(cards.indexOf(card), runningCard);
				return;
			}
		}
		cards.add(runningCard);
	}

	@Override
	public int getItemCount(){
		return ComparacaoService.getInstanciaSize();
	}

	public void populateCard(Card runningCard, Bundle data){
		for(String s : data.keySet()){
			if(s == null) continue;
			if(s.equals(STRING_PROGRESS))
				runningCard.lblProgresso.setText(data.getString(STRING_PROGRESS));

			else if(s.equals(INT_PROGRESS)){
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
					runningCard.barProgresso.setProgress(data.getInt(INT_PROGRESS), true);
				}else{
					runningCard.barProgresso.setProgress(data.getInt(INT_PROGRESS));
				}
			}else if(s.equals(INT_DURATION))
				runningCard.txtDuracao.setText(DateTime.getDurationStringFromSeconds(data.getInt(INT_DURATION)));
			else if(s.equals(INT_DUPLICATES))
				runningCard.txtDuplicatas.setText(Integer.toString(data.getInt(INT_DUPLICATES)));
			else if(s.equals(INT_FILES))
				runningCard.txtArquivos.setText(Integer.toString(data.getInt(INT_FILES)));
			else if(s.equals(INT_MAX))
				runningCard.barProgresso.setMax(data.getInt(INT_MAX));

			else if(s.equals(LONG_DUPLICATES_SIZE))
				runningCard.txtTamanhoDuplicatas.setText(humamSize(data.getLong(LONG_DUPLICATES_SIZE)));
			else if(s.equals(LONG_FILES_SIZE))
				runningCard.txtTamanho.setText(humamSize(data.getLong(LONG_FILES_SIZE)));

			else if(s.equals(BOOLEAN_INDETERMINATE))
				runningCard.barProgresso.setIndeterminate(data.getBoolean(BOOLEAN_INDETERMINATE));
			else if(s.equals(BOOLEAN_FINISED) && data.getBoolean(BOOLEAN_FINISED))
				runningCard.finish();
		}
		runningCard.init();
	}

	public Card getCard(int id){
		for(Card card : cards){
			if(card.getInstancia().getId() == id)
				return card;
		}
		notifyItemInserted(cards.size() - 1);
		return null;
	}

	public void setOnChangeListenner(Runnable onChangeListenner){
		this.onChangeListenner = onChangeListenner;
	}

	public class Card extends RecyclerView.ViewHolder{

		public final ProgressBar barProgresso;
		public final TextView lblProgresso, txtArquivos, txtDuplicatas, txtDuracao, txtTamanho, txtTamanhoDuplicatas;
		public final FloatingActionButton btnFechar, btnAction;
		private final CardView card;
		private boolean first_create = true;
		private ComparacaoService.Instancia instancia;

		public Card(View itemView){
			super(itemView);

			card = (CardView) itemView.findViewById(R.id.cv);
			barProgresso = (ProgressBar) itemView.findViewById(R.id.bar_progresso);
			lblProgresso = (TextView) itemView.findViewById(R.id.lbl_progresso);
			txtArquivos = (TextView) itemView.findViewById(R.id.txt_arquivo);
			txtTamanho = (TextView) itemView.findViewById(R.id.txt_tamanho);
			txtDuplicatas = (TextView) itemView.findViewById(R.id.txt_duplicatas);
			txtTamanhoDuplicatas = (TextView) itemView.findViewById(R.id.txt_duplicatas_tamanho);
			txtDuracao = (TextView) itemView.findViewById(R.id.txt_duracao);

			btnAction = (FloatingActionButton) itemView.findViewById(R.id.btn_action);
			btnAction.setOnClickListener(new View.OnClickListener(){
				boolean cliked = false;

				@Override
				public void onClick(View v){
					if(cliked) return;
					cliked = true;
					save();
				}
			});

			btnFechar = (FloatingActionButton) itemView.findViewById(R.id.btn_fechar);
			btnFechar.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view){
					Context context = view.getContext();
					Dialogos.pergunta(context, context.getString(R.string.msg_stop_process), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which){
							close();
						}
					}, null);
				}
			});

		}

		public ComparacaoService.Instancia getInstancia(){
			return instancia;
		}

		public void setInstancia(ComparacaoService.Instancia instancia){
			this.instancia = instancia;
		}

		private void close(){
			//btnFechar.animate().setDuration(600).translationX(-card.getWidth()).scaleX(0).scaleY(0).start();
			card.animate().setDuration(600).translationX(card.getWidth()).withEndAction(new Runnable(){
				@Override
				public void run(){
					instancia.close();
					ProcessAdapter.this.notifyItemRemoved(getAdapterPosition());
					if(onChangeListenner != null) onChangeListenner.run();
				}
			});
		}

		private void save(){
			instancia.save();
			close();
		}

		public void init(){
			first_create = false;
		}

		public void finish(){
			if(first_create){
				barProgresso.setScaleY(0);
				barProgresso.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
				btnAction.setVisibility(View.VISIBLE);
			}else{
				barProgresso.animate().setDuration(300).scaleY(0).withEndAction(new Runnable(){
					@Override
					public void run(){
						barProgresso.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
						btnAction.setTranslationX(btnFechar.getWidth());
						btnAction.setScaleX(0);
						btnAction.setScaleY(0);
						btnAction.setAlpha(0f);
						btnAction.setVisibility(View.VISIBLE);
						btnAction.animate().setDuration(300).scaleX(1).scaleY(1).alpha(1).translationX(0).start();
					}
				});
			}
		}
	}
}
