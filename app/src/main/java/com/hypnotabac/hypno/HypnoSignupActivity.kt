package com.hypnotabac.hypno

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.hypnotabac.LoginActivity
import com.hypnotabac.R
import kotlinx.android.synthetic.main.activity_h_signup.*

class HypnoSignupActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val TAG = "HypnoSignupActivity"
    private lateinit var billingClient:BillingClient
    private lateinit var email:String
    private lateinit var firstName:String
    private lateinit var lastName:String
    private lateinit var password:String

    private val skuList = listOf("hypno")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_signup)
        login.setOnClickListener{
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
        setupBillingClient()
    }

    private fun setupBillingClient(){
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    Log.w(TAG, "Setup Billing Done")
                    querySkuDetails()
                }
                else
                    Log.w(TAG, "Setup Billing Failed")
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.w(TAG, "onBillingServiceDisconnected")
            }
        })
    }

    private fun querySkuDetails() = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {
                    if (skuDetails.sku == "hypno"){
                        btnSignUp!!.setOnClickListener(View.OnClickListener {
                            loading.visibility = View.VISIBLE
                            email = editEmail!!.text.toString().trim { it <= ' ' }
                            firstName = editFirstName!!.text.toString().trim { it <= ' ' }
                            lastName = editLastName!!.text.toString().trim { it <= ' ' }
                            password = editPassword!!.text.toString().trim { it <= ' ' }
                            val confirmPwd = confirmPassword!!.text.toString().trim { it <= ' ' }
                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(this@HypnoSignupActivity, "Veuillez entrer un email", Toast.LENGTH_SHORT)
                                    .show()
                                loading.visibility = View.GONE
                                return@OnClickListener
                            }
                            if (TextUtils.isEmpty(firstName)) {
                                Toast.makeText(
                                    this@HypnoSignupActivity,
                                    "Veuillez entrer un prénom",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading.visibility = View.GONE
                                return@OnClickListener
                            }
                            if (TextUtils.isEmpty(lastName)) {
                                Toast.makeText(
                                    this@HypnoSignupActivity,
                                    "Veuillez entrer un nom de famille",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading.visibility = View.GONE
                                return@OnClickListener
                            }
                            if (TextUtils.isEmpty(password)) {
                                Toast.makeText(
                                    this@HypnoSignupActivity,
                                    "Veuillez entrer un mot de passe",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading.visibility = View.GONE
                                return@OnClickListener
                            }
                            if (TextUtils.isEmpty(confirmPwd)) {
                                Toast.makeText(
                                    this@HypnoSignupActivity,
                                    "Veuillez confirmer votre mot de passe",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading.visibility = View.GONE
                                return@OnClickListener
                            }
                            if (password.length < 3 || password.length > 12) {
                                Toast.makeText(
                                    this@HypnoSignupActivity,
                                    "Veuillez choisir un mot de passe entre 3 et 12 caractères",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading.visibility = View.GONE
                                return@OnClickListener
                            }
                            if (password == confirmPwd) {
                                val billingFlowParams = BillingFlowParams
                                    .newBuilder()
                                    .setSkuDetails(skuDetails)
                                    .build()
                                Log.w(TAG, "billingFlowParams")
                                billingClient.launchBillingFlow(this, billingFlowParams)
                            } else {
                                Toast.makeText(
                                    this@HypnoSignupActivity,
                                    "Le mot de passe ne correspond pas",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading.visibility = View.GONE
                                return@OnClickListener
                            }
                        })

                    }
                }
            }
        }

    } else {
        println("Billing Client not ready")
    }
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                Log.w(TAG, "Purchase complete")
                acknowledgePurchase(purchase.purchaseToken)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.w(TAG, "Purchase cancelled")

        } else {
            // Handle any other error codes.
            Log.w(TAG, "Purchase error")
        }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this@HypnoSignupActivity
                ) { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val profileUpdates =
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(email).build()
                        user!!.updateProfile(profileUpdates)
                        val dbCurrentUser =
                            firebaseDatabase.getReference("users")
                                .child(user.uid)
                        dbCurrentUser.child("email").setValue(email)
                        dbCurrentUser.child("firstName").setValue(firstName)
                        dbCurrentUser.child("lastName").setValue(lastName)
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@HypnoSignupActivity,
                            "Création du compte réussie",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                applicationContext,
                                LoginActivity::class.java
                            )
                        )
                    } else {
                        Toast.makeText(
                            this@HypnoSignupActivity,
                            "La création du compte a échoué",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }
}

