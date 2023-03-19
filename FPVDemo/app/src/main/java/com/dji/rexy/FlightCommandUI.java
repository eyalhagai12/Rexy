package com.dji.rexy;

import android.widget.TextView;

public class FlightCommandUI {

    private enum states {Floor, Takeoff, Land, Forward, Backward, Yaw_R, Yaw_L,Right, Left,
        Up, Down, Emergency, Hover}

    private states state = states.Floor;
    private LogCustom log;
    private TextView info;
    private FlightCommandsAPI FPVcontrol;
    private float pitch, roll, yaw, throttle;

    public FlightCommandUI(LogCustom init_log, FlightCommandsAPI init_fpv){
        this.log = init_log;
        this.FPVcontrol = init_fpv;
        this.pitch = (float)0.5;
        this.roll = (float)0.2;
        this.yaw = (float)5;
        this.throttle = (float)0.1;
    }

    public void takeoff(){
        if (state == states.Floor) {
            state = states.Takeoff;
            info.setText(new String("Takeoff"));
            log.setMode("Takeoff");
            FPVcontrol.takeoff();
            state = states.Hover;
            info.setText(new String("Hover"));
            log.setMode("Hover");
        }
    }

    public void land(){
        if (state == states.Hover) {
            state = states.Land;
            info.setText(new String("Landing"));
            log.setMode("Land");
            FPVcontrol.land();
            state = states.Floor;
            info.setText(new String("Floor"));
            log.setMode("Floor");
        }
    }

    public void forward(){
        if (state != states.Floor){
            state = states.Forward;
            info.setText(new String("Forward"));
            log.setMode("Forward");
            FPVcontrol.set_pitch(this.pitch, "Forward");
        }
    }

    public void backward(){
        if (state != states.Floor){
            state = states.Backward;
            info.setText(new String("Backward"));
            log.setMode("Backward");
            FPVcontrol.set_pitch(-this.pitch, "Backward");
        }
    }

    public void turn_left(){
        if (state != states.Floor){
            state = states.Left;
            info.setText(new String("Left"));
            log.setMode("Left");
            FPVcontrol.set_roll(-this.roll, "Left");
        }
    }

    public void turn_right(){
        if (state != states.Floor){
            state = states.Right;
            info.setText(new String("Right"));
            log.setMode("Right");
            FPVcontrol.set_roll(this.roll, "Right");
        }
    }

    public void yaw_left(){
        if (state != states.Floor){
            state = states.Yaw_L;
            info.setText(new String("Yaw left"));
            log.setMode("Yaw Left");
            FPVcontrol.set_yaw(-this.yaw, "Yaw Left");
        }
    }

    public void yaw_right(){
        if (state != states.Floor){
            state = states.Yaw_R;
            info.setText(new String("Yaw right"));
            log.setMode("Yaw Right");
            FPVcontrol.set_yaw(this.yaw, "Yaw Right");
        }
    }

    public void up(){
        if (state != states.Floor){
            state = states.Up;
            info.setText(new String("Up"));
            log.setMode("Up");
            FPVcontrol.set_throttle(this.throttle, "Up");
        }
    }

    public void down(){
        if (state != states.Floor){
            state = states.Down;
            info.setText(new String("Down"));
            log.setMode("Down");
            FPVcontrol.set_throttle(-this.throttle, "Down");
        }
    }

    public void stop(){
        state = states.Hover;
        info.setText(new String("Hover"));
        log.setMode("Hover");
        FPVcontrol.stayOnPlace();
    }

    public void speedUp(){
        if (this.pitch < 1.0){
            this.pitch += 0.1;
        }
        if (this.roll < 0.5){
            this.roll += 0.1;
        }
        if (this.yaw < 25){
            this.yaw += 5;
        }
        if (this.throttle < 0.5){
            this.throttle += 0.1;
        }
    }

    public void slowDown(){
        if (this.pitch > 0.3){
            this.pitch -= 0.1;
        }
        if (this.roll > 0.2){
            this.roll -= 0.1;
        }
        if (this.yaw > 5){
            this.yaw -= 5;
        }
        if (this.throttle > 0.1){
            this.throttle -= 0.1;
        }
    }
}
