package com.yanli.myshop.activity

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yanli.myshop.R
import com.yanli.myshop.holder.ItemHolder
import com.yanli.myshop.model.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    private val REQUEST_LOGIN = 101
    private lateinit var adapter : FirestoreRecyclerAdapter<Item, ItemHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //first push
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        btn_verify.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(it, "Verify success", Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        //recyclerView
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var query = FirebaseFirestore.getInstance()
            .collection("items")
            .limit(10)
        val options = FirestoreRecyclerOptions.Builder<Item>()
            .setQuery(query, Item::class.java)
            .build()
        adapter = object : FirestoreRecyclerAdapter<Item, ItemHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
                layoutInflater
                return ItemHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_row,
                        parent,
                        false
                    )
                )
            }

            override fun onBindViewHolder(holder: ItemHolder, position: Int, model: Item) {
                holder.bindTo(model)
            }

        }

        recycler.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
        adapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val whiteList = listOf("tw", "hk", "cn", "au")
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_login -> {
                startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(
                            Arrays.asList(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build(),
                                AuthUI.IdpConfig.FacebookBuilder().build(),
                                AuthUI.IdpConfig.PhoneBuilder()
                                    .setWhitelistedCountries(whiteList)
                                    .setDefaultCountryIso("tw")
                                    .build()
                            )
                        )
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.ic_online_store)
                        .setTheme(R.style.SignUp)
                        .build(), REQUEST_LOGIN
                )
//                startActivityForResult(Intent(this, LogInActivity::class.java), REQUEST_LOGIN)
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == REQUEST_LOGIN) {
//                //code here
//                val user = FirebaseAuth.getInstance().currentUser
//                if (user != null) {
//                    tv_id.text = "Email: ${user.email} / ${user.isEmailVerified}"
//                    btn_verify.visibility = if (user.isEmailVerified) View.GONE else View.VISIBLE
//                } else {
//                    tv_id.text = "Not login"
//                    btn_verify.visibility = View.GONE
//                }
//            }
//        }
//    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        val user = auth.currentUser
        if (user != null) {
            tv_id.text = "Email: ${user.email} / ${user.isEmailVerified}"
//            btn_verify.visibility = if (user.isEmailVerified) View.GONE else View.VISIBLE
        } else {
            tv_id.text = "Not login"
            btn_verify.visibility = View.GONE
        }
    }
}
