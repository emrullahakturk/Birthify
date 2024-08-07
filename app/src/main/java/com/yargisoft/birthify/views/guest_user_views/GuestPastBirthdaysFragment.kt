package com.yargisoft.birthify.views.guest_user_views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.adapters.PastBirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentGuestMainPageBinding
import com.yargisoft.birthify.databinding.FragmentGuestPastBirthdaysBinding
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.GuestViewModelFactory


class GuestPastBirthdaysFragment : Fragment() {
    private lateinit var binding: FragmentGuestPastBirthdaysBinding
    private lateinit var guestBirthdayViewModel: GuestBirthdayViewModel
    private lateinit var adapter: PastBirthdayAdapter
    private lateinit var userSharedPreferences: UserSharedPreferencesManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_past_birthdays, container, false)

        //Guest Birthday ViewModel Tanımlama için gerekenler
        val guestRepository = GuestRepository(requireContext())
        val guestFactory = GuestViewModelFactory(guestRepository)
        guestBirthdayViewModel = ViewModelProvider(this, guestFactory)[GuestBirthdayViewModel::class]

        //doğum günlerini liveDataya çekiyoruz
        guestBirthdayViewModel.getPastBirthdays()


        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbarGuestPastBirthdays.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            guestRepository,
            userSharedPreferences,
            "GuestPastBirthdays"
        )

        val adapterList= guestBirthdayViewModel.pastBirthdayList.value ?: emptyList()


        //Adapter'ı initialize etme
        adapter = PastBirthdayAdapter(
            adapterList.sortedByDescending { it.recordedDate },
            { birthday ->
//                val action = GuestPastBirthdaysFragmentDirections.guestPastTP(birthday)
//                findNavController().navigate(action)
            },
            requireContext(),
        )



        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        guestBirthdayViewModel.pastBirthdayList.observe(viewLifecycleOwner) { birthdays ->
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

        binding.toolbarGuestPastBirthdays.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            findNavController().navigate(R.id.guestPastToAddBirthday)
        }

//        binding.bottomNavigationView.findViewById<View>(R.id.bottomNavBirthdays)
//            .setOnClickListener {
//                it.findNavController().navigate(R.id.guestPastToMain)
//            }
//
//        binding.bottomNavigationView.findViewById<View>(R.id.bottomNavPastBirthdays)
//            .setOnClickListener {
//                it.findNavController().navigate(R.id.guestPastToPastBirthdays)
//            }
//        binding.bottomNavigationView.findViewById<View>(R.id.bottomNavTrashBin)
//            .setOnClickListener {
//                it.findNavController().navigate(R.id.guestPastToTrashBin)
//            }


        binding.sortButton.setOnClickListener {
            GuestFrequentlyUsedFunctions.showSortMenu(it, requireContext(), adapter, guestBirthdayViewModel)
        }





        return binding.root
    }



}