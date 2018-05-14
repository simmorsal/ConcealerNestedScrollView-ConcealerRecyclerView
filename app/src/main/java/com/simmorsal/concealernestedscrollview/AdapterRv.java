package com.simmorsal.concealernestedscrollview;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simmorsal.library.ConcealerRecyclerView;

import java.util.Arrays;
import java.util.List;



public class AdapterRv extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> data = Arrays.asList("455A64", "90A4AE", "455A64", "90A4AE", "455A64", "90A4AE", "455A64", "90A4AE");
    private Activity context;
    private ConcealerRecyclerView rv;


    public AdapterRv(Activity context, ConcealerRecyclerView rv) {
        this.context = context;
        this.rv = rv;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_layout, parent, false);
        return new CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final CellFeedViewHolder h = (CellFeedViewHolder) viewHolder;

        h.root.setBackgroundColor(Color.parseColor("#" + data.get(position)));

        h.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        rv.setHeaderConcealable(false, true);
                        break;

                    case 1:
                        rv.setFooterConcealable(false, false);
                        break;

                    case 2:
                        rv.setHeaderConcealable(true, false);
                        break;

                    case 3:
                        rv.setFooterConcealable(true, true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        View root;

        CellFeedViewHolder(View view) {
            super(view);

            root = view.findViewById(R.id.root);
        }
    }
}
