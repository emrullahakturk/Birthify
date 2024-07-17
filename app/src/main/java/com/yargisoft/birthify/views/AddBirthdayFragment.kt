package com.yargisoft.birthify.views

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentAddBirthdayBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.SharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory
import java.util.Calendar


class AddBirthdayFragment : Fragment() {
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var binding : FragmentAddBirthdayBinding
    private lateinit var sharedPreferences: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_birthday, container, false)


        sharedPreferences = SharedPreferencesManager(requireContext())

        val repository = BirthdayRepository(requireContext())
        val factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory).get(BirthdayViewModel::class.java)



        binding.saveBirthdayButton.setOnClickListener {
            val name = binding.nameAddBirthdayEditText.text.toString()
            val birthdayDate = binding.birthdayDateEditText.text.toString()
            val note = binding.noteAddBirthdayEditText.text.toString()
            val userId =  sharedPreferences.getUserId()

            if (name.isNotEmpty() && birthdayDate.isNotEmpty() && note.isNotEmpty()) {
                viewModel.saveBirthday(name, birthdayDate, note, userId = userId)
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.saveBirthdayState.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Birthday saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save birthday", Toast.LENGTH_SHORT).show()
            }
        })

        binding.birthdayDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.fabSaveBirthday.setOnClickListener { it.findNavController().popBackStack() }



        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navViewAddBDay


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
                    sharedPreferences.clearUserSession()
                    findNavController().navigate(R.id.firstPageFragment)
                }
                // Diğer menü öğeleri için gerektiği kadar case ekleyebilirsiniz
                else -> false
            }

            // Drawer'ı kapatmak için
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        // Toolbar üzerindeki menü ikonu ile menüyü açma
        binding.includeAddBirthday.findViewById<View>(R.id.menuButtonToolbar).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        return binding.root
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
            binding.birthdayDateEditText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}
