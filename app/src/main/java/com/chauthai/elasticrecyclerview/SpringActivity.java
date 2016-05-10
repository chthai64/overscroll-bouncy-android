package com.chauthai.elasticrecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.Locale;

/**
 * Created by Chau Thai on 5/9/16.
 */
public class SpringActivity extends AppCompatActivity {
    private static final int MAX_SPRING_LENGTH = 20;
    private static final double TENSION = 100;
    private static final double FRICTION = 20;

    private SeekBar seekTension, seekFriction;
    private TextView tvTension, tvFriction;

    private View container;
    private View layout3, layout4, layout5;
    private View layoutBlue;
    private View layoutBlack;
    private View layoutGreen;

    private SpringSystem springSystem;
    private Spring spring1, spring2, spring3, spring4, spring5;

    private final SpringConfig springConfig = new SpringConfig(TENSION, FRICTION);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring);
        getSupportActionBar().hide();
        getWidgets();

        springSystem = SpringSystem.create();

        setupSpring5();
        setupSpring4();
        setupSpring3();
        setupSpring2();
        setupSpring1();

        setupTouch();
        setupSeekBars();
    }

    private void setupSpring5() {
        spring5 = springSystem.createSpring();
        spring5.setSpringConfig(springConfig);
        spring5.setOvershootClampingEnabled(true);
        spring5.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                double value = spring.getCurrentValue();
                double newPos = layout4.getY() - value - layout5.getHeight();
                layout5.setY((float) newPos);
            }
        });
    }

    private void setupSpring4() {
        spring4 = springSystem.createSpring();
        spring4.setSpringConfig(springConfig);
        spring4.setOvershootClampingEnabled(true);
        spring4.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                double value = spring.getCurrentValue();
                double newPos = layout3.getY() - value - layout4.getHeight();
                float prevY = layout4.getY();

                layout4.setY((float) newPos);
                onFourMove(prevY - newPos);
            }
        });
    }

    private void setupSpring3() {
        spring3 = springSystem.createSpring();
        spring3.setSpringConfig(springConfig);
        spring3.setOvershootClampingEnabled(true);
        spring3.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                double value = spring.getCurrentValue();
                double newPos = layoutBlue.getY() - value - layout3.getHeight();
                float prevY = layout3.getY();

                layout3.setY((float) newPos);
                onThreeMove(prevY - newPos);
            }
        });
    }

    private void setupSpring2() {
        spring2 = springSystem.createSpring();
        spring2.setSpringConfig(springConfig);
        spring2.setOvershootClampingEnabled(true);
        spring2.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                double value = spring.getCurrentValue();
                double posBlue = layoutBlack.getY() - value - layoutBlue.getHeight();
                float prevY = layoutBlue.getY();

                layoutBlue.setY((float) posBlue);
                onBlueMove(prevY - posBlue);

            }
        });
    }

    private void setupSpring1() {
        spring1 = springSystem.createSpring();
        spring1.setSpringConfig(springConfig);
        spring1.setOvershootClampingEnabled(true);
        spring1.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                double value = spring.getCurrentValue();
                double posBlack = layoutGreen.getY() - value - layoutBlack.getHeight();
                float prevY = layoutBlack.getY();

                layoutBlack.setY((float) posBlack);
                onBlackMove(prevY - posBlack);
            }
        });
    }

    private void onFourMove(double distance) {
        double diff = getDiff(layout4, layout5);
        double prevVel = spring5.getVelocity();

        spring5.setCurrentValue(diff);
        if (diff > 0) {
            spring5.setVelocity(prevVel);
        }
        spring5.setEndValue(0);
    }

    private void onThreeMove(double distance) {
        double diff = getDiff(layout3, layout4);
        double prevVel = spring4.getVelocity();

        spring4.setCurrentValue(diff);
        if (diff > 0) {
            spring4.setVelocity(prevVel);
        }
        spring4.setEndValue(0);
    }

    private void onBlueMove(double distance) {
        double diff = getDiff(layoutBlue, layout3);
        double prevVelocity = spring3.getVelocity();

        spring3.setCurrentValue(diff);
        if (diff > 0) {
            spring3.setVelocity(prevVelocity);
        }
        spring3.setEndValue(0);
    }

    private void onBlackMove(double distanceY) {
        double diff = getDiff(layoutBlack, layoutBlue);
//        double diff = -distanceY;

        double prevVelocity = spring2.getVelocity();

        spring2.setCurrentValue(diff);
        if (diff > 0) {
            spring2.setVelocity(prevVelocity);
        }
        spring2.setEndValue(0);
    }

    private double getDiff(View view1, View view2) {
        double diff = view1.getY() - view2.getY() - view2.getHeight();
        return Math.min(Math.max(diff, 0), MAX_SPRING_LENGTH);
    }

    private void setupTouch() {
        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                layoutGreen.setY(layoutGreen.getY() - distanceY);
                float diff = (float) getDiff(layoutGreen, layoutBlack);
//                float diff = -distanceY;

                double prevVelocity = spring1.getVelocity();

                spring1.setCurrentValue(diff);
                if (diff > 0) {
                    spring1.setVelocity(prevVelocity);
                }
                spring1.setEndValue(0);
                return true;
            }
        });

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void setupSeekBars() {
        seekTension.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTension.setText("" + progress);
                springConfig.tension = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekFriction.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFriction.setText("" + progress);
                springConfig.friction = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void btnOneOnClick(View v) {
        spring1.setEndValue(1.0);
    }

    private void getWidgets() {
        container = findViewById(R.id.container);
        layoutBlue = findViewById(R.id.layout_blue);
        layoutBlack = findViewById(R.id.layout_1);
        layoutGreen = findViewById(R.id.layout_2);
        layout3 = findViewById(R.id.layout_3);
        layout4 = findViewById(R.id.layout_4);
        layout5 = findViewById(R.id.layout_5);

        seekFriction = (SeekBar) findViewById(R.id.seek_friction);
        seekTension = (SeekBar) findViewById(R.id.seek_tension);

        tvFriction = (TextView) findViewById(R.id.tv_friction);
        tvTension = (TextView) findViewById(R.id.tv_tension);
    }

    private String format(double value) {
        return String.format(Locale.US, "%1$,.2f", value);
    }
}
