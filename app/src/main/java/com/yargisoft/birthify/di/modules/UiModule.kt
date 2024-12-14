package com.yargisoft.birthify.di.modules

import androidx.recyclerview.widget.ItemTouchHelper
import com.yargisoft.birthify.UserSwipeToDeleteCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UiModule {


    @Provides
    fun provideItemTouchHelper(userSwipeToDeleteCallback: UserSwipeToDeleteCallback): ItemTouchHelper {
        return ItemTouchHelper(userSwipeToDeleteCallback)
    }
}