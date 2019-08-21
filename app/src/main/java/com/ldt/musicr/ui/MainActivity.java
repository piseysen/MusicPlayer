package com.ldt.musicr.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ldt.musicr.R;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.service.MusicService;
import com.ldt.musicr.ui.intro.IntroController;
import com.ldt.musicr.ui.playingqueue.PlayingQueueController;
import com.ldt.musicr.ui.bottomnavigationtab.BackStackController;
import com.ldt.musicr.ui.nowplaying.NowPlayingController;
import com.ldt.musicr.util.NavigationUtil;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_WRITE_STORAGE = 1;

    @BindView(R.id.rootEveryThing)
    public ConstraintLayout mRootEverything;

    @BindView(R.id.layer_container)
    public FrameLayout mLayerContainer;

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    private void bindView() {
        mRootEverything = findViewById(R.id.rootEveryThing);
        mLayerContainer = findViewById(R.id.layer_container);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
    }

    public BackStackController mBackStackController;
    public NowPlayingController mNowPlayingController;
    public PlayingQueueController mPlayingQueueController;

    public LayerController getLayerController() {
        return mLayerController;
    }

    public LayerController mLayerController;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_STORAGE: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // Granted
                        onPermissionGranted();
                    } else onPermissionDenied();
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public interface PermissionListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    private PermissionListener mPermissionListener;
    public  void setPermissionListener(PermissionListener listener) {
        mPermissionListener = listener;
    }

    public void removePermissionListener() {
        mPermissionListener = null;
    }

    private void onPermissionGranted() {
        if(mPermissionListener!=null) mPermissionListener.onPermissionGranted();
    }

    private void onPermissionDenied() {
        if(mPermissionListener!=null) mPermissionListener.onPermissionDenied();
    }

    IntroController mIntroController;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemeNoWallpaper);
        super.onCreate(savedInstanceState);
        assign(0);
        setContentView(R.layout.basic_activity_layout);
       // if(true) return;
        bindView();
        assign("set & bind");

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
        assign("set fullscreen");
        mRootEverything.post(new Runnable() {
            @Override
            public void run() {
                assign(2);
                boolean isPermissionGranted = checkSelfPermission();
                assign(3);
                if(!isPermissionGranted) {

                    if(mIntroController ==null)
                    mIntroController = new IntroController();

                    mIntroController.init(MainActivity.this,savedInstanceState);
                    assign(4);
                } else startGUI();
            }
        });

    }
    private long mStart = System.currentTimeMillis();
    private void assign(Object mark) {
        long current = System.currentTimeMillis();
        Log.d(TAG, "logTime: Time "+ mark+" = "+(current - mStart));
        mStart = current;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mLayerController!=null) mLayerController. onConfigurationChanged(newConfig);
    }

    public void startGUI() {
      //  Toast.makeText(this,"Start GUI",Toast.LENGTH_SHORT).show();
        //if(true) return;
        if(mIntroController!=null) {
            removePermissionListener();
            mIntroController.getNavigationController().popAllFragments();
        }

        assign(5);
        //runLoading();
        mLayerController = new LayerController(this);
        mBackStackController = new BackStackController();
        mNowPlayingController = new NowPlayingController();
        mPlayingQueueController = new PlayingQueueController();

        assign(6);
        mBackStackController.attachBottomNavigationView(this);

        assign(7);
        mLayerController.init(mLayerContainer,mBackStackController,mNowPlayingController, mPlayingQueueController);
        mRootEverything.postDelayed(() ->
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER, WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
                ,2500);
    }

    @Override
    protected void onStart() {
        super.onStart();
        assign("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        assign("onResume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
        mRootEverything.post(() -> handlePlaybackIntent(intent));
    }

    public boolean checkSelfPermission() {
     return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, MY_PERMISSIONS_WRITE_STORAGE);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_WRITE_STORAGE);

            }
        } else onPermissionGranted();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        if(mRootEverything!=null)
        mRootEverything.post(() -> handlePlaybackIntent(getIntent()));
    }

    @Override
    public void onBackPressed()
    {
        if(mLayerController !=null&& mLayerController.onBackPressed())
            return;
       super.onBackPressed();
    }

    public void setTheme(boolean white) {
        Log.d(TAG, "setTheme: "+white);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View root = mRootEverything;
            if(root!=null&&!white)
                root.setSystemUiVisibility(0);
            else if(root!=null)
                root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
    public boolean backStackStreamOnTouchEvent(MotionEvent event) {
        if(mBackStackController!=null) return mBackStackController.streamOnTouchEvent(event);
        return false;
    }

    private void handlePlaybackIntent(@Nullable Intent intent) {
        if(intent==null) {
            Log.d(TAG, "handlePlaybackIntent : null intent");
            return;
        }

        Uri uri = intent.getData();
        String mimeType = intent.getType();
        boolean handled = false;

        // log
        if(uri!=null)
            Log.d(TAG, "handlePlaybackIntent: uri_path = " + uri.getPath());
        else
            Log.d(TAG, "handlePlaybackIntent: uri_path = null");
        Log.d(TAG, "handlePlaybackIntent: mimeType = "+mimeType);

        Log.d(TAG, "handlePlaybackIntent: action = "+intent.getAction());

        if(intent.getAction() !=null && intent.getAction().equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {
            Log.d(TAG, "handlePlaybackIntent: type media play from search");
            handled = true;
        }

        if(uri != null && uri.toString().length() > 0) {
            Log.d(TAG, "handlePlaybackIntent: type play file");
            MusicPlayerRemote.playFromUri(uri);
            NavigationUtil.navigateToNowPlayingController(this);
            handled = true;
        } else if(MediaStore.Audio.Playlists.CONTENT_TYPE.equals(mimeType)) {
            Log.d(TAG, "handlePlaybackIntent: type playlist");
            handled = true;
        } else if(MediaStore.Audio.Albums.CONTENT_TYPE.equals(mimeType)) {
            Log.d(TAG, "handlePlaybackIntent: type album");
            handled = true;
        } else if(MediaStore.Audio.Artists.CONTENT_TYPE.equals(mimeType)) {
            Log.d(TAG, "handlePlaybackIntent: type artist");
            handled = true;
        } else if(!handled && MusicService.ACTION_ON_CLICK_NOTIFICATION.equals(intent.getAction())) {
            NavigationUtil.navigateToNowPlayingController(this);
            handled = true;
        } else if(!handled) {
            Log.d(TAG, "handlePlaybackIntent: unhandled: "+intent.getAction());
        }

        //NavigationUtil.navigateToNowPlayingController(this);

        if(handled)
        setIntent(new Intent());
    }

    public void popUpPlaylistTab() {
        if(mPlayingQueueController !=null) mPlayingQueueController.popUp();
    }

    public BackStackController getBackStackController() {
        return mBackStackController;
    }

    public NowPlayingController getNowPlayingController() {
        return mNowPlayingController;
    }

    public PlayingQueueController getPlayingQueueController() {
        return mPlayingQueueController;
    }
}
