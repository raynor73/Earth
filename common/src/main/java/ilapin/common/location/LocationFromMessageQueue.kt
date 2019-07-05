package ilapin.common.location

import ilapin.common.messagequeue.MessageQueue
import io.reactivex.Observable

class LocationFromMessageQueue(private val messageQueue: MessageQueue) : LocationRepository {

    override fun location(): Observable<Location> {
        return messageQueue
            .messages()
            .filter { message -> message is Location }
            .map { message -> message as Location }
    }
}