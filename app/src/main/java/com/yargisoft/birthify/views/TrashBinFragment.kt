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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentTrashBinBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory


class TrashBinFragment : Fragment() {

    private lateinit var binding: FragmentTrashBinBinding
    private lateinit var birthdayViewModel: BirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var adapter: DeletedBirthdayAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trash_bin, container, false)


        //Birthday ViewModel Initialization
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayViewModelFactory = BirthdayViewModelFactory(birthdayRepository)
        birthdayViewModel = ViewModelProvider(this, birthdayViewModelFactory)[BirthdayViewModel::class]

        //Birthday ViewModel Initialization
        val authRepository = AuthRepository(requireContext())
        val authViewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authViewModelFactory)[AuthViewModel::class]


        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.trashBinDrawerLayout
        val navigationView: NavigationView = binding.trashBinNavigationView
        // Toolbar üzerindeki menü ikonu
        val toolbarMenuButton = binding.trashBinToolbar.findViewById<View>(R.id.menuButtonToolbar)


        //adapter initialization
        adapter = DeletedBirthdayAdapter(listOf(),
            { birthday ->
                val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
                findNavController().navigate(action)
            },
            requireContext(),
            )


        //Recyckerview Tanımlamaları
        binding.trashBinRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.trashBinRecyclerView.adapter = adapter


        //deletedBirthdays' listesini güncelleme
        birthdayViewModel.getDeletedBirthdays()


        birthdayViewModel.deletedBirthdayList.observe(viewLifecycleOwner) { deletedBirthdays ->

            binding.trashBinMainTv.visibility = if (deletedBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE

            // Adapter initialization
            adapter = DeletedBirthdayAdapter(deletedBirthdays,
                { birthday ->
                    val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
                    findNavController().navigate(action)
                },
                requireContext()
            )

            binding.trashBinRecyclerView.adapter = adapter
        }


        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.trashBinSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.trashBinToolbar.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {

            findNavController().navigate(R.id.trashToAddBirthday)

        }

        binding.trashBinBottomNavigationView.findViewById<View>(R.id.bottomNavBirthdays).setOnClickListener{
            it.findNavController().navigate(R.id.trashToMainPage)
        }

        binding.sortButtonTrashBin.setOnClickListener{

            FrequentlyUsedFunctions.showSortMenu(it,requireContext(),adapter,birthdayViewModel)

        }


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
            "TrashBin"
        )



        return binding.root
    }
}