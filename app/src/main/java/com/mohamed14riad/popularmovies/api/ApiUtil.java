package com.mohamed14riad.popularmovies.api;

import android.support.annotation.NonNull;

import com.mohamed14riad.popularmovies.utils.AppConstants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {
    private static final String BASE_URL = AppConstants.BASE_URL;
    private static final String API_KEY = AppConstants.API_KEY;

    private static int CONNECT_TIMEOUT = 30;

    private static OkHttpClient okHttpClient = null;
    private static Retrofit retrofit = null;

    public static ApiInterface getApiService() {
        return getClient().create(ApiInterface.class);
    }

    private static Retrofit getClient() {
        if (retrofit == null) {
            initRetrofit();
        }

        return retrofit;
    }

    private static void initRetrofit() {
        if (okHttpClient == null) {
            initOkHttp();
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private static void initOkHttp() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor requestInterceptor = requestInterceptor();

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(requestInterceptor)
                .build();
    }

    private static Interceptor requestInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", API_KEY)
                        .build();

                Request request = original.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        };
    }
}
