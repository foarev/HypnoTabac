package com.hypnotabac.hypno

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


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
        holder.v.setupView(models[position], position)
    }

    override fun onViewDetachedFromWindow(holder: QuestionViewHolder) {
        holder.v.removeWatcher()
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