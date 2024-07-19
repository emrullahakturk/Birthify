package com.yargisoft.birthify.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.SwipeToDeleteCallback
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentMainPageBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.SharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var sharedPreferences: SharedPreferencesManager
    private lateinit var adapter: BirthdayAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main_page, container, false)

/*
        binding.fabMainPageAddBDay.setOnClickListener { it.findNavController().navigate(R.id.mainToAddBirthday) }
*/

        //user SharedPreferences
        sharedPreferences = SharedPreferencesManager(requireContext())

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navViewMainPage

        //viewModel Tanımlama için gerekenler
        val repository = BirthdayRepository(requireContext())
        val factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[BirthdayViewModel::class]

        //adapter initialization
        adapter = BirthdayAdapter(listOf(),

            {birthday ->

            val action = MainPageFragmentDirections.mainToEditBirthday(birthday)
            findNavController().navigate(action)
        }, { birthday ->
            val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
            findNavController().navigate(action)
        },
            requireContext(),
            viewModel)




        binding.birthdayRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.birthdayRecyclerView.adapter = adapter

        // ItemTouchHelper'ın RecyclerView'a doğru şekilde eklendiğinden emin olun
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.birthdayRecyclerView)

        viewModel.getUserBirthdays(sharedPreferences.getUserId())


        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        viewModel.birthdays.observe(viewLifecycleOwner) {   birthdays ->

            binding.clickToAddBirthdayTv.visibility = if (birthdays.isEmpty()) View.VISIBLE else View.INVISIBLE

            adapter = BirthdayAdapter(birthdays,
                { birthday ->

                    val action = MainPageFragmentDirections.mainToEditBirthday(birthday)
                    findNavController().navigate(action)
                }, { birthday ->
                    val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
                    findNavController().navigate(action)
                },

                requireContext(),
                viewModel

            )

            binding.birthdayRecyclerView.adapter = adapter

            adapter.updateData(birthdays)
            Log.d("MainPageFragment", "Fetched birthdays: ${birthdays.size}")

        }





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
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                R.id.labelTrashBin -> {
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                R.id.labelSettings -> {
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                R.id.labelProfile -> {
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
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



        // Inflate the layout for this fragment
        return binding.root
    }



}