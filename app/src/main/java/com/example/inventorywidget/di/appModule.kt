package com.example.inventorywidget.di

import android.content.Context
import com.example.inventorywidget.data.preferences.SessionManager
import com.example.inventorywidget.data.preferences.WidgetPreferences
import com.example.inventorywidget.repository.AuthRepository
import com.example.inventorywidget.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        sessionManager: SessionManager
    ): AuthRepository {
        return AuthRepository(firebaseAuth, sessionManager)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        firestore: FirebaseFirestore,
        authRepository: AuthRepository
    ): ProductRepository {
        return ProductRepository(firestore, authRepository)
    }

    @Provides
    @Singleton
    fun provideWidgetPreferences(
        @ApplicationContext context: Context
    ): WidgetPreferences {
        return WidgetPreferences(context)
    }
}