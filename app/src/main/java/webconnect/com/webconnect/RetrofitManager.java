package webconnect.com.webconnect;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * The type Retrofit util.
 */
public class RetrofitManager {
    private OkHttpClient.Builder mOkHttpClientBuilder = new OkHttpClient.Builder();
    private HttpLoggingInterceptor mInterceptor = new HttpLoggingInterceptor();


    /**
     * Instantiates a new Retrofit util.
     */
    protected RetrofitManager() {
        // Private Constructor
    }

    /**
     * Create service t.
     *
     * @param <T>           the type parameter
     * @param interfaceFile the interface file
     * @param webParam      the web param
     * @return the t
     */
    protected <T> T createService(Class<T> interfaceFile, final WebParam webParam) {
        if (BuildConfig.DEBUG) {
            mInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        mOkHttpClientBuilder.connectTimeout(Configuration.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS);
        mOkHttpClientBuilder.readTimeout(Configuration.getReadTimeoutMillis(), TimeUnit.MILLISECONDS);
        mOkHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (webParam.getHeaderParam() != null && webParam.getHeaderParam().size() > 0) {
                    for (Map.Entry<String, String> entry : webParam.getHeaderParam().entrySet()) {
                        request = request.newBuilder().addHeader(entry.getKey(), entry.getValue()).build();
                    }
                }
                return chain.proceed(request);
            }
        });
        mOkHttpClientBuilder.addInterceptor(mInterceptor);
        String baseUrl = Configuration.getBaseUrl();
        if (!TextUtils.isEmpty(webParam.getBaseUrl())) {
            baseUrl = webParam.getBaseUrl();
        }
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(StringConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(Configuration.getGson()));
        builder.client(mOkHttpClientBuilder.build());
        Retrofit retrofit = builder.build();
        return retrofit.create(interfaceFile);
    }

    /**
     * The type String converter factory.
     */
    private static final class StringConverterFactory extends Converter.Factory {

        /**
         * Create string converter factory.
         *
         * @return the string converter factory
         */
        private static StringConverterFactory create() {
            return new StringConverterFactory();
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return new ConfigurationServiceConverter();
        }

        /**
         * The type Configuration service converter.
         */
        class ConfigurationServiceConverter implements Converter<ResponseBody, String> {

            @Override
            public String convert(ResponseBody value) throws IOException {
                return IOUtils.toString(new InputStreamReader(value.byteStream()));
            }
        }
    }
}
