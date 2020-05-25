package com.hypnotabac.hypno

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hypnotabac.BuildConfig
import com.hypnotabac.R
import kotlinx.android.synthetic.main.activity_add_client.*

class AddClientActivity : AppCompatActivity() {
    val TAG = "AddClientActivity"
    val firebaseDatabase = FirebaseDatabase.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)
        sendemail.setOnClickListener{
            val email = editEmail!!.text.toString().trim { it <= ' ' }
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
                    .setAndroidPackageName(
                        BuildConfig.APPLICATION_ID,
                        false,  /* installIfNotAvailable */
                        null /* minimumVersion */
                    )
                    .setHandleCodeInApp(true)
                    .setUrl("https://hypnotabac.page.link/jdF1") // This must be true
                    .setIOSBundleId("com.hypnotabac.ios")
                    .build()

            FirebaseAuth.getInstance().sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val STRING_CHARACTERS = (('A'..'Z') + ('0'..'9') + ('a'..'z')).toList().toTypedArray()
                        val id = (1..10).map { STRING_CHARACTERS.random() }.joinToString("")

                        firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(id).child("email").setValue(email)

                        Log.d(TAG, "Email sent.")
                        Toast.makeText(this, "Email sent.", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error : "+e.message)
                    Toast.makeText(this, "Error : "+e.stackTrace, Toast.LENGTH_SHORT).show()
                }
        }
    }
}
