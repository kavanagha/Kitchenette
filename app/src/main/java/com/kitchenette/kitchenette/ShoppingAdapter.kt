package com.kitchenette.kitchenette

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kotlinx.android.synthetic.main.list_item_shopping.view.*


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
        return ViewHolderShop(LayoutInflater.from(context).inflate(R.layout.list_item_shopping,
            parent, false))
    }

    private fun onClickMethod(position:Int){
        val message = items[position]
        val intent = Intent(context, FoodItemActivity::class.java)
        intent.putExtra("food", message)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun onBindViewHolder(holder: ViewHolderShop, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)

        val food : Food? = db.findFood(items[position].toInt())

        if(food?.photo!= null){
            val bitmap: Bitmap? = food.photo
            holder.image.setImageBitmap(bitmap)
        }
        holder.image.setOnClickListener {
            onClickMethod(position) }

        holder.tvFoodItem.text = food?.name
        holder.tvFoodItem.setOnClickListener {
            onClickMethod(position) }
        holder.buttonCheck.setOnClickListener{
            db.removeFoodShopping(items[position].toInt())
            db.addFoodBought(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }
        holder.buttonRemoveList.setOnClickListener{
            db.removeFoodShopping(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }
    }
}

class ViewHolderShop (view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_foodShop!!
    val buttonCheck: ImageButton = view.shopCheck!!
    val buttonRemoveList: ImageButton = view.removeList
    val image = view.image_food_icon!!
}
