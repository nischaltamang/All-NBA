package com.gmail.jorgegilcavazos.ballislife.util;

import com.gmail.jorgegilcavazos.ballislife.network.RedditAuthentication;

/**
 * Listener for the {@link RedditAuthentication}.
 */
public interface AuthListener {
    void onSuccess();

    void onFailure();
}
