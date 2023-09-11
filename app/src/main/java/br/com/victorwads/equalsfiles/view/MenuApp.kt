package br.com.victorwads.equalsfiles.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem

import java.util.ArrayList

import br.com.victorwads.equalsfiles.Main
import br.com.victorwads.equalsfiles.R

class MenuApp : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    class HoldFragments {
        private val fragmentComparacao = FragmentComparacao()
        private val fragmentResultados = FragmentResultados()
        private val fragmentResultado = FragmentResultado()
        private val fragmentPerfils = FragmentPerfils()
        private val fragmentIndexacao = FragmentIndexacao()
        private val fragmentHistorico = FragmentHistorico()

        fun getFragment(id: Int): Fragment? {
            return when (id) {
                R.layout.fragment_resultado -> fragmentResultado
                R.id.menuComparar -> fragmentComparacao
                R.id.menuResultados -> fragmentResultados
                R.id.menuPerfils -> fragmentPerfils
                R.id.menuIndexar -> fragmentIndexacao
                R.id.menuHistorico -> fragmentHistorico
                else -> null
            }
        }
    }

    private lateinit var holdFragments: HoldFragments
    private val backHistory = ArrayList<Int>()
    private var navigationView: NavigationView? = null

    companion object {
        private const val BUNDLE_FRAGMENT_HISTORY = "fh"
    }

    private fun initComponents() {
        setContentView(R.layout.activity_menu)

        //Tolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Navigation
        navigationView = findViewById(R.id.nav_view)
        navigationView!!.setNavigationItemSelectedListener(this)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        //Navigation select First Item
        val lastFragment: Int
        if (backHistory.size > 0) {
            lastFragment = backHistory[backHistory.size - 1]
        } else {
            var first = navigationView!!.menu.getItem(0)
            while (first.hasSubMenu()) first = first.subMenu?.getItem(0) ?: break
            lastFragment = first.itemId
        }
        changeFragment(lastFragment, null)
    }

    private fun loadData(dados: Bundle?) {
        var data = dados
        if (data == null) {
            data = Bundle()
            intent.putExtras(data)
        }
        if (data.containsKey(BUNDLE_FRAGMENT_HISTORY)) {
            val fs = data.getIntArray(BUNDLE_FRAGMENT_HISTORY)
            for (f in fs!!) {
                backHistory.add(f)
            }
        }
    }

    fun changeFragment(id: Int, extras: Bundle?) {
        val fragment = holdFragments.getFragment(id) ?: return
        if (extras != null)
            fragment.arguments = extras
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        ft.replace(R.id.main_frame, fragment)
        ft.commit()
        backHistory.remove(id)
        backHistory.add(id)
        if (navigationView!!.menu.findItem(id) != null) {
            navigationView!!.setCheckedItem(id)
            title = navigationView!!.menu.findItem(id).title
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        holdFragments = HoldFragments()
        Main.USERHOME = filesDir
        loadData(savedInstanceState)
        initComponents()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var i = 0
        val ids = IntArray(backHistory.size)
        for (integer in backHistory) {
            ids[i++] = integer
        }
        outState.putIntArray(BUNDLE_FRAGMENT_HISTORY, ids)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments)
            fragment?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (backHistory.size == 1)
                super.onBackPressed()
            else {
                val id = backHistory[backHistory.size - 2]
                changeFragment(id, null)
                backHistory.remove(id)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        changeFragment(item.itemId, null)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}
