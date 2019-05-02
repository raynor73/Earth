package ilapin.earth.frameworkdependent.orientation

import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.orientation.Orientation
import ilapin.earth.domain.orientation.OrientationRepository
import io.reactivex.Observable

class OrientationFromMessageQueueRepository(private val messageQueue: MessageQueue) : OrientationRepository {

    override fun orientation(): Observable<Orientation> {
        return messageQueue
            .messages()
            .filter { message -> message is Orientation }
            .map { message -> message as Orientation }
    }
}