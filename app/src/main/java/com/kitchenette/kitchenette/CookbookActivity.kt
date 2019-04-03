package com.kitchenette.kitchenette

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.AutoCompleteTextView
import com.kitchenette.search.AutoCompleteFoodAdapter
import kotlinx.android.synthetic.main.activity_cookbook.*
import kotlinx.android.synthetic.main.app_bar_cookbook.*

class CookbookActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mSectionsPagerAdapter: CookbookActivity.SectionsPagerAdapter? = null
    private val list : ArrayList<Recipe> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cookbook)
        setSupportActionBar(toolbar)

        /************************ TAB  ACTIVITY ************************/
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        /******************FLOATING ACTION BUTTON *************************/
        fab.setOnClickListener { view ->
            val menuIntent = Intent(this@CookbookActivity, AddRecipeActivity::class.java)
            startActivity(menuIntent)
        }

        /************************ NAVIGATION DRAWER *************************/
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /**************************** SEARCH METHODS ***********************************/
        addRecipeItems()
        val autocompletetextview = findViewById<AutoCompleteTextView>(R.id.autocompletetextview)
        val adapter = AutoCompleteRecipeAdapter(this, list)
        autocompletetextview?.threshold=1
        autocompletetextview?.setAdapter(adapter)
        autocompletetextview?.setOnFocusChangeListener {
                _, _ ->
            autocompletetextview.setOnItemClickListener { _, _, _, _ ->
                val db = DataBaseHandler(this)
                val message = db.findRecipeName(autocompletetextview.text.toString()).toString()
                val intent = Intent(this@CookbookActivity, RecipeItemActivity::class.java)
                intent.putExtra("recipe", message)
                this.startActivity(intent)
            }
        }

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
                val menuIntent = Intent(this@CookbookActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@CookbookActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@CookbookActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@CookbookActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@CookbookActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /************************ SEARCH METHODS ***************************/
    private fun addRecipeItems(){
        val context = this
        val db = DataBaseHandler(context)

        val data = db.readRecipeData()

        for(i in 0..(data.size-1))
            list.add(data[i])
    }

    /**************** TAB ACTIVITY METHODS *****************/
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int { return 2 }
    }

    class PlaceholderFragment : Fragment() {

        private val list : ArrayList<String> = ArrayList()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.content_cookbook, container, false)
            val recipeItem = rootView.findViewById(R.id.recipeItem) as RecyclerView
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data : MutableList<Recipe>

            data = if(arguments?.getInt(ARG_SECTION_NUMBER)==1) {
                db.suggestRecipes()
            } else{
                db.readRecipeData()
            }

            for(i in 0..(data.size-1))
                list.add(data[i].id.toString())

            recipeItem.layoutManager = LinearLayoutManager(activity)
            recipeItem.adapter = RecipeAdapter(list, activity!!.applicationContext)

            var message:String?
            recipeItem.addOnItemTouchListener(
                RecyclerItemClickListener(
                    activity!!.applicationContext,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            message = list[position]
                            val intent = Intent(activity!!.applicationContext, RecipeItemActivity::class.java)
                            intent.putExtra("recipe", message)
                            startActivity(intent)
                        }
                    })
            )

            return rootView
        }

        companion object {

            private const val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }

        }
    }
}
