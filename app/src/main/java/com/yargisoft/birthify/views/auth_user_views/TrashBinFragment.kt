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
import com.yargisoft.birthify.UserFrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.databinding.FragmentAuthTrashBinBinding
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory
import com.yargisoft.birthify.viewmodels.factories.UsersBirthdayViewModelFactory


class TrashBinFragment : Fragment() {

    private lateinit var binding: FragmentAuthTrashBinBinding
    private lateinit var usersBirthdayViewModel: UsersBirthdayViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    private lateinit var adapter: DeletedBirthdayAdapter
    private lateinit var networkConnectionObserver: NetworkConnectionObserver
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_trash_bin, container, false)


        //user SharedPreferences
        userSharedPreferences = UserSharedPreferencesManager(requireContext())

        //Birthday ViewModel Initialization
        val birthdayRepository = BirthdayRepository(requireContext())
        val birthdayViewModelFactory = UsersBirthdayViewModelFactory(birthdayRepository)
        usersBirthdayViewModel = ViewModelProvider(this, birthdayViewModelFactory)[UsersBirthdayViewModel::class]

        //Birthday ViewModel Initialization
        val authRepository = AuthRepository(userSharedPreferences.preferences,requireContext())
        val authViewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authViewModelFactory)[AuthViewModel::class]


        networkConnectionObserver = NetworkConnectionObserver(requireContext())
        networkConnectionObserver.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if(userSharedPreferences.getUserCredentials().second != "guest"){
                if (isConnected) {
                    //Main Page Açıldığında firebase üzerindeki doğum günlerini bday shared pref'e aktararak senkronize etmiş oluyoruz
                    usersBirthdayViewModel.getDeletedBirthdaysFromFirebase(userSharedPreferences.getUserId())
                }
            }
        }

        usersBirthdayViewModel.getDeletedBirthdays()

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
            requireContext(),
            binding.trashBinMainTv
            )


        //Recyckerview Tanımlamaları
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter


        //deletedBirthdays' listesini güncelleme
        usersBirthdayViewModel.getDeletedBirthdays()


        usersBirthdayViewModel.deletedBirthdayList.observe(viewLifecycleOwner) { deletedBirthdays ->

            binding.trashBinMainTv.visibility = if (deletedBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE

            // Adapter initialization
            adapter = DeletedBirthdayAdapter(deletedBirthdays,
                { birthday ->
                    val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
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
                UserFrequentlyUsedFunctions.filterBirthdays(s.toString(), usersBirthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.toolbarUserTrashBin.findViewById<View>(R.id.addButtonToolbar).setOnClickListener {
            binding.searchEditText.requestFocus()
            //tıklandıktan sonra klavyenin açılmasını sağlar
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)

        }


        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavBirthdays -> {
                    findNavController().navigate(R.id.trashToMainPage)
                    true
                }
                R.id.bottomNavTrashBin-> {
                    true
                }
                R.id.bottomNavPastBirthdays -> {
                    findNavController().navigate(R.id.trashToPastBirthdays)
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


        binding.sortButton.setOnClickListener{

            UserFrequentlyUsedFunctions.showSortMenu(it,requireContext(),adapter,usersBirthdayViewModel)

        }

        binding.fabBackButtonTrash.setOnClickListener {
            findNavController().popBackStack()
        }


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
            "TrashBin"
        )



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        usersBirthdayViewModel.getDeletedBirthdaysFromFirebase(userSharedPreferences.getUserId())
        usersBirthdayViewModel.getDeletedBirthdays()
    }
}