package com.hypnotabac.hypno

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.client_list_layout.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.hypnotabac.R

class ClientView @JvmOverloads constructor(context: Context,
                                           attrs: AttributeSet? = null,
                                           defStyleAttr: Int = 0) :
                                    ConstraintLayout(context, attrs, defStyleAttr)
{
    val TAG: String = "ClientView"
    data class Model(val client: Client, var onClickRemove: (String) -> Unit, var onClickEdit: (String) -> Unit, var onClickStats: (String) -> Unit)
    init {
        View.inflate(context, R.layout.client_list_layout, this)
        this.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }
    fun setupView(model: Model){
        if(model.client.isRegistered) joke_text_view.text = model.client.firstName + " " + model.client.lastName
        else joke_text_view.text = "(Unregistered) " + model.client.email
        button_remove.setOnClickListener { model.onClickRemove(model.client.userID) }
        button_edit.setOnClickListener { model.onClickEdit(model.client.userID) }
        button_stats.setOnClickListener { model.onClickStats(model.client.userID) }
    }
}