package ilapin.common.android.time

import android.os.Build
import android.os.SystemClock
import ilapin.common.time.TimeRepository

class LocalTimeRepository : TimeRepository {

    override fun getTimestamp(): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SystemClock.elapsedRealtimeNanos()
        } else {
            System.nanoTime()
        }
    }
}