package com.hypnotabac.hypno.add_client

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.DefaultQuestions
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * @param context, helpful for sharing question
 *
 * @see androidx.lifecycle.ViewModel
 */
class QuestionsResponsesViewModel(
    private val context: Context
) : ViewModel() {

    val TAG: String = "QuestionsViewModel"
    val JOKE_LIST_KEY:String = "JOKE_LIST_KEY"
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance()
    private val composite: CompositeDisposable = CompositeDisposable()

    enum class LoadingStatus { LOADING, NOT_LOADING }

    /** Used as a "dynamic enum" to notify Adapter with correct action. */
    sealed class ListAction {
        data class ItemUpdatedAction(val position: Int) : ListAction()
        data class ItemInsertedAction(val position: Int) : ListAction()
        data class ItemRemovedAction(val position: Int) : ListAction()
        data class ItemMovedAction(val fromPosition: Int, val toPosition: Int) : ListAction()
        object DataSetChangedAction : ListAction()
    }

    private val _questionsSetChangedAction = MutableLiveData<ListAction>()
    private val _questions = MutableLiveData<List<QuestionsResponsesView.Model>>()


    /**
     * Public members of type LiveData.
     * This is what UI will observe and use to update views.
     * They are built with private MutableLiveData above.
     *
     * @see androidx.lifecycle.LiveData
     * @see androidx.lifecycle.Transformations
     */
    val questionsSetChangedAction: LiveData<ListAction> = _questionsSetChangedAction
    val questionModels: LiveData<List<QuestionsResponsesView.Model>> = _questions

    init {
        addQuestionsFromDatabase()
    }

    fun addQuestionsFromDatabase() {
        val questionsResponses:MutableList<QuestionsResponsesView.Model> = mutableListOf()
        if(!_questions.value.isNullOrEmpty() ) {
            questionsResponses.addAll(_questions.value!!)
        }
        firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("questions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value!=null){
                        val questionsList = dataSnapshot.value as ArrayList<*>
                        questionsList.forEach{ q->
                            questionsResponses.add(QuestionsResponsesView.Model(q as String, "", { i:Int, newText:String -> onQuestionEdited(i, newText) }))
                        }
                        _questions.value = questionsResponses
                        _questionsSetChangedAction.value =
                            ListAction.DataSetChangedAction
                    }  else {
                        DefaultQuestions.DEFAULT_QUESTIONS.forEach {
                            questionsResponses.add(QuestionsResponsesView.Model(it, "", { i:Int, newText:String -> onQuestionEdited(i, newText) }))
                        }
                        _questions.value = questionsResponses
                        _questionsSetChangedAction.value =
                            ListAction.DataSetChangedAction
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun onQuestionsReset() {
        _questions.value = mutableListOf()
        _questionsSetChangedAction.value =
            ListAction.DataSetChangedAction
        addQuestionsFromDatabase()
    }

    fun onQuestionEdited(i:Int, newText:String) {
        val questionsResponses:MutableList<QuestionsResponsesView.Model> = mutableListOf()
        questionsResponses.addAll(_questions.value!!)
        questionsResponses[i].editTextValue=newText
        Log.w(TAG, "onQuestionEdited : i = "+i+"; newText = "+newText)
        _questions.value = questionsResponses
        _questionsSetChangedAction.value = ListAction.ItemUpdatedAction(i)
    }

    override fun onCleared() {
        composite.dispose()
        super.onCleared()
    }
}