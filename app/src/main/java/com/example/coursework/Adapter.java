package com.example.coursework;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.coursework.models.Article;


import java.util.List;

/**
 * Adapter class used for linking the UI and the data found in the newsapi.org.
 * Creates holders for the items by utilising the RecyclerView Adapter view holders.
 * Creates listeners for the articles and links the necessary resources from the articles to be displayed
 * in the recycler view holder. The listeners can then be used in main activity when one of these articles
 * are clicked.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {

    private List<Article> articles;
    private Context context;
    private OnItemClickListener onItemClickListener;

    //Main method constructor for the Adapter.
    public Adapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    //Called when the Recycler View needs new view holder.
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new viewHolder(view, onItemClickListener);
    }

    //Updates the view holder contents with the item at a given position.
    @Override
    public void onBindViewHolder(@NonNull viewHolder holders, int position) {

        final viewHolder holder = holders;
        Article model = articles.get(position);

        RequestOptions requestOptions = new RequestOptions();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                })

                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        holder.title.setText(model.getTitle());
        holder.desc.setText((model.getDescription()));
        holder.source.setText(model.getSource().getName());
        holder.time.setText(" \u2022 " + Utils.DateToTimeFormat(model.getPublishedAt()) );
    }

    //Returns the amount of articles.
    @Override
    public int getItemCount(){
        return articles.size();
    }

    //Sets the on item click listener.
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    //Creates the interface for the item click, on what view and where.
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    //Sets up the view holder for the recycler view. Specifies the resources to be used for each part
    //of the article.
    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, desc, source, time;
        ImageView imageView;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;

        public viewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            source = itemView.findViewById(R.id.source);
            time = itemView.findViewById(R.id.time);
            imageView = itemView.findViewById(R.id.img);
            progressBar = itemView.findViewById(R.id.progress_load_photo);

            this.onItemClickListener = onItemClickListener;

        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }


    }
}
