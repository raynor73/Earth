package ilapin.common.android.input

import ilapin.common.input.TouchEvent
import ilapin.common.input.TouchScreenRepository
import ilapin.common.messagequeue.MessageQueue
import io.reactivex.Observable

class TouchEventFromMessageQueueRepository(private val messageQueue: MessageQueue) : TouchScreenRepository {

    override fun touchEvents(): Observable<TouchEvent> {
        return messageQueue
            .messages()
            .filter { message -> message is TouchEvent }
            .map { message -> message as TouchEvent }
    }
}