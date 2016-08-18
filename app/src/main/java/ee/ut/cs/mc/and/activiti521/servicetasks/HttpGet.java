package ee.ut.cs.mc.and.activiti521.servicetasks;

import android.os.AsyncTask;
import android.util.Log;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jakob on 16.06.2016.
 */
public class HttpGet implements JavaDelegate {

    public static final String URL = "http://jsonplaceholder.typicode.com/users/1";
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
            Log.i(TAG, "Sleeping before making request...");
            Thread.sleep(5000);
            Log.i(TAG, "Making request..");
            Log.i(TAG, run(URL));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class HttpGetTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            String responseString = null;
            try {
                Log.i(TAG, "Sleeping before making request...");
                Thread.sleep(5000);
                Log.i(TAG, "Making request");
                responseString = run(url[0]);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return e.getMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, "Request result = " + result);
        }
    }
}