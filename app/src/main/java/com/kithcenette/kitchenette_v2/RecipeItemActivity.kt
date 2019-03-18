package com.kithcenette.kitchenette_v2

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_recipe_item.*
import kotlinx.android.synthetic.main.app_bar_recipe_item.*
import kotlinx.android.synthetic.main.content_recipe_item.*
import java.nio.file.Files.size


class RecipeItemActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_item)
        setSupportActionBar(toolbar)

        /***************FLOATING ACTION BUTTONS ******************/

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Make This", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        /*********************** NAVIGATION ***********************/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /**************  FILL PAGE *******************************/
        val context = this
        val db = DataBaseHandler(context)
        val id: String = intent.getStringExtra("recipe")
        val recipe : Recipe? = db.findRecipe(id.toInt())

        recipeName.text = recipe?.name
        serving.text = recipe?.servings.toString()
        cuisine.text = recipe?.cuisine
        description.text = recipe?.description
        method.text = recipe?.method

        val bitmap: Bitmap? = recipe?.photo
        image.setImageBitmap(bitmap)

        /************************ TAB  ACTIVITY ***********************

        val viewPager = findViewById<ViewPager>(R.id.pager)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        val mTabLayout = findViewById<TabLayout>(R.id.pager_header)
        mTabLayout.setupWithViewPager(viewPager)*/


    }

    /**************** NAVIGATION METHODS *****************/
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.settings_menu, menu)
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
                val menuIntent = Intent(this@RecipeItemActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@RecipeItemActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@RecipeItemActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@RecipeItemActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@RecipeItemActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    /**************** TAB ACTIVITY METHODS *****************/

    /*

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        private val PAGE_COUNT = 2

        //private val PAGE_TITLES : ArrayList<String>? = null
        private val PAGE_TITLES = arrayOf("Ingredients", "Method")
        private val FRAGMENT_LIST : ArrayList<Fragment>? = null

        override fun getCount(): Int {
            return PAGE_COUNT
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return PAGE_TITLES[position]
        }

        fun addFrag(fragment: Fragment) {
            FRAGMENT_LIST?.add(fragment)
         //   PAGE_TITLES?.add(title)
        }

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> Fragment1()
                1 -> Fragment2()
                else -> null
            }
        }
    }


    class Fragment1 : Fragment() {

        private val list : ArrayList<String> = ArrayList()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.method_view, pager, true)
            val recipeItem = rootView.findViewById(R.id.method) as TextView
            recipeItem.text = "HEY"

            return rootView
        }
    }

    class Fragment2 : Fragment() {

        private val list : ArrayList<String> = ArrayList()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.content_recipe_item, pager, false)
           // val recipeItem = rootView.findViewById(R.id.recipeItem) as RecyclerView



            return rootView
        }
    }

    */
}
