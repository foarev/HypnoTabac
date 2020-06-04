package com.hypnotabac.hypno.client_list

import android.content.Context
import android.graphics.Typeface
import android.graphics.Typeface.*
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.client_list_layout.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.hypnotabac.R

class ClientListView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0) :
                                    ConstraintLayout(context, attrs, defStyleAttr)
{
    val TAG: String = "ClientView"
    data class Model(val client: Client, var onClickRemove: (String, String) -> Unit, var onClickStats: (String) -> Unit)
    init {
        View.inflate(context, R.layout.client_list_layout, this)
        this.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }
    fun setupView(model: Model){
        client_text_view.text = model.client.firstName + " " + model.client.lastName
        if(model.client.isRegistered) client_text_view.setTypeface(null, NORMAL)
        else client_text_view.setTypeface(null, ITALIC)
        button_remove.setOnClickListener { model.onClickRemove(model.client.userID, model.client.email) }
        button_stats.setOnClickListener { model.onClickStats(model.client.userID) }
    }
}