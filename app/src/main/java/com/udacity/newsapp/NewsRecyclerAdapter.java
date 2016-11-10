package com.udacity.newsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.newsapp.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by geovani on 10/11/16.
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<DummyContent.DummyItem> mValues;
    private ArrayList<DummyContent.DummyItem> auxNewses;
    private boolean mTwoPane;


    public NewsRecyclerAdapter(Context context, List<DummyContent.DummyItem> items) {
        mContext = context;
        mValues = items;
        auxNewses = new ArrayList<>();
        auxNewses.addAll(mValues);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false);
        if (view.findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mTitleView.setText(mValues.get(position).title);
        holder.mDateView.setText(mValues.get(position).date);
        holder.mSectionView.setText(mValues.get(position).section);

        startupActivityOnClick(holder);
    }

    private void startupActivityOnClick(final ViewHolder holder) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
                } else
                {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    //
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mValues == null) { return 0; }
        return mValues.size();
    }


    public void animateTo(List<DummyContent.DummyItem> newses) {
        applyAndAnimateRemovals(newses);
        applyAndAnimateAdditions(newses);
        applyAndAnimateMovedItems(newses);
    }

    private void applyAndAnimateMovedItems(List<DummyContent.DummyItem> newses) {
        for (int toPosition = newses.size() - 1; toPosition >= 0; toPosition--) {
            final DummyContent.DummyItem model = newses.get(toPosition);
            final int fromPosition = mValues.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void moveItem(int fromPosition, int toPosition) {
        final DummyContent.DummyItem model = mValues.remove(fromPosition);
        mValues.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void applyAndAnimateAdditions(List<DummyContent.DummyItem> newses) {
        for (int i = 0, count = newses.size(); i < count; i++) {
            final DummyContent.DummyItem news = newses.get(i);
            addItem(i, news);
        }
    }

    public void addItem(int position, DummyContent.DummyItem news) {
        mValues.add(position, news);
        notifyItemInserted(position);
    }

    private void applyAndAnimateRemovals(List<DummyContent.DummyItem> newses) {
        for (int i = mValues.size() - 1; i >= 0; i--) {
            final DummyContent.DummyItem news = mValues.get(i);
            if (!newses.contains(news)) {
                removeItem(i);
            }
        }
    }

    public DummyContent.DummyItem removeItem(int position) {
        final DummyContent.DummyItem news = mValues.remove(position);
        notifyItemRemoved(position);
        return news;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public void filter(String trim, ArrayList<DummyContent.DummyItem> newses) {
        trim = trim.toLowerCase(Locale.getDefault());
        if (newses != null) {
            newses.clear();
        }
        if (trim.length() == 0) {
            assert newses != null;
            newses.addAll(auxNewses);//
        } else {
            for (DummyContent.DummyItem news : auxNewses) {
                if (trim.length() != 0 && news.title.toLowerCase(Locale.getDefault()).contains(trim)) {
                    assert newses != null;
                    newses.add(news);
                }
                else
                if (trim.length() != 0 && news.section.toLowerCase(Locale.getDefault()).contains(trim)) {
                    assert newses != null;
                    newses.add(news);
                }
            }
        }
        notifyDataSetChanged();
    }


    public List<DummyContent.DummyItem> filter(String query, List<DummyContent.DummyItem> newses) {
        ArrayList<DummyContent.DummyItem> result = new ArrayList<>();
        query = query.toLowerCase();
        for (DummyContent.DummyItem item : newses) {
            final String id      = item.id.toLowerCase();
            final String title   = item.title.toLowerCase();
            final String section = item.section.toLowerCase();
            final String date    = item.date;
            final String details = item.details;
            if (id.contains(query) || date.contains(query) || details.contains(query) ||
                    title.contains(query) || section.contains(query)) {
                result.add(item);
            }
        }
        return result;
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView mIdView;
        public final TextView mTitleView;
        public final TextView mDateView;
        public final TextView mSectionView;
        public DummyContent.DummyItem mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView        = view;
            mIdView      = (TextView) view.findViewById(R.id.id);
            mTitleView   = (TextView) view.findViewById(R.id.titleText);
            mDateView    = (TextView) view.findViewById(R.id.dateText);
            mSectionView = (TextView) view.findViewById(R.id.sectionText);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }



}
