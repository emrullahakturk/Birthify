package com.yargisoft.birthify.views.guest_user_views

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions.isAlarmScheduled
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions.requestExactAlarmPermission
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions.scheduleBirthdayReminder
import com.yargisoft.birthify.GuestSwipeToDeleteCallback
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentGuestMainPageBinding
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuestMainPageFragment : Fragment() {
    private lateinit var binding: FragmentGuestMainPageBinding
    private val guestBirthdayViewModel: GuestBirthdayViewModel by viewModels()

    private lateinit var adapter: BirthdayAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_main_page, container, false)



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



        //doğum günlerini liveDataya çekiyoruz
        guestBirthdayViewModel.getBirthdays()

        //listedeki doğum günlerini tarihi geçmiş ise past birthdays listesine aktarır
        val listForFilter = guestBirthdayViewModel.birthdayList.value ?: emptyList()
        guestBirthdayViewModel.filterPastAndUpcomingBirthdays(listForFilter)

        //Liste filtrelemeden sonra doğum günlerini tekrar birthdaylist'e aktarıyoruz
        guestBirthdayViewModel.getBirthdays()

        //doğum günlerini kontrol ederek reminder'ı olmayan doğum gününe reminder ekliyoruz
        if (guestBirthdayViewModel.birthdayList.value != null) {
            guestBirthdayViewModel.birthdayList.value!!.forEach { birthday ->


                val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    requestExactAlarmPermission(requireActivity())
                } else {
                   if (!isAlarmScheduled(birthday.id,requireContext())){
                       scheduleBirthdayReminder(birthday.id, birthday.name, birthday.birthdayDate, birthday.notifyDate, requireContext())
                       Log.e("tagımıs","alarm kuruldu")
                   }else{
                       Log.e("tagımıs","alarm zaten var lan")
                   }
                }
            }
        }





        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbarGuestMain.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            "GuestMainPage"
        )

        val adapterList= guestBirthdayViewModel.birthdayList.value ?: emptyList()

        //Adapter'ı initialize etme
        adapter = BirthdayAdapter(
            adapterList.sortedByDescending { it.recordedDate },
            {birthday ->
                val action = GuestMainPageFragmentDirections.guestMainToEditBirthday(birthday)
                findNavController().navigate(action)
            },
            { birthday ->
                val action = GuestMainPageFragmentDirections.guestMainToDetail(birthday)
                findNavController().navigate(action)
            },
            requireContext(),
            binding.clickToAddBirthdayTv)

        //ItemTouchHelper initialize etme
        itemTouchHelper = ItemTouchHelper(
           GuestSwipeToDeleteCallback(
            adapter,
            requireContext(),
            guestBirthdayViewModel,
            viewLifecycleOwner,
            binding.deleteAnimation,
            findNavController(),
            binding.root,
            adapterList,
            R.id.guestMainToMain
        )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)


        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        guestBirthdayViewModel.birthdayList.observe(viewLifecycleOwner) { birthdays ->
            adapter.updateData(birthdays)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter


        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                GuestFrequentlyUsedFunctions.filterBirthdays(s.toString(), guestBirthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


     binding.fabAddButtonMain.setOnClickListener {
         findNavController().navigate(R.id.guestMainToAddBirthday)

     }



        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.guestMainToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.guestMainToPastBirthdays)
                    true
                }
                else -> false
            }
        }

        binding.bottomAppBar.setNavigationOnClickListener {
            binding.searchEditText.requestFocus()
            //tıklandıktan sonra klavyenin açılmasını sağlar
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.sortButton.setOnClickListener {
            GuestFrequentlyUsedFunctions.showSortMenu(it, requireContext(), adapter, guestBirthdayViewModel)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        guestBirthdayViewModel.getBirthdays()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}