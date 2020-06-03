package com.hypnotabac.hypno

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hypnotabac.LoginActivity
import com.hypnotabac.hypno.client_list.ClientsViewModel.LoadingStatus
import com.hypnotabac.hypno.client_list.ClientsViewModel.ListAction
import com.hypnotabac.R
import com.hypnotabac.SaveSharedPreferences
import com.hypnotabac.hypno.add_client.AddClientActivity
import com.hypnotabac.hypno.client_list.ClientListAdapter
import com.hypnotabac.hypno.client_list.ClientListView
import com.hypnotabac.hypno.client_list.ClientsViewModel
import com.hypnotabac.hypno.edit_questions.EditQuestionsActivity
import kotlinx.android.synthetic.main.activity_h_main.*
import kotlinx.android.synthetic.main.status_bar.*

class HypnoMainActivity : AppCompatActivity() {
    private val TAG = "HypnoMainActivity"
    private val clientListAdapter: ClientListAdapter =
        ClientListAdapter()
    private val llm: RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h_main)

        addclient.setOnClickListener{
            startActivity(Intent(applicationContext, AddClientActivity::class.java))
        }
        editQuestions.setOnClickListener{
            startActivity(Intent(applicationContext, EditQuestionsActivity::class.java))
        }
        logout.setOnClickListener{
            SaveSharedPreferences.resetAll(this)
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }

        my_recycler_view.layoutManager = llm
        my_recycler_view.adapter = clientListAdapter

        observeViewModel()
    }
    private val viewModel: ClientsViewModel by viewModels {
        ClientsViewModelFactory(this)
    }

    private fun observeViewModel() {
        viewModel.clientListModels.observe(
            this,
            Observer { jokes: List<ClientListView.Model> ->
                clientListAdapter.models.clear()
                clientListAdapter.models.addAll(jokes)
            })

        viewModel.clientsSetChangedAction.observe(
            this,
            Observer { listAction: ListAction ->
                when(listAction) {
                    is ListAction.ItemUpdatedAction ->
                        clientListAdapter.notifyItemChanged(listAction.position)
                    is ListAction.ItemRemovedAction ->
                        clientListAdapter.notifyItemRemoved(listAction.position)
                    is ListAction.ItemInsertedAction ->
                        clientListAdapter.notifyItemInserted(listAction.position)
                    is ListAction.ItemMovedAction ->
                        clientListAdapter.notifyItemMoved(listAction.fromPosition, listAction.toPosition)
                    is ListAction.DataSetChangedAction ->
                        clientListAdapter.notifyDataSetChanged()
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

    override fun onBackPressed() {

    }
}
