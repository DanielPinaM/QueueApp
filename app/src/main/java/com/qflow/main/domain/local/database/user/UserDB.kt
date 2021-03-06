package com.qflow.main.domain.local.database.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.auth.FirebaseAuth

/**
 * Here we define the local database structure for the entity User
 * */
@Entity(tableName = "users_table")
data class UserDB (

    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0L,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "id_firebase")
    var id_firebase: String

)