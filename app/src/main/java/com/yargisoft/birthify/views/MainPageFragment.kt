package com.yargisoft.birthify.views

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
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.SwipeToDeleteCallback
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentMainPageBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var birthdayViewModel: BirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var adapter: BirthdayAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_page, container, false)

        //Birthday viewModel Tanımlama için gerekenler
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = BirthdayViewModelFactory(birthdayRepository)
        birthdayViewModel = ViewModelProvider(this, birthdayFactory)[BirthdayViewModel::class]


        //Auth ViewModel Tanımlama için gerekenler
        val authRepository = AuthRepository(requireContext())
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class]


        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())


        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.mainPageDrawerLayout
        val navigationView: NavigationView = binding.navViewMainPage
        val toolbarMenuButton = binding.includeMainPage.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        FrequentlyUsedFunctions.drawerLayoutToggle(
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




        //doğum günlerini liveDataya çekiyoruz
        birthdayViewModel.getBirthdays()




         val adapterList= birthdayViewModel.birthdayList.value ?: emptyList()

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
        itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(
            adapter,
            requireContext(),
            birthdayViewModel,
            viewLifecycleOwner,
            binding.mainPageDeleteLottieAnimation,
            findNavController(),
            binding.root,
            adapterList,
            R.id.mainToMain
        ))
        itemTouchHelper.attachToRecyclerView(binding.birthdayRecyclerView)

        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        birthdayViewModel.birthdayList.observe(viewLifecycleOwner) { birthdays ->
            adapter.updateData(birthdays)
        }

        binding.birthdayRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.birthdayRecyclerView.adapter = adapter




        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.mainPageSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


       /* // ActionBarDrawerToggle ile Drawer'ı ActionBar ile senkronize etme
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
*/



        /*// Toolbar üzerindeki menü ikonu ile menüyü açma
        .setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }*/

        binding.includeMainPage.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            findNavController().navigate(R.id.mainToAddBirthday)
        }
        binding.bottomNavigationView.findViewById<View>(R.id.bottomNavBirthdays)
            .setOnClickListener {
                it.findNavController().navigate(R.id.mainToMain)
            }

        binding.sortButtonMainPage.setOnClickListener {
            FrequentlyUsedFunctions.showSortMenu(it, requireContext(), adapter, birthdayViewModel)
        }
        return binding.root
    }
}



