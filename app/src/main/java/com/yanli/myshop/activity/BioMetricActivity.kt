package com.yanli.myshop.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.facebook.internal.FacebookRequestErrorClassification.KEY_NAME
import com.yanli.myshop.R
import kotlinx.android.synthetic.main.activity_bio_metric.*
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.KeyStore
import java.util.*
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BioMetricActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var notificationManager: NotificationManager
    private lateinit var mBuilder: NotificationCompat.Builder

    companion object {
        val CHANNEL_ID = "TEST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bio_metric)

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Log.e(
                    "MY_APP_TAG", "The user hasn't associated " +
                            "any biometric credentials with their account."
                )
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val encryptedInfo: ByteArray? = result.cryptoObject?.cipher?.doFinal(
                        "hello".toByteArray(Charset.defaultCharset())
                    )

                    Log.d("MY_APP_TAG", "Encrypted information: " + Arrays.toString(encryptedInfo))

                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            generateSecretKey(
                KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    // Invalidate the keys if the user has registered a new biometric
                    // credential, such as a new fingerprint. Can call this method only
                    // on Android 7.0 (API level 24) or higher. The variable
                    // "invalidatedByBiometricEnrollment" is true by default.
                    .setInvalidatedByBiometricEnrollment(true)
                    .build()
            )
        }


        biometricPrompt.authenticate(promptInfo)


        log_in.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }


        notification_on.setOnClickListener {
            showNotification()
        }

        notification_off.setOnClickListener {
            cancelNotification()
        }

        notification_download.setOnClickListener {
            showDownloadNotification()
        }
    }

    private fun showDownloadNotification() {
        initRemoteView()
    }

    private fun initRemoteView() {
        createNotificationChannel()


        mBuilder = NotificationCompat.Builder(this, "lottery")
        with(mBuilder) {
//            setWhen(System.currentTimeMillis())
            setSmallIcon(R.drawable.ic_launcher_foreground)
//            setCustomBigContentView(RemoteViews(application.packageName, R.layout.progress))
            priority = NotificationCompat.PRIORITY_MAX
            setAutoCancel(true)
//            setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
        }


        fakeDownload()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "lottery",
//                this.getString(R.string.app_name),
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            channel.setSound(null, null)
//        }


//        val updatePendingIntent = PendingIntent.getActivity(
//            this, 0,
//            Intent(this, RemoteViews::class.java), 0
//        )


        with(mBuilder.build()) {
//            contentIntent = updatePendingIntent
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                contentView = RemoteViews(application.packageName, R.layout.progress)
                contentView.setProgressBar(R.id.progressBar1, 100, 0, false)
                contentView.setTextViewText(R.id.textView1, "0%")
                contentView.setTextViewText(
                    R.id.text_update_name,
                    "hahaha... " + getString(R.string.app_name)
                )
//            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hello"
            val descriptionText = "Fuck off"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
            // Register the channel with the system
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        createNotificationChannel()
        mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        with(mBuilder) {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle("This is a title")
            setContentText("This is a text")
            priority = NotificationCompat.PRIORITY_MAX
            setAutoCancel(true)
            setContentIntent(pendingIntent)
        }


//        notificationManager.notify(0, mBuilder.build())
        with(NotificationManagerCompat.from(this)) {
            notify(0, mBuilder.build())
        }
    }

    private fun cancelNotification() {
        notificationManager.cancelAll()
        notificationManager.cancel(0)
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey(KEY_NAME, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        } else {
            TODO("VERSION.SDK_INT < M")
        }
    }

    private fun encryptSecretInformation() {
        // Exceptions are unhandled for getCipher() and getSecretKey().
        val cipher = getCipher()
        val secretKey = getSecretKey()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                val encryptedInfo: ByteArray = cipher.doFinal(
                    "Hello".toByteArray(Charset.defaultCharset())
                )
                Log.d(
                    "MY_APP_TAG", "Encrypted information: " +
                            Arrays.toString(encryptedInfo)
                )
            } catch (e: InvalidKeyException) {
                Log.e("MY_APP_TAG", "Key is invalid.")
            } catch (e: UserNotAuthenticatedException) {
                Log.d("MY_APP_TAG", "The key's validity timed out.")
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }

    private fun fakeDownload() {
        // Start a the operation in a background thread

        // Start a the operation in a background thread
        Thread {
            Log.e("TAG", "fakeDownload: HHHHHHHHHHHHHHHHHHHHH")
            var incr = 0
            // Do the "lengthy" operation 20 times
            while (incr <= 100) {
                Log.e("TAG", "fakeDownload: ${incr}")

                // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
                mBuilder.setProgress(100, incr, false)
                // Displays the progress bar for the first time.
                with(NotificationManagerCompat.from(this)) {
                    notify(0, mBuilder.build())
                }                // Sleeps the thread, simulating an operation
                try {
                    // Sleep for 1 second
                    Thread.sleep(1 * 1000.toLong())
                } catch (e: InterruptedException) {
                    Log.d("TAG", "sleep failure")
                }
                incr += 5
            }
            // When the loop is finished, updates the notification
            mBuilder.setContentText("Download completed") // Removes the progress bar
                .setProgress(0, 0, false)
            with(NotificationManagerCompat.from(this)) {
                notify(0, mBuilder.build())
            }
        }.start()
    }
}