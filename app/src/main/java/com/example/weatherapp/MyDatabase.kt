package com.example.weatherapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDatabase (
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA foreign_keys = ON")
        db?.execSQL("create table if not exists cityData (cityName varchar(25) primary key, cityInfo varchar(255))")
        db?.execSQL("create table if not exists nowHistory (name varchar(25) primary key, content varchar(255))")
        db?.execSQL("create table if not exists dailyHistory1 (name varchar(25) primary key, content varchar(255))")
        db?.execSQL("create table if not exists dailyHistory2 (name varchar(25) primary key, content varchar(255))")
        db?.execSQL("create table if not exists dailyHistory3 (name varchar(25) primary key, content varchar(255))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS cityData")
        db?.execSQL("DROP TABLE IF EXISTS nowHistory")
        db?.execSQL("DROP TABLE IF EXISTS dailyHistory1")
        db?.execSQL("DROP TABLE IF EXISTS dailyHistory2")
        db?.execSQL("DROP TABLE IF EXISTS dailyHistory3")
        onCreate(db)
    }
}