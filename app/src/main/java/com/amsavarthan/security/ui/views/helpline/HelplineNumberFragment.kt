package com.amsavarthan.security.ui.views.helpline

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amsavarthan.security.data.adapter.ItemsAdapter
import com.amsavarthan.security.data.database.guardian.Guardian
import com.amsavarthan.security.databinding.FragmentHelplineNumbersBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.X

class HelplineNumberFragment : Fragment(), Handler, ItemsAdapter.Handler {

    private var _binding: FragmentHelplineNumbersBinding? = null
    private val binding get() = _binding!!

    private val helpLineNumbers = listOf(
        Guardian(
            name = "National Emergency Number",
            phoneNumber = "112"
        ),
        Guardian(
            name = "Police",
            phoneNumber = "100"
        ),
        Guardian(
            name = "Fire",
            phoneNumber = "101"
        ),
        Guardian(
            name = "Ambulance",
            phoneNumber = "102"
        ),
        Guardian(
            name = "Disaster Management Services",
            phoneNumber = "108"
        ),
        Guardian(
            name = "Women Helpline",
            phoneNumber = "1091"
        ),
        Guardian(
            name = "Road Accident Emergency Service",
            phoneNumber = "1073"
        ),
        Guardian(
            name = "Cyber Crime Helpline",
            phoneNumber = "155620"
        ),
    )

    private var tempPhoneToDial: String = ""
    private val requestCallPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when (isGranted) {
            true -> makeCall(tempPhoneToDial)
            else -> Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(X, true)
        exitTransition = MaterialFade()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelplineNumbersBinding.inflate(inflater, container, false)
        binding.handler = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ItemsAdapter(this, true)
        adapter.submitList(helpLineNumbers)
        binding.recyclerView.setHasFixedSize(true)

        binding.adapter = adapter

    }

    override fun onIconClicked(guardian: Guardian) {
        makeCall(guardian.phoneNumber)
    }

    override fun onItemClicked(guardian: Guardian) {
        makeCall(guardian.phoneNumber)
    }

    private fun makeCall(phoneNumber: String) {

        tempPhoneToDial = phoneNumber

        val permissionStatus = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CALL_PHONE
        )

        val intent = Intent().apply {
            action = Intent.ACTION_CALL
            data = Uri.parse("tel:$phoneNumber")
        }

        when (permissionStatus) {
            PackageManager.PERMISSION_DENIED -> requestCallPermission.launch(Manifest.permission.CALL_PHONE)
            else -> {
                tempPhoneToDial = ""
                startActivity(intent)
            }
        }

    }

    override fun onBackButtonPressed(view: View) {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}