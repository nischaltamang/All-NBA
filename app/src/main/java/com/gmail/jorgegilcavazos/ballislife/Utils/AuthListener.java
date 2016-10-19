package com.gmail.jorgegilcavazos.ballislife.Utils;

import com.gmail.jorgegilcavazos.ballislife.Service.RedditAuthentication;

/**
 * Listener for the {@link RedditAuthentication}.
 */
public interface AuthListener {
    void onSuccess();

    void onFailure();
}
