package com.yargisoft.birthify.ui.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yargisoft.birthify.BirthdaySortFunctions.showSortMenu
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthPastBirthdaysBinding
import com.yargisoft.birthify.ui.adapters.PastBirthdayAdapter
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.NetworkConnectionObserver
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PastBirthdaysFragment : Fragment() {

    private lateinit var binding: FragmentAuthPastBirthdaysBinding
    private val birthdayViewModel: BirthdayViewModel by viewModels()

    @Inject
    lateinit var adapter: PastBirthdayAdapter

    @Inject
    lateinit var networkConnectionObserver: NetworkConnectionObserver

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_auth_past_birthdays,
            container,
            false
        )

        setupNetworkObserver()
        setupRecyclerView()
        observeBirthdayList()
        setupSearchFunctionality()
        setupSortButton()
        setupBackButton()
        synchronizeBirthdaysWithFirebase()
        birthdayViewModel.getBirthdays("past_birthdays")

        return binding.root
    }

    // Ağ bağlantısını gözlemlemek için işlev
    private fun setupNetworkObserver() {
        networkConnectionObserver.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (userSharedPreferences.getUserCredentials().second != "guest") {
                if (isConnected) {
                    synchronizeBirthdaysWithFirebase()
                }
            }
        }
    }

    private fun synchronizeBirthdaysWithFirebase() {
        birthdayViewModel.getBirthdaysFromFirebase(
            userSharedPreferences.getUserId(),
            "past_birthdays"
        )
    }

    // RecyclerView ayarları
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    // Doğum günü listesini gözlemlemek için işlev
    private fun observeBirthdayList() {
        birthdayViewModel.getBirthdays("past_birthdays")
        birthdayViewModel.birthdayList.observe(viewLifecycleOwner) { birthdays ->
            updateUIBasedOnBirthdays(birthdays)
        }
    }

    private fun updateUIBasedOnBirthdays(birthdays: List<Birthday>) {
        binding.thereIsNoPastBirthdays.visibility =
            if (birthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        adapter.updateData(birthdays)
    }

    // Arama işlevini ayarlamak için işlev
    private fun setupSearchFunctionality() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Sıralama menüsü için işlev
    private fun setupSortButton() {
        binding.sortButton.setOnClickListener {
            showSortMenu(
                it,
                requireContext(),
                adapter,
                birthdayViewModel,
                birthdayViewModel.birthdayList.value ?: emptyList()
            )
        }
    }

    // Geri buton işlevi
    private fun setupBackButton() {
        binding.fabBackPastBirthdays.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        synchronizeBirthdaysWithFirebase()
        birthdayViewModel.getBirthdays("past_birthdays")
    }
}
