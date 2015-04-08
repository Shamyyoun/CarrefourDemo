package json;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import utils.StringUtil;

public class JsonReader {
    private String url;

    public JsonReader(String url) {
        this.url = url;
    }

    /**
     * method used to send async get request
     */
    public void sendAsyncGetRequest() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sendGetRequest();
                return null;
            }
        }.execute();
    }

    /**
     * method used to send async get request with parameters
     */
    public void sendAsyncGetRequest(final List<NameValuePair> paramaters) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sendGetRequest(paramaters);
                return null;
            }
        }.execute();
    }

    /**
     * method used to send async get request
     */
    public void sendAsyncPostRequest() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sendPostRequest();
                return null;
            }
        }.execute();
    }

    /**
     * method used to send async get request with parameters
     */
    public void sendAsyncPostRequest(final List<NameValuePair> parameters) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sendPostRequest(parameters);
                return null;
            }
        }.execute();
    }

    /**
     * method used to send get request
     */
    public String sendGetRequest() {
        return sendGetRequest(null);
    }

    /**
     * method used to send get request with parameters
     */
    public String sendGetRequest(List<NameValuePair> parameters) {
        String response = null;
        try {
            // check if found parameters
            if (parameters != null) {
                // concatenate parameters to url
                for (int i = 0; i < parameters.size(); i++) {
                    if (!this.url.contains("?")) {
                        this.url += "?";
                    }

                    this.url += (parameters.get(i).getName() + "=" + parameters.get(i).getValue() + "&");
                }
            }

            // create HTTPURLConnection
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            // set connection properties
            connection.setReadTimeout(15 * 1000);
            connection.setConnectTimeout(15 * 1000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            // then connect
            connection.connect();

            // get response from connection
            InputStream is = connection.getInputStream();
            response = StringUtil.convertStreamToString(is);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * method used to send post request
     */
    public String sendPostRequest() {
        return sendPostRequest(null);
    }

    /**
     * method used to send post request with parameters
     */
    public String sendPostRequest(List<NameValuePair> parameters) {
        String response = null;
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            // check if found parameters
            if (parameters != null) {
                // Add parameters
                httppost.setEntity(new UrlEncodedFormEntity(parameters));
            }

            // Execute HTTP Post Request
            HttpResponse httpResponse = httpclient.execute(httppost);

            // return by response
            response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

        } catch (Exception e) {
            response = null;
            e.printStackTrace();
        }

        return response;
    }
}
