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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentTrashBinBinding
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


@Suppress("UNUSED_EXPRESSION")
class TrashBinFragment : Fragment() {

    private lateinit var binding: FragmentTrashBinBinding
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var sharedPreferences: UserSharedPreferencesManager
    private lateinit var adapter: DeletedBirthdayAdapter
    private var filteredBirthdays: List<Birthday> = listOf() // Filtrelenmiş doğum günleri listesi


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trash_bin, container, false)


        //ViewModel Initialization
        val repository = BirthdayRepository(requireContext())
        val factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BirthdayViewModel::class]


        //user SharedPreferences
        sharedPreferences = UserSharedPreferencesManager(requireContext())

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.trashBinDrawerLayout
        val navigationView: NavigationView = binding.trashBinNavigationView


        //adapter initialization
        adapter = DeletedBirthdayAdapter(listOf(),
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



        viewModel.deletedBirthdayList.observe(viewLifecycleOwner) { deletedBirthdays ->

            binding.trashBinMainTv.visibility = if (deletedBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE


            // Adapter initialization
            adapter = DeletedBirthdayAdapter(deletedBirthdays,
                { birthday ->
                    val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
                    findNavController().navigate(action)
                },
                requireContext(),
                viewModel)

            binding.trashBinRecyclerView.adapter = adapter
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



        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.trashBinSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(),viewModel,adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })



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