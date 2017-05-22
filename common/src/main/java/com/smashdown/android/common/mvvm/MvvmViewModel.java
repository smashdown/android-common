package com.smashdown.android.common.mvvm;

import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.NonNull;

public interface MvvmViewModel<V extends MvvmView> extends Observable {

    void attachView(V view, Bundle savedInstanceState);

    void detachView();

    void saveInstanceState(@NonNull Bundle outState);

    void init(Intent intent);

    void init(Bundle bundle); // for fragments
}