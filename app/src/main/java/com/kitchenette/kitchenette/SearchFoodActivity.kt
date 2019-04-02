package com.kitchenette.kitchenette

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.*
import android.widget.*
import com.kitchenette.search.AutoCompleteFoodAdapter
import kotlinx.android.synthetic.main.activity_search_food.*
import kotlinx.android.synthetic.main.app_bar_search_food.*
import kotlinx.android.synthetic.main.list_item.view.*

class SearchFoodActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val list : ArrayList<Food> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_food)
        setSupportActionBar(toolbar)

        /**************************** TAB ACTIVITY ***********************************/
        supportActionBar?.setDisplayShowTitleEnabled(false)

        spinner.adapter = MyAdapter(
            toolbar.context,
            arrayOf("All","Baking & Grains","Beans & Legumes","Beverages",
                "Broths & Soups","Condiments & Sauces","Dairy","Dairy Alternatives",
                "Desserts & Snacks","Fruit","Meat & Poultry","Nuts & Seeds","Oils","Seafood & Fish",
                "Spices, Herbs & Seasonings","Sweeteners","Vegetables")
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        /**************************** FLOATING BUTTON ***********************************/

        fab.setOnClickListener {
            val intent = Intent(this@SearchFoodActivity, AddFoodActivity::class.java)
            startActivity(intent)
        }

        /**************************** NAV DRAWER ***********************************/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /**************************** SEARCH METHODS ***********************************/

        addFoodItems()

        val adapter = AutoCompleteFoodAdapter(this, list)
        autocompletetextview?.threshold=1
        autocompletetextview?.setAdapter(adapter)
        autocompletetextview?.setOnFocusChangeListener {
                _, _ ->
            autocompletetextview.setOnItemClickListener { _, _, _, _ ->
                val db = DataBaseHandler(this)
                val message = db.findFoodName(autocompletetextview.text.toString()).toString()
                val intent = Intent(this@SearchFoodActivity, FoodItemActivity::class.java)
                intent.putExtra("food", message)
                this.startActivity(intent)
            }
        }

    }

    /**************************** STANDARD METHODS ***********************************/
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

    /**************************** NAV DRAWER METHODS ***********************************/
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@SearchFoodActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@SearchFoodActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@SearchFoodActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@SearchFoodActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
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

    /****************************ADD FOOD ITEMS TO LIST METHODS***********************************/
    private fun addFoodItems() {
        val context = this
        val db = DataBaseHandler(context)

        val data = db.readFoodData()

        for(i in 0..(data.size-1)){
            list.add(data[i])
        }
    }

    /**************************** ADAPTER CLASS ***********************************/
    private class MyAdapter(context: Context, objects: Array<String>) :
        ArrayAdapter<String>(context, R.layout.list_item, objects), ThemedSpinnerAdapter {
        private val dropDownHelper: ThemedSpinnerAdapter.Helper = ThemedSpinnerAdapter.Helper(context)

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View

            view = if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                val inflater = dropDownHelper.dropDownViewInflater
                inflater.inflate(R.layout.list_item, parent, false)
            } else {
                convertView
            }

            view.text1.text = getItem(position)

            return view
        }

        override fun getDropDownViewTheme(): Resources.Theme? {
            return dropDownHelper.dropDownViewTheme
        }

        override fun setDropDownViewTheme(theme: Resources.Theme?) {
            dropDownHelper.dropDownViewTheme = theme
        }
    }

    /**************************** FRAGMENT CLASS ***********************************/

    class PlaceholderFragment : Fragment() {

        val list : ArrayList<String> = ArrayList()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.content_search_food, container, false)
            val foodItem = rootView.findViewById(R.id.foodItem) as RecyclerView
            val foodMessage1 = rootView.findViewById(R.id.all_food_message1) as TextView
            val foodMessage2 = rootView.findViewById(R.id.all_food_message2) as TextView
            val foodMessage3 = rootView.findViewById(R.id.all_food_message3) as TextView
            val foodMessage4 = rootView.findViewById(R.id.all_food_message4) as TextView
            foodItem.layoutManager = LinearLayoutManager(activity)
            foodItem.setHasFixedSize(true)

            val categoryArray = arrayOf("All","Baking & Grains","Beans & Legumes","Beverages",
                "Broths & Soups","Condiments & Sauces","Dairy","Dairy Alternatives",
                "Desserts & Snacks","Fruit","Meat & Poultry","Nuts & Seeds","Oils","Seafood & Fish",
                "Spices, Herbs & Seasonings","Sweeteners","Vegetables")

            if(arguments?.getInt(SearchFoodActivity.PlaceholderFragment.ARG_SECTION_NUMBER)!=1) {
                val position = (arguments?.getInt(SearchFoodActivity.PlaceholderFragment.ARG_SECTION_NUMBER)!! -1)
                val category = categoryArray[position]
                addCategoryFoodItems(category)
                foodMessage1.visibility = LinearLayout.GONE
                foodMessage2.visibility = LinearLayout.GONE
                foodMessage3.visibility = LinearLayout.GONE
                foodMessage4.visibility = LinearLayout.GONE
            }

            foodItem.adapter = FoodAdapter(list, activity!!.applicationContext)

            var message:String?
            foodItem.addOnItemTouchListener(
                RecyclerItemClickListener(
                    activity!!.applicationContext,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            message = list[position]
                            val intent = Intent(activity!!.applicationContext, FoodItemActivity::class.java)
                            intent.putExtra("food", message)
                            startActivity(intent)
                        }
                    })
            )

            return rootView
        }

        /************************ ADD FOOD ITEMS TO LIST METHODS *******************************/

        private fun addCategoryFoodItems(cat:String) {
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)

            val data = db.readFoodCategory(cat)

            for(i in 0..(data.size-1)){
                list.add(data[i].id.toString())
            }
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
