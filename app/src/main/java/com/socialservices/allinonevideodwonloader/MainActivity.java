package com.socialservices.allinonevideodwonloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.socialservices.allinonevideodwonloader.ads.Nelsoft_AdmobBannerAds;
import com.socialservices.allinonevideodwonloader.ads.Nelsoft_UtilsMAIN;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import io.reactivex.observers.DisposableObserver;

import static com.socialservices.allinonevideodwonloader.ExtraMit.ischeckInternetConenction;

public class MainActivity extends AppCompatActivity {

    TextView mTxtHowtoUse;
    EditText edtLink;
    SwitchCompat switchCompat;
    ImageView txtDownload;
    TextView mTxtPaste;
    Random random = new Random();
    LinearLayout linInsta, linFb, linTwitter;
    public Intent mainService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        proceedHome();
        ExtraMit.checkAndRequestPermissions(MainActivity.this);


        if(Nelsoft_UtilsMAIN.isOnline(MainActivity.this)){
            LinearLayout linearLayoust = findViewById(R.id.ad_view);
            Nelsoft_AdmobBannerAds.lordAdmobBannerAds(getApplicationContext(), linearLayoust, AdSize.BANNER, "GBanner");
        }
    }

    private void proceedHome() {
        init();
        setClick();
    }

    private boolean checkApp(String packagename) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void setClick() {

        txtDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if no view has focus:
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String link = edtLink.getText().toString().trim();
                if (ischeckInternetConenction(MainActivity.this)) {
                    if (!link.equals("")) {
                        if (link.contains("http")) {
                            edtLink.getText().clear();
                            //  ExtraMit.countVideoDownload++;
                            new GetUrl().execute(link);

                        } else {
                            edtLink.setError("This link is not available for download");
                        }
                    } else {
                        edtLink.setError("Please enter the link");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Internet is not working, Make sure that you have active internet connection", Toast.LENGTH_LONG).show();
                }

            }
        });


//        mTxtHowtoUse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i=new Intent(MainActivity.this, HowToCopyPaste.class);
//                startActivity(i);
//                overridePendingTransition(R.anim.left_in, R.anim.left_out);
//            }
//        });
        mTxtPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pasteLink();
            }
        });

    }

    public void pasteLink() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteLink = "";

        // If it does contain data, decide if you can handle the data.
        if ((clipboard.hasPrimaryClip())) {
            //since the clipboard contains plain text.
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            // Gets the clipboard as text.
            if (item != null) {
                pasteLink = item.getText().toString();
            }

            edtLink.setText(pasteLink);
        }
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mTxtHowtoUse =(TextView)findViewById(R.id.txt_how_to_use);
        edtLink = findViewById(R.id.edtLink);
        txtDownload = findViewById(R.id.txt_download);
        switchCompat = (SwitchCompat) findViewById(R.id.switchButton);
        mTxtPaste = findViewById(R.id.txt_paste);
        mainService = new Intent(MainActivity.this, ServiceBg.class);

        linInsta = findViewById(R.id.lin_insta);
        linFb = findViewById(R.id.lin_fb);
        linTwitter = findViewById(R.id.lin_twitter);


        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.getBoolean("close")) {
            switchCompat.setChecked(false);

            if (mainService != null) {

                ExtraMit.setbooleanPref(MainActivity.this, false, "service");
                switchCompat.setChecked(false);
                stopService(mainService);
            }
        }

        if (ExtraMit.getBooleanPref(MainActivity.this, "service")) {

            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)//auto download on
                {
                    startService(mainService);
                    ExtraMit.setbooleanPref(MainActivity.this, true, "service");
                  /*  if (ExtraMit.countVideoDownload >=5 || ExtraMit.isAdOpenedAllDownload)
                    {
                                ExtraMit.showFullFacebookAd();
                    }*/
                } else {
                    if (mainService != null) {
                        ExtraMit.setbooleanPref(MainActivity.this, false, "service");
                        stopService(mainService);

                    }
                }
            }
        });

        linInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApps("com.instagram.android");
            }
        });
        linFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApps("com.facebook.katana");
            }
        });
        linTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApps("com.twitter.android");
            }
        });
    }

    public void openApps(String packageName) {
        try {
            Intent i = getPackageManager().getLaunchIntentForPackage(packageName);
            if (i == null) {
                //  startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.whatsapp")));
                ExtraMit.showToast(MainActivity.this, "App Not Installed", 0);
                throw new PackageManager.NameNotFoundException();
            }
            i.addCategory("android.intent.category.LAUNCHER");
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            ExtraMit.showToast(MainActivity.this, "App Not Installed", 0);
        }
    }

    public void onResume() {
        super.onResume();

    }

    private String userName = "file";
    ProgressDialog dialog;

    public class GetUrl extends AsyncTask<String, String, String> {
        private String sourceUrl, urldummy;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Checking link, please wait.");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            urldummy = url;

            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";
            String acceptValue = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";

            try {
                Document doc = Jsoup.connect((String) ExtraMit.extractUrls(url).get(0)).
                        header("Accept", acceptValue).userAgent(userAgent).get();

                if (doc != null) {
                    if (url.contains("facebook")) {
                        long n = random.nextInt(999999999 - 100000000);
                        userName = "Fb_" + String.valueOf(n);

                        Element videoElement = doc.select("meta[property=og:video]").first();
                        if (videoElement != null) {
                            this.sourceUrl = videoElement.attr("content");
                        }
                    } else if (url.contains("instagram")) {
                        userName = getUserName(doc);
                        Element videoElement = doc.select("meta[property=og:video]").first();
                        Element imageElement = doc.select("meta[property=og:image]").first();
                        if (videoElement != null) {
                            this.sourceUrl = videoElement.attr("content");
                        } else {
                            if (imageElement != null) {
                                this.sourceUrl = imageElement.attr("content");
                            }
                        }
                    } else if (url.contains("tiktok")) {
                        Element nameElement = doc.select("meta[name=description]").first();
                        if (nameElement != null) {
                            String user = nameElement.attr("content");
                            String arr[] = user.split("has", 2);
                            userName = arr[0].trim();   //the
                        }
                        try {
                            this.sourceUrl = getTiktokVideo(doc);
                        } catch (Exception e) {
                            ExtraMit.showToast(MainActivity.this, "Download Failed, TikTok Server Having Problem Try Later Or We Will Update You When Problem Solved", 1);
                        }
                    } else if (url.contains("youtu")) {

                    } else if (url.contains("twitter")) {
                        Long tweetId = getTweetId(url);
                        userName = "Twitter" + String.valueOf(tweetId);
                        if (tweetId != null) {
                            userName = "Twitter" + String.valueOf(tweetId);
                            String str2 = "https://twittervideodownloaderpro.com/twittervideodownloadv2/index.php";
                            try {
                                RestClient.callTwitterApi(observer, str2, String.valueOf(tweetId));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Download Not Available, Try Later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (url.contains("go")) {
                        Element nameElement = doc.select("meta[name=twitter:creator]").first();
                        if (nameElement != null) {
                            userName = nameElement.attr("content");
                        }

                        Element ele = doc.select("div[class=player-container]").first();
                        if (ele != null) {
                            this.sourceUrl = ele.attr("data-src");
                        }
                    } else if (url.contains("like")) {
                        long n = random.nextInt(999999999 - 100000000);
                        userName = "LikeeVideo_" + String.valueOf(n);

                        if (doc.toString().contains("video_url")) {
                            String s2 = doc.toString()
                                    .substring(doc.toString().
                                            indexOf("video_url"));
                            String s1 = s2.split(",")[0];
                            s1 = s1.replaceAll("_4.mp4", ".mp4");
                            sourceUrl = s1.replace("video_url", "").substring(3);
                        }
                    } else {
                        long n = random.nextInt(999999999 - 100000000);
                        userName = "Video_" + String.valueOf(n);
                        Element videoElement = doc.select("meta[property=og:video]").first();
                        if (videoElement == null) {
                            videoElement = doc.select("meta[property=og:video:url]").first();
                        }
                        if (doc.toString().contains("media_urls")) {

                            if (doc.toString().contains("props")) {
                                Elements nameElement = doc.select("script");

                                for (int i = 0; i < nameElement.size(); i++) {
                                    String data = (nameElement.get(i)).html().toString();
                                    if (data.contains("__APP_DATA__")) {
                                        try {
                                            JSONObject shortcodeMedia =
                                                    (new JSONObject(data.
                                                            replaceFirst("__APP_DATA__ = ", ""))
                                                            .getJSONObject("props").
                                                                    getJSONObject("videoDetail"));
                                            sourceUrl = shortcodeMedia.getJSONArray("media_urls").
                                                    getJSONObject(0).getString("url");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }

                            } else if (videoElement != null) {
                                this.sourceUrl = videoElement.attr("content");
                            }
                        }
                    }

                }
            } catch (UnknownHostException e) {
            } catch (HttpStatusException e2) {
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            return this.sourceUrl;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            try {

                if (urldummy.contains("like")) {
                    downloadUrl = sourceUrl;
                    new likSaver().execute();
                } else if (urldummy.contains("twitter")) {

                } else {
                    downloadFromUrl(sourceUrl);
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Download Not Available", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public static String getTiktokVideo(Document document) {
        String videourl = "", finalUrl = "";
        String html = document.select("script[id=\"videoObject\"]").last().html();
        String html2 = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
        if (!html.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(html);
                new JSONObject(html2);
                videourl = jSONObject.getString("contentUrl");
                finalUrl = withoutWatermark(videourl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return finalUrl;
    }

    public static String withoutWatermark(String str) {
        String str2 = "";
        String str3 = "vid:";
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            httpURLConnection.getResponseCode();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    stringBuffer.append(readLine);
                    if (stringBuffer.toString().contains(str3)) {
                        try {
                            if (stringBuffer.substring(stringBuffer.indexOf(str3)).substring(0, 4).equals(str3)) {
                                String substring = stringBuffer.substring(stringBuffer.indexOf(str3));
                                String trim = substring.substring(4, substring.indexOf("%")).replaceAll("[^A-Za-z0-9]", str2).trim();
                                StringBuilder sb = new StringBuilder();
                                sb.append("http://api2.musical.ly/aweme/v1/playwm/?video_id=");
                                sb.append(trim);
                                return sb.toString();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //  ExtraMit.showToast(context, "Download Failed, TikTok Server Having Problem Try Later Or We Will Update You When Problem Solved", 1);
                        }
                    } else {
                        continue;
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            return str2;
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
                        userName = shortcodeMedia.getJSONObject("owner").get("username").toString();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return userName;
    }


    private DisposableObserver<TwitterResponse> observer = new DisposableObserver<TwitterResponse>() {
        public void onNext(TwitterResponse twitterResponse) {
            String str = "image", VideoUrl;
            try {
                VideoUrl = (twitterResponse.getVideos().get(0)).getUrl();
                String str2 = "";
                if ((twitterResponse.getVideos().get(0)).getType().equals(str)) {
                    //     Log.e("downloadFromUrl1:",VideoUrl);
                    downloadFromUrl(VideoUrl);
                    return;
                }
                VideoUrl = (twitterResponse.getVideos().get(twitterResponse.getVideos().size() - 1)).getUrl();
                //  Log.e("downloadFromUrl2:",VideoUrl);
                downloadFromUrl(VideoUrl);

            } catch (Exception e) {
                e.printStackTrace();
                ExtraMit.showToast(MainActivity.this, "No Media on Tweet or Invalid Link", 1);
            }
        }

        public void onError(Throwable th) {
            th.printStackTrace();
            ExtraMit.showToast(MainActivity.this, "No Media on Tweet or Invalid Link", 1);
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

    public void downloadFromUrl(String url) throws IOException {
        Toast.makeText(MainActivity.this, "Download will start above in Notification Bar", Toast.LENGTH_LONG).show();
        //  Log.e("downloadFromUrl:",url);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        if (Build.VERSION.SDK_INT > 13) {
            request.setNotificationVisibility(1);
        } else {
            request.setNotificationVisibility(0);
        }
        String fileName = userName;
        request.setTitle(fileName);


        if (url.contains(".jpg")) {
            fileName = userName + ".jpg";
            request.setTitle(userName + "'s photo");
            request.setDestinationInExternalPublicDir(getString(R.string.app_name) + " Images", fileName);
            request.setDescription(getString(R.string.app_name) + " Images" + "/" + fileName);
        } else {
            fileName = userName + ".mp4";
            request.setTitle(userName + "'s video");
            request.setDestinationInExternalPublicDir(getString(R.string.app_name), fileName);
            request.setDescription(getString(R.string.app_name) + "/" + fileName);
        }


        request.allowScanningByMediaScanner();
        downloadManager.enqueue(request);
    }

    String downloadUrl = "";

    private class likSaver extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog pDialog;
        File file;

        private likSaver() {

        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog = new ProgressDialog(MainActivity.this);
            this.pDialog.setMessage("Saving the video to the folder " + getString(R.string.app_name));
            this.pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.pDialog.setCanceledOnTouchOutside(false);
            this.pDialog.setMax(100);
            this.pDialog.show();
        }

        protected Boolean doInBackground(String... args) {
            String fileName = userName + ".mp4";
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {

                File mFolder = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));
                file = new File(mFolder.getAbsolutePath() + "/" + fileName);
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                file = new File(MainActivity.this.getFilesDir(), getString(R.string.app_name) + "/" + fileName);

                if (!file.exists()) {
                    file.mkdirs();
                }
            }

            //  downloadUrl=ExtraMit.extractUrls(downloadUrl).get(0);
            //    downloadUrl="https://video.like.video//in_live//2nz//0FF5wB.mp4?crc=2500821549&type=5%22";
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
                connection.setRequestMethod("GET");
                //connection.setRequestProperty("Content-Type","application/json; charset=utf-8");
                // connection.setRequestProperty("Accept","application/json; charset=utf-8");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();
                // getting file length

                int lenghtOfFile = connection.getContentLength();
                FileOutputStream out = new FileOutputStream(file);
                InputStream is = (InputStream) new URL(downloadUrl).getContent();
                byte[] buffer = new byte[1024];
                long total = 0;
                while (true) {
                    int len1 = is.read(buffer);
                    total += len1;
                    publishProgress((int) ((total * 100) / lenghtOfFile));
                    if (len1 == -1) {
                        break;
                    }
                    out.write(buffer, 0, len1);
                }
                out.close();
                is.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                //   ExtraMit.showToast(MainActivity.this, "Error, Try Again After Sometime", 1);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(values[0]);
        }

        protected void onPostExecute(Boolean isDone) {

            if (pDialog != null && pDialog.isShowing()) {
                this.pDialog.dismiss();
            }
            if (isDone) {
                try {
                    Toast.makeText(MainActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        MediaScannerConnection.scanFile(MainActivity.this, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            /*
                             *   (non-Javadoc)
                             * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                             */
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                    } else {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error, Try Again After Sometime", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Error, Try Again After Sometime", Toast.LENGTH_SHORT).show();
            }


        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
//            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } catch (Exception e) {
        }
    }


}
