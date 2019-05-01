package ilapin.earth.frameworkdependent.magneticfield

import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.magneticfield.MagneticField
import ilapin.earth.domain.magneticfield.MagneticFieldRepository
import io.reactivex.Observable

class MagneticFieldFromMessageQueueRepository(private val messageQueue: MessageQueue) : MagneticFieldRepository {

    override fun magneticField(): Observable<MagneticField> {
        return messageQueue
            .messages()
            .filter { message -> message is MagneticField }
            .map { message -> message as MagneticField }
    }
}