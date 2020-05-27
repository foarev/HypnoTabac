package com.hypnotabac.hypno

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.DefaultQuestions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

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
    private val _questions = MutableLiveData<List<String>>()


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
    val questionModels: LiveData<List<EditQuestionsView.Model>> = Transformations.map(_questions) {
        it.toQuestionsViewModel()
    }

    init {
        addQuestionsFromDatabase()
    }

    fun addQuestionsFromDatabase() {
        _questionsLoadingStatus.value=LoadingStatus.LOADING
        val questions:MutableList<String> = mutableListOf()
        if(!_questions.value.isNullOrEmpty() ) {
            questions.addAll(_questions.value!!)
        }
        firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("questions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value!=null){
                        val questionsList = dataSnapshot.value as ArrayList<*>
                        questionsList.forEach{ q->
                            questions.add(q as String)
                        }
                        _questions.value = questions
                        _questionsSetChangedAction.value = ListAction.DataSetChangedAction
                    }  else {
                        DefaultQuestions.DEFAULT_QUESTIONS.forEach {
                            questions.add(it)
                        }
                        _questions.value = questions
                        _questionsSetChangedAction.value = ListAction.DataSetChangedAction
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
        /*composite.add(
            .subscribeOn(Schedulers.io())
            .delay(50, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {_questionsLoadingStatus.value=LoadingStatus.NOT_LOADING}
            .subscribeBy(
                onError = { e -> Log.wtf(TAG, ""+e) },
                onComplete = {
                    _questions.value = questions
                    _questionsSetChangedAction.value = ListAction.DataSetChangedAction
                }
            )
                )*/

    }
    fun addQuestionBox() {
        val questions:MutableList<String> = mutableListOf()
        if(!_questions.value.isNullOrEmpty() ) {
            Log.wtf(TAG, _questions.value.toString())
            questions.addAll(_questions.value!!)
        }
        questions.add("")
        _questions.value = questions
        Log.wtf(TAG, "addQuestionBox : "+_questions.value.toString())
        _questionsSetChangedAction.value = ListAction.ItemInsertedAction(_questions.value!!.lastIndex)
        _questionsSetChangedAction.value = ListAction.DataSetChangedAction
    }

    fun onQuestionsReset() {
        _questions.value = mutableListOf()
        _questionsSetChangedAction.value = ListAction.DataSetChangedAction
        addQuestionBox()
    }

    fun onQuestionPositionChanged(previous: Int, target: Int) {
        _questions.value = _questions.value!!.moveItem(previous, target)
        _questionsSetChangedAction.value = ListAction.ItemMovedAction(previous, target)
    }

    fun onQuestionRemovedAt(i: Int) {
        val questions:MutableList<String> = mutableListOf()
        Log.w(TAG, "onQuestionRemoved : i = "+i)
        questions.addAll(_questions.value!!)
        questions.removeAt(i)
        _questions.value = questions
        _questionsSetChangedAction.value = ListAction.ItemRemovedAction(i)
        _questionsSetChangedAction.value = ListAction.DataSetChangedAction
        if(_questions.value.isNullOrEmpty()) onQuestionsReset()
    }

    fun onQuestionEdited(i: Int, newText:String) {
        val questions:MutableList<String> = mutableListOf()
        Log.w(TAG, "onQuestionEdited : i = "+i+"; newText = "+newText)
        questions.addAll(_questions.value!!)
        if(i<=_questions.value!!.lastIndex && i>=0)
            questions[i] = newText
        _questions.value = questions
        _questionsSetChangedAction.value = ListAction.ItemUpdatedAction(i)
    }

    private fun List<String>.toQuestionsViewModel(): List<EditQuestionsView.Model> = mapIndexed { i, question ->
        Log.w(TAG, "toquestionsVM : index = "+i+" ; Value : "+question)
        EditQuestionsView.Model(question, { n:Int, newText:String -> onQuestionEdited(n, newText) })
    }

    override fun onCleared() {
        composite.dispose()
        super.onCleared()
    }

    /** Convenient method to change an item position in a List */
    private inline fun <reified T> List<T>.moveItem(sourceIndex: Int, targetIndex: Int): List<T> =
        apply {
            if (sourceIndex <= targetIndex)
                Collections.rotate(subList(sourceIndex, targetIndex + 1), -1)
            else Collections.rotate(subList(targetIndex, sourceIndex + 1), 1)
        }

}