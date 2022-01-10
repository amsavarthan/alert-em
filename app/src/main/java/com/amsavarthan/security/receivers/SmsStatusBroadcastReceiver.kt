package com.amsavarthan.security.receivers

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE
import android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE
import android.util.Log
import com.amsavarthan.security.BuildConfig

class SmsStatusBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        var message: String
        var destination: String

        with(intent?.extras) {
            message = this?.getString("message") ?: ""
            destination = this?.getString("destination") ?: ""
        }

        when (intent?.action) {
            ACTION_SMS_SENT -> handleSentStatus(resultCode, message, destination)
            ACTION_SMS_DELIVERED -> handleDeliveryStatus(resultCode, message, destination)
            else -> Unit
        }
    }

    private fun handleDeliveryStatus(resultCode: Int, message: String, destination: String) {
        when (resultCode) {
            RESULT_OK -> debug("SMS DELIVERED", message, destination)
            RESULT_CANCELED -> debug("SMS NOT DELIVERED TO $destination", message, destination)
            else -> Unit
        }
    }

    private fun handleSentStatus(resultCode: Int, message: String, destination: String) {
        when (resultCode) {
            RESULT_OK -> debug("SMS SENT", message, destination)
            RESULT_ERROR_GENERIC_FAILURE -> debug("SMS GENERIC FAILURE", message, destination)
            RESULT_ERROR_NO_SERVICE -> debug("SMS NO SERVICE", message, destination)
            else -> Unit
        }
    }

    companion object {
        const val ACTION_SMS_SENT = "${BuildConfig.APPLICATION_ID}.SMS_SENT"
        const val ACTION_SMS_DELIVERED = "${BuildConfig.APPLICATION_ID}.SMS_DELIVERED"
    }

    private fun debug(message: String, smsMessage: String, destination: String) {
        Log.d("DEBUG", "###################################")
        Log.d("DEBUG", "status: $message")
        Log.d("DEBUG", "destination: $destination")
        Log.d("DEBUG", "message: $smsMessage")
        Log.d("DEBUG", "###################################")
    }

}