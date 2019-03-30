package com.kitchenette.search

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kitchenette.kitchenette.DataBaseHandler
import com.kitchenette.kitchenette.Food
import com.kitchenette.kitchenette.R

import java.util.*

class AutoCompleteFoodAdapter(private val context: Activity, private var foodItems : ArrayList<Food>)
    : ArrayAdapter<Food>(context, R.layout.list_item_food, foodItems) {


    private var resultList: MutableList<Food> = ArrayList()

    override fun getCount(): Int {
        return resultList.size
    }
    override fun getItem(index: Int): Food {
        return resultList[index]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        if (view == null) {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_item_food, parent, false)
        }

        val db = DataBaseHandler(context)

        val food : Food? = db.findFood(resultList[position].id)

        val tvFood = view!!.findViewById<View>(R.id.tv_food) as TextView
        val image = view.findViewById<View>(R.id.image_food_icon) as ImageView
        tvFood.text = food?.name
        if(food?.photo!=null){
            val bitmap: Bitmap? = food.photo
            image.setImageBitmap(bitmap)
        }

        return view
    }

    override fun getFilter() = filter

    private var filter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = FilterResults()

            val query = if (constraint != null && constraint.isNotEmpty()) autocomplete(constraint.toString())
            else arrayListOf()

            results.values = query
            results.count = query.size

            return results
        }

        private fun autocomplete(input: String): ArrayList<Food> {
            val results = arrayListOf<Food>()

            for (food in foodItems) {
                if (food.name.toLowerCase().contains(input.toLowerCase())) results.add(food)
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
            resultList = results.values as ArrayList<Food>
            notifyDataSetInvalidated()
        }

        override fun convertResultToString(result: Any) = (result as Food).name
    }
}