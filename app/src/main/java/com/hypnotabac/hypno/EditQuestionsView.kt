package com.hypnotabac.hypno

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.hypnotabac.R
import kotlinx.android.synthetic.main.question_list_layout.view.*


class EditQuestionsView @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0) :
                                    ConstraintLayout(context, attrs, defStyleAttr)
{
    val TAG: String = "QuestionsView"
    data class Model(var editTextValue:String, var onTextEdited:(Int, String) -> Unit)
    init {
        View.inflate(context, R.layout.question_list_layout, this)
        this.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }
    fun setupView(model: Model, index:Int){
        Log.w(TAG, "setupView : " + model.toString())

        editQuestion.hint="Question #"+(index+1)
        if(editQuestion.text.toString()!=model.editTextValue){
            editQuestion.setText(model.editTextValue)
        }

        editQuestion.doOnTextChanged { text, start, count, after ->
            Log.w(TAG, "doOnTextChanged : index = "+index+" ; Value : "+model.editTextValue + "; start : "+start+"; count : "+count+"; after : "+after)
            model.onTextEdited(index, editQuestion.text.toString())
        }
    }
}