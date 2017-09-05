package com.example.noahh_000.bluetoothgix;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

public class BreathingActivity extends Activity {

    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        circleView = new CircleView(this);
        setContentView(circleView);
        circleView.setWillNotDraw(false);

        // Breathing routine
        final Routine routine = new Routine();
        routine.steps = new LinkedList<RoutineStep>();
        routine.steps.add(new RoutineStep(5000, (float)0, 1, "Inhale"));
        routine.steps.add(new RoutineStep(5000, 1, (float)0.1, "Exhale"));
        routine.steps.add(new RoutineStep(5000, (float)0.1, 1, "None"));
        routine.steps.add(new RoutineStep(5000, 1, (float)0.1, "None"));
        routine.steps.add(new RoutineStep(5000, (float)0.1, 1, "None"));
        routine.steps.add(new RoutineStep(5000, 1, (float)0.1, "None"));
        routine.steps.add(new RoutineStep(5000, (float)0.1, 1, "None"));
        routine.steps.add(new RoutineStep(5000, 1, (float)0.1, "None"));

        // Breathing preamble
        Animator be_Still = circleView.getChangeTextAnimation("Be still. Take slow deep breaths ...", false, 3000, 1000);
        Animator sense_breath = circleView.getChangeTextAnimation("Sensing your breathing.", false, 3000, 1000);
        Animator follow_circle = circleView.getChangeTextAnimation("Now follow the circle", false, 3000, 1000);
        AnimatorSet as = new AnimatorSet();
        as.play(be_Still);//.before(sense_breath);
        //as.play(sense_breath).before(follow_circle);
        as.start();
        as.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startRoutine(routine);
            }
        });
    }

    private void startRoutine(Routine routine)
    {
        AnimatorSet as = new AnimatorSet();

        Animator la = null;
        for (final RoutineStep rs : routine.steps) {
            Animator circleAnimator = rs.getAnimator();
            if (la != null)
                as.play(la).before(circleAnimator);
            la = circleAnimator;
        }
        as.start();
    }

    public class RoutineStep
    {
        public int length;
        public float endSize;
        public float startSize;
        public String type;

        public RoutineStep(int length, float startSize, float endSize, String type)
        {
            this.length = length;
            this.endSize = endSize;
            this.startSize = startSize;
            this.type = type;
        }

        public Animator getAnimator()
        {
            AnimatorSet as = new AnimatorSet();
            Animator circleAnim = circleView.getCircleSizeAnimator(this.startSize, this.endSize, this.length);
            if (type.equals("Inhale")) {
                Animator changeTextAnim = circleView.getChangeTextAnimation("Inhale", true, 1000, 1000);
                as.play(changeTextAnim).with(circleAnim);
            }

            if (type.equals("Exhale")) {
                Animator changeTextAnim = circleView.getChangeTextAnimation("Exhale", false, 1000, 1000);
                as.play(changeTextAnim).with(circleAnim);
            }
            as.play(circleAnim);

            return as;
        }
    }

    private class Routine
    {
        public List<RoutineStep> steps;
    }
}
