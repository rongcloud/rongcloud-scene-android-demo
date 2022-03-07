package com.basis.ui.mvp;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseModel<V extends BasePresenter> implements ILifeCycle, LifecycleObserver {

    public V present;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BaseModel(V present, Lifecycle lifecycle) {
        this.present=present;
        lifecycle.addObserver(this);
    }

    public void addSubscription(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        present = null;
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }
}
