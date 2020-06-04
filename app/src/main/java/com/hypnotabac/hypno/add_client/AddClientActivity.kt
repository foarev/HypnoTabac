package com.hypnotabac.hypno.add_client

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hypnotabac.BuildConfig
import com.hypnotabac.R
import com.hypnotabac.hypno.HypnoMainActivity
import kotlinx.android.synthetic.main.activity_add_client.*

class AddClientActivity : AppCompatActivity() {
    val TAG = "AddClientActivity"
    private val questionsResponsesAdapter: QuestionsResponsesAdapter =
        QuestionsResponsesAdapter()
    private val llm: RecyclerView.LayoutManager = LinearLayoutManager(this)
    val firebaseDatabase = FirebaseDatabase.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)

        sendemail.setOnClickListener{
            questionsResponsesAdapter.retrieveAllValues()
            val email = questionsResponsesAdapter.models[0].editTextValue//editEmail!!.text.toString().trim { it <= ' ' }
            val firstName = questionsResponsesAdapter.models[1].editTextValue
            val lastName = questionsResponsesAdapter.models[2].editTextValue

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(this, "Please enter a first name", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(this, "Please enter a last name", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

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
                        firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(id).child("firstName").setValue(firstName)
                        firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(id).child("lastName").setValue(lastName)
                        questionsResponsesAdapter.models.forEachIndexed { i, model ->
                            Log.w(TAG, "Model : "+model.toString())
                            if(i>2)
                                firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(id).child("responses").child((i-3).toString()).setValue(model.editTextValue)
                        }

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

        questions_answers_view.layoutManager = llm
        questions_answers_view.adapter = questionsResponsesAdapter

        observeViewModel()
    }

    private val viewModel: QuestionsResponsesViewModel by viewModels {
        QuestionsViewModelFactory(
            this
        )
    }

    private fun observeViewModel() {
        viewModel.questionModels.observe(
            this,
            Observer { questions: List<QuestionsResponsesView.Model> ->
                questionsResponsesAdapter.models.clear()
                questionsResponsesAdapter.models.addAll(questions)
            })

        viewModel.questionsSetChangedAction.observe(
            this,
            Observer { listAction: QuestionsResponsesViewModel.ListAction ->
                when(listAction) {
                    is QuestionsResponsesViewModel.ListAction.ItemUpdatedAction ->
                        if(!questions_answers_view.isComputingLayout) questionsResponsesAdapter.notifyItemChanged(listAction.position)
                    is QuestionsResponsesViewModel.ListAction.ItemRemovedAction ->
                        if(!questions_answers_view.isComputingLayout) questionsResponsesAdapter.notifyItemRemoved(listAction.position)
                    is QuestionsResponsesViewModel.ListAction.ItemInsertedAction ->
                        if(!questions_answers_view.isComputingLayout) questionsResponsesAdapter.notifyItemInserted(listAction.position)
                    is QuestionsResponsesViewModel.ListAction.ItemMovedAction ->
                        if(!questions_answers_view.isComputingLayout) questionsResponsesAdapter.notifyItemMoved(listAction.fromPosition, listAction.toPosition)
                    is QuestionsResponsesViewModel.ListAction.DataSetChangedAction ->
                        if(!questions_answers_view.isComputingLayout) questionsResponsesAdapter.notifyDataSetChanged()
                }
            })
    }


    /**
     * Convenient class used to build the instance of our JokeViewModel,
     * passing some params to its constructor.
     *
     * @see androidx.lifecycle.ViewModelProvider
     */
    private class QuestionsViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            QuestionsResponsesViewModel(context) as T
    }
}
