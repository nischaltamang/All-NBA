package com.gmail.jorgegilcavazos.ballislife.features.profile;

import com.hannesdorfmann.mosby.mvp.MvpView;

import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;


public interface ProfileView extends MvpView {

    void setLoadingIndicator(boolean active);

    void showContent(Listing<Contribution> contentList);

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
