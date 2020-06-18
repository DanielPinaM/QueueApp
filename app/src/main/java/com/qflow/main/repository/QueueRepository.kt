package com.qflow.main.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.qflow.main.core.BaseRepository
import com.qflow.main.core.Failure
import com.qflow.main.core.extensions.empty
import com.qflow.main.domain.adapters.QueueAdapter
import com.qflow.main.domain.local.SharedPrefsRepository
import com.qflow.main.domain.local.models.Queue
import com.qflow.main.domain.server.ApiService
import com.qflow.main.domain.server.models.QueueServerModel
import com.qflow.main.usecases.Either

interface QueueRepository {

    suspend fun createQueue(
        token: String,
        name: String,
        description: String,
        capacity: Int,
        business_associated: String,
        avg_service_time: Int
    ): Either<Failure, String>

    suspend fun joinQueue(joinId: Int, token: String): Either<Failure, String>
    //suspend fun fetchAdminQueuesRepository(isActive: Boolean): Either<Failure, List<Queue>>
    suspend fun fetchQueueByJoinId(idJoin: Int): Either<Failure, Queue>
    suspend fun fetchQueueById(idQueue: Int): Either<Failure, Queue>
    suspend fun fetchQueuesByUser(token: String, expand: String?, finished: Boolean?): Either<Failure, List<Queue>>

    class General
    constructor(
        private val queueAdapter: QueueAdapter,
        private val apiService: ApiService,
        private val prefsRepository: SharedPrefsRepository
    ) : BaseRepository(), QueueRepository {

        override suspend fun createQueue(
            token: String,
            name: String,
            description: String,
            capacity: Int,
            business_associated: String,
            avg_service_time: Int
        ): Either<Failure, String> {

            val queueMap =
                QueueServerModel(name, description, capacity, business_associated, avg_service_time).createMap()
            val prueba = Gson().toJson(queueMap)

            return request(
                apiService.postQueue(
                    prueba,
                    token
                ), {
                    it
                },
                String.empty()
            )
        }

        override suspend fun fetchQueueById(idQueue: Int): Either<Failure, Queue> {
            return request(apiService.getQueueByQueueId(idQueue), {
                queueAdapter.jsonStringToQueue(it)
                    }, String.empty())
        }

        override suspend fun joinQueue(
            joinId: Int,
            token: String
        ): Either<Failure, String> {
            return request(apiService.postJoinQueue(joinId,token), {
                it
            }, String.empty())
        }

        override suspend fun fetchQueueByJoinId(idJoin: Int): Either<Failure, Queue> {
            return request(apiService.getQueueByJoinId(idJoin), {
                queueAdapter.jsonStringToQueue(it)
            }, String.empty())
        }


        override suspend fun fetchQueuesByUser(token: String, expand: String?, finished: Boolean?): Either<Failure, List<Queue>> {
            return request(
                apiService.getQueuesByUser(
                    token,
                    expand,
                    finished
                ), {
                    queueAdapter.jsonStringToQueueList(it)
                }, String.empty()
            )
        }
    }
}
