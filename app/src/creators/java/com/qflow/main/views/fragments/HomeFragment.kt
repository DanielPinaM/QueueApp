package com.qflow.main.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qflow.main.R
import com.qflow.main.R.*
import com.qflow.main.core.ScreenState
import com.qflow.main.domain.local.models.Queue
import com.qflow.main.views.adapters.QueueAdminAdapter
import com.qflow.main.views.screenstates.HomeFragmentScreenState
import com.qflow.main.views.viewmodels.HomeViewModel
import kotlinx.android.synthetic.creators.fragment_home.*
import kotlinx.android.synthetic.creators.item_queueadmin.*
import kotlinx.android.synthetic.main.item_home_historical.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var queuesAdminAdapter: QueueAdminAdapter
    private lateinit var queuesAdminHistory: QueueAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeObservers()
        initializeListeners()
        initializeRecycler()
    }

    private fun initializeListeners() {
        initializeButtons()
    }

    private fun initializeButtons() {
        //img_profile.setImageResource()
        btn_create.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_createQueueFragment)
        }
    }

    private fun initializeRecycler() {

        queuesAdminHistory = QueueAdminAdapter(ArrayList(), ::onClickOnQueue)
        queuesAdminAdapter = QueueAdminAdapter(ArrayList(), ::onClickOnQueue)
        rv_admin_queues.adapter = queuesAdminAdapter
        rv_admin_historical.adapter = queuesAdminHistory
        rv_admin_historical.layoutManager =
            GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        rv_admin_queues.layoutManager =
            GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        viewModel.getQueues("id")
        viewModel.getHistory("id")
    }
//
//    private fun handleErrors(failure: Failure?) {
//        when () {
//
//            else -> {}
//        }
//    }

    private fun renderScreenState(renderState: HomeFragmentScreenState) {
        when (renderState) {
            is HomeFragmentScreenState.QueuesActiveObtained -> {
                queuesAdminAdapter.setData(renderState.queues)
            }
            is HomeFragmentScreenState.QueuesHistoricalObtained -> {
                queuesAdminHistory.setData(renderState.queues)
            }
        }
    }

    //TODO Ver como hacer la prueba con una cola
    private fun onClickOnQueue(queue: Queue) {
        btn_view.setOnClickListener {
            val action =
                queue.id?.let { it1 ->
                    HomeFragmentDirections
                        .actionHomeFragmentToHomeInfoQueueDialog(it1)
                }
            if (action != null) {
                view?.findNavController()?.navigate(action)
            }
        }

        btn_view_historical.setOnClickListener {
            val action =
                queue.id?.let { it1 ->
                   /* HomeFragmentDirections
                        .actionHomeFragmentToHomeInfoQueueDialog(it1)*/
                }
            if (action != null) {
                //view?.findNavController()?.navigate(action)
            }
        }
    }

    private fun updateUI(screenState: ScreenState<HomeFragmentScreenState>?) {
        when (screenState) {
            ScreenState.Loading -> {
            }
            is ScreenState.Render -> renderScreenState(screenState.renderState)
        }
    }

    private fun initializeObservers() {
        viewModel.screenState.observe(::getLifecycle, ::updateUI)
//        viewModel.failure.observe(::getLifecycle, ::handleErrors)
    }

}


