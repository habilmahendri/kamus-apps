package com.example.habilmahendri.kamusproject.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.example.habilmahendri.kamusproject.AppPreference;
import com.example.habilmahendri.kamusproject.R;
import com.example.habilmahendri.kamusproject.db.KamusHelper;
import com.example.habilmahendri.kamusproject.model.KamusModel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreloadActivity extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);

        ButterKnife.bind(this);
        new LoadData().execute();
    }



    private class LoadData extends AsyncTask<Void, Integer, Void> {
        KamusHelper kamusHelper;
        AppPreference appPreference;
        double loading;
        double maxLoading = 100;

        @Override
        protected void onPreExecute() {
            kamusHelper = new KamusHelper(PreloadActivity.this);
            appPreference = new AppPreference(PreloadActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Boolean firstRun = appPreference.getFirstRun();
            if (firstRun) {
                ArrayList<KamusModel> indonesia = preLoadRaw(R.raw.indonesia_english);
                ArrayList<KamusModel> english = preLoadRaw(R.raw.english_indonesia);
                publishProgress((int) loading);

                try {
                    kamusHelper.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Double progressMaxInsert = 100.0;
                Double progressDiff = (progressMaxInsert - loading) / (english.size() + indonesia.size());

                kamusHelper.insertTransaction(english, true);
                loading += progressDiff;
                publishProgress((int) loading);
                kamusHelper.insertTransaction(indonesia, false);
                loading += progressDiff;
                publishProgress((int) loading);
                kamusHelper.close();
                appPreference.setFirstRun(false);

                publishProgress((int) maxLoading);
            } else {
                try {
                    synchronized (this) {
                        this.wait(1000);
                        publishProgress(50);

                        this.wait(300);
                        publishProgress((int) maxLoading);
                    }
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress_bar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent i = new Intent(PreloadActivity.this, MainActivity.class);
            startActivity(i);

            finish();
        }
    }

    public ArrayList<KamusModel> preLoadRaw(int data) {
        ArrayList<KamusModel> kamusModels = new ArrayList<>();
        BufferedReader reader;
        try {
            Resources res = getResources();
            InputStream raw_dict = res.openRawResource(data);

            reader = new BufferedReader(new InputStreamReader(raw_dict));
            String line = null;
            do {
                line = reader.readLine();
                String[] splitstr = line.split("\t");
                KamusModel kamusModel;
                kamusModel = new KamusModel(splitstr[0], splitstr[1]);
                kamusModels.add(kamusModel);
            } while (line != null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kamusModels;
    }
}


