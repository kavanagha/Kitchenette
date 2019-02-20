package com.kithcenette.kitchenette_v2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kotlinx.android.synthetic.main.bought_list_item.view.*


class BoughtAdapter(private val items : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<ViewHolderBought>() {

    val adapter = this

    override fun onBindViewHolder(p0: ViewHolderBought, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBought {
        return ViewHolderBought(LayoutInflater.from(context).inflate(R.layout.bought_list_item,
            parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderBought, position: Int, payloads: MutableList<Any>) {
        var db = DataBaseHandler(context)

        var food : Food? = db.findFood(items[position].toInt())

        holder?.tvFoodItem?.text = food?.name
        holder?.buttonAddShop.setOnClickListener{
            db.addFoodShopping(items[position].toInt())
            db.removeFoodBought(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }
        holder?.buttonRemoveList.setOnClickListener{
            db.removeFoodBought(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }
    }
}

class ViewHolderBought (private val view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_foodBought!!
    val buttonAddShop: ImageButton = view.addShop
    val buttonAddCupboard:ImageButton = view.addCupboard
    val buttonRemoveList: ImageButton = view.removeList
}
