package com.kitchenette.kitchenette

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_ingredient_edit.view.*


class AddIngredientAdapter(private val items : ArrayList<String>, private val qty: ArrayList<Double>,
                           private val msr : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<AddIngredientViewHolder>() {

    val adapter = this

    override fun onBindViewHolder(p0: AddIngredientViewHolder, p1: Int) {    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddIngredientViewHolder {
        return AddIngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_ingredient_edit,
            parent, false))
    }

    override fun onBindViewHolder(holder: AddIngredientViewHolder, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)

        val food : Food? = db.findFood(items[position].toInt())

        holder.foodName.text = food?.name
        holder.quantity.text = qty[position].toString()
        holder.measure.text = msr[position]

        holder.remove.setOnClickListener {
            items.remove(items[position])
            qty.remove(qty[position])
            msr.remove(msr[position])
            adapter.notifyDataSetChanged()
        }
        db.close()
    }
}

class AddIngredientViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val foodName = view.tv_food!!
    val quantity = view.tv_quantity!!
    val measure = view.tv_measure!!
    val remove = view.removeList!!
}