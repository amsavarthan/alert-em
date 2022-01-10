package com.amsavarthan.security.data.database.guardian

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guardian")
@Keep
data class Guardian(
    @ColumnInfo var name: String,
    @PrimaryKey @ColumnInfo(name = "phone_number") var phoneNumber: String,
)
