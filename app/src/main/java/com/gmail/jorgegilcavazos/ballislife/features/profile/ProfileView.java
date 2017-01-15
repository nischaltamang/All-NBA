package com.gmail.jorgegilcavazos.ballislife.features.profile;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

public interface ProfileView extends MvpView {

    void setLoadingIndicator(boolean active);

    void showContent(List<String> contentList);

    void hideContent();

    void setToolbarTitle(String title);

    void setProfileImage(int resId);

    void setUsername(String username);

    void setPostKarma(int postKarma);

    void setPostKarmaVisibility(boolean visible);

    void setCommentKarma(int commentKarma);

    void setCommentKarmaVisibility(boolean visible);

    void showSnackbar(boolean canReload);

    void dismissSnackbar();
}
