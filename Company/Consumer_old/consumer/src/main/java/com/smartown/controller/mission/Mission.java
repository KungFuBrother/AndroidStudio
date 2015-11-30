package com.smartown.controller.mission;

public abstract class Mission implements Runnable {

    private boolean isCanceled = false;

    @Override
    public void run() {
        start();
        cacel();
    }

    public void cacel() {
        isCanceled = true;
    }

    public abstract void start();

    public boolean isCanceled() {
        return isCanceled;
    }

}
