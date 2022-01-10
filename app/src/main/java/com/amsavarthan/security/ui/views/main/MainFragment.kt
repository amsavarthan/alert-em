package com.amsavarthan.security.ui.views.main

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.amsavarthan.security.App
import com.amsavarthan.security.R
import com.amsavarthan.security.data.datastore.AppDataStore
import com.amsavarthan.security.data.datastore.dataStore
import com.amsavarthan.security.databinding.FragmentMainBinding
import com.amsavarthan.security.helpers.RecentsHelper
import com.amsavarthan.security.receivers.SmsStatusBroadcastReceiver
import com.amsavarthan.security.receivers.SmsStatusBroadcastReceiver.Companion.ACTION_SMS_DELIVERED
import com.amsavarthan.security.receivers.SmsStatusBroadcastReceiver.Companion.ACTION_SMS_SENT
import com.amsavarthan.security.ui.viewmodels.GuardianViewModel
import com.amsavarthan.security.ui.viewmodels.MyViewModelFactory
import com.amsavarthan.security.ui.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class MainFragment : Fragment(), Handler {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<MainFragmentArgs>()

    private var canAlert = true
    private var _indefiniteSnackBar: Snackbar? = null
    private val smsManager by lazy {
        ContextCompat.getSystemService(
            requireContext(),
            SmsManager::class.java
        )
    }

    private val appDataStore by lazy {
        AppDataStore(requireContext().dataStore)
    }

    private val guardianViewModel: GuardianViewModel by viewModels {
        MyViewModelFactory(
            guardianDao = (activity?.application as App).database.guardianDao(),
        )
    }

    private val userViewModel: UserViewModel by viewModels {
        MyViewModelFactory(
            userDao = (activity?.application as App).database.userDao(),
        )
    }

    private val requestSendSMSPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when (isGranted) {
                true -> initiateAlert()
                else -> snackBar("Permission denied")
            }
        }

    private val requestMultiplePermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.run {
            handler = this@MainFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFade()
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.studentViewModel = userViewModel

        requestAppPermissions()

        appDataStore.preferenceFlow.asLiveData().observe(viewLifecycleOwner) { isFirstRun ->

            if (!isFirstRun) {
                binding.layout.visibility = View.VISIBLE
                return@observe
            }

            if (args.bypassFirstRunCheck) {
                binding.layout.visibility = View.VISIBLE
                lifecycleScope.launch {
                    appDataStore.saveFirstRunStatusToPreferencesStore(requireContext(), false)
                }
                return@observe
            }

            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToStudentDetailsFragment(),
                navOptions {
                    popUpTo(R.id.mainFragment) {
                        inclusive = true
                    }
                }
            )
        }

    }

    private fun requestAppPermissions() {
        val smsPermissionStatus = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.SEND_SMS
        )
        val callLogPermissionStatus = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_CALL_LOG
        )

        val permissionsToAsk = mutableListOf<String>()

        if (smsPermissionStatus == PackageManager.PERMISSION_DENIED) {
            permissionsToAsk.add(Manifest.permission.SEND_SMS)
        }

        if (callLogPermissionStatus == PackageManager.PERMISSION_DENIED) {
            permissionsToAsk.add(Manifest.permission.READ_CALL_LOG)
        }

        if (permissionsToAsk.isNotEmpty())
            requestMultiplePermission.launch(permissionsToAsk.toTypedArray())
    }

    private fun initiateAlert(): Boolean {
        if (!canAlert) {
            snackBar(getString(R.string.please_wait))
            return false
        }
        snackBar(
            getString(R.string.alerting_status),
            Snackbar.LENGTH_INDEFINITE
        )
        updateUI(false)
        return true
    }

    private fun sendAlertAsSMS() {
        try {

            val user = userViewModel.user.value ?: return

            val recentCallLogsAsMessage = StringBuilder()
                .append("Here is ${user.name}'s recent call log :")
                .appendLine()
                .appendLine()

            val callLogs = RecentsHelper.getRecentCallLogs(requireContext())
            callLogs.forEachIndexed { index, call ->
                val callerName = if (call.name.isBlank())
                    getString(R.string.unknown_caller) else call.name

                with(recentCallLogsAsMessage) {
                    append("${index + 1}. $callerName - ${call.phoneNumber}")
                    if (call.occurance > 0) append(" (${call.occurance})")
                    if (index != callLogs.size - 1) appendLine()
                }

            }

            guardianViewModel.guardians.observe(viewLifecycleOwner) { guardians ->
                if (guardians.isEmpty()) return@observe
                guardians.forEach { guardian ->

                    val destination = guardian.phoneNumber.formatAsNumber()
                    val message = getString(
                        R.string.sms_template,
                        user.name,
                        user.gender.pronoun
                    )

                    sendSMS(message, destination)
                    if (callLogs.isNotEmpty()) {
                        sendSMS(recentCallLogsAsMessage.toString(), destination, true)
                    }

                }
            }

        } finally {
            updateUI(true)
        }
    }

    private fun updateUI(revertToNormal: Boolean) {

        if (revertToNormal) {
            binding.buttonThumb.run {
                removeAllAnimatorListeners()
                speed = -1f
                playAnimation()
            }
            Timer().schedule(TIMEOUT) {
                canAlert = true
            }
            _indefiniteSnackBar?.dismiss()
            return
        }

        canAlert = false
        binding.buttonThumb.run {
            speed = 1f
            playAnimation()
            addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    sendAlertAsSMS()
                }
            })
        }

    }

    private fun sendSMS(message: String, destination: String, multipart: Boolean = false) {

        val extras = Bundle().apply {
            putString("destination", destination)
            putString("message", message)
        }

        val sentPI = PendingIntent.getBroadcast(
            requireContext(),
            0,
            Intent(requireContext(), SmsStatusBroadcastReceiver::class.java).apply {
                action = ACTION_SMS_SENT
                putExtras(extras)
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }
        )

        val deliveredPI = PendingIntent.getBroadcast(
            requireContext(),
            0,
            Intent(requireContext(), SmsStatusBroadcastReceiver::class.java).apply {
                action = ACTION_SMS_DELIVERED
                putExtras(extras)
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }
        )

        if (multipart) {
            val parts = smsManager?.divideMessage(message)
            smsManager?.sendMultipartTextMessage(destination, null, parts, null, null)
            return
        }

        smsManager?.sendTextMessage(
            destination,
            null,
            message,
            sentPI,
            deliveredPI
        )
    }

    private fun snackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        val snackBar = Snackbar.make(binding.root, message, duration)
        if (duration == Snackbar.LENGTH_INDEFINITE) {
            _indefiniteSnackBar = snackBar
        }
        snackBar.show()
    }

    override fun onSettingsButtonClicked(view: View) {
        if (_indefiniteSnackBar?.isShown == true) return
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToStudentDetailsFragment())
    }

    override fun goToHelplineNumbersFragment(view: View) {
        if (_indefiniteSnackBar?.isShown == true) return
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToHelplineNumberFragment())
    }

    override fun onButtonThumbLongPressed(view: View): Boolean {

        val permissionStatus = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.SEND_SMS
        )

        return when (permissionStatus) {
            PackageManager.PERMISSION_DENIED -> {
                requestSendSMSPermission.launch(Manifest.permission.SEND_SMS)
                true
            }
            else -> initiateAlert()
        }

    }

    private fun String.formatAsNumber(): String {
        if (!this.startsWith("+91")) {
            return "+91${this.replace(" ", "")}"
        }
        return this.replace(" ", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TIMEOUT: Long = 10000
    }
}


