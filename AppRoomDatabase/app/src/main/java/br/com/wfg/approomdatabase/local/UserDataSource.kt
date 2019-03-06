package br.com.wfg.approomdatabase.local

import br.com.wfg.approomdatabase.database.IUserDataSource
import br.com.wfg.approomdatabase.model.User
import io.reactivex.Flowable

class UserDataSource(private val userDao: UserDAO) : IUserDataSource {


    override val allUser: Flowable<List<User>>
        get() = userDao.allUsers

    override fun getuserById(userId: Int): Flowable<User> {
        return userDao.getUserById(userId)
    }

    override fun insertUser(vararg users: User) {
        return userDao.insertUser(*users)
    }

    override fun updateUser(vararg users: User) {
        return userDao.updateUser(*users)
    }

    override fun deleteUser(user: User) {
        return userDao.deleteUser(user)
    }

    override fun deleteAllUsers() {
        return userDao.deleteAllUsers()
    }

    companion object {
        private var mInstance: UserDataSource? = null

        fun getInstance(userDao: UserDAO): UserDataSource {
            if (mInstance == null)
                mInstance = UserDataSource(userDao)

            return mInstance!!
        }

    }
}