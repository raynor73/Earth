package ilapin.common.android.magneticfield

import ilapin.common.messagequeue.MessageQueue
import ilapin.common.magneticfield.MagneticField
import ilapin.common.magneticfield.MagneticFieldRepository
import io.reactivex.Observable

class MagneticFieldFromMessageQueueRepository(private val messageQueue: MessageQueue) : MagneticFieldRepository {

    override fun magneticField(): Observable<MagneticField> {
        return messageQueue
            .messages()
            .filter { message -> message is MagneticField }
            .map { message -> message as MagneticField }
    }
}