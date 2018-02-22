package com.waymaps.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.waymaps.api.RetrofitService;
import com.waymaps.data.requestEntity.UpdateCredentials;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.JSONUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 07.02.2018.
 */

public class SessionUpdateService extends Service {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final long UPDATE_TIME = 300;
    private static Intent currentIntent;
    private static UpdateCredentials credentials;


    public void onCreate() {
        super.onCreate();
        logger.debug("Service {} created",this.getClass());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String methodName = "onStartCommand";
        logger.debug("Service's method {} started", methodName);
        currentIntent = intent;
        getUpdateCredentials();
        updateProcedure();
        return super.onStartCommand(intent, flags, startId);
    }

    private void getUpdateCredentials() {
        try {
            credentials = JSONUtil.getObjectMapper().readValue(currentIntent.getExtras()
                    .getCharSequence("user").toString(), UpdateCredentials.class);
            logger.debug("Service {} got user {}",this.getClass(), credentials.getUser_id());
        } catch (IOException e) {
            logger.debug("Something went wrong while try to parse update credentials");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        logger.debug("Service {} destroyed" ,this.getClass());
    }

    public IBinder onBind(Intent intent) {
        logger.debug("Service {} destroyed" ,this.getClass());
        return null;
    }

    void updateProcedure() {
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    RetrofitService.getWayMapsService().updateProcedure(credentials.getAction(),credentials.getUser_id(),
                            credentials.getIdentificator(),credentials.getOs()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            logger.debug("Session updated");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            logger.debug("Failed sesion update");
                        }
                    });
                    try {
                        TimeUnit.SECONDS.sleep(UPDATE_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
