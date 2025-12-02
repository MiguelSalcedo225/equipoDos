package com.example.inventorywidget.di

import android.content.Context
import android.content.SharedPreferences
import com.example.inventorywidget.data.preferences.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    private const val PREF_NAME = "inventory_session"

    @Provides
    @Singleton
    fun provideSessionManager(
        prefs: SharedPreferences
    ): SessionManager {
        return SessionManager(prefs)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}