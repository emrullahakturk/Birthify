package com.yargisoft.birthify.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ApplicationContext'i sağlamak için
    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    // SharedPreferences provide
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }


    @Provides
    fun provideNavController(@ActivityContext context: Context): NavController {
        return NavController(context)
    }

    @Provides
    fun provideLifecycleOwner(@ActivityContext context: Context): LifecycleOwner {
        return context as LifecycleOwner
    }

}