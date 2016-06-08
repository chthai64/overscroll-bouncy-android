package com.chauthai.elasticrecyclerview;

import android.graphics.PointF;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ConstantSmoothScroller mSmoothScroller;
    private SeekBar seekBar;
    Spring spring1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setupRecyclerView();

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSmoothScroller.setScrollSpeed(progress);
                mSmoothScroller.setTargetPosition(0);
                recyclerView.getLayoutManager().startSmoothScroll(mSmoothScroller);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        spring1 = mSpringSystem.createSpring();
        spring1.setSpringConfig(mSpringConfig);
        spring1.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                long currTime = SystemClock.elapsedRealtime();
                long delta = currTime - prevTime;
                prevTime = currTime;

                Log.d("yolo", "delta: " + delta + " ms");
            }
        });

    }


    private long prevTime = 0;

    private static final double TENSION = 100;
    private static final double FRICTION = 50;
    private final SpringSystem mSpringSystem = SpringSystem.create();
    private final SpringConfig mSpringConfig = new SpringConfig(TENSION, FRICTION);

    public void onButtonClick(View v) {
//        mSmoothScroller.setTargetPosition(0);
////        mSmoothScroller.forceVerticalSnap(ConstantSmoothScroller.SNAP_TO_END);
//        recyclerView.getLayoutManager().startSmoothScroll(mSmoothScroller);

        prevTime = SystemClock.elapsedRealtime();
        spring1.setCurrentValue(100);
        spring1.setEndValue(0);
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ElasticAdapter adapter = new ElasticAdapter(recyclerView, this, getDataSet(20));
        AdapterWrapper adapterWrapper = new AdapterWrapper(this, recyclerView, adapter);
        recyclerView.setAdapter(adapterWrapper);


        mSmoothScroller = new ConstantSmoothScroller(this) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return new PointF(0, -1); // -1 scroll down
            }
        };
    }

    private List<String> getDataSet(int n) {
        List<String> data = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            data.add("data " + i);
        }

        return data;
    }
}
