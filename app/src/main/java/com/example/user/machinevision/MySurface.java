package com.example.user.machinevision;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * Created by user on 2017.02.20..
 */
public class MySurface extends SurfaceView implements Callback {

    SurfaceHolder surfaceHolder;

    public MySurface(Context context) {
        super(context);
        setWillNotDraw(false);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Surface", " surfaceCreated,  height: " + surfaceHolder.getSurfaceFrame().height());
//surfaceHolder.setSizeFromLayout();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
