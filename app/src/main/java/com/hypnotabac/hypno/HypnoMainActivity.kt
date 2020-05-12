package com.hypnotabac

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_h_main.*

class HypnoMainActivity : AppCompatActivity() {
    val TAG = "hMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_main)


        sendemail.setOnClickListener{
            val email = username!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            Log.d(TAG, email)
            Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
            val actionCodeSettings =
                ActionCodeSettings.newBuilder() // URL you want to redirect back to. The domain (www.example.com) for this
                    // URL must be whitelisted in the Firebase Console.
                    .setUrl("https://hypnotabac.page.link/jdF1") // This must be true
                    .setHandleCodeInApp(true)
                    .setIOSBundleId("com.hypnotabac.ios")
                    .setAndroidPackageName(
                        "com.hypnotabac",
                        true,  /* installIfNotAvailable */
                        "12" /* minimumVersion */
                    )
                    .build()
            Log.d(TAG, "built")
            Toast.makeText(this, "built", Toast.LENGTH_SHORT).show()

            FirebaseAuth.getInstance().sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                        Toast.makeText(this, "Email sent.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error : "+e.message)
                    Toast.makeText(this, "Error : "+e.stackTrace, Toast.LENGTH_SHORT).show()
                }
        }
    }
}
