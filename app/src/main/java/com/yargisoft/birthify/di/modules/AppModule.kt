package com.yargisoft.birthify.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ApplicationContext'i sağlamak için
    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideGeneralPref(context:Context): SharedPreferences {
        return context.getSharedPreferences("general_preferences",Context.MODE_PRIVATE)
    }

}