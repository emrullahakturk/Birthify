package com.yargisoft.birthify.views.guest_user_views

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.GuestFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentGuestTrashBinBinding
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.GuestViewModelFactory

class GuestTrashBinFragment : Fragment() {

    private lateinit var binding: FragmentGuestTrashBinBinding
    private lateinit var guestBirthdayViewModel: GuestBirthdayViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var adapter: DeletedBirthdayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_trash_bin, container, false)

        //Guest Birthday ViewModel Tanımlama için gerekenler
        val guestRepository = GuestRepository(requireContext())
        val guestFactory = GuestViewModelFactory(guestRepository)
        guestBirthdayViewModel = ViewModelProvider(this, guestFactory)[GuestBirthdayViewModel::class]


        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())



        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        // Toolbar üzerindeki menü ikonu
        val toolbarMenuButton = binding.toolbarGuestTrashBin.findViewById<View>(R.id.menuButtonToolbar)

        //adapter initialization
        adapter = DeletedBirthdayAdapter(listOf(),
            { birthday ->
                val action =
                    GuestTrashBinFragmentDirections.guestTrashBinToDeletedDetail(
                        birthday
                    )
                findNavController().navigate(action)
            },
            requireContext(),
            binding.trashBinMainTv,
        )

        //Recyckerview Tanımlamaları
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        //deletedBirthdays' listesini güncelleme
        guestBirthdayViewModel.getDeletedBirthdays()


        guestBirthdayViewModel.deletedBirthdayList.observe(viewLifecycleOwner) { deletedBirthdays ->

            binding.trashBinMainTv.visibility = if (deletedBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE

            // Adapter initialization
            adapter = DeletedBirthdayAdapter(deletedBirthdays,
                { birthday ->
                    val action = GuestTrashBinFragmentDirections.guestTrashBinToDeletedDetail(birthday)
                    findNavController().navigate(action)
                },
                requireContext(),
                binding.trashBinMainTv
            )

            binding.recyclerView.adapter = adapter
        }



        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               GuestFrequentlyUsedFunctions.filterBirthdays(s.toString(), guestBirthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.bottomAppBar.setNavigationOnClickListener {
            binding.searchEditText.requestFocus()
            //tıklandıktan sonra klavyenin açılmasını sağlar
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }



        binding.toolbarGuestTrashBin.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {

            findNavController().navigate(R.id.guestTrashToAddBirthday)

        }


        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                     findNavController().navigate(R.id.guestTrashBinToMain)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.guestTrashBinToPastBirthdays)
                    true
                }
                else -> false
            }
        }


        binding.sortButton.setOnClickListener{

            GuestFrequentlyUsedFunctions.showSortMenu(it,requireContext(),adapter,guestBirthdayViewModel)

        }

        binding.fabBackButtonTrash.setOnClickListener {
            findNavController().popBackStack()
        }

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        GuestFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            "GuestTrashBin"
        )

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        guestBirthdayViewModel.getDeletedBirthdays()
    }
}