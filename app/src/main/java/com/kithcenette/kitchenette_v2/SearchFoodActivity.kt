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
import kotlinx.android.synthetic.main.activity_search_food.*
import kotlinx.android.synthetic.main.app_bar_search_food.*
import kotlinx.android.synthetic.main.content_search_food.*

class SearchFoodActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val list : ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_food)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val intent = Intent(this@SearchFoodActivity, AddFoodActivity::class.java)
            startActivity(intent)
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
                this@SearchFoodActivity,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        message = list[position]
                        val intent = Intent(this@SearchFoodActivity, FoodItemActivity::class.java)
                        intent.putExtra("food", message)
                        startActivity(intent)
                    }
                })
        )

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search_food, menu)
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
                val menuIntent = Intent(this@SearchFoodActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {

            }
            R.id.nav_shopping -> {

            }
            R.id.nav_favourite -> {

            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@SearchFoodActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun addFoodItems()
    {
        val context = this
        var db = DataBaseHandler(context)

        var data = db.readFoodData()

        for(i in 0..(data.size-1)){
            list.add(data[i].id.toString())
            //FoodList.add(data.get(i).id.toString() + " " + data.get(i).name + "\n")
        }
    }
}
