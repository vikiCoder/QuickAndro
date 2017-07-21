package com.rarity.apps.quickandro.Modules;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import com.rarity.apps.quickandro.R;

import java.lang.reflect.Method;

public class Switch {

    private Context context;

    public Switch(Context context){
        this.context = context;
    }

    public String utility(String argument){
        String state="", device="";

        try {
            state = argument.split(" ")[0];
            device = argument.split(" ")[1];
        }
        catch(Exception e){}

        switch (device){
            case "bluetooth":
                if(state.equals(context.getString(R.string.ON)))
                    return onBluetooth();
                else
                    return offBluetooth();
            case "wifi":
            case "wi-fi":
                if(state.equals(context.getString(R.string.ON)))
                    return onWifi();
                else
                    return offWifi();
            case "hotspot":
                if(state.equals(context.getString(R.string.ON)))
                    return onHotspot();
                else
                    return offHotspot();
            case "rotation":
                if(state.equals(context.getString(R.string.ON)))
                    return onRotation();
                else
                    return offRotation();
            case "location":
                if(state.equals(context.getString(R.string.ON)))
                    return onLocation();
                else
                    return offLocation();
            case "flash":
            case "flashlight":
                if(state.equals(context.getString(R.string.ON)))
                    return onFlash();
                else
                    return offFlash();
            default:
                return context.getString(R.string.could_not_catch);
        }
    }

    private String onBluetooth(){
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth.isEnabled()) {
            return context.getString(R.string.bluetooth) + context.getString(R.string.already_on);
        }
        else {
            bluetooth.enable();
            return context.getString(R.string.bluetooth) + context.getString(R.string.turned_on);
        }
    }

    private String offBluetooth(){
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth.isEnabled()) {
            bluetooth.disable();
            return context.getString(R.string.bluetooth) + context.getString(R.string.turned_off);
        }
        else {
            return context.getString(R.string.bluetooth) + context.getString(R.string.already_off);
        }
    }

    private String onWifi(){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifi.isWifiEnabled()){
            return context.getString(R.string.wifi) + context.getString(R.string.already_on);
        }
        else{
            wifi.setWifiEnabled(true);
            return context.getString(R.string.wifi) + context.getString(R.string.turned_on);
        }
    }

    private String offWifi(){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifi.isWifiEnabled()){
            wifi.setWifiEnabled(false);
            return context.getString(R.string.wifi) + context.getString(R.string.turned_off);
        }
        else{
            return context.getString(R.string.wifi) + context.getString(R.string.already_off);
        }
    }

    private String onHotspot(){
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            wifimanager.setWifiEnabled(false);
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, true);
            return context.getString(R.string.hotspot) + context.getString(R.string.started);
        }
        catch (Exception e) {
            return context.getString(R.string.could_not_start_hotspot);
        }
    }

    private String offHotspot(){
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            wifimanager.setWifiEnabled(false);
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, false);
            return context.getString(R.string.hotspot) + context.getString(R.string.stopped);
        }
        catch (Exception e) {
            return context.getString(R.string.could_not_start_hotspot);
        }
    }

    private String onRotation(){
        Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
        return context.getString(R.string.auto_rotation) + context.getString(R.string.is_on);
    }

    private String offRotation(){
        Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
        return context.getString(R.string.auto_rotation) + context.getString(R.string.is_off);
    }

    private String onLocation(){
        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        return context.getString(R.string.need_your_help);
    }

    private String offLocation(){
        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        return context.getString(R.string.need_your_help);
    }

    private static Camera camera = null;
    private String onFlash(){
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                String cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, true);
            }else {
                camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
            }
            return context.getString(R.string.flashLight) + context.getString(R.string.started);
        } catch(Exception e){
            return context.getString(R.string.flashLight) + context.getString(R.string.already_on);
        }
    }

    private String offFlash(){
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                String cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, false);
            }else {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
                camera.release();
            }
            return context.getString(R.string.flashLight) + context.getString(R.string.turned_off);
        } catch (Exception e){
            return context.getString(R.string.flashLight) + context.getString(R.string.already_off);
        }
    }
}
