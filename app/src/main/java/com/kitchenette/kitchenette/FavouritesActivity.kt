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
import kotlinx.android.synthetic.main.activity_favourites.*
import kotlinx.android.synthetic.main.app_bar_favourites.*


class FavouritesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mSectionsPagerAdapter: FavouritesActivity.SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)
        setSupportActionBar(toolbar)

        /************************* TAB ACTIVITY *************************/
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        /************************ NAVIGATION DRAWER **********************/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    /************************ NAV DRAWER METHODS **************************/

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@FavouritesActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@FavouritesActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@FavouritesActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@FavouritesActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)

            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@FavouritesActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /*************************** TAB ACTIVITY METHODS *****************************/
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return FavouritesActivity.PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            return 2
        }
    }

    class PlaceholderFragment : Fragment() {

        private val list : ArrayList<String> = ArrayList()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.content_favourites, container, false)
            val favItem = rootView.findViewById(R.id.favItem) as RecyclerView
            var message:String?
            var intent : Intent

            if (arguments?.getInt(ARG_SECTION_NUMBER) == 1) {
                addRecipeItems()
                favItem.layoutManager = LinearLayoutManager(activity)
                favItem.adapter = RecipeAdapter(list, activity!!.applicationContext)
                favItem.addOnItemTouchListener(
                    RecyclerItemClickListener(activity!!.applicationContext,
                        object : RecyclerItemClickListener.OnItemClickListener{
                            override fun onItemClick(view: View, position: Int) {
                                message = list[position]
                                intent = Intent(activity!!.applicationContext, RecipeItemActivity::class.java)
                                intent.putExtra("recipe",message)
                                startActivity(intent)
                            }
                        })
                )
            }
            else{
                addFoodItems()
                favItem.layoutManager = LinearLayoutManager(activity)
                favItem.adapter = FoodAdapter(list, activity!!.applicationContext)
                favItem.addOnItemTouchListener(
                    RecyclerItemClickListener(activity!!.applicationContext,
                        object : RecyclerItemClickListener.OnItemClickListener {
                            override fun onItemClick(view: View, position: Int) {
                                message = list[position]
                                intent = Intent(activity!!.applicationContext, FoodItemActivity::class.java)
                                intent.putExtra("food", message)
                                startActivity(intent)
                            }
                        })
                )
            }
            return rootView
        }

        private fun addFoodItems(){
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data = db.readFoodFavourites()

            for(i in 0..(data.size-1))
                list.add(data[i].id.toString())
            db.close()
        }
        private fun addRecipeItems(){
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data = db.readRecipeFavourites()

            for(i in 0..(data.size-1))
                list.add(data[i].id.toString())
            db.close()
        }

        companion object {
            private const val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): FavouritesActivity.PlaceholderFragment {
                val fragment = FavouritesActivity.PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
