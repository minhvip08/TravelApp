package com.example.travelapp.data.di

import android.content.Context
import android.content.SharedPreferences
import com.example.travelapp.ui.util.SharedPrefConstants



object AppModule {

    fun provideSharedPref( context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
    }


}