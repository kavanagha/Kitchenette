package com.kithcenette.kitchenette_v2

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_cupboard.*
import kotlinx.android.synthetic.main.app_bar_cupboard.*
import kotlinx.android.synthetic.main.content_cupboard.*

class CupboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val list : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cupboard)
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

        var message:String?

        addFoodItems()
        foodItem.layoutManager = LinearLayoutManager(this)
        foodItem.adapter = FoodAdapter(list, this)
        foodItem.addOnItemTouchListener(
            RecyclerItemClickListener(
                this@CupboardActivity,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        message = list[position]
                        val intent = Intent(this@CupboardActivity, FoodItemActivity::class.java)
                        intent.putExtra("food", message)
                        startActivity(intent)
                    }
                })
        )
    }

    //////////////////// GENERAL METHODS /////////////////////////
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.cupboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //////////////////// NAVIGATION DRAWER /////////////////////////
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@CupboardActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {

            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@CupboardActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@CupboardActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@CupboardActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //////////////////// RECYLER VIEW METHODS /////////////////////////////
    fun addFoodItems(){
        val context = this
        var db = DataBaseHandler(context)

        var data = db.readFoodCupboard()

        for(i in 0..(data.size-1)){
            list.add(data[i].id.toString())
        }
    }




}
