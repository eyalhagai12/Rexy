package com.dji.rexy;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.dji.FPVDemo.R;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.useraccount.UserAccountManager;

public class MainActivity extends Activity implements SurfaceTextureListener, OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    protected VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
    // Codec for video live view
    protected DJICodecManager mCodecManager = null;
    protected TextureView mVideoSurface = null;
    private Button forward_button, backward_button, turn_left_button, turn_right_button, land_button,
            takeoff_button, save_button, stop_button, yaw_right_button, yaw_left_button, up_button, down_button;
    //    private ToggleButton toggleVirtualStick;
    private TextView info, bat_status;
    private FlightCommandsAPI FPVcontrol;
    private Handler handler;
    private enum states {Floor, Takeoff, Land, Forward, Backward, Yaw_R, Yaw_L,Right, Left, Emergency, Hover}
    private states state = states.Floor;
    private LogCustom log;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        initUI();
        initListeners();

        // create log instance
        log = new LogCustom(getExternalFilesDir("LOG"));
        // create Flight Controller instance wrapped with FPV-API
        FPVcontrol = new FlightCommandsAPI(log, bat_status);
        // set a new timer for updating the Log each 1 second
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                log.write();
            }
        };
        timer = new Timer();
        timer.schedule(t, 0, 100);


        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {
            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };
    }

    protected void onProductChange() {
        initPreviewer();
        loginAccount();
    }

    private void loginAccount() {

        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        Log.e(TAG, "Login Success");
                    }

                    @Override
                    public void onFailure(DJIError error) {
                        showToast("Login Error:"
                                + error.getDescription());
                    }
                });
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        initPreviewer();
        onProductChange();

        if (mVideoSurface == null) {
            Log.e(TAG, "mVideoSurface is null");
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        uninitPreviewer();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view) {
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        uninitPreviewer();
        super.onDestroy();
    }

    private void initUI() {
        // init mVideoSurface
        mVideoSurface = findViewById(R.id.video_previewer_surface);
        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
        info = findViewById(R.id.status);
        info.setText(new String("Floor"));
        bat_status = findViewById(R.id.Battery_status);
        bat_status.setText(new String("100"));
        forward_button = findViewById(R.id.forward_button);
        backward_button = findViewById(R.id.backward_button);
        yaw_left_button = findViewById(R.id.yaw_left_button);
        yaw_right_button = findViewById(R.id.yaw_right_button);
        turn_left_button = findViewById(R.id.turn_left_button);
        turn_right_button = findViewById(R.id.turn_right_button);
        land_button = findViewById(R.id.land_button);
        takeoff_button = findViewById(R.id.take_off_button);
        up_button = findViewById(R.id.up_button);
        down_button = findViewById(R.id.down_button);
        save_button = findViewById(R.id.save_button);
        stop_button = findViewById(R.id.stop_button);
    }

    private void initListeners() {
        takeoff_button.setOnClickListener(this);
        land_button.setOnClickListener(this);
        forward_button.setOnClickListener(this);
        backward_button.setOnClickListener(this);
        turn_right_button.setOnClickListener(this);
        turn_left_button.setOnClickListener(this);
        save_button.setOnClickListener(this);
        stop_button.setOnClickListener(this);
        yaw_left_button.setOnClickListener(this);
        yaw_right_button.setOnClickListener(this);
        up_button.setOnClickListener(this);
        down_button.setOnClickListener(this);
    }


    private void initPreviewer() {

        BaseProduct product = FPVDemoApplication.getProductInstance();

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        } else {
            if (null != mVideoSurface) {
                mVideoSurface.setSurfaceTextureListener(this);
            }
            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
                VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(mReceivedVideoDataListener);
            }
        }
    }

    private void uninitPreviewer() {
        Camera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            // Reset the callback
            VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(null);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(this, surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        /*
            Main UI method, an onClick listener for all UI buttons.
            Updates the current state.
            Send commands to the drone using the interface class FPVcontrol.
         */
        switch (v.getId()) {
            case R.id.take_off_button:
                if (state == states.Floor) {
                    state = states.Takeoff;
                    info.setText(new String("Takeoff"));
                    log.setMode("Takeoff");
                    FPVcontrol.takeoff();
                    state = states.Hover;
                    info.setText(new String("Hover"));
                    log.setMode("Hover");
                }
                break;

            case R.id.land_button:
                if (state == states.Hover) {
                    state = states.Land;
                    info.setText(new String("Landing"));
                    log.setMode("Land");
                    FPVcontrol.land();
                    state = states.Floor;
                    info.setText(new String("Floor"));
                    log.setMode("Floor");
                }
                break;

            case R.id.forward_button:
                if (state != states.Floor){
                    state = states.Forward;
                    info.setText(new String("Forward"));
                    log.setMode("Forward");
                    FPVcontrol.set_pitch((float) 0.5, "Forward");
                }
                break;

            case R.id.backward_button:
                if (state != states.Floor){
                    state = states.Backward;
                    info.setText(new String("Backward"));
                    log.setMode("Backward");
                    FPVcontrol.set_pitch((float) -0.5, "Backward");
                }
                break;

            case R.id.yaw_left_button:
                if (state != states.Floor){
                    state = states.Yaw_L;
                    info.setText(new String("Yaw left"));
                    log.setMode("Yaw Left");
                    FPVcontrol.set_yaw((float) -5, "Yaw Left");
                }
                break;

            case R.id.yaw_right_button:
                if (state != states.Floor){
                    state = states.Yaw_R;
                    info.setText(new String("Yaw right"));
                    log.setMode("Yaw Right");
                    FPVcontrol.set_yaw((float) 5, "Yaw Right");
                }
                break;

            case R.id.turn_left_button:
                if (state != states.Floor){
                    state = states.Left;
                    info.setText(new String("Left"));
                    log.setMode("Left");
                    FPVcontrol.set_roll((float) -0.2, "Left");
                }
                break;

            case R.id.turn_right_button:
                if (state != states.Floor){
                    state = states.Right;
                    info.setText(new String("Right"));
                    log.setMode("Right");
                    FPVcontrol.set_roll((float) 0.2, "Right");
                }
                break;

            case R.id.stop_button:
                state = states.Hover;
                info.setText(new String("Hover"));
                log.setMode("Hover");
                FPVcontrol.stayOnPlace();
                break;

            case R.id.save_button:
                timer.cancel();
                log.close();
                showToast("Log Saved!");
                break;

            default:
                break;
        }
    }


    private boolean isMavicAir2() {
        BaseProduct baseProduct = FPVDemoApplication.getProductInstance();
        if (baseProduct != null) {
            return baseProduct.getModel() == Model.MAVIC_AIR_2;
        }
        return false;
    }

    private boolean isM300() {
        BaseProduct baseProduct = FPVDemoApplication.getProductInstance();
        if (baseProduct != null) {
            return baseProduct.getModel() == Model.MATRICE_300_RTK;
        }
        return false;
    }
}
