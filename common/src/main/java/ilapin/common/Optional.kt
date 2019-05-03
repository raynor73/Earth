package ilapin.common

import java.util.NoSuchElementException

class Optional<M> private constructor(value: M?) {

    private var _value: M? = value

    val value: M
        get() = _value ?: throw NoSuchElementException("No value present")

    fun isEmpty(): Boolean {
        return _value == null
    }

    companion object {

        fun <M> create(value: M): Optional<M> {
            return Optional(value)
        }

        fun <M> empty(): Optional<M> {
            return Optional(null)
        }
    }
}