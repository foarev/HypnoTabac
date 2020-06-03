package com.hypnotabac.hypno.edit_questions

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class EditQuestionsAdapter() : RecyclerView.Adapter<EditQuestionsAdapter.QuestionViewHolder>(){
    val TAG:String = "QuestionsAdapter"

    class QuestionViewHolder(val v: EditQuestionsView) : RecyclerView.ViewHolder(v)

    val models:MutableList<EditQuestionsView.Model> = mutableListOf()
    val holders:MutableList<QuestionViewHolder> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val v =
            EditQuestionsView(parent.context)
        return QuestionViewHolder(
            v
        )
    }

    override fun getItemCount(): Int {
        return models.count()
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.v.setupView(models[position], position)
        holders.add(holder)
    }

    override fun onViewDetachedFromWindow(holder: QuestionViewHolder) {
        //holder.v.removeWatcher()
        Log.w(TAG, "onViewDetachedFromWindow")
        holder.v.retrieveValue()
        holders.remove(holder)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        Log.w(TAG, "onDetachedFromRecyclerView")
        retrieveAllValues()
    }
    fun retrieveAllValues(){
        Log.w(TAG, "retrieveAllValues")
        holders.forEach{
            it.v.retrieveValue()
        }
    }

    /*
    override fun onViewRecycled(holder: QuestionViewHolder) {
        holder.v.removeView()
        super.onViewRecycled(holder)
    }*/
}