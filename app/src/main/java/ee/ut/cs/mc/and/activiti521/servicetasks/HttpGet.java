package ee.ut.cs.mc.and.activiti521.servicetasks;

import android.os.AsyncTask;
import android.util.Log;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jakob on 16.06.2016.
 */
public class HttpGet implements JavaDelegate {

    private Expression URL; // this should be set via Field Injection in the process instance
    private static final String TAG = HttpGet.class.getName();
    OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }



    @Override
    public void execute(DelegateExecution execution) {
        try {
            Log.i(TAG, "Making request..");
            Log.i(TAG, run((String) URL.getValue(execution)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setURL(Expression URL) {
        this.URL = URL;
    }
}