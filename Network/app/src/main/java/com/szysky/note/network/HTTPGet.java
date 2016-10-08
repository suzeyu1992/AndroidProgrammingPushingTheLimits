package com.szysky.note.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;


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
        Log.e(TAG, "service 启动了");
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




    private static final long MAX_FIXED_SIZE = 5 * 1024 * 1024;
    private static final String CRLF = "\r\n";
    /**
     *  使用HTTP POST往服务器发送文件
     */
    public int postFileToURL(File file, String mimeType, URL url) throws IOException {

        DataOutputStream requestData = null;
        try {
            long fileSize = file.length();
            String fileName = file.getName();

            // 创建一个随机边界符字符串
            Random random = new Random();
            byte[] randomBytes = new byte[16];
            random.nextBytes(randomBytes);
            String boundary = Base64.encodeToString(randomBytes, Base64.NO_WRAP);

            // 配置请求设置
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setUseCaches(false);
            uc.setDoOutput(true);           // 设置可以发送数据
            uc.setRequestMethod("POST");

            // 设置HTTP header
            uc.setRequestProperty("Connection", "Keep-Alive");
            uc.setRequestProperty("Cache-Control", "no-cache");
            uc.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            // 如果文件大于max_fixed_size, 使用分块流模式
            if (fileSize > MAX_FIXED_SIZE){
                uc.setChunkedStreamingMode(0);
            }else{
                uc.setFixedLengthStreamingMode((int)fileSize);
            }

            // 打开文件方便读取
            FileInputStream fileIn = new FileInputStream(file);
            // 打开服务器连接
            OutputStream out = uc.getOutputStream();
            requestData = new DataOutputStream(out);

            // 开始写数据
            // 首先写入第一个边界符
            requestData.writeBytes("--" +boundary + CRLF);
            // 让服务器知道文件名
            requestData.writeBytes("Content-Disposition: form-data; name=\""+ fileName + "\"; filename=\""+fileName + CRLF);
            // 文件的MIME类型
            requestData.writeBytes("Content-Type: "+mimeType + CRLF);

            // 循环读取本地文件, 并写入服务器
            int bytesRead;
            byte[] buffer = new byte[8 * 1024];
            while((bytesRead = fileIn.read(buffer)) != -1){
                requestData.write(buffer, 0, bytesRead);
            }

            // 写入边界字符串, 表明已到文件结尾
            requestData.writeBytes(CRLF);
            requestData.writeBytes("--" +boundary +"--"+ CRLF);
            requestData.flush();

            return uc.getResponseCode();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (requestData != null){
                requestData.close();
            }
            return -1;
        }

    }


}
