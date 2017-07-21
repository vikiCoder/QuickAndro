package com.rarity.apps.quickandro.Modules;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.rarity.apps.quickandro.MainActivity;
import com.rarity.apps.quickandro.R;

import static android.R.attr.phoneNumber;

public class Call extends Activity {

    private Context context;
    private final int MY_REQUEST_CALL_PERMISSION = 0;

    public Call(Context context){
        this.context = context;
    }

    public String call(String phoneNumber) {
        if(!checkPermission()) {
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.CALL_PHONE},MY_REQUEST_CALL_PERMISSION);
            //return context.getString(R.string.not_have_permission) + context.getString(R.string.call);
        }

        try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
           return context.getString(R.string.can_not_make_call);
        }
        return context.getString(R.string.calling);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CALL_PERMISSION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText((Activity)context, "Permission denied to Call", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
}
