package com.yargisoft.birthify.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    fun provideGeneralPref(context:Context):SharedPreferences{
        return context.getSharedPreferences("general_preferences",Context.MODE_PRIVATE)
    }

}