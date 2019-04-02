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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_shopping.*
import kotlinx.android.synthetic.main.app_bar_shopping_list.*
import android.R.attr.country
import android.widget.Toast
import kotlinx.android.synthetic.main.content_barcode_history.*


class ShoppingListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var sectionsPagerAdapter: ShoppingListActivity.SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)
        setSupportActionBar(toolbar)

        /**************************** TAB ACTIVITY ***********************************/
        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = sectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        /**************************** NAVIGATION DRAWER ***********************************/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    /**************************** NAVIGATION DRAWER METHODS ***********************************/

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
                val menuIntent = Intent(this@ShoppingListActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@ShoppingListActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@ShoppingListActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@ShoppingListActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@ShoppingListActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    /**************************** TAB SECTIONS CLASS ***********************************/
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int { return 2 }
    }

    /**************************** FRAGMENT CLASS ***********************************/
    class PlaceholderFragment : Fragment() {

        private var list : ArrayList<String> = ArrayList()
        private var categoryList : Array<String>  = arrayOf("All","Baking & Grains",
            "Beans & Legumes","Beverages", "Broths & Soups","Condiments & Sauces",
            "Dairy","Dairy Alternatives", "Desserts & Snacks","Fruit","Meat & Poultry",
            "Nuts & Seeds","Oils","Seafood & Fish", "Spices, Herbs & Seasonings","Sweeteners","Vegetables")
        private var selected: String? = ""

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return if(arguments?.getInt(ARG_SECTION_NUMBER)==1) {
                inflater.inflate(R.layout.fragment_shopping_list, container, false)
            } else{
                inflater.inflate(R.layout.fragment_bought_list, container, false)
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            if(arguments?.getInt(ARG_SECTION_NUMBER)==1) {
                list.clear()
                val foodItem = view.findViewById(R.id.foodItem) as RecyclerView
                val spinner = view.findViewById(R.id.spinner_shop) as Spinner

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, arg1: View, position: Int, id: Long
                    ) {
                        selected = categoryList[position]
                        if (selected != null) {
                            if(selected.equals("All")){
                                addShoppingItems()
                            }
                            else{
                                addShoppingCategoryItems()
                            }
                            foodItem.layoutManager = LinearLayoutManager(activity)
                            foodItem.adapter = ShoppingAdapter(list, activity!!.applicationContext)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) { }
                }

                val aa = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_item, categoryList)
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = aa
            }
            else{
                list.clear()
                val foodItem = view.findViewById(R.id.foodItem) as RecyclerView
                val spinner = view.findViewById(R.id.spinner_bought) as Spinner

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, arg1: View, position: Int, id: Long
                    ) {
                        selected = categoryList[position]
                        if (selected != null) {
                            if(selected.equals("All")){
                                addBoughtItems()
                            }
                            else{
                                addBoughtCategoryItems()
                            }
                            foodItem.layoutManager = LinearLayoutManager(activity)
                            foodItem.adapter = BoughtAdapter(list, activity!!.applicationContext)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) { }
                }

                val aa = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_item, categoryList)
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = aa
            }
        }

        private fun addBoughtCategoryItems(){
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data : MutableList<Food>
            data = db.readBoughtCategory(selected!!)
            list.clear()

            for(i in 0..(data.size-1))
                list.add(data[i].id.toString())
        }

        private fun addBoughtItems(){
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data : MutableList<Food>
            data = db.readBought()
            list.clear()

            for(i in 0..(data.size-1))
                list.add(data[i].id.toString())
        }

        private fun addShoppingCategoryItems() {
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data: MutableList<Food>
            data = db.readShoppingCategory(selected!!)
            list.clear()

            for (i in 0..(data.size - 1))
                list.add(data[i].id.toString())
        }

        private fun addShoppingItems() {
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data: MutableList<Food>
            data = db.readShopping()
            list.clear()

            for (i in 0..(data.size - 1))
                list.add(data[i].id.toString())
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
