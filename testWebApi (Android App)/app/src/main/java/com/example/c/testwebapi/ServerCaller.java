package com.example.c.testwebapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

								   //   <Params, Progress, Result>
public class ServerCaller extends AsyncTask<String, Void, String>{
    public OnOprationDoing onOprationDoing;

    public ServerCaller(OnOprationDoing ponOprationDoing)
    {
        onOprationDoing = ponOprationDoing;
    }

    protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException
    {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0)
        {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }

	public static Context context;
	public static boolean isConnected()
    {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
	
	
    @Override
    protected void onPreExecute() {
        System.out.println("----------------------------onPreExecute");
        if(!isConnected()){
			if(onOprationDoing != null){
				onOprationDoing.OnFail(0,"no internet connection");
				onOprationDoing.afterFinish();
				
				cancel(true); // if not connected cancel the task
			}
        }
    }
	
	
    @Override
    protected String doInBackground(String... urls)
    {
        System.out.println("----------------------------doInBackground");

        if(onOprationDoing !=null)
            onOprationDoing.beforStart();


        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();

        HttpGet httpGet = new HttpGet(urls[0]);
        try
        {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();

            String results = getASCIIContentFromEntity(entity);

            lastOpriationCode = response.getStatusLine().getStatusCode();

            return results;
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    public int lastOpriationCode=-1;

    @Override
    protected void onPostExecute(String results)
    {
        System.out.println("----------------------------onPostExecute");
        if(onOprationDoing!=null)
        {
            if (lastOpriationCode == 200 || lastOpriationCode == 201)
                onOprationDoing.OnSuccess(results);
            else
            {
                if (lastOpriationCode == -1)
                    onOprationDoing.OnFail(408, "request timeout");
                else
                {
                    if (!results.isEmpty() && results.charAt(0) == '\"' && results.charAt(results.length() - 1) == '\"')
                        onOprationDoing.OnFail(lastOpriationCode, results.substring(1, results.length() - 1));
                    else
                        onOprationDoing.OnFail(lastOpriationCode, results);
                }
            }
            onOprationDoing.afterFinish();
        }
    }
}