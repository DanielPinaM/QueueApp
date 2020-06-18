package com.qflow.main.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qflow.main.R
import com.qflow.main.R.layout
import com.qflow.main.core.Failure
import com.qflow.main.core.ScreenState
import com.qflow.main.domain.local.models.Queue
import com.qflow.main.utils.enums.ValidationFailureType
import com.qflow.main.views.adapters.QueueAdminAdapter
import com.qflow.main.views.dialogs.InfoQueueDialog
import com.qflow.main.views.dialogs.ManagementQueueDialog
import com.qflow.main.views.screenstates.HomeFragmentScreenState
import com.qflow.main.views.viewmodels.HomeViewModel
import kotlinx.android.synthetic.creators.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), ManagementQueueDialog.OnAdvanceDialogButtonClick,
    ManagementQueueDialog.OnCloseDialogButtonClick, ManagementQueueDialog.OnResumeDialogButtonClick,
    ManagementQueueDialog.OnStopDialogButtonClick {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var queuesAdminAdapter: QueueAdminAdapter
    private lateinit var queuesAdminHistory: QueueAdminAdapter
    private var mInfoQueueDialog: InfoQueueDialog? = null
    private var mManageQueueDialog: ManagementQueueDialog? = null

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
        viewModel.screenState.observe(::getLifecycle, ::updateUI)
        viewModel.failure.observe(::getLifecycle, ::handleErrors)
    }

    private fun initializeButtons() {

        btn_create.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_createQueueFragment)
        }

    }

    private fun initializeRecycler() {

        queuesAdminAdapter =
            QueueAdminAdapter(ArrayList(), ::onClickOnQueue, ::onClickManageQueue, false)
        queuesAdminHistory =
            QueueAdminAdapter(ArrayList(), ::onClickHistoricalOnQueue, null, true)

        rv_adminqueues.adapter = queuesAdminAdapter
        rv_adminhistorial.adapter = queuesAdminHistory
        rv_adminhistorial.layoutManager =
            GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        rv_adminqueues.layoutManager =
            GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        viewModel.getQueues("all", false) //alluser, null All queues from that user
        viewModel.getHistory("all", true) //history, null All queues with a dateFinished
    }


    private fun renderScreenState(renderState: HomeFragmentScreenState) {
        loadingComplete()
        when (renderState) {
            is HomeFragmentScreenState.QueuesActiveObtained -> {
                queuesAdminAdapter.setData(renderState.queues)
            }
            is HomeFragmentScreenState.QueuesHistoricalObtained -> {
                queuesAdminHistory.setData(renderState.queues)
            }
            is HomeFragmentScreenState.QueueManageDialog -> {
                initializeDialogManagement(renderState.queues)
            }
        }
    }

    private fun initializeDialogManagement(queue: Queue) {
        if (mManageQueueDialog != null)
            mManageQueueDialog!!.dismiss()
        mManageQueueDialog = ManagementQueueDialog(queue)
        mManageQueueDialog!!.onAttachFragment(this)
        mManageQueueDialog!!.show(this.childFragmentManager, "MANAGEMENTDIALOG")
    }

    private fun onClickOnQueue(queue: Queue) {
        mInfoQueueDialog = InfoQueueDialog(queue, false)
        mInfoQueueDialog!!.onAttachFragment(this)
        mInfoQueueDialog!!.show(this.childFragmentManager, "INFODIALOG")
    }

    private fun onClickHistoricalOnQueue(queue: Queue) {
        mInfoQueueDialog = InfoQueueDialog(queue, true)
        mInfoQueueDialog!!.onAttachFragment(this)
        mInfoQueueDialog!!.show(this.childFragmentManager, "INFODIALOG")
    }

    private fun onClickManageQueue(queue: Queue) {
        mManageQueueDialog = ManagementQueueDialog(queue)
        mManageQueueDialog!!.onAttachFragment(this)
        mManageQueueDialog!!.show(this.childFragmentManager, "MANAGEMENTDIALOG")
    }

    private fun handleErrors(failure: Failure?) {
        when (failure) {
            is Failure.ValidationFailure -> {
                when (failure.validationFailureType) {
                    ValidationFailureType.FULL_CAPACITY -> {
                        Toast.makeText(
                            this.context, "Queue is full", Toast.LENGTH_LONG
                        ).show()
                    }
                    ValidationFailureType.QUEUE_CLOSE -> {
                        Toast.makeText(
                            this.context,
                            "You can´t advance a queue that has been closed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ValidationFailureType.QUEUE_LOCK -> {
                        Toast.makeText(
                            this.context,
                            "You can´t advance a queue that has been stopped",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ValidationFailureType.QUEUE_ADVANCE_STOP -> {
                        Toast.makeText(
                            this.context,
                            "If you want advance the queue that is stopped, first resume queue",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ValidationFailureType.QUEUE_ADVANCE_CLOSE -> {
                        Toast.makeText(
                            this.context,
                            "You can´t advance a queue which has been closed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ValidationFailureType.QUEUE_CLOSE_CLOSED -> {
                        Toast.makeText(
                            this.context,
                            "You can´t close a queue which has been closed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            is Failure.ServerException -> {
                loadingComplete()
                Toast.makeText(
                    this.context,
                    getString(R.string.LoadQueue),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateUI(screenState: ScreenState<HomeFragmentScreenState>?) {
        when (screenState) {
            ScreenState.Loading -> {
                loading()
            }
            is ScreenState.Render -> renderScreenState(screenState.renderState)
        }
    }


    private fun initializeObservers() {
        viewModel.screenState.observe(::getLifecycle, ::updateUI)
    }

    private fun loading() {

        loading_bar_home.visibility = View.VISIBLE
    }

    private fun loadingComplete() {
        loading_bar_home.visibility = View.INVISIBLE
    }


    override fun onStopButtonClick(queue: Queue) {
            queue.id?.let {
                queue.numPersons?.let { it1 ->
                    viewModel.stopQueue(it, it1)
                }
                Toast.makeText(
                    this.context,
                    "Queue has been stopped",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onCloseButtonClick(queue: Queue) {
        queue.id?.let {
            queue.numPersons?.let { it1 ->
                queue.dateFinished?.let { it2 ->
                    viewModel.closeQueue(
                        it, it1,
                        it2
                    )
                }
            }
            Toast.makeText(
                this.context,
                "Queue has been closed",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    override fun onAdvanceButtonClick(queue: Queue) {

        queue.id?.let {
            queue.dateFinished?.let { it1 ->
                queue.numPersons?.let { it2 ->
                    queue.capacity?.let { it3 ->
                        viewModel.advanceQueue(
                            queue.id,
                            queue.isLock,
                            it1,
                            it2,
                            it3
                        )
                    }
                }
            }
        }
    }

    override fun onResumeButtonClick(queue: Queue) {
        queue.id?.let {
            viewModel.resumeQueue(it)
            Toast.makeText(
                this.context,
                "Queue has been resumed",
                Toast.LENGTH_SHORT
            ).show()
        }


    }
}


