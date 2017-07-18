package com.rarity.apps.quickandro.Modules;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;

import java.util.HashMap;
import java.util.List;

public class OpenApp {

    private HashMap<String, String> res;
    private Context context;

    public OpenApp(Context context) {
        this.context = context;

        res = new HashMap<String, String>();
        final List<PackageInfo> apps = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < apps.size(); i++) {
            PackageInfo p = apps.get(i);
            res.put(p.applicationInfo.loadLabel(context.getPackageManager()).toString().toLowerCase(), p.packageName);
        }
    }

    public String openApp(String name) {
        name = name.toLowerCase();

        if(name.equals("quickandro")){
            return "quickandro is already open";
        }

        if(res.containsKey(name)){
            Intent launchApp = context.getPackageManager().getLaunchIntentForPackage(res.get(name));
            launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchApp);
            return "Opening " + name;
        }

        return name + " is not installed in your phone.";
    }
}
