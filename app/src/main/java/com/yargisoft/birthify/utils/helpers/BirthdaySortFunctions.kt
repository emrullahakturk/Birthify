package com.yargisoft.birthify.utils.helpers

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.ui.adapters.AdapterInterface
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel

object BirthdaySortFunctions {


    // Sorting Menümüze font eklemek için kullanılan class
    class CustomTypefaceSpan(private val typeface: Typeface?) : TypefaceSpan("") {
        override fun updateDrawState(textPaint: TextPaint) {
            applyCustomTypeFace(textPaint, typeface)
        }

        override fun updateMeasureState(textPaint: TextPaint) {
            applyCustomTypeFace(textPaint, typeface)
        }

        private fun applyCustomTypeFace(paint: Paint, typeface: Typeface?) {
            paint.typeface = typeface
        }
    }

    // PastBirthday Page için sort menüsünü açma fonksiyonları
    fun showSortMenu(
        view: View,
        context: Context,
        adapter: AdapterInterface,
        birthdayViewModel: BirthdayViewModel,
        sortList: List<Birthday>
    ) {
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view)
        popupMenu.menuInflater.inflate(R.menu.sorting_birthday_menu, popupMenu.menu)

        // Menü öğelerine özel stil uygulama
        for (i in 0 until popupMenu.menu.size()) {
            val menuItem = popupMenu.menu.getItem(i)
            val spanString = SpannableString(menuItem.title)
            spanString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)),
                0,
                spanString.length,
                0
            )

            // wittgenstein_font ataması yapma
            val typeface = ResourcesCompat.getFont(context, R.font.wittgenstein_font)
            spanString.setSpan(
                CustomTypefaceSpan(typeface),
                0,
                spanString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )


            menuItem.title = spanString
        }

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            val sortedList = when (menuItem.itemId) {
                R.id.sort_by_name_asc -> birthdayViewModel.sortBirthdays(
                    "sortBirthdaysByNameAsc",
                    sortList
                )

                R.id.sort_by_birth_date_asc -> birthdayViewModel.sortBirthdays(
                    "sortBirthdaysByBirthdayDateAsc",
                    sortList
                )

                R.id.sort_by_recorded_date_asc -> birthdayViewModel.sortBirthdays(
                    "sortBirthdaysByRecordedDateAsc",
                    sortList
                )

                R.id.sort_by_name_dsc -> birthdayViewModel.sortBirthdays(
                    "sortBirthdaysByNameDsc",
                    sortList
                )

                R.id.sort_by_birth_date_dsc -> birthdayViewModel.sortBirthdays(
                    "sortBirthdaysByBirthdayDateDsc",
                    sortList
                )

                R.id.sort_by_recorded_date_dsc -> birthdayViewModel.sortBirthdays(
                    "sortBirthdaysByRecordedDateDsc",
                    sortList
                )

                else -> emptyList()
            }
            adapter.updateData(sortedList)
            true
        }
        popupMenu.show()
    }




}