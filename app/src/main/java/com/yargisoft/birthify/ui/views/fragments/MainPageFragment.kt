package com.yargisoft.birthify.ui.views.fragments

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.BirthdaySortFunctions.showSortMenu
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserSwipeToDeleteCallback
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthMainPageBinding
import com.yargisoft.birthify.ui.adapters.BirthdayAdapter
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.NetworkConnectionObserver
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.isAlarmScheduled
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.requestExactAlarmPermission
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.scheduleBirthdayReminder
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentAuthMainPageBinding
    private val birthdayViewModel: BirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var networkConnectionObserver: NetworkConnectionObserver
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @Inject
    lateinit var adapter: BirthdayAdapter
    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager
    @Inject
    lateinit var birthdayRepository: BirthdayRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_main_page, container, false)


        // SharedPreferences ile ilk açılışı kontrolü
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)


        // İzin isteme işlemini başlatmak için launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("NotificationPermission", "Bildirim izni verildi.")
            } else {
                Log.d("NotificationPermission", "Bildirim izni reddedildi.")
            }
        }

        if (isFirstLaunch) {
            // İlk açılışsa bildirim izni istenir
            requestNotificationPermission()
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
        }


        authViewModel.getUserCredentials()

        Log.e("tagımıs", authViewModel.userCredentials.toString())


        networkConnectionObserver = NetworkConnectionObserver(requireContext())
        networkConnectionObserver.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if(userSharedPreferences.getUserCredentials().second != "guest"){
                if (isConnected) {
                    //Main Page Açıldığında firebase üzerindeki doğum günlerini bday shared pref'e aktararak senkronize etmiş oluyoruz
                    birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(),"birthdays")
                    birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(),"past_birthdays")
                    birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(),"deleted_birthdays")
                }
            }
        }


        birthdayViewModel.getBirthdays("birthdays")


        //Geçmiş doğum günlerini burada filtreleyerek pastBirthdays'e gödneriyoruz
        val listForFilter = birthdayViewModel.birthdayList.value ?: emptyList()
        birthdayViewModel.filterPastAndUpcomingBirthdays(listForFilter)


        //filtreleme sonrası doğum günlerini liveDataya aktarıyoruz
        birthdayViewModel.getBirthdays("birthdays")

        //doğum günlerini kontrol ederek reminder'ı olmayan doğum gününe reminder ekliyoruz
        if (birthdayViewModel.birthdayList.value != null) {
            birthdayViewModel.birthdayList.value!!.forEach { birthday ->


                val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    requestExactAlarmPermission(requireActivity())
                } else {
                    if (!isAlarmScheduled(birthday.id,requireContext())) {
                        scheduleBirthdayReminder(birthday.id, birthday.name, birthday.birthdayDate, birthday.notifyDate, requireContext())
                    }
                }
            }
        }


        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbarUserMain.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        FrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
             toolbarMenuButton,
            requireActivity(),
            authViewModel,
            birthdayViewModel,
            birthdayRepository,
            userSharedPreferences,
         )

        binding.toolbarUserMain.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            binding.searchEditText.requestFocus()
            //tıklandıktan sonra klavyenin açılmasını sağlar
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)

        }


         val adapterList= birthdayViewModel.birthdayList.value ?: emptyList()

         adapter.updateData(adapterList.sortedByDescending { it.recordedDate })

         adapter.setOnEditClickListener {birthday ->
             val action =MainPageFragmentDirections.mainToEditBirthday(birthday)
             findNavController().navigate(action)
         }
        adapter.setOnDetailClickListener { birthday ->
            val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
            findNavController().navigate(action)
        }



        //ItemTouchHelper initialize etme
        itemTouchHelper = ItemTouchHelper(UserSwipeToDeleteCallback(
            adapter,
            requireContext(),
            birthdayViewModel,
            viewLifecycleOwner,
            binding.deleteAnimation,
            findNavController(),
            binding.root,
            adapterList,
            R.id.mainToMain,
            navOptions {  }
        ))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        birthdayViewModel.birthdayList.observe(viewLifecycleOwner) { birthdays ->
            adapter.updateData(birthdays)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter


        binding.bottomAppBar.setNavigationOnClickListener {
            binding.searchEditText.requestFocus()
            //tıklandıktan sonra klavyenin açılmasını sağlar
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }


        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.fabAddButtonMain.setOnClickListener {
            findNavController().navigate(R.id.mainToAddBirthday)
        }


        binding.sortButton.setOnClickListener {
            showSortMenu(it, requireContext(), adapter, birthdayViewModel)
        }



        return binding.root
    }

    override fun onResume() {

        super.onResume()

        if (birthdayViewModel.birthdayList.value != null) {
            birthdayViewModel.birthdayList.value!!.forEach { birthday ->

                val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    requestExactAlarmPermission(requireActivity())
                } else {
                    if (!isAlarmScheduled(birthday.id,requireContext())) {
                        scheduleBirthdayReminder(birthday.id, birthday.name, birthday.birthdayDate, birthday.notifyDate, requireContext())
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

}
