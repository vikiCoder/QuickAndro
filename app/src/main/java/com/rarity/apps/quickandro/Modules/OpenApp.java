package com.rarity.apps.quickandro.Modules;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;

import com.rarity.apps.quickandro.R;

import java.util.ArrayList;
import java.util.List;

public class OpenApp {

    private ArrayList<AppInfo> res;
    private Context context;

    public OpenApp(Context context) {
        this.context = context;

        res = new ArrayList<AppInfo>();
        final List<PackageInfo> apps = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < apps.size(); i++) {
            PackageInfo p = apps.get(i);
            AppInfo newInfo = new AppInfo(p.applicationInfo.loadLabel(context.getPackageManager()).toString(), p.packageName);
            res.add(newInfo);
        }
    }

    public String openApp(String name) {

        if(name.equalsIgnoreCase("quickandro")){
            return "quickandro " + context.getString(R.string.already_open);
        }

        for(int i=0; i<res.size(); i++){
            if(res.get(i).appName.compareToIgnoreCase(name) == 0){
                String packageName = res.get(i).packageName;
                Intent launchApp = context.getPackageManager().getLaunchIntentForPackage(packageName);
                launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchApp);
                return context.getString(R.string.opening) + name;
            }
        }
        return name + context.getString(R.string.not_installed);
    }

    private class AppInfo {
        String appName;
        String packageName;

        AppInfo(String appName, String packageName){
            this.appName = appName;
            this.packageName = packageName;
        }
    }
}
