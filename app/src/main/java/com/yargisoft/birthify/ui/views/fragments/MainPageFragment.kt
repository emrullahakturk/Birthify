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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.IS_FIRST_LAUNCH_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_SETTINGS
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainPageFragment : Fragment() {

    private lateinit var binding: FragmentAuthMainPageBinding
    private val birthdayViewModel: BirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var itemTouchHelper: ItemTouchHelper

    @Inject
    lateinit var networkConnectionObserver: NetworkConnectionObserver

    @Inject
    lateinit var adapter: BirthdayAdapter

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_main_page, container, false)

        // Bildirim izni kontrolü ve ilk açılış işlemleri
        handleNotificationPermission()

        // Kullanıcı kimlik bilgilerini yükleme
        authViewModel.getUserCredentials()

        // Ağ bağlantısını gözlemleme
        observeNetworkConnection()



        // RecyclerView ve Adapter yapılandırması
        setupRecyclerView()

        // Swipe işlemleri için ItemTouchHelper yapılandırma
        setupItemTouchHelper()

        // Arama ve sıralama butonları için işlevsellik ekleme
        setupSearchAndSort()

        // Floating Action Button ile doğum günü ekleme
        setupFab()

        // Doğum günlerini yükleme ve senkronizasyon
        loadAndSyncBirthdays()

        return binding.root
    }

    private fun handleNotificationPermission() {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean(IS_FIRST_LAUNCH_KEY, true)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            Log.d("NotificationPermission", if (isGranted) "Bildirim izni verildi." else "Bildirim izni reddedildi.")
        }

        if (isFirstLaunch) {
            requestNotificationPermission()
            sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH_KEY, false).apply()
        }
    }

    private fun observeNetworkConnection() {
        networkConnectionObserver.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (userSharedPreferences.getUserCredentials().second != "guest" && isConnected) {
                birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(), "birthdays")
                birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(), "past_birthdays")
                birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(), "deleted_birthdays")
            }
        }
    }

    private fun loadAndSyncBirthdays() {
        birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(), "birthdays")
        birthdayViewModel.getBirthdays("birthdays")
        birthdayViewModel.filterPastAndUpcomingBirthdays(birthdayViewModel.birthdayList.value ?: emptyList())
        birthdayViewModel.getBirthdays("birthdays")

        birthdayViewModel.birthdayList.value?.forEach { birthday ->
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission(requireActivity())
            } else if (!isAlarmScheduled(birthday.id, requireContext())) {
                scheduleBirthdayReminder(
                    birthday.id,
                    birthday.name,
                    birthday.birthdayDate,
                    birthday.notifyDate,
                    requireContext()
                )
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        birthdayViewModel.birthdayList.observe(viewLifecycleOwner) { birthdays ->
            binding.clickToAddBirthdayTv.visibility =
                if (birthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
            adapter.updateData(birthdays)
        }

        adapter.setOnEditClickListener { birthday ->
            val action = MainPageFragmentDirections.mainToEditBirthday(birthday)
            findNavController().navigate(action)
        }

        adapter.setOnDetailClickListener { birthday ->
            val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
            findNavController().navigate(action)
        }
    }

    private fun setupItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(
            UserSwipeToDeleteCallback(
                adapter,
                requireContext(),
                birthdayViewModel,
                viewLifecycleOwner,
                binding.deleteAnimation,
                findNavController(),
                binding.root,
                birthdayViewModel.birthdayList.value ?: emptyList(),
                R.id.mainToMain,
                navOptions { }
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupSearchAndSort() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.sortButton.setOnClickListener {
            showSortMenu(it, requireContext(), adapter, birthdayViewModel, birthdayViewModel.birthdayList.value ?: emptyList())
        }
    }

    private fun setupFab() {
        binding.fabAddButtonMain.setOnClickListener {
            findNavController().navigate(R.id.mainToAddBirthday)
        }
    }

    override fun onResume() {
        loadAndSyncBirthdays()
        super.onResume()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

