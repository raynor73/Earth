package ilapin.common.android.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class MessageDialog : DialogFragment() {

    private lateinit var listener: Listener

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        listener = requireContext() as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(TITLE_KEY) ?: throw IllegalArgumentException("No message dialog title")
        val message = arguments?.getString(MESSAGE_KEY)
        val positiveButtonTitle = arguments?.getString(POSITIVE_BUTTON_TITLE_KEY) ?: throw IllegalAccessException("No message dialog positive button title")
        val neutralButtonTitle = arguments?.getString(NEUTRAL_BUTTON_TITLE_KEY)
        val negativeButtonTitle = arguments?.getString(NEGATIVE_BUTTON_TITLE_KEY)
        val builder = AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setPositiveButton(positiveButtonTitle) { _, _ -> listener.onConfirm() }
        message?.let { builder.setMessage(it) }
        neutralButtonTitle?.let {
            builder.setNeutralButton(it) { _, _ -> listener.onNeutral() }
        }
        negativeButtonTitle?.let {
            builder.setNegativeButton(it) { _, _ -> listener.onCancel() }
        }
        return builder.create()
    }

    interface Listener {

        fun onConfirm()

        fun onNeutral()

        fun onCancel()
    }

    companion object {

        private const val TITLE_KEY = "messageDialogTitle"
        private const val MESSAGE_KEY = "messageDialogMessage"
        private const val POSITIVE_BUTTON_TITLE_KEY = "positiveButtonTitle"
        private const val NEUTRAL_BUTTON_TITLE_KEY = "neutralButtonTitle"
        private const val NEGATIVE_BUTTON_TITLE_KEY = "negativeButtonTitle"

        @JvmStatic
        fun newInstance(
                title: String,
                message: String?,
                positiveButtonTitle: String,
                neutralButtonTitle: String?,
                negativeButtonTitle: String?
        ): MessageDialog {
            val fragment = MessageDialog()

            Bundle().apply {
                putString(TITLE_KEY, title)
                message?.let { putString(MESSAGE_KEY, it) }
                putString(POSITIVE_BUTTON_TITLE_KEY, positiveButtonTitle)
                neutralButtonTitle?.let { putString(NEUTRAL_BUTTON_TITLE_KEY, it) }
                negativeButtonTitle?.let { putString(NEGATIVE_BUTTON_TITLE_KEY, it) }
                fragment.arguments = this
            }

            return fragment
        }
    }
}