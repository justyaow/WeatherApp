package com.example.weatherapp.view.adapter

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.utils.MyDatabase
import com.example.weatherapp.R

class MyAdapter(private val context: Context, private val itemList: MutableList<String>, private val onItemClick: (Int) -> Unit, private val onItemLongClick: (Int) -> Unit) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    init {
        val dbInfo = MyDatabase(context, "weather", null, 1)
        val infoR: SQLiteDatabase = dbInfo.readableDatabase
        val cursor = infoR.rawQuery("select * from cityData", null)
        if (cursor.moveToFirst()) {
            do {
                val content: String = cursor.getString(cursor.getColumnIndexOrThrow("cityInfo"))
                addItem(content)
            } while(cursor.moveToNext())
        }
        dbInfo.close()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.itemTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = itemList[position]
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun addItem(item: String) {
        Log.d("aaa", item)
        itemList.add(item)
        Log.d("aaa", "itemList size: ${itemList.size}")
        notifyItemInserted(itemList.size - 1)
    }

    fun removeItem(pos: Int) {
        itemList.removeAt(pos)
        notifyItemRemoved(pos)
    }
}