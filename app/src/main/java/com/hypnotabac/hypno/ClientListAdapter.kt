package com.hypnotabac.hypno

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class ClientListAdapter() : RecyclerView.Adapter<ClientListAdapter.ClientViewHolder>(){
    val TAG:String = "ClientAdapter"

    class ClientViewHolder(val v: ClientListView) : RecyclerView.ViewHolder(v)

    var models:MutableList<ClientListView.Model> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val v = ClientListView(parent.context)
        return ClientViewHolder(v)
    }

    override fun getItemCount(): Int {
        return models.count()
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.v.setupView(models[position])
    }
}