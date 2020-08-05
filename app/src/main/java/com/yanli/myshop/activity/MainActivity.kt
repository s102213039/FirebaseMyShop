package com.yanli.myshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yanli.myshop.R
import com.yanli.myshop.database.ItemDatabase
import com.yanli.myshop.holder.ItemHolder
import com.yanli.myshop.model.Category
import com.yanli.myshop.model.Item
import com.yanli.myshop.viewModel.ItemViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    private val REQUEST_LOGIN = 101
    var categories = mutableListOf<Category>()
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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

        FirebaseFirestore.getInstance().collection("categories").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let {
                        categories.add(Category("", "全部"))
                        for (document in it) {
//                            println("------- ${document.id}")
                            categories.add(
                                Category(
                                    document.id,
                                    document.data["name"].toString()
                                )
                            )
                        }
                        spinner.adapter = ArrayAdapter<Category>(
                            this@MainActivity,
                            android.R.layout.simple_spinner_item,
                            categories
                        ).apply {
                            setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                        }
                        spinner.setSelection(0, false)
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    itemViewModel.setCategory(categories[position].id)
                                }
                            }
                    }
                }
            }

        //recyclerView
        itemViewModel = ViewModelProvider(this@MainActivity).get(ItemViewModel::class.java)
        itemViewModel.getItems().observe(this, androidx.lifecycle.Observer {

            Thread {
                for (item in it) {
                    ItemDatabase.getDatabase(this).getItemDao().addItem(item)
                }
            }.run()
            itemAdapter.items = it
            itemAdapter.notifyDataSetChanged()
//
//            it.forEach { item ->
//                ItemDatabase.getDatabase(this)?.getItemDao()?.addItem(item)
//            }
        })

        itemAdapter = ItemAdapter(mutableListOf())
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = itemAdapter
    }

    inner class ItemAdapter(var items: List<Item>) : RecyclerView.Adapter<ItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_row, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bindTo(items[position])
            holder.itemView.setOnClickListener {
                itemClicked(items[position], position)
            }
        }
    }

    private fun itemClicked(model: Item, position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("item", model)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
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
            R.id.action_biometric -> {
                startActivity(Intent(this, BioMetricActivity::class.java))
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
