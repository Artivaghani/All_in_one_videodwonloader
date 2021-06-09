package com.socialservices.allinonevideodwonloader;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import android.widget.Toast;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

import io.reactivex.observers.DisposableObserver;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ServiceBg extends Service {

    Random random = new Random();
    String msourceUrl, mUserName;
    int enabled;
    ClipboardManager mClipBoard;
    CopyListener mCopyListener=new CopyListener();
    @Override
    public void onCreate() {
        super.onCreate();
      //  Log.e("ServiceBg","onCreate");
        showNotifiOn();
        mClipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipBoard.addPrimaryClipChangedListener(mCopyListener);
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class CopyListener implements ClipboardManager.OnPrimaryClipChangedListener {

        class downloadURL implements Runnable {
            ClipData clipData;

            downloadURL(ClipData clipData) {
                this.clipData = clipData;
            }

            @Override
            public void run() {
                String socialUrl = this.clipData.getItemAt(0).getText().toString();
                if ( socialUrl.contains("http")) {
                    try {
                        String url = parseWebSite(socialUrl);
                        enabled = getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
                        if (enabled == 1 || enabled == 0) {
                          //  Log.e("ServiceBg","enabled"+enabled);
                            if (!socialUrl.contains("twitter"))
                            {
                                downloadFromUrl(url);
                            }
                        } else {
                            showErrorToast();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showErrorToast();
                    }
                }
            }
        }

        @Override
        public void onPrimaryClipChanged() {
            ClipData clipData = ServiceBg.this.mClipBoard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0 && clipData.getItemAt(0).getText() != null && mClipBoard.hasPrimaryClip()) {
                ClipData clip = mClipBoard.getPrimaryClip();
                if (clip.getDescription().hasMimeType("text/plain") && clip != null) {
                    new Thread(new downloadURL(mClipBoard.getPrimaryClip())).start();
                }
            }
        }

    }

    public String parseWebSite(String url) {
        try {
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";
            String acceptValue = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";

            Document doc= Jsoup.connect((String) ExtraMit.extractUrls(url).get(0)).header("Accept",acceptValue).userAgent(userAgent).get();

            if (doc!=null) {
                if (url.contains("facebook")) {
                    long n = random.nextInt(999999999 - 100000000);
                    mUserName = "Fb_"+String.valueOf(n);

                    Element videoElement = doc.select("meta[property=og:video]").first();
                    if (videoElement != null) {
                        this.msourceUrl = videoElement.attr("content");
                    }
                }
                else if (url.contains("instagram"))
                {
                    mUserName = getUserName(doc);
                    Element videoElement = doc.select("meta[property=og:video]").first();
                    Element imageElement = doc.select("meta[property=og:image]").first();
                    if (videoElement != null) {
                        this.msourceUrl = videoElement.attr("content");
                    }else
                    {
                        if (imageElement != null) {
                            this.msourceUrl = imageElement.attr("content");
                        }
                    }

                }
                else if (url.contains("tiktok"))
                {
                    Element nameElement = doc.select("meta[name=description]").first();
                    if (nameElement != null) {
                        String user = nameElement.attr("content");
                        String arr[] = user.split("has", 2);
                        mUserName = arr[0].trim();   //the
                    }
                    try {
                        this.msourceUrl = MainActivity.getTiktokVideo(doc);
                    }catch (Exception e)
                    {
                        showErrorToast();
                    }
                }
                else if(url.contains("twitter"))
                {
                    Long tweetId = getTweetId(url);
                    mUserName = "Twitter"+String.valueOf(tweetId);
                    if (tweetId != null) {
                        mUserName = "Twitter"+String.valueOf(tweetId);
                        String str2 = "https://twittervideodownloaderpro.com/twittervideodownloadv2/index.php";
                        try {

                            RestClient.callTwitterApi(observer, str2, String.valueOf(tweetId));

                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorToast();
                        }
                    }
                }
                else if (url.contains("youtu"))
                {

                }
                else if(url.contains("go"))
                {
                    Element nameElement = doc.select("meta[name=twitter:creator]").first();
                    if (nameElement != null) {
                        mUserName = nameElement.attr("content");
                    }

                    Element ele = doc.select("div[class=player-container]").first();
                    if (ele != null) {
                        this.msourceUrl = ele.attr("data-src");
                    }
                }
                else if(url.contains("like"))
                {
                }
                else
                {
                    long n = random.nextInt(999999999 - 100000000);
                    mUserName = "Video_"+String.valueOf(n);
                    Element videoElement = doc.select("meta[property=og:video]").first();
                    if (videoElement==null)
                    {
                        videoElement = doc.select("meta[property=og:video:url]").first();
                    }
                    if (doc.toString().contains("media_urls")) {

                        if (doc.toString().contains("props")) {
                            Elements nameElement = doc.select("script");

                            for (int i = 0; i < nameElement.size(); i++) {
                                String data = ( nameElement.get(i)).html().toString();
                                if (data.contains("__APP_DATA__")) {
                                    try {
                                        JSONObject shortcodeMedia =
                                                ( new JSONObject(data.
                                                        replaceFirst("__APP_DATA__ = ", ""))
                                                        .getJSONObject("props").
                                                                getJSONObject("videoDetail"));
                                        msourceUrl = shortcodeMedia.getJSONArray("media_urls").
                                                getJSONObject(0).getString("url");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                        }
                        else if (videoElement != null) {
                            this.msourceUrl = videoElement.attr("content");
                        }
                    }
                }

            }

        } catch (UnknownHostException e) {
        } catch (HttpStatusException e2) {
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return this.msourceUrl;
    }


    private DisposableObserver<TwitterResponse> observer = new DisposableObserver<TwitterResponse>() {
        public void onNext(TwitterResponse twitterResponse) {
            String str = "image",VideoUrl;
            try {
                VideoUrl = ( twitterResponse.getVideos().get(0)).getUrl();
                String str2 = "";
                if (( twitterResponse.getVideos().get(0)).getType().equals(str)) {
                  //       Log.e("downloadFromUrl1:",VideoUrl);
                    downloadFromUrl(VideoUrl);
                    return;
                }
                VideoUrl = ( twitterResponse.getVideos().get(twitterResponse.getVideos().size() - 1)).getUrl();
                //  Log.e("downloadFromUrl2:",VideoUrl);
                downloadFromUrl(VideoUrl);

            } catch (Exception e) {
                e.printStackTrace();
                showErrorToast();
            }
        }

        public void onError(Throwable th) {
            th.printStackTrace();
            showErrorToast();
        }

        public void onComplete() {

        }
    };

    private Long getTweetId(String str) {
        try {
            return Long.valueOf(Long.parseLong(str.split("\\/")[5].split("\\?")[0]));
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("getTweetId: ");
            sb.append(e.getLocalizedMessage());
            //   Log.d("TAG", sb.toString());
            return null;
        }
    }

    public String getUserName(Document doc) {
        Elements nameElement = doc.select("script[type=text/javascript]");
        for (int i = 0; i < nameElement.size(); i++) {
            String data = ((Element) nameElement.get(i)).html().toString();
            if (data.contains("window._sharedData") && data.endsWith(";")) {
                data = data.substring(0, data.length() - 1);
                if (data.startsWith("window._sharedData = ")) {
                    try {
                        JSONObject shortcodeMedia = ((JSONObject) new JSONObject(data.replaceFirst("window._sharedData = ", "")).getJSONObject("entry_data").getJSONArray("PostPage").get(0)).getJSONObject("graphql").getJSONObject("shortcode_media");
                        mUserName = shortcodeMedia.getJSONObject("owner").get("username").toString();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mUserName;
    }


    public void downloadFromUrl(String url) throws IOException {
       // Log.e("downloadFromUrl:",url);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        if (Build.VERSION.SDK_INT > 13) {
            request.setNotificationVisibility(1);
        } else {
            request.setNotificationVisibility(0);
        }
        String fileName = mUserName;
        request.setTitle(fileName);

        if (url.contains(".mp4")) {
            fileName = mUserName + ".mp4";

            request.setDestinationInExternalPublicDir(getString(R.string.app_name), fileName);
            request.setTitle(mUserName + "'s video");
            request.setDescription( getString(R.string.app_name) + "/" + fileName);
        } else if (url.contains(".jpg")) {
            fileName = mUserName + ".jpg";

            request.setDestinationInExternalPublicDir(getString(R.string.app_name)+" Images", fileName);
            request.setTitle(mUserName + "'s photo");
            request.setDescription(getString(R.string.app_name) +" Images"+ "/" + fileName);
        }
        else {
            fileName = mUserName + ".mp4";

            request.setDestinationInExternalPublicDir(getString(R.string.app_name), fileName);
            request.setTitle(mUserName + "'s video");
            request.setDescription( getString(R.string.app_name) + "/" + fileName);
        }


        request.allowScanningByMediaScanner();
        downloadManager.enqueue(request);

    }
    @SuppressLint("WrongConstant")
    public void showNotifiOn() {
        String channelID = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            channelID= startMyNotifi();
        }
        CharSequence tickerTxt = getString(R.string.app_name);
        CharSequence mTickerContent = "Click Copy Link To Download";
        PendingIntent downloadManagerPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent("android.intent.action.VIEW_DOWNLOADS").
                        addFlags(1073741824).
                        addFlags(67108864), 0);

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        mainIntent.setFlags(AccessibilityNodeInfoCompat.ACTION_SET_SELECTION);
        mainIntent.setFlags(603979776);

        Intent closeIntent = new Intent(this, MainActivity.class);
        closeIntent.setFlags(1073741824);
        closeIntent.setFlags(603979776);
        closeIntent.putExtra("close", true);
        closeIntent.setFlags(67108864);

        PendingIntent closePendingIntent = PendingIntent.getActivity(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, channelID);
        Notification notification = notiBuilder.setOngoing(true).setContentTitle(tickerTxt).
                setContentText(mTickerContent).
                setSmallIcon(R.mipmap.ic_launcher).
                addAction(R.mipmap.ic_launcher, "Downloads", downloadManagerPendingIntent).
                addAction(R.mipmap.ic_launcher, "Close", closePendingIntent).
                setPriority(-2).setContentIntent(mainActivityPendingIntent).build();

        notification.flags |= 32;
        startForeground(7524583, notification);
    }

@RequiresApi(Build.VERSION_CODES.O)
    private String startMyNotifi()
    {
        String NOTIFICATION_CHANNEL_ID = "tiktokwatermark.videodownloader";
        String channelName = "Tik Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        return NOTIFICATION_CHANNEL_ID;
    }

    @Override
    public void onDestroy() {
      //  Log.e("ServiceBg","onDestroy");
     //   Process.killProcess(Process.myPid());

        if (mCopyListener!=null)
        {
        mClipBoard.removePrimaryClipChangedListener(mCopyListener);}

        stopSelf();
        stopForeground(true);
        ExtraMit.setbooleanPref(ServiceBg.this,false,"service");
        super.onDestroy();
    }

    class ErrorRun implements Runnable {
        ErrorRun() {
        }

        public void run() {
            Toast.makeText(ServiceBg.this, "dadadad", Toast.LENGTH_SHORT).show();
        }
    }

    public void showErrorToast() {
        new Handler(Looper.getMainLooper()).post(new ErrorRun());
    }
}
