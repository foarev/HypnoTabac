package com.hypnotabac.hypno.add_client

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class QuestionsResponsesAdapter() : RecyclerView.Adapter<QuestionsResponsesAdapter.QuestionViewHolder>(){
    val TAG:String = "QuestionsResponsesAdapter"

    class QuestionViewHolder(val v: QuestionsResponsesView) : RecyclerView.ViewHolder(v)

    var models:MutableList<QuestionsResponsesView.Model> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val v =
            QuestionsResponsesView(parent.context)
        return QuestionViewHolder(
            v
        )
    }

    override fun getItemCount(): Int {
        return models.count()
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.v.setupView(models[position], position)
    }

    override fun onViewDetachedFromWindow(holder: QuestionViewHolder) {
        holder.v.retrieveValue()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
/*
    override fun onViewRecycled(holder: QuestionViewHolder) {
        holder.v.removeView()
        super.onViewRecycled(holder)
    }*/
}