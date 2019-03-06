package br.com.wfg.approomdatabase.database

import br.com.wfg.approomdatabase.model.User
import io.reactivex.Flowable

interface IUserDataSource {
    val allUser: Flowable<List<User>>
    fun getuserById(userId: Int): Flowable<User>
    fun insertUser(vararg users: User)
    fun updateUser(vararg users: User)
    fun deleteUser(user: User)
    fun deleteAllUsers()
}