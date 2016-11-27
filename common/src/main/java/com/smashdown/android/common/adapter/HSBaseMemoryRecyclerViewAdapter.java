package com.smashdown.android.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smashdown.android.common.R;
import com.smashdown.android.common.adapter.listener.HSBaseRefreshInterface;
import com.smashdown.android.common.adapter.listener.HSRefreshListener;
import com.smashdown.android.common.adapter.viewholder.EmptyViewHolder;
import com.smashdown.android.common.adapter.viewholder.FailedViewHolder;
import com.smashdown.android.common.adapter.viewholder.LoadingViewHolder;
import com.smashdown.android.common.app.HSApp;

import java.util.List;

public abstract class HSBaseMemoryRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter implements HSBaseRefreshInterface {
    protected HSRecyclerViewStatus mStatus      = HSRecyclerViewStatus.LOADING;
    protected boolean              mCanLoadMore = false;

    protected String mLoadingMessage = "Loading...";
    protected String mEmptyMessage   = "There is no data";
    protected String mFailedMessage  = "Cannot connect to server, after checking network status try it again please.";

    protected Context mContext = null;
    HSRefreshListener mRefreshListener;

    protected List<T> mDataList = null;

    public HSBaseMemoryRecyclerViewAdapter(Context context, List<T> data, HSRefreshListener refreshListener) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");

        mContext = context;
        mDataList = data;
        mRefreshListener = refreshListener;
    }

    @Override
    public int getPureItemCount() {
        int pureItemCount = mDataList.size();
        //        Log.d(HSApp.LOG_TAG, this.getClass().getSimpleName() + "::getPureItemCount() - pureItemCount=" + pureItemCount);

        return pureItemCount;
    }


    protected T getItem(int position) {
        if (mDataList == null) {
            return null;
        } else {
            try {
                return mDataList.get(position - getHeaderViewCount());
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Override
    public long getItemId(int index) {
        return super.getItemId(index);
    }

    // Header & Footer should be shown anytime.
    public int getHeaderViewCount() {
        return 0;
    }

    public int getFooterViewCount() {
        return 0;
    }

    public void setStatus(HSRecyclerViewStatus mStatus) {
        this.mStatus = mStatus;
    }

    public HSRecyclerViewStatus getStatus() {
        return mStatus;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.mLoadingMessage = loadingMessage;
    }

    public void setEmptyMessage(String emptyMessage) {
        this.mEmptyMessage = emptyMessage;
    }

    public void setFailedMessage(String failedMessage) {
        this.mFailedMessage = failedMessage;
    }

    @Override
    public boolean canLoadMore() {
        return mCanLoadMore;
    }

    public void setCanLoadMore(boolean mCanLoadMore) {
        this.mCanLoadMore = mCanLoadMore;
    }

    @Override
    public int getItemCount() {
        int itemCount;
        switch (mStatus) {
            case DONE:
                if (getPureItemCount() > 0) {
                    itemCount = getPureItemCount() + getHeaderViewCount() + getFooterViewCount();
                } else {
                    itemCount = 1 + getHeaderViewCount() + getFooterViewCount();
                }
                break;
            default:
                itemCount = 1;
                itemCount += getHeaderViewCount();
                itemCount += getFooterViewCount();
                break;
        }
        Log.d(HSApp.LOG_TAG, this.getClass().getSimpleName() + "::getItemCount() - mStatus=" + mStatus + ", itemCount=" + itemCount);

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        int type = HSRecyclerViewType.LOADING.ordinal();

        if (position < getHeaderViewCount()) {
            // Log.i(HSApp.LOG_TAG, this.getClass().getSimpleName() + "::getItemViewType() - pos=" + position + ", HEADER");
            return HSRecyclerViewType.HEADER.ordinal();
        }
        if (position > getHeaderViewCount() + getPureItemCount()) {
            // Log.i(HSApp.LOG_TAG, this.getClass().getSimpleName() + "::getItemViewType() - pos=" + position + ", FOOTER");
            return HSRecyclerViewType.FOOTER.ordinal();
        }

        switch (mStatus) {
            case LOADING:
                type = HSRecyclerViewType.LOADING.ordinal();
                break;
            case DONE:
                if (getPureItemCount() > 0)
                    type = HSRecyclerViewType.ITEM.ordinal();
                else
                    type = HSRecyclerViewType.EMPTY.ordinal();
                break;
            case FAILED:
                type = HSRecyclerViewType.FAILED.ordinal();
                break;
        }
        Log.i(HSApp.LOG_TAG, this.getClass().getSimpleName() + "::getItemViewType() - pos=" + position + ", type=" + type);

        return type;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == HSRecyclerViewType.LOADING.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_adapter_loading, parent, false);
            VH holder = (VH) new LoadingViewHolder(view);
            ((LoadingViewHolder) holder).tvLoadingMessage = (TextView) view.findViewById(R.id.tvLoadingMessage);
            ((LoadingViewHolder) holder).root = view.findViewById(R.id.root);
            return holder;
        } else if (viewType == HSRecyclerViewType.EMPTY.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_adapter_empty, parent, false);
            return (VH) new EmptyViewHolder(view, mRefreshListener);
        } else if (viewType == HSRecyclerViewType.FAILED.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_adapter_failed, parent, false);
            return (VH) new FailedViewHolder(view, mRefreshListener);
        }

        Log.e(HSApp.LOG_TAG, HSBaseMemoryRecyclerViewAdapter.class.getSimpleName() + "::onCreateViewHolder() - ERROR");
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            if (holder instanceof LoadingViewHolder) {
                ((LoadingViewHolder) holder).tvLoadingMessage.setText(mLoadingMessage);
                if (getHeaderViewCount() > 0) {
                    ((LoadingViewHolder) holder).root.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                } else {
                    ((LoadingViewHolder) holder).root.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                }
            } else if (holder instanceof EmptyViewHolder) {
                ((EmptyViewHolder) holder).tvEmptyMessage.setText(mEmptyMessage);
                if (getHeaderViewCount() > 0) {
                    ((EmptyViewHolder) holder).root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                } else {
                    ((EmptyViewHolder) holder).root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                }
            } else if (holder instanceof FailedViewHolder) {
                ((FailedViewHolder) holder).tvFailedMessage.setText(mFailedMessage);
                if (getHeaderViewCount() > 0) {
                    ((FailedViewHolder) holder).root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                } else {
                    ((FailedViewHolder) holder).root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                }
            }
        }
    }

    public void addData(List<T> newData, boolean clear) {
        if (clear)
            this.mDataList.clear();

        this.mDataList.addAll(newData);
    }
}