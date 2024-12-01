package com.yargisoft.birthify.di.modules

import android.content.Context
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    @Provides
    fun provideTextInputLayout(@ApplicationContext context: Context): TextInputLayout {
        return TextInputLayout(context)
    }
    @Provides
    fun provideTextInputEditText(@ApplicationContext context: Context): TextInputEditText {
        return TextInputEditText(context)
    }
}