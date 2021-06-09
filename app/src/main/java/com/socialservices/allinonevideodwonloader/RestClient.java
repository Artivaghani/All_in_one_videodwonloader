package com.socialservices.allinonevideodwonloader;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.json.JSONObject;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/* renamed from: com.cd.statussaver.api.RestClient */
public class RestClient {
    private static final RestClient restClient = new RestClient();
    private static Retrofit retrofit;

    public static RestClient getInstance() {
        return restClient;
    }

    private RestClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(Level.BODY);
        OkHttpClient build = new Builder().readTimeout(2, TimeUnit.MINUTES).connectTimeout(2, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES).addInterceptor(new Interceptor() {
            public final Response intercept(Chain chain) throws IOException {
                return newRestClient(chain);
            }
        }).addInterceptor(httpLoggingInterceptor).build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("https://www.instagram.com/").addConverterFactory(GsonConverterFactory.create(new Gson())).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(build).build();
        }
    }
    public static void callTwitterApi(final DisposableObserver disposableObserver, String str, String str2) {
        RestClient.getInstance().getService().callTwitter(str, str2).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<TwitterResponse>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(TwitterResponse twitterResponse) {
                disposableObserver.onNext(twitterResponse);
            }

            public void onError(Throwable th) {
                disposableObserver.onError(th);
            }

            public void onComplete() {
                disposableObserver.onComplete();
            }
        });
    }

    public /* synthetic */ Response newRestClient(Chain chain) throws IOException {
        Response response = null;
        try {
            response = chain.proceed(chain.request());
            if (response.code() == 200) {
                try {
                    String jSONObject = new JSONObject(response.body().string()).toString();
                    StringBuilder sb = new StringBuilder();
                    sb.append(jSONObject);
                    sb.append("");
                    printMsg(sb.toString());
                    return response.newBuilder().body(ResponseBody.create(response.body().contentType(), jSONObject)).build();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketTimeoutException e2) {
            e2.printStackTrace();
        }
        catch (IOException io)
        {

        }
        return response;
    }

    public APIServices getService() {
        return (APIServices) retrofit.create(APIServices.class);
    }

    private void printMsg(String str) {
        int length = str.length() / 4050;
        int i = 0;
        while (i <= length) {
            int i2 = i + 1;
            int i3 = i2 * 4050;
            String str2 = "Response::";
            if (i3 >= str.length()) {
                Log.d(str2, str.substring(i * 4050));
            } else {
                Log.d(str2, str.substring(i * 4050, i3));
            }
            i = i2;
        }
    }
}
