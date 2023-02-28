package com.dji.rexy;
import android.util.Log;
import android.widget.TextView;

import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightOrientationMode;
import dji.common.flightcontroller.virtualstick.FlightControlData;
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
    private Aircraft aircraft;
    private TextView bat_status;
    private FlightControlData flightcontroldata;
    private static final String TAG = FlightCommandsAPI.class.getName();

    public FlightCommandsAPI(LogCustom main_log, TextView bat_stat){
        initFlightController();
        bat_status = bat_stat;
        // init log variables
        log = main_log;
        log.setController(flightController);
        log.setGimbal(aircraft.getGimbal());
        log.setBattery(aircraft.getBattery());
        log.initListeners();
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

    public void stayOnPlace(){
        setControlCommand(0, 0, 0, 0, 0, "Stay on place");
    }
    public void forward(float pitch){
        setControlCommand(0, pitch, 0, 0, 0, "Forward");
    }

    public void setControlCommand(float yaw, float pitch, float roll, float throttle, float gimbal_pitch, String command_name){
        /*
            This method receives flight params and sends a command to the drone
         */
        flightcontroldata.setPitch(pitch);
        flightcontroldata.setRoll(roll);
        flightcontroldata.setYaw(yaw);
        flightcontroldata.setVerticalThrottle(throttle);

        if (flightController.isVirtualStickControlModeAvailable()) {
            flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
            flightController.sendVirtualStickFlightControlData(flightcontroldata, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null){
                        log.setDebug(command_name + " " + djiError.toString());
                    }
                    else
                        log.setDebug("Command " + command_name + " sent successfully");
                }
            });
        }
    }

    private void initListeners(){
        aircraft.getBattery().setStateCallback(new BatteryState.Callback() {
            @Override
            public void onUpdate(BatteryState batteryState) {
                bat_status.setText(Integer.toString(batteryState.getLifetimeRemaining()));
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
            flightcontroldata = new FlightControlData(0, 0, 0, 0);
            aircraft = FPVDemoApplication.getAircraftInstance();
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
            this.enableVirtualStick();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


}
