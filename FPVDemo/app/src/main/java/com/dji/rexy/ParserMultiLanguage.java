package com.dji.rexy;

public class ParserMultiLanguage implements Parser{

    @Override
    public int parseCommandEnglish(String command) {
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
        if (list_contain(parts_of_command,"go") || list_contain(parts_of_command, "forward")){
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
        if (list_contain(parts_of_command,"down")){
            // Fully understood, the user commands a right turn
            return 9;
        }

        // Stop section
        if (list_contain(parts_of_command,"stop")){
            // Fully understood, the user commands a right turn
            return 10;
        }

        // speed section
        if (list_contain(parts_of_command,"speed") || list_contain(parts_of_command,"spid")){
            return 11;
        }

        // slow section
        if (list_contain(parts_of_command,"slow") || list_contain(parts_of_command,"snow")){
            return 12;
        }

        // ask for clarification if the command isn't clear.
        return -1;

    }

    @Override
    public int parseCommandHebrew(String command) {
        String[] parts_of_command = command.split(" ");

        // TakeOff section
        if (list_contain(parts_of_command, "המראה") || list_contain(parts_of_command, "תמריא")){
            // Fully understood, user commands a Takeoff
            return 0;
        }

        // Land section
        if (list_contain(parts_of_command, "נחיתה") || list_contain(parts_of_command, "תנחת")){
            // Fully understood, user commands a Land
            return 1;
        }

        // Forward section
        if (list_contain(parts_of_command,"קדימה") || list_contain(parts_of_command, "ישר")){
            // Fully understood, the user commands moving Forward
            return 2;
        }

        // Backward section
        if (parts_of_command[0].equalsIgnoreCase("אחורה")){
            // Fully understood, the user commands moving Backward
            return 3;
        }

        // Left turn section
        if (list_contain(parts_of_command, "שמאלה") || list_contain(parts_of_command, "שמאל")){
            // Fully understood, the user commands a left turn
            return 4;
        }

        // Right turn section
        if (list_contain(parts_of_command,"ימינה") || list_contain(parts_of_command,"ימין")){
            // Fully understood, the user commands a right turn
            return 5;
        }

        // Yaw turn section
        if (list_contain(parts_of_command,"תסתובב") || list_contain(parts_of_command,"סתובב") || list_contain(parts_of_command,"סיבוב")){
            if (list_contain(parts_of_command,"שמאלה")){
                // Fully understood, the user commands a Yaw left turn
                return 6;
            }
            else if (list_contain(parts_of_command,"ימינה")){
                // Fully understood, the user commands a Yaw right turn
                return 7;
            }
            else{
                // command isn't clear, asking the user for clarifications
                return -1;
            }
        }

        // Up section
        if (list_contain(parts_of_command,"למעלה") || list_contain(parts_of_command, "תעלה")){
            // Fully understood, the user commands a right turn
            return 8;
        }

        // Down section
        if (list_contain(parts_of_command,"למטה") || list_contain(parts_of_command,"תרד")){
            // Fully understood, the user commands a right turn
            return 9;
        }

        // Stop section
        if (list_contain(parts_of_command,"תעצור") || list_contain(parts_of_command,"עצור")){
            // Fully understood, the user commands a right turn
            return 10;
        }

        // speed section
        if (list_contain(parts_of_command,"תמהר") || list_contain(parts_of_command,"מהר")){
            return 11;
        }

        // slow section
        if (list_contain(parts_of_command,"תאט") || list_contain(parts_of_command,"לאט")){
            return 12;
        }

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
