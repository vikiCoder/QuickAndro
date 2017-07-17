package com.rarity.apps.quickandro.Modules;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;

public class Temp_SpeechToText {

    private Activity activity;

    public Temp_SpeechToText(Activity activity){
        this.activity = activity;
    }

    public void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        activity.startActivityForResult(i, 1010);
    }


}
