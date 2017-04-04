package com.example.cosas_faciles.mizu_a;

/**
 * Created by Durgrim on 4/4/2017.
 */

public class Singleton {
    private static Singleton mInstance = null;

    private ConnectedThread mConnectedThread;

    private Singleton(){}

    public static Singleton getInstance(){
        if(mInstance == null) {
            mInstance = new Singleton();
        }

        return mInstance;
    }

    public ConnectedThread getConnectedThread(){
        return this.mConnectedThread;
    }

    public void setConnectedThread(ConnectedThread connectedThread){
        mConnectedThread = connectedThread;
    }
}
