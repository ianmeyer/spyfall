package com.iantmeyer.spyfall.util;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class CounterHelper {

    private final CounterListener mListener;
    private final View mIncrementView;
    private final View mDecrementView;

    private final long mMin;
    private final long mMax;
    private final long mStep;

    private final long mMaxDelay;
    private final long mMinDelay;
    private final long mDelayStep;

    private final boolean mLoop;

    private long mValue;
    private long mDelay;

    private boolean mIncrementPressed = false;
    private boolean mDecrementPressed = false;

    private Handler mHandler = new Handler();

    private CounterHelper(Builder builder) {
        mListener = builder.mListener;
        mIncrementView = builder.mIncrementView;
        mDecrementView = builder.mDecrementView;

        mMin = builder.mMin;
        mMax = builder.mMax;
        mStep = builder.mStep;

        mMaxDelay = builder.mMaxDelay;
        mMinDelay = builder.mMinDelay;
        mDelayStep = builder.mDelayStep;

        mLoop = builder.mLoop;

        mValue = builder.mStart;
        mDelay = builder.mMaxDelay;

        initIncrement();
        initDecrement();

        if (mListener != null) {
            mListener.onCounterChange(mValue);
        }
    }

    public interface CounterListener {
        void onCounterChange(long value);
    }

    private void initIncrement() {
        if (mIncrementView == null) {
            return;
        }
        mIncrementView.setOnTouchListener(mOnTouchListener);
    }

    private void initDecrement() {
        if (mDecrementView == null) {
            return;
        }
        mDecrementView.setOnTouchListener(mOnTouchListener);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (view.equals(mIncrementView)) {
                        mIncrementPressed = true;
                        increment();
                    } else {
                        mDecrementPressed = true;
                        decrement();
                    }
                    mHandler.postDelayed(mRunnable, mMaxDelay);
                    break;

                case MotionEvent.ACTION_UP:
                    mDecrementPressed = false;
                    mIncrementPressed = false;
                    mDelay = mMaxDelay;
                    mHandler.removeCallbacksAndMessages(null);
                    break;
            }
            return false;
        }
    };

    private void increment() {
        if (mValue <= mMax && mValue + mStep > mMax) {
            if (mLoop) {
                mValue = mMin;
            } else {
                mValue = mMax;
            }
        } else {
            mValue += mStep;
        }
        mListener.onCounterChange(mValue);
    }

    private void decrement() {
        if (mValue >= mMin && mValue - mStep < mMin) {
            if (mLoop) {
                mValue = mMax;
            } else {
                mValue = mMin;
            }
        } else {
            mValue -= mStep;
        }
        mListener.onCounterChange(mValue);
    }

    private long getDelay() {
        mDelay -= mDelayStep;
        mDelay = mDelay < mMinDelay ? mMinDelay : mDelay;
        return mDelay;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIncrementPressed) {
                increment();
                mHandler.postDelayed(mRunnable, getDelay());
            } else if (mDecrementPressed) {
                decrement();
                mHandler.postDelayed(mRunnable, getDelay());
            }
        }
    };

    public static final class Builder {
        private CounterListener mListener;
        private View mIncrementView;
        private View mDecrementView;
        private long mMin = 0;
        private long mMax = -1;
        private long mStart = 0;
        private long mStep = 1;
        private long mMaxDelay = 1000;
        private long mDelayStep = 0;
        private long mMinDelay = 1000;
        private boolean mLoop = false;

        public Builder() {
        }

        public Builder listener(CounterListener listener) {
            mListener = listener;
            return this;
        }

        public Builder incrementView(View view) {
            mIncrementView = view;
            return this;
        }

        public Builder decrementView(View view) {
            mDecrementView = view;
            return this;
        }

        public Builder range(long min, long max) {
            mMin = min;
            mMax = max;
            return this;
        }

        public Builder start(long value) {
            mStart = value;
            return this;
        }

        public Builder step(long value) {
            mStep = value;
            return this;
        }

        public Builder delay(long millis) {
            mMaxDelay = millis;
            mMinDelay = millis;
            mDelayStep = 0;
            return this;
        }

        public Builder delay(long maxMillis, long minMillis, long stepMillis) {
            mMaxDelay = maxMillis;
            mMinDelay = minMillis;
            mDelayStep = stepMillis;
            return this;
        }

        public Builder loop(boolean loop) {
            mLoop = loop;
            return this;
        }

        public CounterHelper build() {
            return new CounterHelper(this);
        }
    }
}