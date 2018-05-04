package bodyfriend.com.customerapp.home.util;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.miscellaneous.Log;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import bodyfriend.com.customerapp.BuildConfig;
import bodyfriend.com.customerapp.base.NetConst;

import static java.security.AccessController.getContext;

/**
 * Created by 이주영 on 2017-02-06.
 */

public class PdfDownloader {

    private Context mContext;

    public PdfDownloader(Context c) {
        mContext = c;
    }

    public static PdfDownloader newInstance(Context c) {
        return new PdfDownloader(c);
    }

    /**
     * pdf를 다운로드 하거나 로컬에 저장되어있으면 실행한다.
     */
    public void downloadPdf(String path) {
        Log.d("downloadPdf => " + path);
        // 파일이 저장경로에 있는지 확인
        File file = new File(filePath(path));
        // 파일이 있으면
        if (file.exists()) {
            // pdf를 실행시킨다.
            runPdf(Uri.fromFile(file), file);
        } else {
            new DownloadTask().execute(path);
        }
    }

    private String filePath(String path) {
        String[] paths = path.split("/");
        String p = paths[paths.length - 1];
        p = String.format("%s%s.pdf", folderPath(), p);
        Log.d(p);
        return p;
    }

    private String folderPath() {
        return "/sdcard/bodyfriend/customer/";
    }

    /**
     * pdf 실행
     */
    private boolean runPdf(File f) {
        return runPdf(Uri.fromFile(f), f);
    }

    /**
     * pdf파일을 실행한다.
     * 실행 실패하면 return false를 준다.
     */
    private boolean runPdf(Uri path, File newFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("VERSION_CODES.N~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", newFile);
            intent.setDataAndType(contentUri, "application/pdf");
        } else {
            Log.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ <= Build.VERSION_CODES.N ");
//            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            return false;
        }
        return true;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog mDlg;
        private String mUrl;

        @Override
        protected void onPreExecute() {
            mDlg = new ProgressDialog(mContext);
            mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDlg.setMessage("파일을 다운로드중입니다.");
            mDlg.show();

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            mDlg.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mDlg.dismiss();
            runPdf(new File(filePath(mUrl)));
        }

        public DownloadTask() {
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            HttpURLConnection connection = null;
            OutputStream output = null;
            try {
                mUrl = sUrl[0];
                URL url = new URL(mUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();

                // 폴더 생성
                File file = new File(folderPath());
                file.mkdirs();

                output = new FileOutputStream(filePath(mUrl));

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}
