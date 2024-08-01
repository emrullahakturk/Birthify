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
import com.yargisoft.birthify.GuestSwipeToDeleteCallback
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentGuestMainPageBinding
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.GuestViewModelFactory
import com.yargisoft.birthify.views.auth_user_views.MainPageFragmentDirections


class GuestMainPageFragment : Fragment() {
    private lateinit var binding: FragmentGuestMainPageBinding
    private lateinit var guestsBirthdayViewModel: GuestBirthdayViewModel
    private lateinit var adapter: BirthdayAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var userSharedPreferences: UserSharedPreferencesManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_main_page, container, false)



        //Guest Birthday ViewModel Tanımlama için gerekenler
        val guestRepository = GuestRepository(requireContext())
        val guestFactory = GuestViewModelFactory(guestRepository)
        guestsBirthdayViewModel = ViewModelProvider(this, guestFactory)[GuestBirthdayViewModel::class]


        //doğum günlerini liveDataya çekiyoruz
        guestsBirthdayViewModel.getBirthdays()

        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

//        Log.e("tagımıs","${userSharedPreferences.getUserCredentials()} ${guestsBirthdayViewModel.birthdayList.value} ")


        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbar.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            guestRepository,
            userSharedPreferences,
            "GuestMainPage"
        )

        val adapterList= guestsBirthdayViewModel.birthdayList.value ?: emptyList()

        //Adapter'ı initialize etme
        adapter = BirthdayAdapter(
            adapterList.sortedByDescending { it.recordedDate },
            {birthday ->
                val action = MainPageFragmentDirections.mainToEditBirthday(birthday)
                findNavController().navigate(action)
            },
            { birthday ->
                val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
                findNavController().navigate(action)
            },
            requireContext(),
            binding.clickToAddBirthdayTv)

        //ItemTouchHelper initialize etme
        itemTouchHelper = ItemTouchHelper(
           GuestSwipeToDeleteCallback(
            adapter,
            requireContext(),
            guestsBirthdayViewModel,
            viewLifecycleOwner,
            binding.deleteAnimation,
            findNavController(),
            binding.root,
            adapterList,
            R.id.mainToMain
        )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)


        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        guestsBirthdayViewModel.birthdayList.observe(viewLifecycleOwner) { birthdays ->
            adapter.updateData(birthdays)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                GuestFrequentlyUsedFunctions.filterBirthdays(s.toString(), guestsBirthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.toolbar.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            findNavController().navigate(R.id.guestMainToAddBirthday)
        }
        binding.bottomNavigationView.findViewById<View>(R.id.bottomNavBirthdays)
            .setOnClickListener {
                it.findNavController().navigate(R.id.guestMainToMain)
            }

        binding.sortButton.setOnClickListener {
            GuestFrequentlyUsedFunctions.showSortMenu(it, requireContext(), adapter, guestsBirthdayViewModel)
        }



        return binding.root
    }

}