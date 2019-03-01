package com.kithcenette.kitchenette_v2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kotlinx.android.synthetic.main.shopping_list_item.view.*


class ShoppingAdapter(private val items : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<ViewHolderShop>() {

    val adapter = this

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
        holder?.buttonCheck.setOnClickListener{
            db.removeFoodShopping(items[position].toInt())
            db.addFoodBought(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }
        holder?.buttonRemoveList.setOnClickListener{
            db.removeFoodShopping(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }
    }
}

class ViewHolderShop (private val view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_foodShop!!
    val buttonCheck: ImageButton = view.shopCheck!!
    val buttonRemoveList: ImageButton = view.removeList

}
