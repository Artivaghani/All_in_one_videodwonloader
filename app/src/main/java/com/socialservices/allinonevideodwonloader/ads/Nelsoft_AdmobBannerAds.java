package com.socialservices.allinonevideodwonloader.ads;

import android.content.Context;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class Nelsoft_AdmobBannerAds {

    public static void lordAdmobBannerAds(Context activity, ViewGroup layout, AdSize adSize, String sdd) {
        try {
            if (Nelsoft_UtilsMAIN.isOnline(activity.getApplicationContext())) {

                AdView adView = new AdView(activity);
                adView.setAdSize(adSize);
                adView.setAdUnitId("ca-app-pub-7484057413460570/9567530268");
                adView.setAdListener(new lordAds(activity, layout, adView));
                adView.loadAd(new AdRequest.Builder().build());
            }
        } catch (Exception e) {

        }
    }

    private static class lordAds extends AdListener {

        ViewGroup viewGroup;
        AdView adView;
        Context context;

        public lordAds(Context activity, ViewGroup layout, AdView adView) {
            this.context = activity;
            this.viewGroup = layout;
            this.adView = adView;
        }


        public void onAdLoaded() {
            super.onAdLoaded();
            try {
                if (this.viewGroup.getChildCount() == 0) {
                    this.viewGroup.addView(this.adView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
