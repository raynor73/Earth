package ilapin.common.orientation

import ilapin.common.messagequeue.MessageQueue
import io.reactivex.Observable

class OrientationFromMessageQueueRepository(private val messageQueue: MessageQueue) : OrientationRepository {

    override fun orientation(): Observable<Orientation> {
        return messageQueue
            .messages()
            .filter { message -> message is Orientation }
            .map { message -> message as Orientation }
    }
}