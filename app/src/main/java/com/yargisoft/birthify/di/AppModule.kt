package com.yargisoft.birthify.di

import android.app.Application
import android.content.Context
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
}