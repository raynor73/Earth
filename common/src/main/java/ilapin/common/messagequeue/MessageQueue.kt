package ilapin.common.messagequeue

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.LinkedBlockingDeque

class MessageQueue {

    private val blockingQueue = LinkedBlockingDeque<Any>()

    private val messageSubject = PublishSubject.create<Any>()

    fun messages(): Observable<Any> = messageSubject

    fun putMessage(message: Any) {
        blockingQueue.put(message)
    }

    fun putMessageAndWaitForExecution(message: Any) {
        blockingQueue.put(message)
        /*while (blockingQueue.size > 0) {
            // do nothing
        }*/
        update()
    }

    fun update() {
        while (blockingQueue.size > 0) {
            messageSubject.onNext(blockingQueue.take())
        }
    }
}