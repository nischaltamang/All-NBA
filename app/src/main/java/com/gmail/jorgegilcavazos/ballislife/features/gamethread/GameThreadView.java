package com.gmail.jorgegilcavazos.ballislife.features.gamethread;

import com.hannesdorfmann.mosby.mvp.MvpView;

import net.dean.jraw.models.CommentNode;

import java.util.List;

public interface GameThreadView extends MvpView {

    void setLoadingIndicator(boolean active);

    void showComments(List<CommentNode> comments);

    void hideComments();

    void showSnackbar(boolean canReload);

    void dismissSnackbar();
}
