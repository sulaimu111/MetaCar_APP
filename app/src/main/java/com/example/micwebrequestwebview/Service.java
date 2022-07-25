package com.example.micwebrequestwebview;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;

class RecordingService extends Service {
    private MediaRecorder mediaRecorder = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    //錄音
    public void onStart(Intent intent,int startId)
    {
        //設定錄音檔名
        String fileName = "test.amr";
        try{
            File SDCardpath= Environment.getExternalStorageDirectory();
            File myDataPath=new File(SDCardpath.getAbsolutePath()+"/download");

            if( !myDataPath.exists() ) myDataPath.mkdirs();

            File recodeFile = new File(SDCardpath.getAbsolutePath() + "/download/"+fileName);
            mediaRecorder = new MediaRecorder();

            //設定音源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //設定輸出檔案的格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //設定編碼格式
            mediaRecorder
                    .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //設定錄音檔位置
            mediaRecorder.setOutputFile(recodeFile.getAbsolutePath());
            mediaRecorder.prepare();
            //開始錄音
            mediaRecorder.start();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        super.onStart(intent, startId);
    }

    @Override
    //停止背景時
    public void onDestroy() {
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.onDestroy();
    }


}
