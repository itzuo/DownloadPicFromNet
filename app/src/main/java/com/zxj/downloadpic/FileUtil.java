package com.zxj.downloadpic;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class FileUtil {
    public final static String pathPath = Environment.getExternalStorageDirectory().getPath() + "/AAA" + File.separator ;

    public static boolean writeResponseBodyToDisk(ResponseBody body, String saveName) {

        File file = new File(pathPath+saveName);
        File path = new File(pathPath);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        long fileSize = body.contentLength();
        Log.e("zxj","fileSize:"+fileSize+",file.length():"+file.length());
        try {
            if (!path.exists()) {
                path.mkdir();
            }
            byte[] fileReader = new byte[1024 * 1024];
            long fileSizeDownloaded = 0;
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(file);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.length() > 0) {
                return true;
            }
        }
        return false;
    }
}
