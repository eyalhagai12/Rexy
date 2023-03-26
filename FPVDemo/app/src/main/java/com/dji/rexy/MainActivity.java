package com.dji.rexy;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dji.rexy.R;
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

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pytorch.Module;

public class MainActivity extends Activity implements SurfaceTextureListener, OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    protected VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
    // Codec for video live view
    protected DJICodecManager mCodecManager = null;
    protected TextureView mVideoSurface = null;
    private ImageView testImg;

    private ImageButton forward_button, backward_button, turn_left_button, turn_right_button, land_button,
            takeoff_button, save_button, stop_button, yaw_right_button, yaw_left_button, up_button,
            down_button, record_button, lang_button;
//    private TextView info, bat_status, voice_command_view;
    private FlightCommandsAPI FPVcontrol;
    private FlightCommandUI UI_commands;
    private Handler handler;
    private LogCustom log;
    private T2S speaker;
    private AndroidSpeechRecognition speechRec;
    private Timer timer;

    // Speech2Text params
    private final static int REQUEST_RECORD_AUDIO = 13;
    public static final Integer RecordAudioRequestCode = 1;
    private final static int AUDIO_LEN_IN_SECOND = 4;
    private final static int SAMPLE_RATE = 16000;
    private final static int RECORDING_LENGTH = SAMPLE_RATE * AUDIO_LEN_IN_SECOND;
    private int mStart = 1;

    static {
        System.loadLibrary("opencv_java4");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initListeners();
        initParams();
        requestMicrophonePermission();
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

    private void initParams(){
        // create log instance
        log = new LogCustom(getExternalFilesDir("LOG"));
        // create Flight Controller instance wrapped with FPV-API
        FPVcontrol = new FlightCommandsAPI(log);
        // A UI class for flight commands.
        UI_commands = new FlightCommandUI(this.log, this.FPVcontrol);
        // set a new timer for updating the Log each 1 second
        handler = new Handler();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                log.write();
            }
        };
        timer = new Timer();
        timer.schedule(t, 0, 100);
        // init Text 2 Speech engine
        speaker = new TextToSpeechAPI(getApplicationContext());
        speechRec = new AndroidSpeechRecognition(getApplicationContext(), this.record_button, UI_commands, speaker, this);
    }

    private void initUI() {
        // init mVideoSurface
        mVideoSurface = findViewById(R.id.video_previewer_surface);
        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
//        info = findViewById(R.id.status);
//        info.setText(new String("Floor"));
//        bat_status = findViewById(R.id.Battery_status);
//        bat_status.setText(new String("100"));
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
        record_button = findViewById(R.id.record_button);
//        voice_command_view = findViewById(R.id.command_text);
//        lang_button = findViewById(R.id.lang_button);
        testImg = findViewById(R.id.test_view);
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
        record_button.setOnClickListener(this);
//        lang_button.setOnClickListener(this);

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
//        mVideoSurface.setVisibility(View.INVISIBLE);
        Bitmap sourceBitmap = Bitmap.createScaledBitmap(mVideoSurface.getBitmap(),mVideoSurface.getWidth(),mVideoSurface.getHeight(),false);
        Mat src = new Mat(sourceBitmap.getHeight(), sourceBitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(sourceBitmap, src);
        Mat gray = new Mat(src.rows(), src.cols(), src.type());
        Mat edges = new Mat(src.rows(), src.cols(), src.type());
        Mat dst = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));
        //Converting the image to Gray
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
        //Blurring the image
        Imgproc.blur(gray, edges, new Size(3, 3));
        //Detecting the edges
        Imgproc.Canny(edges, edges, 20, 20*3);
        //Copying the detected edges to the destination matrix
        src.copyTo(dst, edges);
        Bitmap resultBitmap = Bitmap.createScaledBitmap(mVideoSurface.getBitmap(),mVideoSurface.getWidth(),mVideoSurface.getHeight(),false);
        Utils.matToBitmap(dst, resultBitmap);
        testImg.setImageBitmap(resultBitmap);
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
                UI_commands.takeoff();
                speakCommand(0);
                break;

            case R.id.land_button:
                UI_commands.land();
                speakCommand(1);
                break;

            case R.id.forward_button:
                UI_commands.forward();
                speakCommand(2);
                break;

            case R.id.backward_button:
                UI_commands.backward();
                speakCommand(3);
                break;

            case R.id.yaw_left_button:
                UI_commands.yaw_left();
                speakCommand(6);
                break;

            case R.id.yaw_right_button:
                UI_commands.yaw_right();
                speakCommand(7);
                break;

            case R.id.turn_left_button:
                UI_commands.turn_left();
                speakCommand(4);
                break;

            case R.id.turn_right_button:
                UI_commands.turn_right();
                speakCommand(5);
                break;

            case R.id.up_button:
                UI_commands.up();
                speakCommand(8);
                break;

            case R.id.down_button:
                UI_commands.down();
                speakCommand(9);
                break;

            case R.id.stop_button:
                UI_commands.stop();
                speakCommand(10);
                break;

            case R.id.save_button:
                this.save_log();
                break;

            case R.id.record_button:
                this.speechRec.recognition();
                break;

//            case R.id.lang_button:
//                break;
            default:
                break;
        }
    }

    private void requestMicrophonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
            }
        }
    }

    private void save_log(){
        timer.cancel();
        log.close();
        showToast("Log Saved!");
    }

    private void speakCommand(int commandKey){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speaker.keyToSpeech(commandKey);
            }
        });
    }



}
