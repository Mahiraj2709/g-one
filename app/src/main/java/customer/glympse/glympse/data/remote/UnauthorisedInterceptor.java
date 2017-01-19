package customer.glympse.glympse.data.remote;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class UnauthorisedInterceptor implements Interceptor {
    Context mContent = null;
    public UnauthorisedInterceptor(Context mContext) {
        this.mContent = mContext;
    }
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if (response.code() == 401) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
            return response;
        }
}
