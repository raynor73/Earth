package ilapin.earth.domain.time

interface TimeRepository {

    fun getTimestamp(): Long
}