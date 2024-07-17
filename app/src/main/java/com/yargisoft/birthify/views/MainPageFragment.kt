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
import androidx.lifecycle.Observer
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
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.SharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var preferences: SharedPreferencesManager
    private lateinit var adapter: BirthdayAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main_page, container, false)

/*
        binding.fabMainPageAddBDay.setOnClickListener { it.findNavController().navigate(R.id.mainToAddBirthday) }
*/

        //user SharedPreferences
        preferences = SharedPreferencesManager(requireContext())

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


        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        viewModel.birthdays.observe(viewLifecycleOwner, Observer { birthdays ->

            adapter = BirthdayAdapter(birthdays,
                {birthday ->
                    val action = MainPageFragmentDirections.mainToEditBirthday(birthday)
                    findNavController().navigate(action)
                }, { birthday ->
                    val action = MainPageFragmentDirections.mainToDetailBirthday(birthday)
                    findNavController().navigate(action)
                },

                requireContext(),
                viewModel)

            binding.birthdayRecyclerView.adapter = adapter

        })

        viewModel.getUserBirthdays(preferences.getUserId())






        // ActionBarDrawerToggle ile Drawer'ı ActionBar ile senkronize etme
        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // NavigationView'deki öğeler için click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Menü öğelerine tıklandığında yapılacak işlemler
            when (menuItem.itemId) {
                R.id.navMenuAccount -> {
                    // İlgili menü öğesi seçildiğinde yapılacak işlemler
                    true
                }
                R.id.navMenuLogout -> {
                    // İlgili menü öğesi seçildiğinde yapılacak işlemler
                    true
                }
                // Diğer menü öğeleri için gerektiği kadar case ekleyebilirsiniz
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




        // Inflate the layout for this fragment
        return binding.root
    }



}