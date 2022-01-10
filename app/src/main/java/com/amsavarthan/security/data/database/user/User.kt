package com.amsavarthan.security.data.database.user

import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
@Keep
data class User(
    @NonNull @PrimaryKey @ColumnInfo val id: Int = 1,
    @NonNull @ColumnInfo var name: String,
    @NonNull @ColumnInfo(name = "phone_number") var phoneNumber: String,
    @NonNull @ColumnInfo var gender: Gender
)
