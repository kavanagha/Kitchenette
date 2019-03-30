package com.kitchenette.kitchenette

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_ingredient.view.*


class AddIngredientAdapter(private val items : ArrayList<String>, val qty: ArrayList<Double>,
                           val msr : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<AddIngredientViewHolder>() {


    override fun onBindViewHolder(p0: AddIngredientViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddIngredientViewHolder {
        return AddIngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_ingredient,
            parent, false))
    }

    override fun onBindViewHolder(holder: AddIngredientViewHolder, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)

        val food : Food? = db.findFood(items[position].toInt())

        holder.foodName.text = food?.name
        holder.quantity.text = qty[position].toString()
        holder.measure.text = msr[position]

        db.close()
    }
}

class AddIngredientViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val foodName = view.tv_food!!
    val quantity = view.tv_quantity!!
    val measure = view.tv_measure!!
}