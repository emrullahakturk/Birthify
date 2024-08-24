package com.yargisoft.birthify.views.auth_user_views

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
import com.yargisoft.birthify.NetworkConnectionObserver
import com.yargisoft.birthify.R
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.adapters.PastBirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentAuthPastBirthdaysBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory

class PastBirthdaysFragment : Fragment() {

    private lateinit var binding: FragmentAuthPastBirthdaysBinding
    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var adapter: PastBirthdayAdapter
    private lateinit var networkConnectionObserver: NetworkConnectionObserver



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_past_birthdays, container, false)


        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())


        //Birthday viewModel Tanımlama için gerekenler
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this, birthdayFactory)[UsersBirthdayViewModel::class]


        //Auth ViewModel Tanımlama için gerekenler
        val authRepository = AuthRepository(userSharedPreferences.preferences)
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class]

        networkConnectionObserver = NetworkConnectionObserver(requireContext())
        networkConnectionObserver.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if(userSharedPreferences.getUserCredentials().second != "guest"){
                if (isConnected) {
                    //Main Page Açıldığında firebase üzerindeki doğum günlerini bday shared pref'e aktararak senkronize etmiş oluyoruz
                    usersBirthdayViewModel.getPastBirthdaysFromFirebase(userSharedPreferences.getUserId())
                }
            }
        }

        usersBirthdayViewModel.getPastBirthdays()



        // DrawerLayout ve NavigationView tanımlamaları
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView
        val toolbarMenuButton = binding.toolbarPastBirthdays.findViewById<View>(R.id.menuButtonToolbar)

        //Navigation View'i açıp kapamaya ve menü içindeki elemanlarla başka sayfalara gitmemizi sağlayan fonksiyon
        UserFrequentlyUsedFunctions.drawerLayoutToggle(
            drawerLayout,
            navigationView,
            findNavController(),
            toolbarMenuButton,
            requireActivity(),
            authViewModel,
            usersBirthdayViewModel,
            birthdayRepository,
            userSharedPreferences,
            "PastBirthdays"
        )

        val adapterList= usersBirthdayViewModel.pastBirthdayList.value ?: emptyList()
        //Adapter'ı initialize etme
        adapter = PastBirthdayAdapter(
            adapterList.sortedByDescending { it.recordedDate },
            {_ ->
//                val action =MainPageFragmentDirections.mainToEditBirthday(birthday)
//                findNavController().navigate(action)
            },
            requireContext(),
            binding.thereIsNoPastBirthdays
            )


        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        usersBirthdayViewModel.pastBirthdayList.observe(viewLifecycleOwner) { birthdays ->
            adapter.updateData(birthdays)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                UserFrequentlyUsedFunctions.filterBirthdays(s.toString(), usersBirthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.toolbarPastBirthdays.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            findNavController().navigate(R.id.pastBirthdaysToAddBirthday)
        }

        binding.fabBackPastBirthdays.setOnClickListener {
        findNavController().popBackStack()
        }





        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.pastBirthdaysToMainPage)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    findNavController().navigate(R.id.pastBirthdaysToTrashBin)
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    true
                }
                else -> false
            }
        }


        binding.bottomAppBar.setNavigationOnClickListener {
            binding.searchEditText.requestFocus()
            //tıklandıktan sonra klavyenin açılmasını sağlar
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }


        binding.sortButton.setOnClickListener {
            UserFrequentlyUsedFunctions.showSortMenu(it, requireContext(), adapter, usersBirthdayViewModel)
        }


        return  binding.root
    }

    override fun onResume() {
        super.onResume()
        usersBirthdayViewModel.getPastBirthdaysFromFirebase(userSharedPreferences.getUserId())
        usersBirthdayViewModel.getPastBirthdays()
    }
}