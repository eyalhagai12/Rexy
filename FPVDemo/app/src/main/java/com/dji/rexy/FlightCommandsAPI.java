package com.dji.rexy;
import android.util.Log;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class FlightCommandsAPI {

    private FlightController flightController;
    private static final String TAG = FlightCommandsAPI.class.getName();

    public FlightCommandsAPI(){
        initFlightController();
        Log.d(TAG, "Flight Controller init successfully!");

    }

    public void takeoff(){
        flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                Log.d(TAG, "Takeoff successfully!");
            }
        });
    }

    public void land(){
        flightController.getFlightAssistant().setLandingProtectionEnabled(false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                Log.d(TAG, "Disabled protection mode");
            }
        });
        flightController.startLanding(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                Log.d(TAG, "Started Landing!");
            }
        });
    }


    private void enableVirtualStick() {
        flightController.setVirtualStickAdvancedModeEnabled(true);
    }

    private void disableVirtualStick() {
        flightController.setVirtualStickAdvancedModeEnabled(false);
    }




    private void initFlightController() {
        try {
            Aircraft aircraft = FPVDemoApplication.getAircraftInstance();
            if (aircraft == null || !aircraft.isConnected()) {
                //showToast("Disconnected");
                flightController = null;
            } else {
                // reinit flight controller only if needed
                if (flightController == null) {
                    flightController = aircraft.getFlightController();
                    flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                    flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
                    flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
                    flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
                }

            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


}
