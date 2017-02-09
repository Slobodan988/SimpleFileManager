package com.amaze.filemanager.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.widget.Toast;

import com.amaze.filemanager.helper.UlazniPodaci;
import com.amaze.filemanager.utils.provider.UtilitiesProvider;
import com.amaze.filemanager.utils.provider.UtilitiesProviderInterface;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by vishal on 7/12/16.
 */

public class AppConfig extends Application {


    Activity actForAdvertisingId = null;

    String gPlayServicesUserId="";

    public static final String FLURRY_ANALYTICS_TAG = "flurry_analytics_tag";


    public static final String TAG = AppConfig.class.getSimpleName();

    private UtilitiesProviderInterface utilsProvider;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Handler mApplicationHandler = new Handler();

    private static AppConfig mInstance;

    public UtilitiesProviderInterface getUtilsProvider() {
        return utilsProvider;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // sApplicationContext = getApplicationContext();

        LogFlurryInitializingSteps(" ----> AppClass onCreate() called" );


        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .withListener(new FlurryAgentListener()
                {

                    @Override
                    public void onSessionStarted() {

                        LogFlurryInitializingSteps(" ----> GameAppClass FlurryAgentListener onSessionStarted" );


                    }
                })
                .build(this, UlazniPodaci.FLURRY);
        utilsProvider = new UtilitiesProvider(this);

        // disabling file exposure method check for api n+
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    /**
     * Shows a toast message
     *
     * @param context Any context belonging to this application
     * @param message The message to show
     */
    public static void toast(Context context, String message) {
        // this is a static method so it is easier to call,
        // as the context checking and casting is done for you

        if (context == null) return;

        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }

        if (context instanceof Application) {
            final Context c = context;
            final String m = message;

            ((AppConfig) context).runInApplicationThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, m, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Run a runnable in the main application thread
     *
     * @param r Runnable to run
     */
    public void runInApplicationThread(Runnable r) {
        mApplicationHandler.post(r);
    }

    public static synchronized AppConfig getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            this.mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
        }
        return mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static final String md5(final String s) {


        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            // e.printStackTrace();
        }
        catch(Exception e)
        {

        }
        return "";
    }

    public  void TryToSetFlurryUserID()
    {

        if(!gPlayServicesUserId.equals(""))
        {

            LogFlurryInitializingSteps(" ---->  User id found: " + gPlayServicesUserId);

            String idForFlurry = md5(gPlayServicesUserId);


            if(!idForFlurry.equals(""))
            {


                FlurryAgent.setUserId(idForFlurry);

                LogFlurryInitializingSteps(" ----> Flurryid found: " + idForFlurry + " and set");

            }
            else
            {
                LogFlurryInitializingSteps(" ----> Flurryid not found");
            }

        }
        else
        {
            LogFlurryInitializingSteps(" ----> User id not found");
        }
    }

    AsyncTask flurryIdTask;

    private void StopFlurryIdTask()
    {
        try
        {
            if(flurryIdTask!= null)
            {
                flurryIdTask.cancel(true);
            }
        }
        catch(Exception e)
        {

        }
    }
    public void  FlurryActCreated(Activity act)
    {
        actForAdvertisingId = act;
        StartFlurryIdTask();
    }

    public void FlurryActDestroyed()
    {
        actForAdvertisingId = null;
        StopFlurryIdTask();
    }

    private void StartFlurryIdTask()
    {
        StopFlurryIdTask();

        flurryIdTask = new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object[] params)
            {
                gPlayServicesUserId="";
                try
                {
                    if(actForAdvertisingId!= null)
                    {
                        gPlayServicesUserId = AdvertisingIdClient.getAdvertisingIdInfo(actForAdvertisingId).getId();
                    }
                    else
                    {
                        LogFlurryInitializingSteps(" ----> UnityPlayerActivity.actInstance je null" );
                        return true;
                    }

                }
                catch (Exception exp)
                {

                }
                TryToSetFlurryUserID();

                return true;
            }
        };

        flurryIdTask.execute("start");

        LogFlurryInitializingSteps(" ----> Pokrenut flurryIdTask" );
    }

    void LogFlurryInitializingSteps(String message)
    {
        //Log.i(FLURRY_ANALYTICS_TAG, message);
    }
}
