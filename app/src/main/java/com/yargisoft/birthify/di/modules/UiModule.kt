package com.yargisoft.birthify.di.modules

import android.content.Context
import android.widget.TextView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UiModule {
    @Provides
    fun provideTextView(@ApplicationContext context: Context):TextView{
        return TextView(context)
    }
}