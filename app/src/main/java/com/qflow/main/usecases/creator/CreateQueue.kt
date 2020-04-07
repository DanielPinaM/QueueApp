package com.qflow.main.usecases.creator

import com.qflow.main.core.Failure
import com.qflow.main.repository.QueueRepository
import com.qflow.main.usecases.Either
import com.qflow.main.usecases.UseCase
import com.qflow.main.utils.enums.ValidationFailureType
import kotlinx.coroutines.CoroutineScope

/**
 * UseCaseCreateUserInDatabase
 * */
class CreateQueue(private val queueRepository: QueueRepository) :
    UseCase<String, CreateQueue.Params, CoroutineScope>() {
    override suspend fun run(params: Params): Either<Failure, String> {

        return when (val result = validQueue(params.capacity)) {
            is Either.Left -> Either.Left(result.a)
            is Either.Right -> {
                queueRepository.createQueue(
                    params.nameCreateQueue, params.businessAssociated,
                    params.capacity.toInt(), params.queueDescription)
            }
        }
    }

    private fun validQueue(capacity: String): Either<Failure, Unit> {
        return if (capacity.toInt() > 0)
            Either.Right(Unit)
        else
            Either.Left(Failure.ValidationFailure(ValidationFailureType.CAPACITY_TOO_SMALL))
    }

    class Params(
        val nameCreateQueue: String, val businessAssociated: String, val queueDescription: String,
        val capacity: String
    )


}