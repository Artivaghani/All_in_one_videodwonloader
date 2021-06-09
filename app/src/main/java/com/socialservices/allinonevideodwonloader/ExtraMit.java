package com.socialservices.allinonevideodwonloader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtraMit {
    public static boolean ischeckInternetConenction(MainActivity activity) {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)activity.getSystemService(activity.getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(activity, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


    public static void showToast(Activity mainActivity, String app_not_installed, int i) {
        Toast.makeText(mainActivity, app_not_installed, Toast.LENGTH_SHORT).show();
    }

    public static void setbooleanPref(Context mainActivity, boolean b, String service) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(service, b);
        editor.commit();
    }

    public static boolean getBooleanPref(Context mainActivity, String service) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        return prefs.getBoolean(service, true);
    }
    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList();
        Matcher urlMatcher = Pattern.compile("((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", Pattern.CASE_INSENSITIVE ).matcher(text);
        //    Matcher urlMatcher=Pattern.compile("\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=_()|!:,.;]*[-A-Za-z0-9+&@#/%=_()|]").matcher(text);
        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }
        return containedUrls;
    }
    public static boolean checkAndRequestPermissions(Context context) {

        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 2);
            return false;
        }
        return true;
    }
}
