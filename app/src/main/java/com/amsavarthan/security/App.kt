package com.amsavarthan.security

import android.app.Application
import com.amsavarthan.security.data.database.LocalDatabase

class App : Application() {

    val database by lazy {
        LocalDatabase.getInstance(this)
    }

}