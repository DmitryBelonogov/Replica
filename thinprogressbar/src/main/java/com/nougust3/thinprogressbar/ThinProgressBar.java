package com.nougust3.thinprogressbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class ThinProgressBar extends RelativeLayout {

    private FrameLayout progressView;

    private int max;
    private int progress;

    public ThinProgressBar(Context context) {
        super(context);
        setupView();
    }

    public ThinProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    public ThinProgressBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setupView();
    }

    private void setupView() {
        setGravity(Gravity.START);

        createProgressView();

        setMax(0);
        setProgress(0);
    }

    private void createProgressView() {
        progressView = new FrameLayout(getContext());

        progressView.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
        progressView.setBackgroundColor(0x6655ff55);

        this.addView(progressView);
    }

    private void updateProgress() {
        if(max == 0) {
            return;
        }

        progressView.getLayoutParams().width = getWidth() * progress / max;
        progressView.requestLayout();
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public void setProgress(int progress) {
        if(progress > max) {
            this.progress = max;
        }
        else {
            this.progress = progress;
        }

        updateProgress();
    }

    public int getProgress() {
        return progress;
    }

    public void setColor(int color) {
        progressView.setBackgroundColor(color);
    }

}
