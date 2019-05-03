package ilapin.common.android.log

import timber.log.Timber

class L {

    companion object {

        fun d(tag: String, msg: String, vararg args: Any) {
            Timber.tag(tag)
            Timber.d(msg, *args)
        }

        fun e(tag: String, t: Throwable, msg: String, vararg args: Any) {
            Timber.tag(tag)
            Timber.e(t, msg, *args)
        }

        fun e(tag: String, t: Throwable) {
            Timber.tag(tag)
            Timber.e(t)
        }
    }
}