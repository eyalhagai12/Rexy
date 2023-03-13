package com.dji.rexy;

import android.widget.TextView;

public class FlightCommandUI {

    private enum states {Floor, Takeoff, Land, Forward, Backward, Yaw_R, Yaw_L,Right, Left,
        Up, Down, Emergency, Hover}

    private states state = states.Floor;
    private LogCustom log;
    private TextView info;
    private FlightCommandsAPI FPVcontrol;

    public FlightCommandUI(LogCustom init_log, TextView init_info, FlightCommandsAPI init_fpv){
        this.log = init_log;
        this.FPVcontrol = init_fpv;
        this.info = init_info;
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
            FPVcontrol.set_pitch((float) 0.5, "Forward");
        }
    }

    public void backward(){
        if (state != states.Floor){
            state = states.Backward;
            info.setText(new String("Backward"));
            log.setMode("Backward");
            FPVcontrol.set_pitch((float) -0.5, "Backward");
        }
    }

    public void turn_left(){
        if (state != states.Floor){
            state = states.Left;
            info.setText(new String("Left"));
            log.setMode("Left");
            FPVcontrol.set_roll((float) -0.2, "Left");
        }
    }

    public void turn_right(){
        if (state != states.Floor){
            state = states.Right;
            info.setText(new String("Right"));
            log.setMode("Right");
            FPVcontrol.set_roll((float) 0.2, "Right");
        }
    }

    public void yaw_left(){
        if (state != states.Floor){
            state = states.Yaw_L;
            info.setText(new String("Yaw left"));
            log.setMode("Yaw Left");
            FPVcontrol.set_yaw((float) -5, "Yaw Left");
        }
    }

    public void yaw_right(){
        if (state != states.Floor){
            state = states.Yaw_R;
            info.setText(new String("Yaw right"));
            log.setMode("Yaw Right");
            FPVcontrol.set_yaw((float) 5, "Yaw Right");
        }
    }

    public void up(){
        if (state != states.Floor){
            state = states.Up;
            info.setText(new String("Up"));
            log.setMode("Up");
            FPVcontrol.set_throttle((float) 0.1, "Up");
        }
    }

    public void down(){
        if (state != states.Floor){
            state = states.Down;
            info.setText(new String("Down"));
            log.setMode("Down");
            FPVcontrol.set_throttle((float) -0.1, "Down");
        }
    }

    public void stop(){
        state = states.Hover;
        info.setText(new String("Hover"));
        log.setMode("Hover");
        FPVcontrol.stayOnPlace();
    }
}
