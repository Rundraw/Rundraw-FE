package com.example.rundraw_fe.listener;

import com.example.rundraw_fe.response.CommentResponse;

public interface CommentMenuListener {

    void onEdit(CommentResponse comment);

    void onDelete(CommentResponse comment);

}
