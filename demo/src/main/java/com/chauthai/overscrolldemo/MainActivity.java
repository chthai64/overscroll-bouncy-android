package com.chauthai.overscrolldemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SIZE = 15;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setupRecyclerView();
    }

    public void onRowClicked(View v) {}

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        MyAdapter adapter = new MyAdapter(this, getDataSet(SIZE));
        recyclerView.setAdapter(adapter);
    }

    private List<String> getDataSet(int n) {
        List<String> data = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            data.add("row " + (i + 1));
        }

        return data;
    }
}
