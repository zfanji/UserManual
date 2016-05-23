package com.lx.usermanual;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String ASSIGN_PATH = "/storage/emulated/0/VideoBook";
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String fileName = "UserManual_"+getCurrentLanguage()+".pdf";
        new AsyncWorkTask().execute(new Object[] {fileName});

    }

    /**
     * 异步任务
     * @author hellogv
     */
    class AsyncWorkTask extends AsyncTask<Object,Object,Void> {
        @Override
        protected Void doInBackground(Object... params) {
            String fileName=(String) params[0];
            Uri fileUri=searchFile(ASSIGN_PATH,fileName);
            if(fileUri!=null)
                publishProgress(new Object[] {fileUri});

            return null;
        }

        protected void onProgressUpdate(Object... progress) {
            Uri fileUri=(Uri) progress[0];
            openPdf(fileUri);
            finish();
            System.exit(0);
        }
    }
    public void openPdf(Uri fileUri)
    {
        if(fileUri==null){
            return;
        }
        Log.d(TAG,"openPdf="+fileUri.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Looper.prepare();
            Toast.makeText(MainActivity.this,
                    "No Application Available to View PDF",
                    Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

    }

    private String getCurrentLanguage() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        Log.d(TAG,"language="+language);
        return language;
    }

    private Uri searchFile(String dir, String fileName) {
        Uri result=null;
        boolean findOK=false;
        File[] files = new File(dir).listFiles();
        for (File f : files) {
            Log.d(TAG,"list: "+f.toString());
            if(f.getName().indexOf(fileName)>=0){
                result = Uri.fromFile(f);
                findOK=true;
            }
        }
        //查找默认
        if(findOK==false) {
            for (File f : files) {
                Log.d(TAG, "list: " + f.toString());
                if (f.getName().indexOf("UserManual_default.pdf") >= 0) {
                    result = Uri.fromFile(f);
                    findOK = true;
                }
            }
        }

        if(result==null){
            Looper.prepare();
            showErroDialog("Can't find the file, find the position: "+dir+"/"+fileName);
            Looper.loop();
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            finish();
            System.exit(0);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    protected void showErroDialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Erro!!!");

        builder.setMessage(msg);

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                finish();
                System.exit(0);

            }
        });

        builder.create().show();
    }
}
