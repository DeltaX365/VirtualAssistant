package vn.sttechnology.virtualassistant.ui.common;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private String mViewType;
    private List<T> mDataSource;
    private OnRecyclerViewRowTouchListener<T> mOnRecyclerViewRowTouchListener;
    private int mLayoutId;

    public RecyclerViewAdapter(Context context, String viewType, int layoutId) {
        mContext = context;
        mViewType = viewType;
        mLayoutId = layoutId;
    }

    public void setData(List<T> dataSource) {
        mDataSource = dataSource;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewRowTouchListener<T> onRecyclerViewRowTouchListener) {
        mOnRecyclerViewRowTouchListener = onRecyclerViewRowTouchListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        IRecyclerViewRow<T> row = (IRecyclerViewRow<T>) inflater.inflate(mLayoutId, parent, false);
        ViewHolder vh = new ViewHolder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.showData(mDataSource.get(position), mViewType);
        ((View) holder.itemView).setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (mOnRecyclerViewRowTouchListener != null) {
                        mOnRecyclerViewRowTouchListener.onDoubleTap(mDataSource.get(position));
                    }
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (mOnRecyclerViewRowTouchListener != null) {
                        mOnRecyclerViewRowTouchListener.onTap(mDataSource.get(position));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    if (mOnRecyclerViewRowTouchListener != null) {
                        mOnRecyclerViewRowTouchListener.onLongTap(mDataSource.get(position));
                    }
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        holder.itemView.setOnItemClickListener(mOnRecyclerViewRowTouchListener);
    }

    @Override
    public int getItemCount() {
        return (mDataSource != null)? mDataSource.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public IRecyclerViewRow itemView;

        public ViewHolder(IRecyclerViewRow itemView) {
            super((View) itemView);
            this.itemView = itemView;
        }
    }
}
