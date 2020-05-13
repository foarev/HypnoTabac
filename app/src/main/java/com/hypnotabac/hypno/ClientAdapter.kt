package com.hypnotabac.hypno

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class ClientAdapter() : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>(){
    val TAG:String = "ClientAdapter"

    class ClientViewHolder(val v: ClientView) : RecyclerView.ViewHolder(v)

    var models:MutableList<ClientView.Model> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val v = ClientView(parent.context)
        return ClientViewHolder(v)
    }

    override fun getItemCount(): Int {
        return models.count()
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.v.setupView(models[position])
    }
}