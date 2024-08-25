package com.yargisoft.birthify

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel


class GuestSwipeToDeleteCallback(
    private val adapter: BirthdayAdapter,
    private val context: Context,
    private val guestsBirthdayViewModel: GuestBirthdayViewModel,
    private val lifeCycleOwner: LifecycleOwner,
    private val deleteLottieAnimationView: LottieAnimationView,
    private val findNavController: NavController,
    private val fragmentView: View,
    private val birthdayList: List<Birthday>,
    private val action: Int
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (position >= 0 || position < adapter.itemCount) {

            GuestFrequentlyUsedFunctions.showDeleteDialogBirthdayAdapter(
                position,
                fragmentView,
                context,
                birthdayList,
                deleteLottieAnimationView,
                guestsBirthdayViewModel,
                lifeCycleOwner,
                findNavController,
                action,
                navOptions {  }
            )

            // Swipe işlemini sıfırla
            adapter.notifyItemChanged(position)

        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val paint = Paint()
            paint.color = Color.RED
            paint.isAntiAlias = true

            val background = RectF(
                itemView.right + dX ,
                itemView.top.toFloat() ,
                itemView.right.toFloat() ,
                itemView.bottom.toFloat()
            )

            // Background sadece card'ın genişliği kadar olacak şekilde ayarlanır
            if (dX < -itemView.width) {
                background.left = itemView.left.toFloat()
            }

            val cornerRadius = 40f  // Border radius değerini burada ayarlayabilirsiniz
            c.drawRoundRect(background, cornerRadius, cornerRadius, paint)

            val deleteIcon = ContextCompat.getDrawable(adapter.context, R.drawable.ic_delete)!!

            val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
            val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
            val iconBottom = iconTop + deleteIcon.intrinsicHeight
            val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
            val iconRight = itemView.right - iconMargin

            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            deleteIcon.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}
