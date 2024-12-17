package com.yargisoft.birthify.ui.views.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yargisoft.birthify.utils.helpers.BirthdaySortFunctions.showSortMenu
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthPastBirthdaysBinding
import com.yargisoft.birthify.ui.adapters.PastBirthdayAdapter
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.helpers.NetworkConnectionObserver
import com.yargisoft.birthify.data.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PastBirthdaysFragment : Fragment() {

    private var _binding: FragmentAuthPastBirthdaysBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentAuthPastBirthdaysBinding.inflate(inflater, container, false)

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

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

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

    private fun setupSearchFunctionality() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
