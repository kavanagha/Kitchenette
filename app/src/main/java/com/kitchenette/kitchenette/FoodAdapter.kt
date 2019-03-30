package com.kitchenette.kitchenette

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_food.view.*


class FoodAdapter(private val items : ArrayList<String>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {


    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_food, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)

        val food : Food? = db.findFood(items[position].toInt())

        holder.tvFoodItem.text = food?.name
        if(food?.photo!= null){
            val bitmap: Bitmap? = food?.photo
            holder.image.setImageBitmap(bitmap)
        }
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_food!!
    val image = view.image_food_icon!!
}
