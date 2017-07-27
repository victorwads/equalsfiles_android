package br.com.victorwads.equalsfiles.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import br.com.victorwads.equalsfiles.Main;
import br.com.victorwads.equalsfiles.R;

public class MenuApp extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

	public final static class Fragments{
		private final static Fragment
			fragmentComparacao = new FragmentComparacao(),
			fragmentResultados = new FragmentResultados(),
			fragmentResultado = new FragmentResultado(),
			fragmentPerfils = new FragmentPerfils(),
			fragmentIndexacao = new FragmentIndexacao(),
			fragmentHistorico = new FragmentHistorico();

		public final static Fragment getFragment(int id){
			if(id == R.layout.fragment_resultado)
				return fragmentResultado;
			else if(id == R.id.menuComparar){
				return fragmentComparacao;
			}else if(id == R.id.menuResultados){
				return fragmentResultados;
			}else if(id == R.id.menuPerfils){
				return fragmentPerfils;
			}else if(id == R.id.menuIndexar){
				return fragmentIndexacao;
			}else if(id == R.id.menuHistorico){
				return fragmentHistorico;
			}else{
				return null;
			}
		}
	}

	private final ArrayList<Integer> backHistory = new ArrayList<>();
	private final String BUNDLE_FRAGMENT_HISTORY = "fh";
	private NavigationView navigationView;

	private void initComponents(){
		setContentView(R.layout.activity_menu);

		//Tolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//Navigation
		navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		//Navigation select First Item
		int lastFragment;
		if(backHistory.size() > 0){
			lastFragment = backHistory.get(backHistory.size() - 1);
		}else{
			MenuItem first = navigationView.getMenu().getItem(0);
			while(first.hasSubMenu()) first = ((Menu) first.getSubMenu()).getItem(0);
			lastFragment = first.getItemId();
		}
		changeFragment(lastFragment, null);
	}

	private void loadData(Bundle dados){
		if(dados == null){
			dados = new Bundle();
			getIntent().putExtras(dados);
		}
		if(dados.containsKey(BUNDLE_FRAGMENT_HISTORY)){
			int[] fs = dados.getIntArray(BUNDLE_FRAGMENT_HISTORY);
			for(int f : fs){
				backHistory.add(f);
			}
		}
	}

	public void changeFragment(int id, Bundle extras){
		Fragment fragment = Fragments.getFragment(id);
		if(fragment == null)
			return;
		if(extras != null)
			fragment.setArguments(extras);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		ft.replace(R.id.main_frame, fragment);
		ft.commit();
		backHistory.remove(new Integer(id));
		backHistory.add(id);
		if(navigationView.getMenu().findItem(id) != null){
			navigationView.setCheckedItem(id);
			setTitle(navigationView.getMenu().findItem(id).getTitle());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Main.USERHOME = getFilesDir();
		loadData(savedInstanceState);
		initComponents();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);

		int i = 0;
		int[] ids = new int[backHistory.size()];
		for(Integer integer : backHistory){
			ids[i++] = integer;
		}
		outState.putIntArray(BUNDLE_FRAGMENT_HISTORY, ids);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if(fragments != null)
			for(Fragment fragment : fragments)
				if(fragment != null)
					fragment.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed(){
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if(drawer.isDrawerOpen(GravityCompat.START)){
			drawer.closeDrawer(GravityCompat.START);
		}else{
			if(backHistory.size() == 1)
				super.onBackPressed();
			else{
				int id = backHistory.get(backHistory.size() - 2);
				changeFragment(id, null);
				backHistory.remove(new Integer(id));
			}
		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu){
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item){
//		int id = item.getItemId();
//		if(id == R.id.action_settings){
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item){
		changeFragment(item.getItemId(), null);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

}
