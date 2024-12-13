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
import com.yargisoft.birthify.BirthdaySortFunctions
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthTrashBinBinding
import com.yargisoft.birthify.ui.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.NetworkConnectionObserver
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class TrashBinFragment : Fragment() {

    private lateinit var binding: FragmentAuthTrashBinBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth_trash_bin, container, false)
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

    // Ağ bağlantısını kontrol eder ve gerekli işlemleri yapar
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

    // RecyclerView'ı kurar
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        adapter.setOnDetailClickListener { birthday ->
            navigateToBirthdayDetail(birthday)
        }
    }

    // Doğum günü detay ekranına yönlendirir
    private fun navigateToBirthdayDetail(birthday: Birthday) {
        val action = TrashBinFragmentDirections.trashToDeletedDetail(birthday)
        findNavController().navigate(action)
    }

    // Doğum günlerini arama işlevi
    private fun setupSearchFunctionality() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Sıralama menüsünü ayarlar
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

    // Geri butonu işlevi
    private fun setupBackButton() {
        binding.fabBackButtonTrash.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Silinen doğum günlerini gözlemler ve adapter'ı günceller
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

    // Firebase'den doğum günlerini senkronize eder
    private fun syncBirthdaysWithFirebase() {
        birthdayViewModel.getBirthdaysFromFirebase(
            userSharedPreferences.getUserId(),
            "deleted_birthdays"
        )
    }

    // Silinen doğum günlerini yerel olarak getirir
    private fun fetchDeletedBirthdays() {
        birthdayViewModel.getBirthdays("deleted_birthdays")
    }
}
