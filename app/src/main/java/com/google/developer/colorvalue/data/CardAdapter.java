package com.google.developer.colorvalue.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.developer.colorvalue.R;
import com.google.developer.colorvalue.ui.ColorView;

import java.util.Objects;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    // TODO: Remove
    private static final String TAG = CardAdapter.class.getSimpleName();

    public Cursor mCursor;
    private Context mContext;

    //declare interface
    private OnItemClicked onClick;

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    final private ListItemClickListener mItemClickListener;

    public CardAdapter(ListItemClickListener listener) {
//        this.mCursor = Objects.requireNonNull(cursor);
//        this.mContext = Objects.requireNonNull(context);
        this.mItemClickListener = listener;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onBindViewHolder(CardAdapter.ViewHolder holder, int position) {
        // TODO bind data to view
        Card card;
        String name;
        String color;

        if (mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                card = getItem(position);
                name = card.getName();
                color = card.getHex();
                holder.name.setBackgroundColor(Color.parseColor(color));
            }
        }


    }

    public Cursor getCursor() {
        return mCursor;
    }


    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }

    }

    /**
     * Return a {@link Card} represented by this item in the adapter.
     * Method is used to run machine tests.
     *
     * @param position Cursor item position
     * @return A new {@link Card}
     */
    public Card getItem(int position) {
        if (mCursor.moveToPosition(position)) {
            return new Card(mCursor);
        }
        return null;
    }

    /**
     * @param data update cursor
     */
    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    public interface ListItemClickListener {

        void onListItemClick(int clickedIndex);
    }

    /**
     * An Recycler item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ColorView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (ColorView) itemView.findViewById(R.id.color_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickPosition = getAdapterPosition();
            mItemClickListener.onListItemClick(clickPosition);

        }
    }
}
