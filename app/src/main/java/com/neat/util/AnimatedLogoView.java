package com.neat.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.neat.R;

import java.io.IOException;

/**
 * Created by f.gatti.gomez on 01/11/2016.
 */

public class AnimatedLogoView extends TextureView implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer;

    public AnimatedLogoView(Context context) {
        super(context);
        init();
    }

    public AnimatedLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedLogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setSurfaceTextureListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Surface s = new Surface(surface);

        try {
            mMediaPlayer= new MediaPlayer();
            AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.logo);
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mMediaPlayer.setSurface(s);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.start();
        } catch (IllegalArgumentException | SecurityException | IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
