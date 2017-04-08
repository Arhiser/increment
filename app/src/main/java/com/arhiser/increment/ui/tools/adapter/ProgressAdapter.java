package com.arhiser.increment.ui.tools.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arhiser.increment.R;


/**
 * Created by arhis on 25.01.2017.
 */

public abstract class ProgressAdapter<ItemType> extends RecyclerView.Adapter {

    protected static final int ITEM_PROGRESS = 0;
    protected static final int ITEM_REGULAR = 1;
    protected static final int ITEM_EMPTY_PLACEHOLDER = -1;

    protected boolean isInProgress = false;
    protected boolean showPlaceholder = false;

    protected abstract int getRegularItemsCount();
    protected abstract ItemType getRegularItemAt(int position);
    protected abstract RegularViewHolder getNewRegularHolder(ViewGroup parent);
    protected abstract int getPlaceholderLayoutId();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM_PROGRESS:
                return new ProgressViewHolder(inflater.inflate(R.layout.item_progress_view, parent, false));
            case ITEM_REGULAR:
                return getNewRegularHolder(parent);
            case ITEM_EMPTY_PLACEHOLDER:
                return new EmptyHolder(inflater.inflate(getPlaceholderLayoutId(), parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(isInProgress && position == getProgressPosition()) && !showPlaceholder) {
            ((RegularViewHolder<ItemType>)holder).bind(getRegularItemAt(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showPlaceholder) {
            return ITEM_EMPTY_PLACEHOLDER;
        }
        if (isInProgress && position == getProgressPosition()) {
            return ITEM_PROGRESS;
        }
        return ITEM_REGULAR;
    }

    protected int getProgressPosition() {
        return getRegularItemsCount();
    }

    @Override
    public int getItemCount() {
        if (showPlaceholder) {
            return 1;
        }
        return getRegularItemsCount() + (isInProgress ? 1 : 0);
    }

    public void setProgress(boolean inProgress) {
        if (isInProgress && !inProgress) {
            notifyItemRemoved(getProgressPosition());
        }
        if (!isInProgress && inProgress) {
            notifyItemInserted(getProgressPosition());
        }
        isInProgress = inProgress;
    }

    public void showPlaceholder(boolean showPlaceholder) {
        if (this.showPlaceholder != showPlaceholder) {
            this.showPlaceholder = showPlaceholder;
            notifyDataSetChanged();
        }
    }

    public static abstract class RegularViewHolder<ItemType> extends RecyclerView.ViewHolder {

        public abstract void bind(ItemType item);

        public RegularViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
}
