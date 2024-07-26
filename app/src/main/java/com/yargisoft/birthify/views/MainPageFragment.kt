package com.yargisoft.birthify.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.SwipeToDeleteCallback
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentMainPageBinding
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.BirthdaySharedPreferencesManager
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


@Suppress("UNUSED_EXPRESSION")
class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var birthdayViewModel: BirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var birthdaySharedPreferences: BirthdaySharedPreferencesManager
    private lateinit var adapter: BirthdayAdapter
    private var filteredBirthdays: List<Birthday> = listOf() // Filtrelenmiş doğum günleri listesi



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main_page, container, false)

        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())
        //birthday SharedPreferences
        birthdaySharedPreferences = BirthdaySharedPreferencesManager(requireContext())


        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.mainPageDrawerLayout
        val navigationView: NavigationView = binding.navViewMainPage


        //Birthday viewModel Tanımlama için gerekenler
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = BirthdayViewModelFactory(birthdayRepository)
        birthdayViewModel = ViewModelProvider(this,birthdayFactory)[BirthdayViewModel::class]


        //Auth ViewModel Tanımlama için gerekenler
        val authRepository = AuthRepository(requireContext())
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this,authFactory)[AuthViewModel::class]


        //Firebase üzerinden doğum günleri çekilir ve hem viewModel birthday listesi hem sharedPreferences olarak saklanan birthday listesi güncellenir
        birthdayViewModel.getUserBirthdays(userSharedPreferences.getUserId())


        //adapter initialization
        adapter = BirthdayAdapter(
            birthdaySharedPreferences.getBirthdays().sortedByDescending { it.recordedDate },
            {birthday ->
                val action = MainPageFragmentDirections.mainToEditBirthday(birthday)
                findNavController().navigate(action)
            },
            { birthday ->
                val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
                findNavController().navigate(action)
            },
            requireContext(),
            birthdayViewModel,
            viewLifecycleOwner,
            binding.mainPageDeleteLottieAnimation,
            binding.mainPageThreePointLottieAnim,
            binding.root,
            binding.clickToAddBirthdayTv)

        binding.birthdayRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.birthdayRecyclerView.adapter = adapter


        // ItemTouchHelper'ın RecyclerView'a doğru şekilde eklendiğinden emin olun
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.birthdayRecyclerView)

        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        birthdayViewModel.birthdays.observe(viewLifecycleOwner) {   birthdays ->
            adapter.updateData(birthdays)
            /* //adapter initialization
             adapter = BirthdayAdapter(
                 birthdays.sortedByDescending { it.recordedDate },
                 {birthday ->
                     val action = MainPageFragmentDirections.mainToEditBirthday(birthday)
                     findNavController().navigate(action)
                 },
                 { birthday ->
                     val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
                     findNavController().navigate(action)
                 },
                 requireContext(),
                 birthdayViewModel,
                 viewLifecycleOwner,
                 binding.mainPageDeleteLottieAnimation,
                 binding.mainPageThreePointLottieAnim,
                 binding.root,
                 binding.clickToAddBirthdayTv)

             binding.birthdayRecyclerView.adapter = adapter*/
        }


        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.mainPageSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBirthdays(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        // ActionBarDrawerToggle ile Drawer'ı ActionBar ile senkronize etme
        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // NavigationView'deki öğeler için click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Menü öğelerine tıklandığında yapılacak işlemler
            when (menuItem.itemId) {
                R.id.labelBirthdays -> {
                    findNavController().navigate(R.id.mainToMain)
                }
                R.id.labelLogOut -> {
                    userSharedPreferences.clearUserSession()
                    authViewModel.logoutUser()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                R.id.labelTrashBin -> {
                    findNavController().navigate(R.id.mainToTrashBin)
                }
                R.id.labelSettings -> {
                    findNavController().navigate(R.id.mainToSettings)
                }
                R.id.labelProfile -> {
                    findNavController().navigate(R.id.mainToProfile)
                }
                else -> false
            }

            // Drawer'ı kapatmak için
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Toolbar üzerindeki menü ikonu ile menüyü açma
        binding.includeMainPage.findViewById<View>(R.id.menuButtonToolbar).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.includeMainPage.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            findNavController().navigate(R.id.mainToAddBirthday)
        }
        binding.bottomNavigationView.findViewById<View>(R.id.bottomNavBirthdays).setOnClickListener{
            it.findNavController().navigate(R.id.mainToMain)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun filterBirthdays(query: String) {
        filteredBirthdays = if (query.isEmpty()) {
            birthdayViewModel.birthdays.value!!
        } else {
            birthdayViewModel.birthdays.value!!
                .filter { it.name.contains(query, ignoreCase = true) }
        }
        adapter.submitList(filteredBirthdays)
    }
}