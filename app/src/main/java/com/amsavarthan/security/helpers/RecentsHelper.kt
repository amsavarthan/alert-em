package com.amsavarthan.security.helpers

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import androidx.core.content.ContextCompat

data class RecentCall(
    val name: String,
    val phoneNumber: String,
    var occurance: Int = 0
)

object RecentsHelper {

    fun getRecentCallLogs(
        context: Context,
        limit: Int = 10
    ): List<RecentCall> {

        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        )

        if (permissionStatus == PackageManager.PERMISSION_DENIED) return emptyList()

        val recentCalls = _getRecentCallLogs(context)

        if (recentCalls.size <= limit) return recentCalls
        return recentCalls.subList(0, limit)
    }

    private fun _getRecentCallLogs(context: Context): List<RecentCall> {

        val recentCalls = mutableListOf<RecentCall>()
        var previousRecentCallFrom = ""

        val uri = CallLog.Calls.CONTENT_URI
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
        )

        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bundle = Bundle().apply {
                putStringArray(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    arrayOf(CallLog.Calls._ID)
                )
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
            }

            context.contentResolver.query(uri, projection, bundle, null)
        } else {
            val sortOrder = "${CallLog.Calls._ID} DESC"
            context.contentResolver.query(uri, projection, null, null, sortOrder)
        }


        if (cursor != null && cursor.moveToFirst()) {
            do {
                val nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)

                val name = cursor.getString(nameIndex) ?: ""
                val number = cursor.getString(numberIndex)

                val recentCall = RecentCall(name, number)
                if ("$number$name" != previousRecentCallFrom) {
                    recentCalls.add(recentCall)
                } else {
                    recentCalls.lastOrNull()?.let {
                        it.occurance += 1
                    }
                }

                previousRecentCallFrom = "$number$name"

            } while (cursor.moveToNext())
        }

        cursor?.close()
        return recentCalls
    }

}