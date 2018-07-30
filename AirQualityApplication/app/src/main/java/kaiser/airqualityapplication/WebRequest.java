package kaiser.airqualityapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Log;
import eewinter.struct.BLEBeaconDTO;

public class WebRequest {

    private static final int HTTP_STATUS_OK = 200;

    public static String commBeaconInfo( BLEBeaconDTO obj )
    {
        HttpPost request = makeHttpPost(Settings.Global.HTTP_ADDRESS + "/setScanResult.jsp", obj);
        return makeHttpConnection(request);
    }

    /**
     * @함수명 : makeHttpPost
     * @매개변수 :
     * @반환 : HttpPost
     * @기능 : option1: request to server, option2: reply from server
     * @작성자 : THYang
     * @작성일 : 2016. 6. 28.
     */
    private static HttpPost makeHttpPost( String url, BLEBeaconDTO obj )
    {
        System.out.println("URL:" + url);
        HttpPost request = new HttpPost(url);

        Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
        nameValue.add(new BasicNameValuePair("id", obj.getId()));
        nameValue.add(new BasicNameValuePair("major", obj.getMajor() + ""));
        nameValue.add(new BasicNameValuePair("minor", obj.getMinor() + ""));
        nameValue.add(new BasicNameValuePair("rssi", obj.getRssi() + ""));
        nameValue.add(new BasicNameValuePair("option", obj.getOption() + ""));
        nameValue.add(new BasicNameValuePair("msg_time", obj.getMsg_time() + ""));

        request.setEntity(makeEntity(nameValue));
        return request;
    }

    private static String makeHttpConnection( HttpPost request )
    {
        String result = null;

        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            response = client.execute(request);

            // detection true, as response message
            StatusLine status = response.getStatusLine();
            if ( status.getStatusCode() != HTTP_STATUS_OK )
            {
                result = "Invalid response from server : " + status.toString();
                return result;
            }

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            // Entity change for string
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ( (line = reader.readLine()) != null )
            {
                sb.append(line).append("\n");
            }
            is.close();
            result = sb.toString();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    private static HttpEntity makeEntity( Vector<NameValuePair> nameValue )
    {
        HttpEntity result = null;
        try
        {
            result = new UrlEncodedFormEntity(nameValue, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static Bitmap loadBitmap( URL urls )
    {
        try
        {
            HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            conn.setDoInput(true);

            conn.connect();

            InputStream is = conn.getInputStream();

            return BitmapFactory.decodeStream(is);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("ERR", "1" + e.getMessage());
            Log.e("ERR", "\n");
        }
        return null;
    }
}

