package ilapin.common.android.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PressReleaseButton extends AppCompatButton {

	private Listener mListener;

	public PressReleaseButton(final Context context) {
		super(context);
	}

	public PressReleaseButton(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public PressReleaseButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if (mListener != null) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mListener.onPress();
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					mListener.onRelease();
					break;
			}
		}

		return super.onTouchEvent(event);
	}

	public void setListener(final Listener listener) {
		mListener = listener;
	}

	public interface Listener {

		void onPress();

		void onRelease();
	}
}
