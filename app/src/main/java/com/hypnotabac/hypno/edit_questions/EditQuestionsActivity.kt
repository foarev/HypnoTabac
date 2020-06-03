package com.hypnotabac.hypno.edit_questions

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hypnotabac.R
import com.hypnotabac.hypno.*
import kotlinx.android.synthetic.main.activity_edit_questions.*

class EditQuestionsActivity : AppCompatActivity() {
    private val TAG = "EditQuestionsActivity"
    private val editQuestionsAdapter: EditQuestionsAdapter =
        EditQuestionsAdapter()
    private val llm: RecyclerView.LayoutManager = LinearLayoutManager(this)
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_questions)

        addQuestion.setOnClickListener{
            viewModel.addQuestionBox()
        }

        btnDone.setOnClickListener{
            val responses = mutableListOf<String>()
            editQuestionsAdapter.retrieveAllValues()
            editQuestionsAdapter.models.forEachIndexed { i, model ->
                responses.add(model.editTextValue)
                firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("questions").child(""+i).setValue(model.editTextValue)
            }
            startActivity(Intent(applicationContext, HypnoMainActivity::class.java))
        }
        EditQuestionsTouchHelper(
            { fromPosition, toPosition ->
                viewModel.onQuestionPositionChanged(
                    fromPosition,
                    toPosition
                )
            },
            { position -> viewModel.onQuestionRemovedAt(position) }
        ).attachToRecyclerView(my_recycler_view)

        my_recycler_view.layoutManager = llm
        my_recycler_view.adapter = editQuestionsAdapter

        observeViewModel()
    }

    private val viewModel: QuestionsViewModel by viewModels {
        QuestionsViewModelFactory(
            this
        )
    }

    private fun observeViewModel() {
        viewModel.questionModels.observe(
            this,
            Observer { questions: List<EditQuestionsView.Model> ->
                editQuestionsAdapter.models.clear()
                editQuestionsAdapter.models.addAll(questions)
            })

        viewModel.questionsSetChangedAction.observe(
            this,
            Observer { listAction: QuestionsViewModel.ListAction ->
                when(listAction) {
                    is QuestionsViewModel.ListAction.ItemUpdatedAction ->
                        if(!my_recycler_view.isComputingLayout) editQuestionsAdapter.notifyItemChanged(listAction.position)
                    is QuestionsViewModel.ListAction.ItemRemovedAction ->
                        if(!my_recycler_view.isComputingLayout) editQuestionsAdapter.notifyItemRemoved(listAction.position)
                    is QuestionsViewModel.ListAction.ItemInsertedAction ->
                        if(!my_recycler_view.isComputingLayout) editQuestionsAdapter.notifyItemInserted(listAction.position)
                    is QuestionsViewModel.ListAction.ItemMovedAction ->
                        if(!my_recycler_view.isComputingLayout) editQuestionsAdapter.notifyItemMoved(listAction.fromPosition, listAction.toPosition)
                    is QuestionsViewModel.ListAction.DataSetChangedAction ->
                        if(!my_recycler_view.isComputingLayout) editQuestionsAdapter.notifyDataSetChanged()
                }
            })

        viewModel.questionsLoadingStatus.observe(
            this,
            Observer { loadingStatus: QuestionsViewModel.LoadingStatus ->
                //swipe_refresh_layout.isRefreshing = loadingStatus == LoadingStatus.LOADING
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
            QuestionsViewModel(context) as T
    }
}
