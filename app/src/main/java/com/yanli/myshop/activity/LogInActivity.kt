package com.yanli.myshop.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yanli.myshop.R
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN_REQUEST = 102
    private lateinit var googleSignClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignClient = GoogleSignIn.getClient(this, gso)

        google_signin.setOnClickListener {
            startActivityForResult(googleSignClient.signInIntent, GOOGLE_SIGN_IN_REQUEST)
        }


        btn_signup.setOnClickListener {
            signUp()
        }

        btn_login.setOnClickListener {
            logIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                Log.d(":::::", "onActivityResult ${account?.id}")
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                FirebaseAuth.getInstance()
                    .signInWithCredential(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Log.d(":::::", "onActivityResult ${it.exception?.message}")
                            Snackbar.make(
                                main_log_in,
                                "Firebase authentication failed",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private fun logIn() {
        val email = input_email.text.toString()
        val password = input_password.text.toString()
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    AlertDialog.Builder(this@LogInActivity)
                        .setTitle("Log In")
                        .setMessage(it.exception?.message)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
    }

    private fun signUp() {
        val email = input_email.text.toString()
        val password = input_password.text.toString()
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    AlertDialog.Builder(this@LogInActivity)
                        .setTitle("Sign Up")
                        .setMessage("Account created!")
                        .setPositiveButton("OK") { dialog, which ->
                            setResult(Activity.RESULT_OK)
                            finish()
                        }.show()
                } else {
                    AlertDialog.Builder(this@LogInActivity)
                        .setTitle("Sign Up")
                        .setMessage(it.exception?.message)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
    }
}
