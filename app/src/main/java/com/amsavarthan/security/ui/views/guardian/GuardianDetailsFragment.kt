package com.amsavarthan.security.ui.views.guardian

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.amsavarthan.security.App
import com.amsavarthan.security.R
import com.amsavarthan.security.data.adapter.ItemsAdapter
import com.amsavarthan.security.data.database.guardian.Guardian
import com.amsavarthan.security.data.datastore.AppDataStore
import com.amsavarthan.security.data.datastore.dataStore
import com.amsavarthan.security.databinding.FragmentGuardianDetailsBinding
import com.amsavarthan.security.helpers.ContactsHelper
import com.amsavarthan.security.ui.viewmodels.GuardianViewModel
import com.amsavarthan.security.ui.viewmodels.MyViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialSharedAxis

class GuardianDetailsFragment : Fragment(), Handler, ItemsAdapter.Handler {

    private var _binding: FragmentGuardianDetailsBinding? = null
    private val binding get() = _binding!!

    private val appDataStore by lazy {
        AppDataStore(requireContext().dataStore)
    }

    private val viewModel: GuardianViewModel by viewModels {
        MyViewModelFactory(
            guardianDao = (activity?.application as App).database.guardianDao(),
        )
    }

    private val requestReadContactPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when (isGranted) {
                true -> openContactPicker()
                else -> Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

    private val requestForPickContact =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> addToEmergencyContact(result.data?.data)
                else -> Unit
            }
        }

    private val contactsAdapter: ItemsAdapter by lazy {
        ItemsAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialFade()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuardianDetailsBinding.inflate(inflater, container, false)
        binding.handler = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.adapter = contactsAdapter
        viewModel.guardians.observe(viewLifecycleOwner, contactsAdapter::submitList)

    }

    private fun openContactPicker() {
        requestForPickContact.launch(Intent().apply {
            action = Intent.ACTION_PICK
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        })
    }

    private fun addToEmergencyContact(contactUri: Uri?) {

        val contact = ContactsHelper.getContact(requireContext(), contactUri)
        if (contact == null) {
            snackBar("Error getting contact")
            return
        }

        contact.let { (name, phone) ->
            val guardian = Guardian(name, phone)
            viewModel.insertGuardian(guardian)
        }

    }

    override fun onAddContactButtonClicked(view: View) {

        val permissionStatus = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        )

        when (permissionStatus) {
            PackageManager.PERMISSION_GRANTED -> openContactPicker()
            else -> requestReadContactPermission.launch(Manifest.permission.READ_CONTACTS)
        }

    }

    override fun onNextButtonClicked(view: View) {

        if (contactsAdapter.itemCount == 0) {
            snackBar("Add at least one contact to continue")
            return
        }

        goToHomeFragment()
    }

    private fun goToHomeFragment() {
        val navController = findNavController()

        if (isMainFragmentPresentInBackStack(navController)) {
            navController.popBackStack(R.id.userDetailsFragment, true)
        } else {
            navController.navigate(
                GuardianDetailsFragmentDirections.actionGuardianDetailsFragmentToMainFragment(
                    bypassFirstRunCheck = true
                ),
                navOptions {
                    popUpTo(R.id.userDetailsFragment) {
                        inclusive = true
                    }
                }
            )
        }

    }

    override fun onIconClicked(guardian: Guardian) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Remove contact")
            setMessage("Are you sure, Do you want to remove this contact?")
            setPositiveButton("Remove") { _, _ ->
                viewModel.deleteGuardian(guardian)
            }
            setNegativeButton("No", null)
            show()
        }
    }

    override fun onItemClicked(guardian: Guardian) {
        //do nothing
    }

    private fun isMainFragmentPresentInBackStack(navController: NavController): Boolean = try {
        navController.getBackStackEntry(R.id.mainFragment)
        true
    } catch (e: IllegalArgumentException) {
        false
    }

    private fun snackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}