package com.amsavarthan.security.data.database.user

import androidx.annotation.Keep

@Keep
enum class Gender(
    val value: String,
    val pronoun: String
) {
    MALE(
        "Male",
        "him"
    ),
    FEMALE(
        "Female",
        "her"
    ),
    OTHER(
        "Other",
        "them"
    );
}