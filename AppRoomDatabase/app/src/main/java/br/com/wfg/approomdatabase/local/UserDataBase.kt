package br.com.wfg.approomdatabase.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import br.com.wfg.approomdatabase.local.UserDataBase.Companion.DATABASE_VERSION
import br.com.wfg.approomdatabase.model.User

@Database(entities = [User::class], version = DATABASE_VERSION)
abstract class UserDataBase: RoomDatabase() {
    abstract fun userDao(): UserDAO

    companion object {
        const val DATABASE_VERSION = 1
        private const val DATABASE_NAME="wfg-Database-Room"

        private var mInstance: UserDataBase?=null

        fun getInstance(context: Context): UserDataBase{
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(context, UserDataBase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return mInstance!!
        }
    }


}