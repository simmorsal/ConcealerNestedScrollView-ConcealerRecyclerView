package com.simmorsal.concealernestedscrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.simmorsal.library.ConcealerRecyclerView;

public class ConcealerRecyclerViewTestActivity extends AppCompatActivity {

    private ConcealerRecyclerView rv;
    private View viewHeader, viewFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concealer_recycler_view_test);

        rv = findViewById(R.id.rv);
        viewHeader = findViewById(R.id.crdHeaderView);
        viewFooter = findViewById(R.id.linFooterView);

        setupConcealerRV();
        runRv();
    }

    private void setupConcealerRV() {
        viewHeader.post(new Runnable() {
            @Override
            public void run() {
                rv.setHeaderView(viewHeader, 16);
            }
        });
        viewFooter.post(new Runnable() {
            @Override
            public void run() {
                rv.setFooterView(viewFooter, 0);
            }
        });

        rv.setFooterPercentageToHide(80);
    }

    private void runRv() {
        rv.setAdapter(new AdapterRv(this, rv));
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
}
