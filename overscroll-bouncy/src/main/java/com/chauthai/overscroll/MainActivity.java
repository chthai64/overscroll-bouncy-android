package com.chauthai.overscroll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SIZE = 20;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MyAdapter adapter;
    private OverScrollHelper overScrollHelper;

    private SeekBar seekBar;

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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void onButtonClick(View v) {
//        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//        recyclerView.fling(0, 2000);

            adapter.notifyItemRemoved(3);
    }


    public void onRowClicked(View v) {}

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyAdapter(this, getDataSet(SIZE));
        recyclerView.setAdapter(adapter);

//        overScrollHelper = new OverScrollHelper.Builder(this, recyclerView).build();
//        overScrollHelper.bindAdapter(adapter);

    }

    private List<String> getDataSet(int n) {
        List<String> data = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            data.add("data " + i);
        }

        return data;
    }
}
