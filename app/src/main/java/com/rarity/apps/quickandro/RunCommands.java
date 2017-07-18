package com.rarity.apps.quickandro;

import android.content.Context;
import android.widget.Toast;

import com.rarity.apps.quickandro.Modules.Calculator;
import com.rarity.apps.quickandro.Modules.Call;
import com.rarity.apps.quickandro.Modules.Contacts;
import com.rarity.apps.quickandro.Modules.Message;
import com.rarity.apps.quickandro.Modules.OpenApp;
import com.rarity.apps.quickandro.Modules.ProfileManager;
import com.rarity.apps.quickandro.Modules.Search;
import com.rarity.apps.quickandro.Modules.SetAlarm;
import com.rarity.apps.quickandro.Modules.Switch;

public class RunCommands {

    private Context context;
    private MainActivity mainActivity;
    private Contacts contacts;

    public RunCommands(Context context){
        this.context = context;
        this.mainActivity = (MainActivity) context;

        contacts = new Contacts(context);
    }

    public void callModule(String message){
        message = message.toLowerCase();
        String temp[] = message.split(" ", 2);

        if(temp.length < 2){
            mainActivity.tts.speak(context.getString(R.string.Invalid_command));
            mainActivity.updateLayout(context.getString(R.string.Invalid_command));
            return;
        }

        String command = temp[0];     //stores the first word(ie. command)
        String argument = temp[1];

        /*call functions based on command*/
        switch (command){
            case "call":
                commandCall(argument);
                break;
            case "calculate":
                commandCalculate(argument);
                break;
            case "message":
                commandMessage(argument);
                break;
            case "open":
                commandOpen(argument);
                break;
            case "profile":
                commandProfile(argument);
                break;
            case "search":
                commandSearch(argument);
                break;
            case "turn":
                commandTurn(argument);
                break;
            case "alarm":
                commandAlarm(argument);
                Toast.makeText(context, argument, Toast.LENGTH_LONG).show();
                break;
            case "close":
                mainActivity.finish();
                System.exit(0);
                break;
            default:
                mainActivity.tts.speak(context.getString(R.string.Invalid_command));
                mainActivity.updateLayout(context.getString(R.string.Invalid_command));
                break;
        }

//        /*call functions based on command*/
//        if(command.equalsIgnoreCase("call")){
//            commandCall(argument);
//        }
//        else if(command.equalsIgnoreCase("calculate")){
//            commandCalculate(argument);
//        }
//        else if(command.equalsIgnoreCase("message")){
//            commandMessage(argument);
//        }
//        else if(command.equalsIgnoreCase("open")){
//            commandOpen(argument);
//        }
//        else if(command.equalsIgnoreCase("profile")){
//            commandProfile(argument);
//        }
//        else if(command.equalsIgnoreCase("search")){
//            commandSearch(argument);
//        }
//        else if(command.equalsIgnoreCase("turn")){
//            commandTurn(argument);
//        }
//        else if(command.equalsIgnoreCase("alarm")){
//            commandAlarm(argument);
//            Toast.makeText(context, argument, Toast.LENGTH_LONG).show();
//        }
//        else if(command.equalsIgnoreCase("close")){
//            mainActivity.finish();
//            System.exit(0);
//        }
//        else{
//            mainActivity.tts.speak("Invalid command, try again");
//            mainActivity.updateLayout("Invalid command, try again");
//        }
    }












    /*function to call a person*/
    private void commandCall(String argument){
        Call call = new Call(context);
        String reply;

        if(argument.length()==0) {
            reply = context.getString(R.string.try_with_name);
        } else {
            try {
                String temp = argument.replaceAll(" ", "");
                Long.parseLong(temp);
                reply = call.call(temp);
            } catch (NumberFormatException e) {
                if (contacts.findNumber(argument) == null) {
//                    ((OneFragment) f1).setSuggest(contacts.getFinallist());
//                    ((OneFragment) f1).setnumberList(contacts.getnumberlist());

                    if (contacts.getFinallist().size() != 0)
                        reply = context.getString(R.string.multiple_contacts);
                    else {
                        reply = context.getString(R.string.could_not_find_num) + argument;
                    }
                } else
                    reply = call.call(contacts.findNumber(argument));
            }
        }

        if(reply.compareToIgnoreCase(context.getString(R.string.calling)) == 0)
            reply += " " + argument;

        mainActivity.tts.speak(reply);
        mainActivity.updateLayout(reply);
    }

    /*function to calculate a mathematical expression*/
    private void commandCalculate(String argument){
        Calculator calculator = new Calculator(context);

        if(argument.equals(""))
            calculator.calculate();
        else
            mainActivity.updateLayout(calculator.calculate(argument));
    }

    /*function to send a message*/
    private void commandMessage(String argument){
        Message msg = new Message(context);

        Toast.makeText(context, "."+argument+".", Toast.LENGTH_SHORT).show();

        if(argument.equals(""))
            msg.sendMessage();
        else
            msg.sendMessage(argument);
    }

    /*function to open a app*/
    private void commandOpen(String argument){
        OpenApp openApp = new OpenApp(context);
        mainActivity.updateLayout(openApp.openApp(argument));
    }

    /*function to change prpofile*/
    private void commandProfile(String argument){
        ProfileManager profileManager = new ProfileManager(context);
        mainActivity.updateLayout(profileManager.changeProfile(argument));
    }

    /*function for search*/
    private void commandSearch(String argument) {
        Search search = new Search(context);
        if(argument.split(" ")[0].equalsIgnoreCase("wiki"))
            mainActivity.updateLayout(search.wikiSearch(argument.replaceFirst("wiki ", "")));
        else if(argument.split(" ")[0].equalsIgnoreCase("wikipedia"))
            mainActivity.updateLayout( search.wikiSearch(argument.replaceFirst("wikipedia ", "")) );
        else if(argument.split(" ")[0].equalsIgnoreCase("dictionary"))
            mainActivity.updateLayout( search.dictionarySearch(argument.replaceFirst("dictionary ", "")) );
        else if(argument.split(" ")[0].equalsIgnoreCase("youtube"))
            mainActivity.updateLayout( search.youtubeSearch(argument.replaceFirst("youtube ", "")) );
        else
            mainActivity.updateLayout( search.googleSearch(argument) );
    }

    /*function for extra utilities*/
    private void commandTurn(String argument){
        Switch s = new Switch(context);
        mainActivity.updateLayout( s.utility(argument) );
    }

    /*function for alarm*/
    private void commandAlarm(String argument){
        SetAlarm alarm = new SetAlarm(context);
        mainActivity.updateLayout( alarm.setAlarm(argument) );
    }
}
