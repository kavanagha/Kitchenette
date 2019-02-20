package com.kithcenette.kitchenette_v2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.shopping_list_item.view.*


class ShoppingAdapter(private val items : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<ViewHolderShop>() {


    override fun onBindViewHolder(p0: ViewHolderShop, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShop {
        return ViewHolderShop(LayoutInflater.from(context).inflate(R.layout.shopping_list_item,
            parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderShop, position: Int, payloads: MutableList<Any>) {
        var db = DataBaseHandler(context)

        var food : Food? = db.findFood(items[position].toInt())

        holder?.tvFoodItem?.text = food?.name
    }
}

class ViewHolderShop (view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_foodShop!!
}
