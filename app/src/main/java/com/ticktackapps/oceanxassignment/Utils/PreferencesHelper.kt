package com.ticktackapps.oceanxassignment.Utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PreferencesHelper {

    fun addOrder(context: Context, order : OrderData){

        val currentList = getOrderList(context)

        currentList.add(order)

        saveOrderList(context, currentList)

    }

    fun removeOrder(context: Context, order: OrderData){

        val currentList = getOrderList(context)

        currentList.remove(order)

        saveOrderList(context, currentList)

    }

    fun saveOrderList(context: Context, orderList : ArrayList<OrderData>){

        val prefs : SharedPreferences = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)

        val editor = prefs.edit()

        val gson = Gson()

        val json = gson.toJson(orderList)

        editor.putString(CREATED_ORDERS_DATA, json)

        editor.apply()

    }

    fun getOrderList(context: Context) : ArrayList<OrderData>{

        val prefs : SharedPreferences = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(CREATED_ORDERS_DATA, null) ?: return ArrayList()

        val type = object : TypeToken<ArrayList<OrderData>>() {}.type

        return gson.fromJson(json, type) ?: ArrayList()

    }

}