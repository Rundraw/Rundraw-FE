package com.example.rundraw_fe.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.listener.CommentMenuListener;
import com.example.rundraw_fe.response.CommentResponse;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<CommentResponse> items = new ArrayList<>();

    private final CommentMenuListener listener;



    public CommentAdapter(
            CommentMenuListener listener
    ){

        this.listener = listener;

    }

    public void setItems(List<CommentResponse> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.comment_item,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        CommentResponse comment = items.get(position);

        holder.writer.setText(comment.getMemberName());
        holder.content.setText(comment.getContent());
//        holder.date.setText(comment.getCreatedAt());


        // 기본 상태 초기화
        holder.btnMenu.setVisibility(View.GONE);
        holder.btnMenu.setOnClickListener(null);


        // 내 댓글만 메뉴 표시
        if(Boolean.TRUE.equals(comment.getIsMine())){


            holder.btnMenu.setVisibility(View.VISIBLE);


            holder.btnMenu.setOnClickListener(v -> {


                PopupMenu popupMenu =
                        new PopupMenu(
                                v.getContext(),
                                holder.btnMenu
                        );


                popupMenu.inflate(
                        R.menu.menu_comment
                );


                popupMenu.setOnMenuItemClickListener(item -> {


                    if(item.getItemId() == R.id.menu_edit){


                        listener.onEdit(comment);

                        return true;


                    }else if(item.getItemId() == R.id.menu_delete){


                        listener.onDelete(comment);

                        return true;

                    }


                    return false;

                });


                popupMenu.show();


            });

        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView writer;
        TextView content;
        TextView date;
        ImageButton btnMenu;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            writer = itemView.findViewById(R.id.tvCommentWriter);
            content = itemView.findViewById(R.id.tvCommentContent);
//            date = itemView.findViewById(R.id.tvCommentDate);
            btnMenu = itemView.findViewById(R.id.btnMenu);

        }

    }

}