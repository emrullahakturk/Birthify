package com.yargisoft.birthify.data.di.modules

import androidx.recyclerview.widget.ItemTouchHelper
import com.yargisoft.birthify.utils.helpers.UserSwipeToDeleteCallback
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