package com.rarity.apps.quickandro.Modules;

import android.content.Context;
import android.media.AudioManager;

import com.rarity.apps.quickandro.R;

public class ProfileManager {

    private AudioManager audioManager;
    private Context context;

    public ProfileManager(Context context){
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public String changeProfile(String profile){
        switch (profile){
            case "silent":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case "general":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case "vibrate":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            default:
                return context.getString(R.string.Invalid_pro);
        }

        return context.getString(R.string.profile_changed) + profile;
    }
}
