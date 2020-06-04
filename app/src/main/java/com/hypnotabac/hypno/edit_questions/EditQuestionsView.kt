package com.hypnotabac.hypno.edit_questions

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import com.hypnotabac.R
import kotlinx.android.synthetic.main.question_list_layout.view.*


class EditQuestionsView @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0) :
                                    ConstraintLayout(context, attrs, defStyleAttr)
{
    val TAG: String = "EditQuestionsView"
    var m: Model = Model("", "", { s: String, s2: String -> })
    var i:Int = -1

    data class Model(val id:String, var editTextValue:String, var onTextEdited:(String, String) -> Unit)
    init {
        View.inflate(context, R.layout.question_list_layout, this)
        this.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }
    fun setupView(model: Model, index:Int){
        editQuestion.hint="Question #"+(index+1)
        editQuestion.setText(model.editTextValue)
        m = model
        i = index
    }
    fun retrieveValue(){
        m.onTextEdited(m.id, editQuestion.text.toString())
    }
}