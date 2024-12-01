package com.yargisoft.birthify.ui.views.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.yargisoft.birthify.BirthdaySortFunctions
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthTrashBinBinding
import com.yargisoft.birthify.ui.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.ui.viewmodels.AuthViewModel
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.NetworkConnectionObserver
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class TrashBinFragment : Fragment() {

    private lateinit var binding: FragmentAuthTrashBinBinding
    private val birthdayViewModel: BirthdayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var adapter: DeletedBirthdayAdapter
    private lateinit var networkConnectionObserver: NetworkConnectionObserver

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_trash_bin, container, false)


        networkConnectionObserver = NetworkConnectionObserver(requireContext())
        networkConnectionObserver.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if(userSharedPreferences.getUserCredentials().second != "guest"){
                if (isConnected) {
                    //Main Page Açıldığında firebase üzerindeki doğum günlerini bday shared pref'e aktararak senkronize etmiş oluyoruz
                    birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(),"deleted_birthdays")
                }
            }
        }

        birthdayViewModel.getBirthdays("deleted_birthdays")

        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView

        // Toolbar üzerindeki menü ikonu
        val toolbarMenuButton = binding.toolbarUserTrashBin.findViewById<View>(R.id.menuButtonToolbar)


        //adapter initialization
        adapter = DeletedBirthdayAdapter(listOf(),
            { birthday ->
                val action =
                   TrashBinFragmentDirections.trashToDeletedDetail(
                        birthday
                    )
                findNavController().navigate(action)
            },
            binding.trashBinMainTv
            )


        //Recyckerview Tanımlamaları
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter


        //deletedBirthdays' listesini güncelleme
        birthdayViewModel.getBirthdays("deleted_birthdays")


        birthdayViewModel.birthdayList.observe(viewLifecycleOwner) { deletedBirthdays ->

            binding.trashBinMainTv.visibility = if (deletedBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE

            // Adapter initialization
            adapter = DeletedBirthdayAdapter(deletedBirthdays,
                { birthday ->
                    val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
                    findNavController().navigate(action)
                },
                binding.trashBinMainTv
            )

            binding.recyclerView.adapter = adapter
        }


        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.toolbarUserTrashBin.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            binding.searchEditText.requestFocus()
            //tıklandıktan sonra klavyenin açılmasını sağlar
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)

        }



        binding.bottomAppBar.setNavigationOnClickListener {
            binding.searchEditText.requestFocus()
            //tıklandıktan sonra klavyenin açılmasını sağlar
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }


        binding.sortButton.setOnClickListener{

           BirthdaySortFunctions.showSortMenu(it,requireContext(),adapter,birthdayViewModel)

        }

        binding.fabBackButtonTrash.setOnClickListener {
            findNavController().popBackStack()
        }


        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        FrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
             toolbarMenuButton,
            requireActivity(),
            authViewModel,
            birthdayViewModel,
            birthdayRepository,
            userSharedPreferences,
         )



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        birthdayViewModel.getBirthdaysFromFirebase(userSharedPreferences.getUserId(),"deleted_birthdays")
        birthdayViewModel.getBirthdays("deleted_birthdays")
    }
}