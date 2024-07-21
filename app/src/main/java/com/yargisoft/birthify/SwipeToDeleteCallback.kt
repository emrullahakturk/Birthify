package com.yargisoft.birthify

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.models.Birthday

class SwipeToDeleteCallback(
    private val adapter: BirthdayAdapter,
    private val birthdayList: List<Birthday>,
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

            adapter.updateData(birthdayList)
            adapter.showDeleteDialog(position)

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
                itemView.right + dX + 100,
                itemView.top.toFloat() ,
                itemView.right.toFloat()+ 100 ,
                itemView.bottom.toFloat()
            )

            /*// Background sadece card'ın genişliği kadar olacak şekilde ayarlanır
            if (dX < -itemView.width) {
                background.right = itemView.left.toFloat()
            }*/

            val cornerRadius = 100f  // Border radius değerini burada ayarlayabilirsiniz
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
