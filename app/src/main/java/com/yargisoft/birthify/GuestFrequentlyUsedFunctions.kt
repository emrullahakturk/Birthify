
@file:Suppress("UNUSED_EXPRESSION")

package com.yargisoft.birthify

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.util.Patterns
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.adapters.PastBirthdayAdapter
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



object GuestFrequentlyUsedFunctions {

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
            spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, spanString.length, 0)

            // wittgenstein_font ataması yapma
            val typeface = ResourcesCompat.getFont(context, R.font.wittgenstein_font)
            spanString.setSpan(UserFrequentlyUsedFunctions.CustomTypefaceSpan(typeface), 0, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

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
                                         guestBirthdayViewModel: GuestBirthdayViewModel
    ) {

        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByNameAsc","Main")
            R.id.sort_by_birth_date_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByBirthdayDateAsc","Main")
            R.id.sort_by_recorded_date_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByRecordedDateAsc","Main")
            R.id.sort_by_name_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByNameDsc","Main")
            R.id.sort_by_birth_date_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByBirthdayDateDsc","Main")
            R.id.sort_by_recorded_date_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByRecordedDateDsc","Main")
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
            spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, spanString.length, 0)

            // wittgenstein_font ataması yapma
            val typeface = ResourcesCompat.getFont(context, R.font.wittgenstein_font)
            spanString.setSpan(UserFrequentlyUsedFunctions.CustomTypefaceSpan(typeface), 0, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)



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
            R.id.sort_by_name_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByNameAsc","TrashBin")
            R.id.sort_by_birth_date_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByBirthdayDateAsc","TrashBin")
            R.id.sort_by_recorded_date_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByRecordedDateAsc","TrashBin")
            R.id.sort_by_name_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByNameDsc","TrashBin")
            R.id.sort_by_birth_date_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByBirthdayDateDsc","TrashBin")
            R.id.sort_by_recorded_date_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByRecordedDateDsc","TrashBin")
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
            spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, spanString.length, 0)

            // wittgenstein_font ataması yapma
            val typeface = ResourcesCompat.getFont(context, R.font.wittgenstein_font)
            spanString.setSpan(UserFrequentlyUsedFunctions.CustomTypefaceSpan(typeface), 0, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


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
            R.id.sort_by_name_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByNameAsc","PastBirthdays")
            R.id.sort_by_birth_date_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByBirthdayDateAsc","PastBirthdays")
            R.id.sort_by_recorded_date_asc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByRecordedDateAsc","PastBirthdays")
            R.id.sort_by_name_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByNameDsc","PastBirthdays")
            R.id.sort_by_birth_date_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByBirthdayDateDsc","PastBirthdays")
            R.id.sort_by_recorded_date_dsc -> guestBirthdayViewModel.sortWithPage("sortBirthdaysByRecordedDateDsc","PastBirthdays")
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

                loadAndStateOperation(viewLifecycleOwner,lottieAnimationView,view,findNavController,action, navOptions )
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
    fun loadAndStateOperation(viewLifecycleOwner:LifecycleOwner, lottieAnimationView: LottieAnimationView, view:View, findNavController: NavController, action:Int, navOptions: NavOptions)
    {
        disableViewEnableLottie(lottieAnimationView,view)
        isLoadingCheck(viewLifecycleOwner,lottieAnimationView,view, findNavController, action, navOptions)
    }


    //Auth View Model ve UsersBirthdayViewModel için isLoading Kontrolü yapan fonksiyon
    // (Suspend fonksiyonun bitip bitmediğini kontrol ediyoruz)
    private fun isLoadingCheck(viewLifecycleOwner: LifecycleOwner, lottieAnimationView: LottieAnimationView, view: View, findNavController: NavController?, action: Int?, navOptions: NavOptions)
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
                    when(sourcePage){
                        "GuestTrashBin"-> findNavController.navigate(R.id.guestTrashBinToLogin)
                        "GuestMainPage"-> findNavController.navigate(R.id.guestMainToLogin)
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.guestDeletedDetailToLogin)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.guestEditToLogin)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.guestDetailToLogin)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.guestAddToLogin)
                        "GuestPastBirthdays"-> findNavController.navigate(R.id.guestPastToLogin)
                    }
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


    /*Login page içerisinde login butonuna tıkladığımızda, verilen email validasyonunu yapan,
     animasyonları gösterip devre dışı bırakan, kullanıcı etkileşimini sağlayan fonksiyon (snackbar iler)*/
    fun loginGuestValidation(
        view: View,
        email: String,
        password: String,
        authViewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner: LifecycleOwner,
        findNavController:NavController,
        context:Context,
        guestViewModel:GuestBirthdayViewModel
    ) {
        if (isValidEmail(email) && password.isNotEmpty()) {

            authViewModel.loginUser(email, password)

            disableViewEnableLottie(lottieAnimationView, view)

            Handler(Looper.getMainLooper()).postDelayed({
                viewLifecycleOwner.lifecycleScope.launch {
                    var isLoadedEmitted = false // Kontrol değişkeni

                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        authViewModel.isLoaded.collect { isLoaded ->
                            if (isLoaded && !isLoadedEmitted) {
                                isLoadedEmitted = true // Tekrar çalışmasını engellemek için işaretler

                                val isSuccess = authViewModel.authSuccess.value
                                val errorMessage = authViewModel.authError.value

                                if (isSuccess) {
                                    Snackbar.make(view, "You successfully logged in", Snackbar.LENGTH_SHORT).show()

                                    val navOptions = NavOptions.Builder()
                                        .setPopUpTo(R.id.guest_auth_nav, inclusive = true)
                                        .setPopUpTo(R.id.guest_nav, inclusive = true)
                                        .build()

                                     AlertDialog.Builder(context)
                                             .setTitle("Confirm Synchronization")
                                             .setMessage("Do you want to sync your birthdays with your new account? Otherwise your birthdays will be deleted")
                                             .setPositiveButton("Yes") { _, _ ->

                                                    saveBirthdaysToFirebase(guestViewModel.birthdayList.value)
                                                    savePastBirthdaysToFirebase(guestViewModel.pastBirthdayList.value)
                                                    saveDeletedBirthdaysToFirebase(guestViewModel.deletedBirthdayList.value)


                                                 findNavController.navigate(R.id.guestLoginToUserMainPage,null,navOptions)

                                             }
                                             .setNegativeButton("No"){_,_->
                                                 //animasyonu durdurup view'i visible yapıyoruz
                                                 enableViewDisableLottie(lottieAnimationView, view)
                                                 findNavController.navigate(R.id.guestLoginToUserMainPage,null,navOptions)
                                             }
                                             .show()

                                } else {
                                    Snackbar.make(view, errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                }

                                // View'i tekrar aktif et ve animasyonu durdur
                               enableViewDisableLottie( lottieAnimationView,view)
                            }
                        }
                    }
                }

            },1500)

        } else {
            Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
        }
    }



    fun registerGuestValidation(
        email: String,
        password: String,
        name: String,
        viewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner: LifecycleOwner,
        view: View,
        findNavController: NavController,
        action: Int,
        navOptions: NavOptions
    ) {
        if (isValidPassword(password) && isValidEmail(email) && isValidFullName(name)
        ) {

            viewModel.registerUser(name, email, password)

            disableViewEnableLottie(lottieAnimationView, view)

            Handler(Looper.getMainLooper()).postDelayed({
                viewLifecycleOwner.lifecycleScope.launch {
                    var isLoadedEmitted = false // Kontrol değişkeni

                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.isLoaded.collect { isLoaded ->
                            if (isLoaded && !isLoadedEmitted) {
                                isLoadedEmitted = true // Tekrar çalışmasını engellemek için işaretler

                                val isSuccess = viewModel.authSuccess.value
                                val errorMessage = viewModel.authError.value

                                if (isSuccess) {
                                    Snackbar.make(view, "Registration successful", Snackbar.LENGTH_SHORT).show()
                                    findNavController.navigate(action, null, navOptions)
                                } else {
                                    Snackbar.make(view, errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                }

                                enableViewDisableLottie(
                                    lottieAnimationView,
                                    view
                                )
                            }
                        }
                    }
                }
            },1500)


        } else {
            Snackbar.make(view, "Please correctly fill in all fields", Snackbar.LENGTH_SHORT).show()
        }
    }

    /*Şifre sıfırlama ekranında (Forgot Password Page) kutucuğa uazılan mailin validayion işlemlerini,
     animasyon işlemlerini vs yapan fonksiyon
     */
    fun resetPasswordGuestValidation(
        email: String,
        viewLifecycleOwner: LifecycleOwner,
        viewModel: AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        view: View,
        findNavController: NavController,
        action: Int,
        navOptions: NavOptions
    ) {
        if (isValidEmail(email)) {

            viewModel.resetPassword(email)

            disableViewEnableLottie(lottieAnimationView, view)

            Handler(Looper.getMainLooper()).postDelayed({
                viewLifecycleOwner.lifecycleScope.launch {
                    var isLoadedEmitted = false // Kontrol değişkeni

                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.isLoaded.collect { isLoaded ->
                            if (isLoaded && !isLoadedEmitted) {
                                isLoadedEmitted = true // Tekrar çalışmasını engellemek için işaretler

                                val isSuccess = viewModel.authSuccess.value
                                val errorMessage = viewModel.authError.value

                                if (isSuccess) {
                                    Snackbar.make(view, "Password reset email sent successfully", Snackbar.LENGTH_SHORT).show()
                                    findNavController.navigate(action, null, navOptions)
                                } else {
                                    Snackbar.make(view, errorMessage ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                                }

                               enableViewDisableLottie(
                                    lottieAnimationView,
                                    view
                                )
                            }
                        }
                    }
                }

            },1500)

        } else {
            Snackbar.make(view, "Please enter a valid email address", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun navigateToFragmentAndClearStack(navController: NavController, currentFragmentId: Int, targetFragmentId: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(currentFragmentId, inclusive = true)
            .build()

        navController.navigate(targetFragmentId, null, navOptions)
    }


    //Email, password ve fullname validation için kullanılan fonksiyonlar
    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }
    private fun isValidPassword(password: String): Boolean {
        if (password.isBlank()) {
            return false
        }
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*\\W)(?=.{6,})\\S*$")
        return passwordPattern.matches(password)
    }
    private fun isValidFullName(fullName: String): Boolean {
        // Boş olup olmadığını kontrol et
        if (fullName.isBlank()) {
            return false
        }

        // Kelimeleri ayır
        val words = fullName.trim().split("\\s+".toRegex())

        // En az iki kelime ve her kelimenin en az 3 harf uzunluğunda olması gerekiyor
        if (words.size < 2 || words.any { it.length < 2 }) {
            return false
        }

        // Kelimelerde rakam veya noktalama işareti olmamalı
        val namePattern = Regex("^[a-zA-Z]+$")

        return !words.any { !namePattern.matches(it) }
    }
    //Email, password ve fullname validation için kullanılan fonksiyonlar



    private fun saveBirthdaysToFirebase(birthdayList: List<Birthday>?){
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        if (birthdayList != null) {
            if (birthdayList.isNotEmpty()){
                birthdayList.forEach { birthday ->

                    val updatedBirthday = birthday.copy(userId = auth.currentUser!!.uid)
                    //doğum gününü firebase'e kaydediyoruz
                    val document = firestore.collection("birthdays").document(updatedBirthday.id)
                    firestore.runTransaction { transaction ->
                        transaction.set(document, updatedBirthday)
                    }
                }
            }
        }
    }
    private fun savePastBirthdaysToFirebase(birthdayList: List<Birthday>?){
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        if (birthdayList != null) {
            if (birthdayList.isNotEmpty()){
                birthdayList.forEach { birthday ->
                    val updatedBirthday = birthday.copy(userId = auth.currentUser!!.uid)
                    //doğum gününü firebase'e kaydediyoruz
                    val document = firestore.collection("past_birthdays").document(updatedBirthday.id)
                    firestore.runTransaction { transaction ->
                        transaction.set(document, updatedBirthday)
                    }
                }
            }
        }
    }
    private fun saveDeletedBirthdaysToFirebase(birthdayList: List<Birthday>?){
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        if (birthdayList != null) {
            if (birthdayList.isNotEmpty()){
                birthdayList.forEach { birthday ->

                    val updatedBirthday = birthday.copy(userId = auth.currentUser!!.uid)

                    //doğum gününü firebase'e kaydediyoruz
                    val document = firestore.collection("deleted_birthdays").document(updatedBirthday.id)
                    firestore.runTransaction { transaction ->
                        transaction.set(document, updatedBirthday)
                    }
                }
            }
        }
    }

}