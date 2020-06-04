package com.hypnotabac.hypno.edit_questions

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
class QuestionsViewModel(
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

    private val _questionsLoadingStatus = MutableLiveData<LoadingStatus>()
    private val _questionsSetChangedAction = MutableLiveData<ListAction>()
    private val _questions = MutableLiveData<List<EditQuestionsView.Model>>()


    /**
     * Public members of type LiveData.
     * This is what UI will observe and use to update views.
     * They are built with private MutableLiveData above.
     *
     * @see androidx.lifecycle.LiveData
     * @see androidx.lifecycle.Transformations
     */
    val questionsLoadingStatus: LiveData<LoadingStatus> = _questionsLoadingStatus
    val questionsSetChangedAction: LiveData<ListAction> = _questionsSetChangedAction
    val questionModels: LiveData<List<EditQuestionsView.Model>> = _questions

    init {
        addQuestionsFromDatabase()
    }

    fun addQuestionsFromDatabase() {
        _questionsLoadingStatus.value=
            LoadingStatus.LOADING
        val questions:MutableList<EditQuestionsView.Model> = mutableListOf()
        if(!_questions.value.isNullOrEmpty() ) {
            questions.addAll(_questions.value!!)
        }
        firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("questions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value!=null){
                        val questionsList = dataSnapshot.value as ArrayList<*>
                        questionsList.forEach{ q->
                            questions.add(EditQuestionsView.Model(getNewId(), q as String, { id:String, newText:String -> onQuestionEdited(id, newText) }))
                        }
                        _questions.value = questions
                        _questionsSetChangedAction.value =
                            ListAction.DataSetChangedAction
                    }  else {
                        DefaultQuestions.DEFAULT_QUESTIONS.forEach {
                            questions.add(EditQuestionsView.Model(getNewId(), it, { id:String, newText:String -> onQuestionEdited(id, newText) }))
                        }
                        _questions.value = questions
                        _questionsSetChangedAction.value =
                            ListAction.DataSetChangedAction
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
    }
    fun addQuestionBox() {
        val questions:MutableList<EditQuestionsView.Model> = mutableListOf()
        if(!_questions.value.isNullOrEmpty() ) {
            questions.addAll(_questions.value!!)
        }
        questions.add(EditQuestionsView.Model(getNewId(), "", { id:String, newText:String -> onQuestionEdited(id, newText) }))
        _questions.value = questions
        _questionsSetChangedAction.value =
            ListAction.ItemInsertedAction(
                _questions.value!!.lastIndex
            )
    }

    fun onQuestionsReset() {
        _questions.value = mutableListOf()
        _questionsSetChangedAction.value =
            ListAction.DataSetChangedAction
        addQuestionBox()
    }

    fun onQuestionPositionChanged(previous: Int, target: Int) {
        _questions.value = _questions.value!!.moveItem(previous, target)
        _questionsSetChangedAction.value =
            ListAction.ItemMovedAction(
                previous,
                target
            )
    }

    fun onQuestionRemovedAt(i: Int) {
        val questions:MutableList<EditQuestionsView.Model> = mutableListOf()
        questions.addAll(_questions.value!!)
        questions.removeAt(i)
        _questions.value = questions
        _questionsSetChangedAction.value =
            ListAction.ItemRemovedAction(
                i
            )
        _questionsSetChangedAction.value =
            ListAction.DataSetChangedAction
        if(_questions.value.isNullOrEmpty()) onQuestionsReset()
    }


    fun onQuestionEdited(id: String, newText:String) {
        val questions:MutableList<EditQuestionsView.Model> = mutableListOf()
        questions.addAll(_questions.value!!)
        var finalIndex=-1
        questions.forEachIndexed {index, q->
            if(id==q.id){
                q.editTextValue = newText
                finalIndex=index
            }
        }
        if(finalIndex!=-1){
            _questions.value = questions
            _questionsSetChangedAction.value = ListAction.ItemUpdatedAction(finalIndex)
        }
        else {
            Log.w(TAG, "onQuestionEdited : ID not found")
        }
    }

    override fun onCleared() {
        composite.dispose()
        super.onCleared()
    }

    fun getNewId(): String {
        val STRING_CHARACTERS = (('A'..'Z') + ('0'..'9') + ('a'..'z')).toList().toTypedArray()
        var newId = ""
        if(!_questions.value.isNullOrEmpty() ) {
            var b = true
            do {
                newId = (1..10).map { STRING_CHARACTERS.random() }.joinToString("")
                _questions.value!!.forEach { q -> if (q.id == newId) b = false }
            } while(!b)
        } else {
            newId = (1..10).map { STRING_CHARACTERS.random() }.joinToString("")
        }
        return newId
    }

    /** Convenient method to change an item position in a List */
    private inline fun <reified T> List<T>.moveItem(sourceIndex: Int, targetIndex: Int): List<T> =
        apply {
            if (sourceIndex <= targetIndex)
                Collections.rotate(subList(sourceIndex, targetIndex + 1), -1)
            else Collections.rotate(subList(targetIndex, sourceIndex + 1), 1)
        }

}