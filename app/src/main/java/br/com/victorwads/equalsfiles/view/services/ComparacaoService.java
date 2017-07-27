package br.com.victorwads.equalsfiles.view.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Date;

import br.com.victorwads.equalsfiles.controller.Processa;
import br.com.victorwads.equalsfiles.controller.ProcessaInterface;
import br.com.victorwads.equalsfiles.controller.Resultados;
import br.com.victorwads.equalsfiles.model.Resultado;

import static br.com.victorwads.equalsfiles.view.services.ComparacaoService.FIELDS.*;

/**
 * Created by victo on 08/07/2017.
 */

public class ComparacaoService extends Service{

	private LocalBroadcastManager broadcast;
	private long time = 0;
	public static final String COMPARACAO_CHANGE = "br.com.victorwads.equalsfiles.comparacao_action";

	@Nullable
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}

	@Override
	public void onCreate(){
		super.onCreate();
		broadcast = LocalBroadcastManager.getInstance(this);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		int ret = super.onStartCommand(intent, flags, startId);
		if(intent == null) return ret;

		new Instancia(intent);
		return ret;
	}

	public static int getInstanciaSize(){
		return INSTANCIAS.size();
	}

	public static Instancia getInstancia(int i){
		return INSTANCIAS.get(i);
	}

	public final static class FIELDS{
		public static final String
			INT_INSTANCE_ID = "id",
			INT_DURATION = "d",
			INT_MAX = "m",
			INT_PROGRESS = "p",
			INT_FILES = "f",
			INT_DUPLICATES = "e",
			LONG_FILES_SIZE = "fs",
			LONG_DUPLICATES_SIZE = "es",
			BOOLEAN_INDETERMINATE = "i",
			BOOLEAN_FINISED = "F",
			STRING_ARRAY_DIRS = "D",
			STRING_PROGRESS = "ps";
		private int id;
	}

	private static final ArrayList<Instancia> INSTANCIAS = new ArrayList<>();
	private static int instacias = 0;

	public class Instancia implements ProcessaInterface{

		private final int id = instacias++;
		private final Intent intent;
		private final Resultado resultado;
		private final Processa processo;
		private final Thread thread;
		private boolean indeterminate, finised = false;
		private long duplicates_size, files_size;
		private int max, progress, duplicates, duration, files;
		private String str_progress;
		private boolean saved = false;

		public Instancia(Intent intent){
			INSTANCIAS.add(this);
			this.intent = intent;
			this.intent.setAction(COMPARACAO_CHANGE);

			resultado = new Resultado();
			resultado.setDiretorios(intent.getStringArrayExtra(STRING_ARRAY_DIRS));
			resultado.setData(new Date());
			processo = new Processa(this, resultado);
			thread = new Thread(processo);
			thread.start();
		}

		public Bundle getExtras(){
			Bundle cache = new Bundle();
			cache.putInt(INT_INSTANCE_ID, id);
			cache.putInt(INT_MAX, max);
			cache.putInt(INT_PROGRESS, progress);
			cache.putInt(INT_DUPLICATES, duplicates);
			cache.putInt(INT_DURATION, duration);
			cache.putInt(INT_FILES, files);
			cache.putLong(LONG_DUPLICATES_SIZE, duplicates_size);
			cache.putLong(LONG_FILES_SIZE, files_size);
			cache.putString(STRING_PROGRESS, str_progress);
			cache.putBoolean(BOOLEAN_INDETERMINATE, indeterminate);
			cache.putBoolean(BOOLEAN_FINISED, finised);
			return cache;
		}

		public void save(){
			if(saved) return;
			saved = true;
			Resultados.inserir(resultado);
		}

		public void close(){
			processo.stop();
			INSTANCIAS.remove(this);
		}

		private void send(){
			send(false);
		}

		private void send(boolean force){
			try{
				long newtime = 0;
				if(!force && (newtime = new Date().getTime()) - time < 100) return;
				time = newtime;
				intent.putExtra(INT_INSTANCE_ID, id);
				broadcast.sendBroadcast(intent);
				intent.putExtras(new Bundle());
			}catch(Exception e){
				e.printStackTrace(System.err);
			}
		}

		@Override
		public void clear(){
		}

		@Override
		public void setLoadingTotal(int i){
			intent.putExtra(INT_MAX, i);
			max = i;
		}

		@Override
		public void loading(int i){
			intent.putExtra(INT_PROGRESS, i);
			progress = i;
		}

		@Override
		public void loading(String path, int i){
			loading(i);
			intent.putExtra(STRING_PROGRESS, path);
			str_progress = path;
			send();
		}

		@Override
		public void loading(String info, boolean infinita){
			try{
				intent
					.putExtra(STRING_PROGRESS, info)
					.putExtra(BOOLEAN_INDETERMINATE, infinita);
			}catch(Exception e){
			}
			str_progress = info;
			indeterminate = infinita;
			send();
		}

		@Override
		public void setDuplicates(long tamanho, int quantidade){
			intent
				.putExtra(LONG_DUPLICATES_SIZE, tamanho)
				.putExtra(INT_DUPLICATES, quantidade);
			duplicates = quantidade;
			duplicates_size = tamanho;
		}

		@Override
		public void setFilesAmount(int quantidade){
			intent.putExtra(INT_FILES, quantidade);
			files = quantidade;
		}

		@Override
		public void setFilesSize(long tamanho){
			intent.putExtra(LONG_FILES_SIZE, tamanho);
			files_size = tamanho;
		}

		@Override
		public void setDuration(int segundos){
			intent.putExtra(INT_DURATION, segundos);
			duration = segundos;
			send();
		}

		@Override
		public void finish(){
			finised = true;
			intent.putExtra(BOOLEAN_FINISED, true);
			send(true);
			stopService(intent);
		}

		public int getId(){
			return id;
		}

		public boolean isSaved(){
			return saved;
		}
	}
}
