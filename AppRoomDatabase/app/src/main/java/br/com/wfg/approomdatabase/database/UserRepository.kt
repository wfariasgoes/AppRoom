package br.com.wfg.approomdatabase.database

import br.com.wfg.approomdatabase.model.User
import io.reactivex.Flowable

class UserRepository(private val mLocationDataSource: IUserDataSource) : IUserDataSource {

    override val allUser: Flowable<List<User>>
        get() = mLocationDataSource.allUser

    override fun getuserById(userId: Int): Flowable<User> {
        return mLocationDataSource.getuserById(userId)
    }

    override fun insertUser(vararg users: User) {
        mLocationDataSource.insertUser(*users)
    }

    override fun updateUser(vararg users: User) {
        mLocationDataSource.updateUser(*users)
    }

    override fun deleteUser(user: User) {
        mLocationDataSource.deleteUser(user)
    }

    override fun deleteAllUsers() {
        mLocationDataSource.deleteAllUsers()
    }

    companion object {
        private var mInstance: UserRepository?=null

        fun getInstance(mLocationDataSource: IUserDataSource): UserRepository{
            if (mInstance == null)
                mInstance = UserRepository(mLocationDataSource)

            return mInstance!!
        }

    }
}