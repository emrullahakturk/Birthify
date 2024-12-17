package com.yargisoft.birthify.data.di.modules

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
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
    fun provideNavController(@ActivityContext context: Context): NavController {
        return NavController(context)
    }

    @Provides
    fun provideLifecycleOwner(@ActivityContext context: Context): LifecycleOwner {
        return context as LifecycleOwner
    }

}