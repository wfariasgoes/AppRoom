package br.com.wfg.approomdatabase.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.reactivex.annotations.NonNull

@Entity(tableName = "users")
class User {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Int=0

    @ColumnInfo(name = "nome")
    var nome:String?=null

    @ColumnInfo(name = "email")
    var email:String?=null

    constructor()

    override fun toString(): String {
        return StringBuilder(nome)
                .append("\n")
                .append(email)
                .toString()
    }


}