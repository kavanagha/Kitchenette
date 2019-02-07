package com.kithcenette.kitchenette_v2

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_barcode_history.*
import kotlinx.android.synthetic.main.app_bar_barcode_history.*
import kotlinx.android.synthetic.main.content_barcode_history.*

class BarcodeHistoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val list : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_history)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val context = this
        var db = DataBaseHandler(context)

        var data = db.readBarcodeData()
        view_barcode.text=""
        for(i in 0..(data.size-1)){
            view_barcode.append(data[i].id.toString() + " " + data[i].barcode.toString() +  "\n")
        }
    }

    //////////// NAV DRAWER METHODS ///////////////////
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.barcode_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@BarcodeHistoryActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {

            }
            R.id.nav_shopping -> {

            }
            R.id.nav_favourite -> {

            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@BarcodeHistoryActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //////////// VIEW BARCODE HISTORY METHODS ////////////////////////
    fun addBarcodeItems()
    {
        val context = this
        var db = DataBaseHandler(context)

        var data = db.readBarcodeData()

        for(i in 0..(data.size-1)){
            list.add(data[i].id.toString() + " " + data[i].barcode.toString() + "\n")
        }
    }
}
