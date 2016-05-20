package com.chauthai.elasticrecyclerview;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.SmoothScroller mSmoothScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setupRecyclerView();
    }

    public void onButtonClick(View v) {
        mSmoothScroller.setTargetPosition(0);
        recyclerView.getLayoutManager().startSmoothScroll(mSmoothScroller);
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ElasticAdapter adapter = new ElasticAdapter(recyclerView, this, getDataSet(20));
        AdapterWrapper adapterWrapper = new AdapterWrapper(this, recyclerView, adapter);
        recyclerView.setAdapter(adapterWrapper);

        mSmoothScroller = new LinearSmoothScroller(this) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return new PointF(0, -1);
            }
        };

        mSmoothScroller = new RecyclerView.SmoothScroller() {
            @Override
            protected void onStart() {

            }

            @Override
            protected void onStop() {

            }

            @Override
            protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {

            }

            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {

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
