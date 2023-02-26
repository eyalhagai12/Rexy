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
    private LogCustom log;
    private static final String TAG = FlightCommandsAPI.class.getName();

    public FlightCommandsAPI(LogCustom main_log){
        initFlightController();
        log = main_log;
        log.setController(flightController);
        log.setDebug("Flight Controller init successfully!");

    }

    public void takeoff(){
        flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                log.setDebug("Takeoff successfully!");
            }
        });
    }

    public void land(){
        flightController.getFlightAssistant().setLandingProtectionEnabled(false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError != null){
                    log.setDebug(djiError.toString());
                }
                else
                    log.setDebug("Disabled protection mode");
            }
        });
        flightController.startLanding(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError != null){
                    log.setDebug(djiError.toString());
                }
                else
                    log.setDebug("Started Landing!");

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
                // re-init flight controller only if needed
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
