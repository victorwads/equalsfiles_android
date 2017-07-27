package br.com.victorwads.equalsfiles.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.victorwads.equalsfiles.R;
import br.com.victorwads.equalsfiles.controller.Resultados;
import br.com.victorwads.equalsfiles.view.services.ComparacaoService;
import br.com.victorwads.equalsfiles.view.util.ProcessAdapter;
import br.com.victorwads.equalsfiles.view.util.ResultadoAdapter;

import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.*;

/**
 * Created by victo on 07/07/2017.
 */

public class FragmentResultados extends Fragment{

	private View view;
	private BroadcastReceiver broadcastReceiver;
	private ProcessAdapter adapter_running = new ProcessAdapter();
	private TextView processosTilte, resultadosTitle;
	private RecyclerView recycler_saved, recycler_running ;

	public FragmentResultados(){
		setArguments(new Bundle());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.fragment_resultados, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
		this.view = view;
		initComponents();

		broadcastReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent){
				updateViewData(intent.getExtras());
			}
		};
	}

	@Override
	public void onStart(){
		super.onStart();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(ComparacaoService.COMPARACAO_CHANGE));
	}

	@Override
	public void onStop(){
		super.onStop();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
	}

	private void attView(){
		ResultadoAdapter adapter_saved = new ResultadoAdapter(Resultados.listar(null, null));
		recycler_saved.setAdapter(adapter_saved);
		resultadosTitle.setText(adapter_saved.getItemCount() == 0 ? getString(R.string.title_no_data) : getString(R.string.title_saved));
		processosTilte.setVisibility(adapter_running.getItemCount() == 0 ? View.GONE : View.VISIBLE);
	}

	private void initComponents(){
		if(getContext() == null) return;
		resultadosTitle = (TextView) view.findViewById(R.id.title_saveds);
		processosTilte = (TextView) view.findViewById(R.id.title_processando);
		recycler_saved = (RecyclerView) view.findViewById(R.id.recycler_saved);
		recycler_running = (RecyclerView) view.findViewById(R.id.recycler_running);

		LinearLayoutManager manager_saved = new LinearLayoutManager(getContext());
		recycler_saved.setLayoutManager(manager_saved);

		LinearLayoutManager manager_running = new LinearLayoutManager(getContext());
		recycler_running.setLayoutManager(manager_running);
		recycler_running.setAdapter(adapter_running);
		adapter_running.setOnChangeListenner(new Runnable(){
			@Override
			public void run(){
				attView();
			}
		});
		attView();
	}

	private void updateViewData(Bundle extras){
		processosTilte.setVisibility(adapter_running.getItemCount() == 0 ? View.GONE : View.VISIBLE);
		final ProcessAdapter.Card card = adapter_running.getCard(extras.getInt(INT_INSTANCE_ID));
		if(card == null){
			return;
		}
		adapter_running.populateCard(card, extras);
	}

}
