package com.truman.demo.imagedownloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ImageDownloader {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "ImageDownloader" + TAG_SUFFIX;
    private static final int PURGER_TIMEOUT_MILLS = 10 * 1000;

    private final HashMap<String, Bitmap> mBitmapCache = new HashMap<>();
    private final Handler mHandler = new Handler();

    private static ImageDownloader sInstance;

    private ImageDownloader() {}
    public static synchronized ImageDownloader getInstance() {
        if (sInstance == null) {
            sInstance = new ImageDownloader();
        }
        return sInstance;
    }

    public void download(String url, ImageView imageView) {
        if (url == null || imageView == null) {
            return;
        }
        resetPurgeTimer();
        synchronized (mBitmapCache) {
            new ImageDownloaderTask(mBitmapCache.get(url), imageView)
                    .execute(url);
        }
    }

    private Bitmap getBitmapFromURL(String src) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resized = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, false);
        return resized;
    }

    private class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> mImageViewRef;
        private final int mImageViewHeight;
        private final int mImageViewWidth;
        private final Bitmap mCached;

        public ImageDownloaderTask(@Nullable Bitmap cached, @NonNull ImageView imageView) {
            mImageViewRef = new WeakReference<>(imageView);
            mImageViewHeight = imageView.getHeight();
            mImageViewWidth = imageView.getWidth();
            mCached = cached;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            if (mCached != null
                    && mCached.getWidth() == mImageViewWidth
                    && mCached.getHeight() == mImageViewHeight) {
                Log.d(TAG, "Draw cached image!");
                return mCached;
            } else {
                Log.d(TAG, "Draw downloaded image!");
                String url = strings[0];
                Bitmap finalBitmap = resizeBitmap(
                        getBitmapFromURL(url), mImageViewWidth, mImageViewHeight);
                synchronized (mBitmapCache) {
                    mBitmapCache.put(url, finalBitmap);
                }
                return finalBitmap;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                return;
            }
            ImageView view = mImageViewRef.get();
            if (view != null) {
                view.setImageBitmap(result);
            }
        }
    }

    public void requsetPurge() {
        mHandler.removeCallbacks(purger);
        mHandler.post(purger);
    }

    private void resetPurgeTimer() {
        mHandler.removeCallbacks(purger);
        mHandler.postDelayed(purger, PURGER_TIMEOUT_MILLS);
    }

    private void clearCache() {
        synchronized (mBitmapCache) {
            Log.d(TAG, "Clear cached image...");
            mBitmapCache.clear();
        }
    }

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    public void setLogger(Logger logger) {
        Log.setLogger(logger);
    }

    private static class Log {
        private static Logger mLogger;

        static void setLogger(Logger logger) {
            mLogger = logger;
        }

        static void d(String tag, String msg) {
            if (mLogger != null) {
                mLogger.d(tag, msg);
            }
        }

        static void e(String tag, String msg) {
            if (mLogger != null) {
                mLogger.e(tag, msg);
            }
        }
    }
}
