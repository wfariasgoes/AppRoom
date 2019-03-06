package br.com.wfg.approomdatabase

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import br.com.wfg.approomdatabase.database.UserRepository
import br.com.wfg.approomdatabase.local.UserDataBase
import br.com.wfg.approomdatabase.local.UserDataSource
import br.com.wfg.approomdatabase.model.User
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var adapter: ArrayAdapter<*>
    private var userList: MutableList<User> = ArrayList()

    //Database
    private var compositeDisposable: CompositeDisposable? = null
    private var userRepository: UserRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init
        compositeDisposable = CompositeDisposable()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        registerForContextMenu(listUsers)
        listUsers!!.adapter = adapter

        //DataBase
        val userDataBase = UserDataBase.getInstance(this)
        userRepository = UserRepository.getInstance(UserDataSource.getInstance(userDataBase.userDao()))

        //Looad all data from DB
        loadData()

        //Event Fab
        fabFloat.setOnClickListener {
            val disposable = Observable.create(ObservableOnSubscribe<Any> { e ->
                val user = User()
                user.nome = "wesleygoes"
                user.email = UUID.randomUUID().toString()+"@gmail.com"

                userRepository!!.insertUser(user)
                e.onComplete()
            }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer {
                    //Success
                },
                    io.reactivex.functions.Consumer { trowable ->
                        Toast.makeText(this@MainActivity, "" + trowable.message, Toast.LENGTH_SHORT)
                            .show()
                    },
                    Action { loadData() })
            compositeDisposable!!.addAll(disposable)
        }

    }

    private fun loadData() {
        val disposale = userRepository!!.allUser
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ users -> onGetAllUserSuccess(users) }) { trowable ->
                    Toast.makeText(this@MainActivity, "" + trowable.message, Toast.LENGTH_SHORT)
                            .show()
                }
        compositeDisposable!!.add(disposale)
    }

    private fun onGetAllUserSuccess(users: List<User>?) {
        userList.clear()
        userList.addAll(users!!)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.clear -> deleteAllUser()

        }
        return super.onOptionsItemSelected(item)
    }

    //Delete All user
    private fun deleteAllUser() {
        val disposable = Observable.create(ObservableOnSubscribe<Any> { e ->
            userRepository!!.deleteAllUsers()
            e.onComplete()
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer {
                    //Success
                },
                        io.reactivex.functions.Consumer { trowable ->
                            Toast.makeText(this@MainActivity, "" + trowable.message, Toast.LENGTH_SHORT)
                                    .show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        menu.setHeaderTitle("Selecione a ação:")
        menu.add(Menu.NONE, 0, Menu.NONE, "UPDATE")
        menu.add(Menu.NONE, 1, Menu.NONE, "DELETE")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val user = userList[info.position]
        when (item.itemId) {
            0 //update
            -> {
                val edtNome = EditText(this@MainActivity)
                edtNome.setText(user.nome)
                edtNome.hint = "Entre com seu nome"

                //Create dialog
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Editar")
                        .setMessage("Editar usuário")
                        .setView(edtNome)
                        .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, wich ->
                            if (TextUtils.isEmpty(edtNome.text.toString()))
                                return@OnClickListener
                            else{
                                user.nome = edtNome.text.toString()
                                updateUser(user)
                            }
                        }).setNegativeButton(android.R.string.cancel) { dialog, which ->
                            dialog.dismiss()
                        }.create().show()
            }

            1 //Delete
            -> {
                AlertDialog.Builder(this@MainActivity)
                        .setMessage("Você tem certeza que deseja deletar?" + user.nome)
                        .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, wich ->
                            deleteUser(user)
                        }).setNegativeButton(android.R.string.cancel) { dialog, which ->
                            dialog.dismiss()
                        }.create().show()
            }
        }
        return true
    }

    private fun deleteUser(user: User) {
        val disposable = Observable.create(ObservableOnSubscribe<Any> { e ->
            userRepository!!.deleteUser(user)
            e.onComplete()
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer {
                    //Success
                },
                        io.reactivex.functions.Consumer { trowable ->
                            Toast.makeText(this@MainActivity, "" + trowable.message, Toast.LENGTH_SHORT)
                                    .show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)
    }

    private fun updateUser(user: User) {
        val disposable = Observable.create(ObservableOnSubscribe<Any> { e ->
            userRepository!!.updateUser(user)
            e.onComplete()
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(io.reactivex.functions.Consumer {
                    //Success
                },
                        io.reactivex.functions.Consumer { trowable ->
                            Toast.makeText(this@MainActivity, "" + trowable.message, Toast.LENGTH_SHORT)
                                    .show()
                        },
                        Action { loadData() })
        compositeDisposable!!.addAll(disposable)
    }

    override fun onDestroy() {
        compositeDisposable!!.clear()
        super.onDestroy()
    }
}
