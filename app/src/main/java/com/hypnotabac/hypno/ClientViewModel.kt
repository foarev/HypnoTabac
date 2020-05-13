package com.hypnotabac.hypno

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    val clientModels: LiveData<List<ClientView.Model>> = Transformations.map(_clients) {
        it.toClientsViewModel()
    }

    init {
        onNewClientsRequest()
    }

    fun onNewClientsRequest() {
        _clientsLoadingStatus.value=LoadingStatus.LOADING
        val clients:MutableList<Client> = mutableListOf()
        if(!_clients.value.isNullOrEmpty() ) {
            clients.addAll(_clients.value!!)
        }

        firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val clientsMap = dataSnapshot.value!! as Map<*, *>
                    clientsMap.forEach{ c->
                        val dbClient = c.value as Map<*, *>
                        if(dbClient.containsKey("userID")){
                            clients.add(Client( dbClient["userID"] as String,
                                dbClient["email"] as String,
                                dbClient["firstName"] as String,
                                dbClient["lastName"] as String,
                                dbClient["hypnoID"] as String,
                                true))
                        } else {
                            clients.add(Client( "",
                                dbClient["email"] as String,
                                "",
                                "",
                                "",
                                false))
                        }
                    }
                    _clients.value = clients
                    _clientsSetChangedAction.value = ListAction.DataSetChangedAction
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
    }

    fun onClientsReset() {
        val clients:MutableList<Client> = mutableListOf()
        _clients.value = clients
        onNewClientsRequest()
        _clientsSetChangedAction.value = ListAction.DataSetChangedAction
    }

    private fun onClientRemoved(id: String) {
        firebaseDatabase.getReference("users").child(firebaseAuth.currentUser!!.uid).child("clients").child(id).removeValue()
        onClientsReset()
    }

    private fun onClientEdited(id: String) {
        startActivity(context, Intent(context, AddClientActivity::class.java),null)
    }

    private fun onClientStats(id: String) {

    }

    private fun List<Client>.toClientsViewModel(): List<ClientView.Model> = map { client ->
        ClientView.Model(client, { clientID -> onClientRemoved(clientID)}, { clientID -> onClientEdited(clientID)}, { clientID -> onClientStats(clientID)} )
    }

    /** Convenient method to change an item position in a List */
    private inline fun <reified T> List<T>.moveItem(sourceIndex: Int, targetIndex: Int): List<T> =
        apply {
            if (sourceIndex <= targetIndex)
                Collections.rotate(subList(sourceIndex, targetIndex + 1), -1)
            else Collections.rotate(subList(targetIndex, sourceIndex + 1), 1)
        }

}