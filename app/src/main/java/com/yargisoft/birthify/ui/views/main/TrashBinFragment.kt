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
import com.yargisoft.birthify.utils.helpers.BirthdaySortFunctions
import com.yargisoft.birthify.utils.helpers.FrequentlyUsedFunctions
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthTrashBinBinding
import com.yargisoft.birthify.ui.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.helpers.NetworkConnectionObserver
import com.yargisoft.birthify.data.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class TrashBinFragment : Fragment() {

    private var _binding: FragmentAuthTrashBinBinding? = null
    private val binding get() = _binding!!
    private val birthdayViewModel: BirthdayViewModel by viewModels()

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    @Inject
    lateinit var adapter: DeletedBirthdayAdapter

    @Inject
    lateinit var networkConnectionObserver: NetworkConnectionObserver

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthTrashBinBinding.inflate(inflater, container, false)
        setupNetworkObserver()
        setupRecyclerView()
        setupSearchFunctionality()
        setupSortButton()
        setupBackButton()
        observeDeletedBirthdays()
        syncBirthdaysWithFirebase()
        fetchDeletedBirthdays()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        syncBirthdaysWithFirebase()
        fetchDeletedBirthdays()
    }

    private fun setupNetworkObserver() {
        networkConnectionObserver = NetworkConnectionObserver(requireContext())
        networkConnectionObserver.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (userSharedPreferences.getUserCredentials().second != "guest" && isConnected) {
                birthdayViewModel.getBirthdaysFromFirebase(
                    userSharedPreferences.getUserId(),
                    "deleted_birthdays"
                )
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        adapter.setOnDetailClickListener { birthday ->
            navigateToBirthdayDetail(birthday)
        }
    }

    private fun navigateToBirthdayDetail(birthday: Birthday) {
        val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
        findNavController().navigate(action)
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
            BirthdaySortFunctions.showSortMenu(
                it,
                requireContext(),
                adapter,
                birthdayViewModel,
                birthdayViewModel.birthdayList.value ?: emptyList()
            )
        }
    }

    private fun setupBackButton() {
        binding.fabBackButtonTrash.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeDeletedBirthdays() {
        birthdayViewModel.birthdayList.observe(viewLifecycleOwner) { deletedBirthdays ->
            binding.trashBinMainTv.visibility =
                if (deletedBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
            adapter.updateData(deletedBirthdays)
            adapter.setOnDetailClickListener { birthday ->
                navigateToBirthdayDetail(birthday)
            }
        }
    }

    private fun syncBirthdaysWithFirebase() {
        birthdayViewModel.getBirthdaysFromFirebase(
            userSharedPreferences.getUserId(),
            "deleted_birthdays"
        )
    }

    private fun fetchDeletedBirthdays() {
        birthdayViewModel.getBirthdays("deleted_birthdays")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
