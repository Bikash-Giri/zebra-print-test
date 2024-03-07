package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveData(key: String, value: String) {
        editor.putString(key, value)
        editor.apply() // Or editor.commit() if immediate save is required
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}
