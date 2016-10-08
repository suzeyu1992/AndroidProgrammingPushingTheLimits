package com.szysky.note.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;


/**
 * 测试网络相关的代码
 */
public class HTTPGet extends IntentService {

    private static final String TAG = HTTPGet.class.getSimpleName();

    public HTTPGet() {
        super("HTTPGet");
    }



    @Override
    protected void onHandleIntent(Intent intent) {

    }


    /**
     *  练习利用HTTPUrlConnection连接get请求
     */
    public JSONObject getJsonFromServer(URL url, long lastModifiedTimestamp){

        try {
            // 开始构建请求头
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("GET");
            uc.setInstanceFollowRedirects(true);
            uc.setIfModifiedSince(lastModifiedTimestamp == 0 ? new Date().getTime() : lastModifiedTimestamp);
            uc.setUseCaches(true);
            uc.connect();

            // 判断响应结果
            if (uc.getResponseCode() == HttpURLConnection.HTTP_OK){
                if (uc.getContentType().contains("application/json")){

                    // 获取响应体的内容长度
                    int contentLength = uc.getContentLength();
                    InputStream in = uc.getInputStream();

                    String jsonStr = readStreamToString(in , contentLength);
                    return new JSONObject(jsonStr);
                }
            }else{
                 // TODO: 16/10/8 网络请求错误处理
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String readStreamToString(InputStream in, int contentLength) throws IOException {
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder(contentLength);

            char[] buffer = new char[contentLength];
            int charsRead;


            while((charsRead = read.read(buffer)) != -1){
                sb.append(buffer, 0, charsRead);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            in.close();
            return "";
        }
    }


}
