package com.arhiser.increment.network.api;

import android.os.Build;

import com.arhiser.increment.BuildConfig;
import com.arhiser.increment.network.json.GsonCustomFactory;
import com.squareup.okhttp.OkHttpClient;

import java.security.cert.CertificateException;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.android.MainThreadExecutor;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;


public class SalonsService {

    public static final String TAG = SalonsService.class.getSimpleName();
    private static final String ENDPOINT_STAGE = "https://api-stage.moiprofi.ru";
    private static final String ENDPOINT_PRODUCTION = "https://api.moiprofi.ru";

    public static String getEndpoint() {
        return ENDPOINT_PRODUCTION;
    }

    public static SalonsAPI getApiImpl() {
        String endpoint = getEndpoint();

        OkHttpClient client = getUnsafeOkHttpClient();

        /*
        int cacheSize = 32 * 1024 * 1024; // 32 MiB
        File cacheDirectory = new File(SalonApp.getApplication().getCacheDir().getAbsolutePath(), "HttpCache");
        Cache cache = new Cache(cacheDirectory, cacheSize);
        */
        client.setCache(null);

        RestAdapter adapter = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(endpoint)
                .setExecutors(Executors.newFixedThreadPool(6), new MainThreadExecutor())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setLog(new AndroidLog(TAG))
                .setConverter(new GsonConverter(GsonCustomFactory.getCustomGson()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request)
                    {
                        request.addQueryParam("slot_variant", "15m");
                        request.addHeader("X-MOIPROFI-APP-VERSION", BuildConfig.VERSION_NAME);
                        request.addHeader("X-MOIPROFI-APP-PLATFORM", "android/" + Build.VERSION.RELEASE);
                    }
                })
                .build();

        return adapter.create(SalonsAPI.class);
    }

    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;//new java.security.cert.X509Certificate[0];
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, null);
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
