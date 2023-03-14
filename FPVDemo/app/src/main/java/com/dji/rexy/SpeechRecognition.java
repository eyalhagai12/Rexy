package com.dji.rexy;

import android.content.Context;


public class SpeechRecognition {

    private Context context;
    private SpeechModel model;

    public SpeechRecognition(Context new_context){
        this.context = new_context;
        this.model = new Wav2Vec2("wav2vec2_large.ptl", this.context);
    }

    public String recognize(float[] floatInputBuffer)  {
        return this.model.transcribe(floatInputBuffer);
    }

    public int parseCommand(String command){

        String[] parts_of_command = command.split(" ");
        

        // TakeOff section
        if (list_contain(parts_of_command, "takeoff")){
            // Fully understood, user commands a Takeoff
            return 0;
        }
        else if (list_contain(parts_of_command, "take")){
            if (list_contain(parts_of_command, "off")){
                // Partly understood, user commands a Takeoff
                return 0;
            }
            else{
                // The command isn't clear, will ask for a clarification from the user.
                return -1;
            }
        }

        // Land section
        if (list_contain(parts_of_command,"land")){
            return 1;
        }

        // Forward section
        if (list_contain(parts_of_command,"go")){
            // Fully understood, the user commands moving Forward
            return 2;
        }

        // Backward section
        if (parts_of_command[0].equalsIgnoreCase("backward")){
            // Fully understood, the user commands moving Backward
            return 3;
        }
        else if (parts_of_command[0].equalsIgnoreCase("back")){
            if (parts_of_command[1].equalsIgnoreCase("ward")){
                // Partly understood, the user commands moving Backward
                return 3;
            }
        } else if (list_contain(parts_of_command, "back")) {
            return 3;
        }

        // Left turn section
        if (list_contain(parts_of_command, "left")){
            // Fully understood, the user commands a left turn
            return 4;
        }

        // Right turn section
        if (list_contain(parts_of_command,"right")){
            // Fully understood, the user commands a right turn
            return 5;
        } else if (list_contain(parts_of_command, "light")) {
            return 5;
        }

        // Yaw turn section
        if (list_contain(parts_of_command,"spin")){
            if (list_contain(parts_of_command,"left")){
                // Fully understood, the user commands a Yaw left turn
                return 6;
            }
            else if (list_contain(parts_of_command,"right") || list_contain(parts_of_command, "light")){
                // Fully understood, the user commands a Yaw right turn
                return 7;
            }
            else{
                // command isn't clear, asking the user for clarifications
                return -1;
            }
        }

        // Up section
        if (list_contain(parts_of_command,"up") || list_contain(parts_of_command, "increase")
                || list_contain(parts_of_command, "icrease")){
            // Fully understood, the user commands a right turn
            return 8;
        }

        // Down section
        if (parts_of_command[0].equalsIgnoreCase("down")){
            // Fully understood, the user commands a right turn
            return 9;
        }

        // Stop section
        if (parts_of_command[0].equalsIgnoreCase("stop")){
            // Fully understood, the user commands a right turn
            return 10;
        }

        // speed section
        if (parts_of_command[0].equalsIgnoreCase("speed") || parts_of_command[0].equalsIgnoreCase("spid")){
            return 11;
        }

        // slow section
        if (parts_of_command[0].equalsIgnoreCase("slow") || parts_of_command[0].equalsIgnoreCase("snow")){
            return 12;
        }

        // ask for clarification if the command isn't clear.
        return -1;


    }
    
    private boolean list_contain(String[] lst, String target){
        for (String str : lst){
            if (str.equalsIgnoreCase(target)){
                return true;
            }
        }
        return false;
    }


}
