package com.hypnotabac.hypno

import android.util.Log
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.question_list_layout.view.*


class EditQuestionsAdapter() : RecyclerView.Adapter<EditQuestionsAdapter.QuestionViewHolder>(){
    val TAG:String = "QuestionsAdapter"

    class QuestionViewHolder(val v: EditQuestionsView) : RecyclerView.ViewHolder(v)

    var models:MutableList<EditQuestionsView.Model> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val v = EditQuestionsView(parent.context)
        return QuestionViewHolder(v)
    }

    override fun getItemCount(): Int {
        return models.count()
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        if(position<=models.lastIndex)
            holder.v.setupView(models[position], position)
    }

    override fun onViewDetachedFromWindow(holder: QuestionViewHolder) {
        holder.v.removeView()
        super.onViewDetachedFromWindow(holder)
    }
/*
    override fun onViewRecycled(holder: QuestionViewHolder) {
        holder.v.removeView()
        super.onViewRecycled(holder)
    }*/
}