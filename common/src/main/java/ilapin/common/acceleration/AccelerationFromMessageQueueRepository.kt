package ilapin.common.acceleration

import ilapin.common.messagequeue.MessageQueue
import io.reactivex.Observable

class AccelerationFromMessageQueueRepository(private val messageQueue: MessageQueue) : AccelerationRepository {

    override fun acceleration(): Observable<Acceleration> {
        return messageQueue
            .messages()
            .filter { message -> message is Acceleration }
            .map { message -> message as Acceleration }
    }
}