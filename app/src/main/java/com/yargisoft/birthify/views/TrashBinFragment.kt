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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.DeletedBrithdayAdapter
import com.yargisoft.birthify.databinding.FragmentTrashBinBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.SharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


class TrashBinFragment : Fragment() {

    private lateinit var binding: FragmentTrashBinBinding
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var sharedPreferences: SharedPreferencesManager
    private lateinit var adapter: DeletedBrithdayAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trash_bin, container, false)


        //ViewModel Initialization
        val repository = BirthdayRepository(requireContext())
        val factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BirthdayViewModel::class]


        //user SharedPreferences
        sharedPreferences = SharedPreferencesManager(requireContext())

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.trashBinDrawerLayout
        val navigationView: NavigationView = binding.trashBinNavigationView



        //adapter initialization
        adapter = DeletedBrithdayAdapter(listOf(),

            { birthday ->
                val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
                findNavController().navigate(action)
            },
            requireContext(),
            viewModel)


        //Recyckerview Tanımlamaları
        binding.trashBinRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.trashBinRecyclerView.adapter = adapter


        //deletedBirthdays' listesini güncelleme
        viewModel.getDeletedBirthdays(sharedPreferences.getUserId())



        viewModel.deletedBirthdayList.observe(viewLifecycleOwner, Observer { deletedBirthdays ->

            binding.trashBinMainTv.visibility = if (deletedBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE


            // Adapter initialization
            adapter = DeletedBrithdayAdapter(deletedBirthdays,
                { birthday ->
                    val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
                    findNavController().navigate(action)
                },
                requireContext(),
                viewModel)

            binding.trashBinRecyclerView.adapter = adapter
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
                    findNavController().navigate(R.id.trashToMainPage)
                }
                R.id.labelLogOut -> {
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.trashToFirstPage)
                }
                R.id.labelTrashBin -> {
                    findNavController().navigate(R.id.trashToTrashBin)
                }
                R.id.labelSettings -> {
                    findNavController().navigate(R.id.trashToSettings)
                }
                R.id.labelProfile -> {
                    findNavController().navigate(R.id.trashToProfile)
                }
                else -> false
            }

            // Drawer'ı kapatmak için
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }




        // Toolbar üzerindeki menü ikonu ile menüyü açma
        binding.trashBinToolbar.findViewById<View>(R.id.menuButtonToolbar).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.trashBinToolbar.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {

            findNavController().navigate(R.id.trashToAddBirthday)

        }


        binding.trashBinBottomNavigationView.findViewById<View>(R.id.bottomNavBirthdays).setOnClickListener{
            it.findNavController().navigate(R.id.trashToMainPage)
        }


        return binding.root

    }


}