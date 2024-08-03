
@file:Suppress("UNUSED_EXPRESSION")

package com.yargisoft.birthify

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.adapters.PastBirthdayAdapter
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object GuestFrequentlyUsedFunctions {


    //EditText ile arama yaparken aramayı filtrelemek için kullanılan fonksiyon (TrashBin İçin)
    fun filterBirthdays(query: String, guestBirthdayViewModel: GuestBirthdayViewModel, adapter: DeletedBirthdayAdapter) {
        val birthdays = guestBirthdayViewModel.deletedBirthdayList.value
        if (birthdays != null) {
            val filteredBirthdays = if (query.isEmpty()) {
                birthdays
            } else {
                birthdays.filter { it.name.contains(query, ignoreCase = true) }
            }
            adapter.updateData(filteredBirthdays)
        }
    }
    //EditText ile arama yaparken aramayı filtrelemek için kullanılan fonksiyon (PastBirthday İçin)
    fun filterBirthdays(query: String, guestBirthdayViewModel: GuestBirthdayViewModel, adapter: PastBirthdayAdapter) {
        val birthdays = guestBirthdayViewModel.pastBirthdayList.value
        if (birthdays != null) {
            val filteredBirthdays = if (query.isEmpty()) {
                birthdays
            } else {
                birthdays.filter { it.name.contains(query, ignoreCase = true) }
            }
            adapter.updateData(filteredBirthdays)
        }
    }

    //EditText ile arama yaparken aramayı filtrelemek için kullanılan fonksiyon  (MainPage İçin)
    fun filterBirthdays(query: String, guestBirthdayViewModel:GuestBirthdayViewModel, adapter: BirthdayAdapter) {
        val birthdays = guestBirthdayViewModel.birthdayList.value
        if (birthdays != null) {
            val filteredBirthdays = if (query.isEmpty()) {
                birthdays
            } else {
                birthdays.filter { it.name.contains(query, ignoreCase = true) }
            }
            adapter.updateData(filteredBirthdays)
        }
    }

    //Ekranı tıklanabilir hale getiren ve lottie animasyonunu durduran fonksiyon
    private fun enableViewDisableLottie(lottieAnimationView: LottieAnimationView, view: View)
    {
        lottieAnimationView.cancelAnimation()
        lottieAnimationView.visibility = View.INVISIBLE
        setViewAndChildrenEnabled(view,true)
    }

    //Ekranı tıklanamaz hale getiren ve lottie animasyonunu başlatan fonksiyon
    private fun disableViewEnableLottie(lottieAnimationView:LottieAnimationView, view: View)
    {
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()
        setViewAndChildrenEnabled(view, false)
    }

    //Main Page için sort menüsünü açma fonksiyonları
    fun showSortMenu(view: View,
                     context:Context,
                     adapter: BirthdayAdapter,
                     guestBirthdayViewModel:GuestBirthdayViewModel){

        val contextThemeWrapper = ContextThemeWrapper(context, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view)
        popupMenu.menuInflater.inflate(R.menu.sorting_birthday_menu, popupMenu.menu)

        // Menü öğelerine özel stil uygulama
        for (i in 0 until popupMenu.menu.size()) {
            val menuItem = popupMenu.menu.getItem(i)
            val spanString = SpannableString(menuItem.title)
            spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_login)), 0, spanString.length, 0)
            menuItem.title = spanString
        }

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            handleSortOptionSelected(menuItem,adapter,guestBirthdayViewModel)
            true
        }
        popupMenu.show()

    }
    private fun handleSortOptionSelected(menuItem: MenuItem,
                                         adapter: BirthdayAdapter,
                                         guestBirthdayViewModel:GuestBirthdayViewModel) {

        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByNameAsc")
            R.id.sort_by_birth_date_asc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByBirthdayDateAsc")
            R.id.sort_by_recorded_date_asc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByRecordedDateAsc")
            R.id.sort_by_name_dsc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByNameDsc")
            R.id.sort_by_birth_date_dsc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByBirthdayDateDsc")
            R.id.sort_by_recorded_date_dsc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByRecordedDateDsc")
            else -> emptyList()
        }

        adapter.updateData(sortedList)
    }
    //Main Page için sort menüsünü açma fonksiyonları


    // Deleted (Trash Bin) Page için sort menüsünü açma fonksiyonları
    fun showSortMenu(view: View, context:Context, adapter: DeletedBirthdayAdapter, guestBirthdayViewModel:GuestBirthdayViewModel) {
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view)
        popupMenu.menuInflater.inflate(R.menu.sorting_birthday_menu, popupMenu.menu)

        // Menü öğelerine özel stil uygulama
        for (i in 0 until popupMenu.menu.size()) {
            val menuItem = popupMenu.menu.getItem(i)
            val spanString = SpannableString(menuItem.title)
            spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_login)), 0, spanString.length, 0)
            menuItem.title = spanString
        }

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            handleSortOptionSelected(menuItem,adapter,guestBirthdayViewModel)
            true
        }
        popupMenu.show()
    }
    private fun handleSortOptionSelected(menuItem: MenuItem, adapter: DeletedBirthdayAdapter, guestBirthdayViewModel:GuestBirthdayViewModel) {
        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByNameAsc")
            R.id.sort_by_birth_date_asc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByBirthdayDateAsc")
            R.id.sort_by_recorded_date_asc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByRecordedDateAsc")
            R.id.sort_by_name_dsc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByNameDsc")
            R.id.sort_by_birth_date_dsc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByBirthdayDateDsc")
            R.id.sort_by_recorded_date_dsc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByRecordedDateDsc")
            else -> emptyList()
        }

        adapter.updateData(sortedList)
    }
    //Deleted (Trash Bin) Page için sort menüsünü açma fonksiyonları


    // PastBirthday Page için sort menüsünü açma fonksiyonları
    fun showSortMenu(view: View, context:Context, adapter: PastBirthdayAdapter, guestBirthdayViewModel:GuestBirthdayViewModel) {
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view)
        popupMenu.menuInflater.inflate(R.menu.sorting_birthday_menu, popupMenu.menu)

        // Menü öğelerine özel stil uygulama
        for (i in 0 until popupMenu.menu.size()) {
            val menuItem = popupMenu.menu.getItem(i)
            val spanString = SpannableString(menuItem.title)
            spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_login)), 0, spanString.length, 0)
            menuItem.title = spanString
        }

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            handleSortOptionSelected(menuItem,adapter,guestBirthdayViewModel)
            true
        }
        popupMenu.show()
    }
    private fun handleSortOptionSelected(menuItem: MenuItem, adapter: PastBirthdayAdapter, guestBirthdayViewModel:GuestBirthdayViewModel) {
        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> guestBirthdayViewModel.sortBirthdaysPastBirthdays("sortBirthdaysByNameAsc")
            R.id.sort_by_birth_date_asc -> guestBirthdayViewModel.sortBirthdaysPastBirthdays("sortBirthdaysByBirthdayDateAsc")
            R.id.sort_by_recorded_date_asc -> guestBirthdayViewModel.sortBirthdaysPastBirthdays("sortBirthdaysByRecordedDateAsc")
            R.id.sort_by_name_dsc -> guestBirthdayViewModel.sortBirthdaysPastBirthdays("sortBirthdaysByNameDsc")
            R.id.sort_by_birth_date_dsc -> guestBirthdayViewModel.sortBirthdaysPastBirthdays("sortBirthdaysByBirthdayDateDsc")
            R.id.sort_by_recorded_date_dsc -> guestBirthdayViewModel.sortBirthdaysPastBirthdays("sortBirthdaysByRecordedDateDsc")
            else -> emptyList()
        }

        adapter.updateData(sortedList)
    }
    //PastBirthday Page için sort menüsünü açma fonksiyonları


    // Tarih seçme ekranı için gereken fonksiyon
    @SuppressLint("DiscouragedApi")
    fun showDatePickerDialog(context: Context, editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, _, selectedMonth, selectedDay ->
            val monthFormat = SimpleDateFormat("MMMM", Locale.US)
            val monthName = monthFormat.format(calendar.apply { set(Calendar.MONTH, selectedMonth) }.time)
            val selectedDate = "$selectedDay $monthName"
            editText.setText(selectedDate)
        }, year, month, day)

        // Yıl seçimini kapatma
        datePickerDialog.datePicker.findViewById<View>(context.resources.getIdentifier("year", "id", "android"))?.visibility = View.GONE

        datePickerDialog.show()
    }


    //parametre olarak gelen view'i devre dışı bırakmak ve tekrar aktif etmek için kullanılan fonksiyon
    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setViewAndChildrenEnabled(view.getChildAt(i), enabled)
            }
        }
    }
    //parametre olarak gelen view'i devre dışı bırakmak ve tekrar aktif etmek için kullanılan fonksiyon



    /*
        silme , yeniden kaydetme ve tamamen silme işlemlerimiz
        için kullandığımız onaylama diyaloğumu çağıran fonksiyon
    */
    fun showConfirmationDialog(view: View, context: Context, guestBirthdayViewModel:GuestBirthdayViewModel, editedBirthday:Birthday, lottieAnimationView: LottieAnimationView, viewLifecycleOwner:LifecycleOwner, condition: String, findNavController: NavController, action:Int,navOptions: NavOptions )
    {
        AlertDialog.Builder(context)
            .setTitle("Confirm Operation")
            .setMessage("Are you sure you want to do this operation?")
            .setPositiveButton("Yes") { _, _ ->

                when (condition) {
                    "permanently" -> {
                        guestBirthdayViewModel.permanentlyDeleteBirthday(editedBirthday.id)
                    }
                    "soft_delete" -> {
                        guestBirthdayViewModel.deleteBirthday(editedBirthday.id)
                    }
                    "re_save" -> {
                        guestBirthdayViewModel.reSaveDeletedBirthday(editedBirthday.id)
                    }
                }

                loadAndStateOperation(viewLifecycleOwner,guestBirthdayViewModel,lottieAnimationView,view,findNavController,action, navOptions )
            }
            .setNegativeButton("No"){_,_->
                //animasyonu durdurup view'i visible yapıyoruz
                enableViewDisableLottie(lottieAnimationView,view)
            }
            .show()
    }




    //Swipe ederek silme işlemi yaparken UserSwipeToDeleteCallback sınıfından bu fonksiyon çağrılır ve silme işlemi başlatılır
    @SuppressLint("NotifyDataSetChanged")
    fun showDeleteDialogBirthdayAdapter(position: Int, view: View, context: Context, birthdayList:List<Birthday>, lottieAnimationView: LottieAnimationView, guestBirthdayViewModel:GuestBirthdayViewModel, lifeCycleOwner: LifecycleOwner, findNavController: NavController, action: Int,navOptions: NavOptions
    ){
        Log.e("HATA","$birthdayList")

        if (birthdayList.isNotEmpty()){
            val birthday = birthdayList[position]
            showConfirmationDialog(view,context,guestBirthdayViewModel,birthday,lottieAnimationView,lifeCycleOwner,"soft_delete", findNavController , action, navOptions  )
        }
    }



    //Yaptığımız suspend işlemlerin tamamlanıp tamamlanmadığını kontrol eden fonksiyon
    // aynı zamanda arayüzü kilitleyip kullanıcının ekranda işlemler yapmasını engelliyor (disableViewEnableLottie ile)
    //isloading işlemin tamamlanıp tamamlanmadığını dönderiyor ve içerisindeki enableViewDisableLottie fonksiyonu ile arayüzü
    //kullanıcının kullanımına açıyor
    fun loadAndStateOperation(viewLifecycleOwner:LifecycleOwner, guestBirthdayViewModel:GuestBirthdayViewModel, lottieAnimationView: LottieAnimationView, view:View, findNavController: NavController, action:Int, navOptions: NavOptions)
    {
        disableViewEnableLottie(lottieAnimationView,view)
        isLoadingCheck(viewLifecycleOwner,guestBirthdayViewModel,lottieAnimationView,view, findNavController, action, navOptions)
    }


    //Auth View Model ve UsersBirthdayViewModel için isLoading Kontrolü yapan fonksiyon
    // (Suspend fonksiyonun bitip bitmediğini kontrol ediyoruz)
    private fun isLoadingCheck(viewLifecycleOwner: LifecycleOwner, viewModel:GuestBirthdayViewModel, lottieAnimationView: LottieAnimationView, view: View, findNavController: NavController?, action: Int?, navOptions: NavOptions)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Handler(Looper.getMainLooper()).postDelayed({
                        //animasyonu durdurup view'i visible yapıyoruz
                        enableViewDisableLottie(lottieAnimationView,view)
                        if (findNavController != null && action != null){
                            findNavController.navigate(action,null,navOptions)
                        }
                },1500)
            }
        }
    }


    fun drawerLayoutToggle(drawerLayout: DrawerLayout,
                                navigationView: NavigationView,
                                findNavController: NavController,
                                menuButtonToolbar:View,
                                activity: Activity,
                                guestRepository:GuestRepository,
                                userSharedPreferences:UserSharedPreferencesManager,
                                sourcePage:String
    ){

        // ActionBarDrawerToggle ile Drawer'ı ActionBar ile senkronize etme
        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // NavigationView'deki öğeler için click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Menü öğelerine tıklandığında yapılacak işlemler
            when (menuItem.itemId) {
                R.id.labelBirthdays -> {
                    when(sourcePage){
                        "GuestTrashBin"-> findNavController.navigate(R.id.guestTrashBinToMain)
                        "GuestMainPage"-> ""
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.guestDeletedDetailToMain)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.guestEditToMain)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.guestDetailToMain)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.guestAddToMain)
                        "GuestPastBirthdays"-> findNavController.navigate(R.id.guestPastToMain)
                    }
                }
                R.id.labelPastBirthdays -> {
                    when(sourcePage){
                        "GuestTrashBin"-> findNavController.navigate(R.id.guestTrashBinToPastBirthdays)
                        "GuestMainPage"-> findNavController.navigate(R.id.guestMainToPastBirthdays)
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.guestDeletedDetailToPastBirthdays)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.guestEditToPastBirthdays)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.guestDetailToPastBirthdays)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.guestAddToPastBirthdays)
                        "GuestPastBirthdays"-> ""
                    }
                }

                R.id.labelLogin -> {
                    userSharedPreferences.clearUserSession()
                    //findNavController.navigate(R.id.firstPageFragment)
                }

                R.id.labelTrashBin -> {
                    when(sourcePage){
                        "GuestMainPage"-> findNavController.navigate(R.id.guestMainToTrashBin)
                        "GuestTrashBin"-> ""
//                        "GuestSettings"-> findNavController.navigate(R.id.)
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.guestDeletedDetailToTrashBin)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.guestEditToTrashBin)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.guestDetailToTrashBin)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.guestAddToTrashBin)
                        "GuestPastBirthdays"-> findNavController.navigate(R.id.guestPastToTrashBin)

                    }
                }

                R.id.labelSettings -> {
                    when(sourcePage){
                        "GuestMainPage"-> findNavController.navigate(R.id.guestMainToSettings)
                        "GuestTrashBin"-> findNavController.navigate(R.id.guestTrashBinToSettings)
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.guestDeletedDetailToSettings)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.guestEditToSettings)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.guestDetailToSettings)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.guestAddToSettings)
                        "GuestPastBirthdays"-> findNavController.navigate(R.id.guestPastToSettings)
                        "GuestSettings"-> ""
                    }
                }


                else -> false
            }

            // Drawer'ı kapatmak için
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        // Toolbar üzerindeki menü ikonu ile menüyü açma
        menuButtonToolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


    }


}