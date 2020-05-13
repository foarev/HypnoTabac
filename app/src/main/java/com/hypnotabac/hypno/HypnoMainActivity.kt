package com.hypnotabac.hypno

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hypnotabac.hypno.ClientsViewModel.LoadingStatus
import com.hypnotabac.hypno.ClientsViewModel.ListAction
import com.hypnotabac.R
import kotlinx.android.synthetic.main.activity_h_main.*

class HypnoMainActivity : AppCompatActivity() {
    private val TAG = "HypnoMainActivity"
    private val clientAdapter: ClientAdapter = ClientAdapter()
    private val llm: RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_main)

        addclient.setOnClickListener{
            startActivity(Intent(applicationContext, AddClientActivity::class.java))
        }
        my_recycler_view.layoutManager = llm
        my_recycler_view.adapter = clientAdapter

        observeViewModel()
    }
    private val viewModel: ClientsViewModel by viewModels {
        ClientsViewModelFactory(this)
    }

    private fun observeViewModel() {
        viewModel.clientModels.observe(
            this,
            Observer { jokes: List<ClientView.Model> ->
                clientAdapter.models.clear()
                clientAdapter.models.addAll(jokes)
            })

        viewModel.clientsSetChangedAction.observe(
            this,
            Observer { listAction: ListAction ->
                when(listAction) {
                    is ListAction.ItemUpdatedAction ->
                        clientAdapter.notifyItemChanged(listAction.position)
                    is ListAction.ItemRemovedAction ->
                        clientAdapter.notifyItemRemoved(listAction.position)
                    is ListAction.ItemInsertedAction ->
                        clientAdapter.notifyItemInserted(listAction.position)
                    is ListAction.ItemMovedAction ->
                        clientAdapter.notifyItemMoved(listAction.fromPosition, listAction.toPosition)
                    is ListAction.DataSetChangedAction ->
                        clientAdapter.notifyDataSetChanged()
                }
            })

        viewModel.clientsLoadingStatus.observe(
            this,
            Observer { loadingStatus: LoadingStatus ->
                //swipe_refresh_layout.isRefreshing = loadingStatus == LoadingStatus.LOADING
            })
    }


    /**
     * Convenient class used to build the instance of our JokeViewModel,
     * passing some params to its constructor.
     *
     * @see androidx.lifecycle.ViewModelProvider
     */
    private class ClientsViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            ClientsViewModel(context) as T
    }
}
