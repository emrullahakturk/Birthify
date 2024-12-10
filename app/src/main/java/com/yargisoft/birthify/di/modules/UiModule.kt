package com.yargisoft.birthify.di.modules

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.lottie.LottieAnimationView
import com.yargisoft.birthify.UserSwipeToDeleteCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UiModule {


    @Provides
    fun provideLottieAnimationView(@ApplicationContext context: Context): LottieAnimationView {
        return LottieAnimationView(context)
    }

    @Provides
    fun provideItemTouchHelpes(userSwipeToDeleteCallback: UserSwipeToDeleteCallback): ItemTouchHelper {
        return ItemTouchHelper(userSwipeToDeleteCallback)
    }
}