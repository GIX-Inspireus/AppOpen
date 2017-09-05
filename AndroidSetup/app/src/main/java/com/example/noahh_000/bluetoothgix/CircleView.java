package com.example.noahh_000.bluetoothgix;

/**
 * Created by NoahH_000 on 05.09.2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.SystemClock;
import android.transition.Fade;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

public class CircleView extends View {

    private Paint mPaint;
    private int width;
    private int height;

    public float guideSize = 0;

    private boolean textViewSetLower = false;
    private boolean textViewSetCenter = false;

    LinearLayout layout;
    public TextView textView;
    public SparkView sparkView;

    boolean userTouched = false;

    public float userSize = 0;

    public CircleView(Context context) {
        super(context);

        layout = new LinearLayout(context);

        textView = new TextView(context);

        textView.setAlpha(0);
        textView.setText("Hello world");
        textView.setTextColor(Color.WHITE);
        layout.setGravity(Gravity.CENTER);
        layout.addView(textView);

        //sparkView = new SparkView(this.getContext());
        //sparkView.setAdapter(new HeartRateAdapter(data));


        // create the Paint and set its color
        mPaint = new Paint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        userTouched = true;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                userTouched = true;
                break;
            case MotionEvent.ACTION_UP:
                userTouched = false;
                break;
        }
        return true;
    }

    public class HeartRateAdapter extends SparkAdapter {
        private float[] yData;

        public HeartRateAdapter(float[] yData) {
            this.yData = yData;
        }

        @Override
        public int getCount() {
            return yData.length;
        }

        @Override
        public Object getItem(int index) {
            return yData[index];
        }

        @Override
        public float getY(int index) {
            return yData[index];
        }
    }

    public Animator getCircleSizeAnimator(float startSize, final float endSize, int length)
    {
        ValueAnimator ChangeSizeAnim = ValueAnimator.ofFloat(startSize, endSize);
        ChangeSizeAnim.setDuration(length); //one second
        ChangeSizeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float intermediateValue = (float) valueAnimator.getAnimatedValue();
                guideSize = intermediateValue;
                invalidate();
            }
        });
        ChangeSizeAnim.setInterpolator(new CustomAccelerateDecelerateInterpolator(4));
        return ChangeSizeAnim;
    }

    public class CustomAccelerateDecelerateInterpolator implements Interpolator {
        float accfactor;

        public CustomAccelerateDecelerateInterpolator(float accfactor) {
            this.accfactor = accfactor;
        }

        @SuppressWarnings({"UnusedDeclaration"})
        public CustomAccelerateDecelerateInterpolator(Context context, AttributeSet attrs) {
        }

        public float getInterpolation(float input) {
            if ((input *= 2) < 1) {
                return (float) (0.5 * Math.pow(input, accfactor));
            }

            return (float) (1 - 0.5 * Math.abs(Math.pow(2 - input, accfactor)));
        }
    }

    public Animator getChangeTextAnimation(final String newText, final boolean showLower, final int waitTime, final int animTime)
    {
        AnimatorSet as = new AnimatorSet();
        ValueAnimator FadeInAnim = ValueAnimator.ofFloat(0, 1);
        FadeInAnim.setDuration(animTime); //one second

        final ValueAnimator FadeOutAnim = ValueAnimator.ofFloat(1, 0);
        FadeOutAnim.setDuration(animTime); //one second

        final ValueAnimator WaitAnim = ValueAnimator.ofFloat(1, 1);
        WaitAnim.setDuration(waitTime); //one second

        FadeInAnim.setInterpolator(new DecelerateInterpolator());
        FadeOutAnim.setInterpolator(new AccelerateInterpolator());

        FadeInAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float intermediateValue = (float) valueAnimator.getAnimatedValue();
                textView.setAlpha(intermediateValue);
                layout.invalidate();
                invalidate();
            }
        });
        FadeOutAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float intermediateValue = (float) valueAnimator.getAnimatedValue();
                textView.setAlpha(intermediateValue);
                layout.invalidate();
                invalidate();
            }
        });

        as.play(FadeInAnim).before(WaitAnim);
        as.play(WaitAnim).before(FadeOutAnim);

        FadeInAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                textView.setText(newText);
                if (showLower)
                    textViewSetLower = true;
            }
        });
        FadeOutAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (showLower)
                    textViewSetCenter = true;
            }
        });
        return as;
    }

    public float velocityFunction(float size)
    {
        return Math.min(size * 4, (1 - size) * (1 - size) * (1 - size));
    }

    public int getUserColor(float guideSize, float userSize)
    {
        int color;
        double diff = (Math.abs(guideSize - userSize));
        if (diff < 0.1)
            color = Color.WHITE;
        else
            color = Color.rgb(255, 255 - (int)((diff-0.1) * 255), 255 - (int)((diff-0.1) * 255));

        return color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = canvas.getWidth();
        height = canvas.getHeight();

        if (userTouched)
            userSize += 0.01;
        else
            if (userSize >= 0)
                userSize -= 0.01;

        float guideVelocity = velocityFunction(guideSize);
        float userVelocity = velocityFunction(userSize);

        int userColor = getUserColor(guideSize, userSize);

        if (textViewSetLower) {
            layout.setPadding(0, 0, 0, height / 2);
            textViewSetLower = false;
        }
        if (textViewSetCenter) {
            layout.setPadding(0, 0, 0, 0);
            textViewSetCenter = false;
        }

        canvas.drawColor(Color.BLACK);

        mPaint = new Paint();
        mPaint.setStrokeWidth(150 * guideVelocity + 10);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(width / 2, height / 2, (height  / 4) * (guideSize), mPaint);

        mPaint = new Paint();
        mPaint.setStrokeWidth(150 * userVelocity + 10);
        mPaint.setColor(userColor);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(width / 2, height / 2, (height  / 4) * (userSize), mPaint);

        layout.measure(canvas.getWidth(), canvas.getHeight());
        layout.layout(0, 0, canvas.getWidth(), canvas.getHeight());

        layout.draw(canvas);
    }

    //int r = (int)(getHeight() / 8 + guideSize * getHeight() / 8);
    //mPaint.setShader(new RadialGradient(getWidth() / 2, getHeight() / 2,
    //        r, Color.TRANSPARENT, Color.YELLOW, Shader.TileMode.MIRROR));
    //mPaint.setAlpha(180);
}