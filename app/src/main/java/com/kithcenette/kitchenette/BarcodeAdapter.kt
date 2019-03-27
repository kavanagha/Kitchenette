package com.kithcenette.kitchenette

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kithcenette.search.AutoCompleteFoodAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.content_barcode_history.view.*
import kotlinx.android.synthetic.main.list_item_barcode.view.*

class BarcodeAdapter(private val items : ArrayList<String>, val context: Context, val activity: Activity)
    : RecyclerView.Adapter<BarcodeHolder>(), AdapterView.OnItemSelectedListener {

    private var list = arrayOf("cup","dessertspoon","fl. oz",
        "grams","kg","litres","ml","oz","pint","tbsp","tsp", "whole")
    private var s : String? = null
    private var foodList : ArrayList<Food> = ArrayList()


    override fun onBindViewHolder(p0: BarcodeHolder, p1: Int) {    }

    override fun getItemCount(): Int {
        return items.size
    }

    /***************************** SPINNER METHODS **************************************/
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        s = list[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeHolder {
        return BarcodeHolder(LayoutInflater.from(context).inflate(
            R.layout.list_item_barcode,
            parent, false))
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: BarcodeHolder, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)
        val id = items[position].toInt()
        val barcode = db.findBarcode(id)

        if (barcode?.foodID != null){
            val food  = db.findFood(barcode?.foodID!!)
            holder.name.text = food?.name
            holder.qty.text = barcode.quantity.toString()
            holder.msr.text = barcode.measurement
            holder.brand.text = barcode.brand
            val bitmap: Bitmap? = food?.photo
            holder.image.setImageBitmap(bitmap)
            holder.save.visibility = View.GONE
            holder.save.isClickable = false
            holder.add.setOnClickListener {
                addFood(holder,barcode.foodID!!,barcode.quantity, barcode.measurement, food?.name!!)
            }
        }
        else{
            holder.name.text = barcode?.barcode
            holder.qty.text=""
            holder.msr.text=""
            holder.brand.text=""
            holder.add.visibility = View.GONE
            holder.add.isClickable = false
            holder.save.setOnClickListener {
                savePopup(holder, barcode!!, items[position].toInt())
            }
        }
        db.close()
    }

    private fun savePopup(holder: BarcodeHolder, barcode : Barcodes, bId : Int){
        val db = DataBaseHandler(context)
        val window = PopupWindow(context)
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.popup_save_barcode,null)

        window.isFocusable = true
        window.isOutsideTouchable = true
        window.update()

        window.contentView = view

        val barcodeLabel =  view.findViewById<TextView>(R.id.tv_barcode)
        barcodeLabel.text = barcode.barcode

        val spinner = view.findViewById<Spinner>(R.id.tv_measure)

        spinner!!.onItemSelectedListener = this
        val aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = aa

        addFoodItems()
        val foodLabel = view.tv_food
        var id : Int? = null
        val autocompletetextview = view.findViewById<AutoCompleteTextView>(R.id.autocompletetextview)
        val adapter = AutoCompleteFoodAdapter(activity, foodList)
        autocompletetextview?.threshold=1
        autocompletetextview?.setAdapter(adapter)
        autocompletetextview?.setOnFocusChangeListener {
                _, _ ->
            autocompletetextview.setOnItemClickListener { _, _, _, _ ->
                id = db.findFoodName(autocompletetextview.text.toString())
                foodLabel.text = autocompletetextview.text.toString()
                autocompletetextview.text.clear()
            }
        }

        val enterBrand = view.findViewById<EditText>(R.id.brand)
        val  enterQty = view.findViewById<EditText>(R.id.tv_qty)

        val save = view.findViewById<ImageButton>(R.id.save)
        save.setOnClickListener{
            if(id != null && enterQty.text.toString().isNotEmpty() &&
                enterBrand.text.toString().isNotEmpty() && s!!.isNotEmpty())
                db.updateBarcode(bId,id!!,enterQty.text.toString().toDouble(),
                    s!!, enterBrand.text.toString())
            else
                Toast.makeText(context, "Please Fill Out All details", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
            window.dismiss()
        }

        val close  = view.findViewById<ImageButton>(R.id.no_save)
        close.setOnClickListener {
            window.dismiss()
        }

        db.close()
        window.showAtLocation(holder.layout, Gravity.CENTER,0,0)
    }

    private fun addFood(holder:BarcodeHolder, fId:Int,  qty:Double, msr:String, fName:String){
        val db = DataBaseHandler(context)
        val window = PopupWindow(context)
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.popup_add_barcode_cupboard,null)

        window.isFocusable = true
        window.isOutsideTouchable = true
        window.update()

        window.contentView = view

        val foodLabel = view.findViewById<TextView>(R.id.tv_food)
        foodLabel.text = fName
        val qtyLabel = view.findViewById<TextView>(R.id.tv_qty)
        qtyLabel.text = qty.toString()
        val msrLabel = view.findViewById<TextView>(R.id.tv_msr)
        msrLabel.text = msr

        val add = view.findViewById<ImageButton>(R.id.save)
        add.setOnClickListener {
            db.addFoodQuantity(fId,qty,msr)
            db.addFoodCupboard(fId)
            db.removeFoodBought(fId)
            db.removeFoodShopping(fId)
            window.dismiss()
        }

        val close  = view.findViewById<ImageButton>(R.id.no_save)
        close.setOnClickListener {
            window.dismiss()
        }

        db.close()
        window.showAtLocation(holder.layout, Gravity.CENTER,0,0)
    }


    private fun addFoodItems(){
        val db = DataBaseHandler(context)
        val data = db.readFoodData()

        for(i in 0..(data.size-1)){
            foodList.add(data[i])
        }
    }

}

class BarcodeHolder (view: View) : RecyclerView.ViewHolder(view) {
    var name = view.tv_food!!
    var qty = view.tvQuantity!!
    var msr = view.tvMeasurement!!
    var image = view.image_food_icon!!
    var add = view.add!!
    var save = view.save!!
    var brand = view.brand!!
    var layout = view.root_layout!!
}
