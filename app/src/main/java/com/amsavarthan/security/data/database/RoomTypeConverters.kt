package com.amsavarthan.security.data.database

import androidx.room.TypeConverter
import com.amsavarthan.security.data.database.user.Gender
import com.google.gson.Gson

class RoomTypeConverters {

    @TypeConverter
    fun genderToJSON(gender: Gender): String {
        return Gson().toJson(gender)
    }

    @TypeConverter
    fun jsonToGender(json: String): Gender {
        return Gson().fromJson(json, Gender::class.java)
    }

}