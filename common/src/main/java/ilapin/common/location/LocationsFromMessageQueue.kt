package ilapin.common.location

import ilapin.common.messagequeue.MessageQueue
import io.reactivex.Observable

class LocationsFromMessageQueue(private val messageQueue: MessageQueue) : LocationsRepository {

    override fun locations(): Observable<Location> {
        return messageQueue
            .messages()
            .filter { message -> message is Location }
            .map { message -> message as Location }
    }
}