package com.example.weatherapp

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import android.database.sqlite.SQLiteDatabase

class ListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private var itemList = mutableListOf<String>()
    private var idList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(this, itemList, ::editItem, ::deleteItem)
        recyclerView.adapter = adapter

        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            getWeather()
        }
    }

    private fun getWeather() {
        val searchText: EditText = findViewById(R.id.searchText)
        val cityName: String = searchText.text.toString()
        if (cityName == "") {
            Toast.makeText(this, "请输入城市名称", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("cityName", cityName)
            intent.putExtra("ok", "y")
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    companion object {
        const val REQUEST_CODE = 100
        const val EXTRA_CITY_ADDED = "extra_city_added"
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            var cityName: String = ""
            var cityInfo: String = ""
            data?.getStringExtra(EXTRA_CITY_ADDED)?.let { city ->
                var ans: String = ""
                for (c in city) {
                    if (c == ' ') {
                        break
                    }
                    ans += c
                }
                cityName = ans
                cityInfo = city
                idList.add(ans)
                adapter.addItem(city)
            }
            val dbInfo = MyDatabase(this, "weather", null, 1)
            val infoW: SQLiteDatabase = dbInfo.writableDatabase
            infoW.execSQL("insert or replace into cityData(cityName, cityInfo) values(?, ?)", arrayOf(cityName, cityInfo))
            dbInfo.close()
        }
    }

    private fun editItem(pos: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("cityName", idList[pos])
        intent.putExtra("ok", "n")
        startActivity(intent)
    }

    private fun deleteItem(pos: Int) {
        AlertDialog.Builder(this)
            .setTitle("删除")
            .setMessage("确定要删除这项城市吗？")
            .setPositiveButton("删除") { _, _ ->
                adapter.removeItem(pos)
            }
            .setNegativeButton("取消", null)
            .show()
    }
}