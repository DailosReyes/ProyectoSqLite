package es.dailos.proyectosqlite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.dailos.proyectosqlite.R;

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener<T> {
        void onEdit(T item);
        void onInfo(T item);
    }

    public interface TextBinder<T> {
        String getTitle(T item);
        String getSubtitle(T item);
    }

    private List<T> items;
    private final TextBinder<T> binder;
    private final OnItemClickListener<T> listener;
    private int iconResId = 0;

    public RecyclerViewAdapter(List<T> items, TextBinder<T> binder, OnItemClickListener<T> listener) {
        this.items = items;
        this.binder = binder;
        this.listener = listener;
    }

    public RecyclerViewAdapter(List<T> items, int iconResId, TextBinder<T> binder, OnItemClickListener<T> listener) {
        this.items = items;
        this.iconResId = iconResId;
        this.binder = binder;
        this.listener = listener;
    }

    public void updateList(List<T> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = items.get(position);
        holder.tvTitulo.setText(binder.getTitle(item));
        holder.tvSubtitulo.setText(binder.getSubtitle(item));
        if (iconResId != 0) {
            holder.imgAvatar.setImageResource(iconResId);
        }
        holder.btnEditar.setOnClickListener(v -> listener.onEdit(item));
        holder.btnInfo.setOnClickListener(v -> listener.onInfo(item));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(
                holder.itemView.getContext(),
                R.anim.zoom_in
        );
        holder.itemView.startAnimation(animation);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvTitulo, tvSubtitulo;
        ImageButton btnEditar, btnInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvSubtitulo = itemView.findViewById(R.id.tvSubtitulo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnInfo = itemView.findViewById(R.id.btnInfo);
        }
    }
}
