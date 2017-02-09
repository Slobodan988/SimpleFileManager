package com.amaze.filemanager.helper;

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by slobodanzdravkovic on 1/31/17.
 */

public class FileMenagerAppClass extends Application {

    // private static Context sApplicationContext;

    Activity actForAdvertisingId = null;

    String gPlayServicesUserId="";

    public static final String FLURRY_ANALYTICS_TAG = "flurry_analytics_tag";

    @Override
    public void onCreate() {
//        ACRA.init(this);
//        ACRA.getErrorReporter().setReportSender(
//                new TomahawkHttpSender(ACRA.getConfig().httpMethod(), ACRA.getConfig().reportType(),
//                        null));

        super.onCreate();

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
    }

    // public static Context getContext() {
//        return sApplicationContext;
//    }


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
        Log.i(FLURRY_ANALYTICS_TAG, message);
    }
}
