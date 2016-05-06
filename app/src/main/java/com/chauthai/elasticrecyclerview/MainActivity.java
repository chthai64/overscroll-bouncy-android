package com.chauthai.elasticrecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.scrollToPosition(10);


        ElasticAdapter adapter = new ElasticAdapter(this, getDataSet(20));
        recyclerView.setAdapter(adapter);

        ElasticDecorator decorator = new ElasticDecorator(recyclerView);
        recyclerView.addItemDecoration(decorator);
    }

    private List<String> getDataSet(int n) {
        List<String> data = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            data.add("data " + i);
        }

        return data;
    }
}
