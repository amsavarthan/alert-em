package com.amsavarthan.security.helpers

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

object ContactsHelper {

    fun getContact(context: Context, contactUri: Uri?): Pair<String, String>? {

        var contact: Pair<String, String>? = null

        if (contactUri == null) return contact

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        )
        val cursor = context.contentResolver.query(
            contactUri,
            projection,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {

            val nameIndex = cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )

            val numberIndex = cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            val name = cursor.getString(nameIndex)
            val phoneNumberFromCursor = cursor.getString(numberIndex)

            val formattedPhoneNumber = try {
                val phoneUtil = PhoneNumberUtil.createInstance(context)
                val phoneNumber = phoneUtil.parse(phoneNumberFromCursor, "IN")
                phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
            } catch (e: Exception) {
                phoneNumberFromCursor
            }

            contact = Pair(name, formattedPhoneNumber)

        }

        cursor?.close()
        return contact

    }

}