package com.hypnotabac.hypno.add_client

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import com.hypnotabac.R
import kotlinx.android.synthetic.main.questions_answers_list_layout.view.*


class QuestionsResponsesView @JvmOverloads constructor(context: Context,
                                                       attrs: AttributeSet? = null,
                                                       defStyleAttr: Int = 0) :
                                    ConstraintLayout(context, attrs, defStyleAttr)
{
    val TAG: String = "ResponsesView"
    var m:Model = Model("","",{ i:Int, s:String->})
    var i:Int = -1

    data class Model(val textViewValue:String, var editTextValue:String, var onTextEdited:(Int, String) -> Unit)
    init {
        View.inflate(context, R.layout.questions_answers_list_layout, this)
        this.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }
    fun setupView(model: Model, index:Int){
        Log.w(TAG, "setupView : " + model.toString()+"; i = "+index)

        question_text_view.text = model.textViewValue

        if(index==0){
            editResponse.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            editResponse.hint="Adresse email"
        } else if(index==1){
            editResponse.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            editResponse.hint = "Prénom"
        } else if(index==2){
            editResponse.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            editResponse.hint = "Nom de famille"
        } else {
            editResponse.inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
            editResponse.hint = "Réponse"
        }
        editResponse.setText(model.editTextValue)

        m = model
        i = index
    }
    fun retrieveValue(){
        m.onTextEdited(i, editResponse.text.toString())
    }
}