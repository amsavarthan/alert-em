package com.amsavarthan.security.data.database.guardian

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GuardianDao {

    @Query("SELECT * FROM guardian")
    fun getAllGuardianDetailsFlow(): Flow<List<Guardian>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardian(guardian: Guardian)

    @Delete
    suspend fun deleteGuardian(guardian: Guardian)

}