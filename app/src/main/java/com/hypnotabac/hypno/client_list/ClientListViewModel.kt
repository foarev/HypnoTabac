package com.hypnotabac.hypno.client_list

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hypnotabac.SaveSharedPreferences
import com.hypnotabac.hypno.stats.HypnoStatsActivity
import java.util.*

/**
 * @param context, helpful for sharing client
 *
 * @see androidx.lifecycle.ViewModel
 */
class ClientsViewModel(
    private val context: Context
) : ViewModel() {

    val TAG: String = "ClientViewModel"
    val JOKE_LIST_KEY:String = "JOKE_LIST_KEY"
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance()

    enum class LoadingStatus { LOADING, NOT_LOADING }

    /** Used as a "dynamic enum" to notify Adapter with correct action. */
    sealed class ListAction {
        data class ItemUpdatedAction(val position: Int) : ListAction()
        data class ItemInsertedAction(val position: Int) : ListAction()
        data class ItemRemovedAction(val position: Int) : ListAction()
        data class ItemMovedAction(val fromPosition: Int, val toPosition: Int) : ListAction()
        object DataSetChangedAction : ListAction()
    }

    private val _clientsLoadingStatus = MutableLiveData<LoadingStatus>()
    private val _clientsSetChangedAction = MutableLiveData<ListAction>()
    private val _clients = MutableLiveData<List<Client>>()


    /**
     * Public members of type LiveData.
     * This is what UI will observe and use to update views.
     * They are built with private MutableLiveData above.
     *
     * @see androidx.lifecycle.LiveData
     * @see androidx.lifecycle.Transformations
     */
    val clientsLoadingStatus: LiveData<LoadingStatus> = _clientsLoadingStatus
    val clientsSetChangedAction: LiveData<ListAction> = _clientsSetChangedAction
    val clientListModels: LiveData<List<ClientListView.Model>> = Transformations.map(_clients) {
        it.toClientsViewModel()
    }

    init {
        onClientsReset()
    }

    fun onNewClientsRequest() {
        _clientsLoadingStatus.value=
            LoadingStatus.LOADING
        val clients:MutableList<Client> = mutableListOf()
        if(!_clients.value.isNullOrEmpty() ) {
            clients.addAll(_clients.value!!)
        }
        firebaseDatabase.getReference("users").child(SaveSharedPreferences.getUserID(context)).child("clients")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.value!=null){
                        val clientsMap = dataSnapshot.value as Map<*, *>
                        clientsMap.forEach{ c->
                            val dbClient = c.value as Map<*, *>
                            if((c.key as String).length>10){
                                clients.add(
                                    Client(
                                        c.key as String,
                                        dbClient["email"] as String,
                                        dbClient["firstName"] as String,
                                        dbClient["lastName"] as String,
                                        dbClient["hypnoID"] as String,
                                        true
                                    )
                                )
                            } else {
                                if(dbClient["email"]!=null && dbClient["firstName"]!=null && dbClient["lastName"]!=null)
                                    clients.add(
                                        Client(
                                            c.key as String,
                                            dbClient["email"] as String,
                                            dbClient["firstName"] as String,
                                            dbClient["lastName"] as String,
                                            "",
                                            false
                                        )
                                    )
                            }
                        }
                        _clients.value = clients
                        _clientsSetChangedAction.value = ListAction.DataSetChangedAction
                        firebaseDatabase.getReference("users").child(SaveSharedPreferences.getUserID(context)).child("clients").removeEventListener(this)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun onClientsReset() {
        _clients.value = mutableListOf()
        _clientsSetChangedAction.value =
            ListAction.DataSetChangedAction
        onNewClientsRequest()
    }

    private fun onClientRemoved(id: String, email: String) {
        _clients.value?.forEach {
            if(it.email == email){
                if(id!=""){
                    firebaseDatabase.getReference("users").child(SaveSharedPreferences.getUserID(context)).child("clients").child(id).removeValue()
                    onClientsReset()
                }
                else {
                    firebaseDatabase.getReference("users").child(SaveSharedPreferences.getUserID(context)).child("clients")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(dataSnapshot.value!=null){
                                    val clientsMap = dataSnapshot.value as Map<*, *>
                                    clientsMap.forEach{ c->
                                        val dbClient = c.value as Map<*, *>
                                        if(dbClient["email"] as String == email){
                                            firebaseDatabase.getReference("users").child(SaveSharedPreferences.getUserID(context)).child("clients").child(c.key as String).removeValue()
                                        }
                                    }
                                    onClientsReset()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Log.w(TAG, "Failed to read value.", error.toException())
                            }
                        })
                }
            }
        }
    }

    private fun onClientStats(id: String) {
        startActivity(context, Intent(context, HypnoStatsActivity::class.java).putExtra("clientID", id),null)
    }

    private fun List<Client>.toClientsViewModel(): List<ClientListView.Model> = map { client ->
        ClientListView.Model(client, { clientID, clientEmail -> onClientRemoved(clientID, clientEmail)}, { clientID -> onClientStats(clientID)} )
    }

    /** Convenient method to change an item position in a List */
    private inline fun <reified T> List<T>.moveItem(sourceIndex: Int, targetIndex: Int): List<T> =
        apply {
            if (sourceIndex <= targetIndex)
                Collections.rotate(subList(sourceIndex, targetIndex + 1), -1)
            else Collections.rotate(subList(targetIndex, sourceIndex + 1), 1)
        }

}