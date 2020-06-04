package com.hypnotabac.hypno.stats

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.answers_list_layout.view.*


class ResponsesAdapter() : RecyclerView.Adapter<ResponsesAdapter.QuestionViewHolder>(){
    val TAG:String = "ResponsesAdapter"

    class QuestionViewHolder(val v: ResponsesView) : RecyclerView.ViewHolder(v)

    var models:MutableList<ResponsesView.Model> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val v = ResponsesView(parent.context)
        return QuestionViewHolder(v)
    }

    override fun getItemCount(): Int {
        return models.count()
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.v.question_text_view.text = models[position].question
        holder.v.response_text_view.text = models[position].response
    }
}