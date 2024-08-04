package com.yargisoft.birthify.views.auth_user_views

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
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserSwipeToDeleteCallback
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentAuthMainPageBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory


class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentAuthMainPageBinding
    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var adapter: BirthdayAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_main_page, container, false)


        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())


        //Birthday viewModel Tanımlama için gerekenler
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this, birthdayFactory)[UsersBirthdayViewModel::class]

        //Auth ViewModel Tanımlama için gerekenler
        val authRepository = AuthRepository(requireContext())
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class]

        //Main Page Açıldığında firebase üzerindeki doğum günlerini bday shared pref'e aktararak senkronize etmiş oluyoruz
        usersBirthdayViewModel.getUserBirthdaysFromFirebase(userSharedPreferences.getUserId())
        usersBirthdayViewModel.getPastBirthdaysFromFirebase(userSharedPreferences.getUserId())
        usersBirthdayViewModel.getDeletedBirthdaysFromFirebase(userSharedPreferences.getUserId())


        usersBirthdayViewModel.getBirthdays()

        //Geçmiş doğum günlerini burada filtreleyerek pastBirthdays'e gödneriyoruz
        val listForFilter = usersBirthdayViewModel.birthdayList.value ?: emptyList()
        usersBirthdayViewModel.filterPastAndUpcomingBirthdays(listForFilter)

        //filtreleme sonrası doğum günlerini liveDataya aktarıyoruz
        usersBirthdayViewModel.getBirthdays()



        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbarUserMainPage.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        UserFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            authViewModel,
            birthdayRepository,
            userSharedPreferences,
            "MainPage"
        )



         val adapterList= usersBirthdayViewModel.birthdayList.value ?: emptyList()

        //Adapter'ı initialize etme
        adapter = BirthdayAdapter(
            adapterList.sortedByDescending { it.recordedDate },
            {birthday ->
                val action =MainPageFragmentDirections.mainToEditBirthday(birthday)
                findNavController().navigate(action)
            },
            { birthday ->
                val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
                findNavController().navigate(action)
            },
            requireContext(),
            binding.clickToAddBirthdayTv)

        //ItemTouchHelper initialize etme
        itemTouchHelper = ItemTouchHelper(UserSwipeToDeleteCallback(
            adapter,
            requireContext(),
            usersBirthdayViewModel,
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
        usersBirthdayViewModel.birthdayList.observe(viewLifecycleOwner) { birthdays ->
            adapter.updateData(birthdays)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter


        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                UserFrequentlyUsedFunctions.filterBirthdays(s.toString(), usersBirthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.toolbarUserMainPage.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            findNavController().navigate(R.id.mainToAddBirthday)
        }
        binding.bottomNavigationView.findViewById<View>(R.id.bottomNavBirthdays)
            .setOnClickListener {
                it.findNavController().navigate(R.id.mainToMain)
            }

        binding.sortButton.setOnClickListener {
            UserFrequentlyUsedFunctions.showSortMenu(it, requireContext(), adapter, usersBirthdayViewModel)
        }
        return binding.root
    }
}



