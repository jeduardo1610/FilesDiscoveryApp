package com.mac.macysfilesapp.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mac.macysfilesapp.R;
import com.mac.macysfilesapp.utils.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    ProgressBar progressBar;
    Button btnStart;
    Button btnStop;
    TextView filesFound;
    TextView avgSize;
    TextView freqExt;
    TextView bigFiles;
    MyTask mytask;
    View view;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setMax(100);
        filesFound = (TextView) view.findViewById(R.id.files_found);
        avgSize = (TextView) view.findViewById(R.id.avg_size);
        freqExt = (TextView) view.findViewById(R.id.freq_ext);
        bigFiles = (TextView) view.findViewById(R.id.big_files);
        btnStart = (Button) view.findViewById(R.id.button_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask();
            }
        });
        btnStop = (Button) view.findViewById(R.id.button_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTask();
            }
        });
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        progressBar.setMax(100);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void startTask() {
        mytask = new MyTask();
        mytask.execute("send it");
        clearUI();
    }

    public void stopTask() {
        cancelTask();
    }

    public void cancelTask() {
        // if the asynctask is running, cancel it
        if (mytask!=null && mytask.getStatus()==AsyncTask.Status.RUNNING) {
            mytask.cancel(true);
            Toast.makeText(getContext(), R.string.task_canceled, Toast.LENGTH_SHORT).show();
        }
        clearUI();
    }

    public void clearUI() {
        progressBar.setProgress(0);
        filesFound.setText("");
        avgSize.setText("");
        freqExt.setText("");
        bigFiles.setText("");
    }

    public class MyTask extends AsyncTask<String, Integer, ArrayList<MyFile>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<MyFile> arr) {
            super.onPostExecute(arr);
            HashMap<String, Integer> hashmap = new HashMap<>();
            long totalSize = 0;
            for (int i=0; i<arr.size(); i++) {
                String filenameArray[] = arr.get(i).getName().split("\\.");
                String extension = filenameArray[filenameArray.length-1];
                if (hashmap.containsKey(extension))
                    hashmap.put(extension, hashmap.get(extension)+1);
                else
                    hashmap.put(extension, 1);
                // get the size of the file
                totalSize += arr.get(i).getSize();
                Log.i("SIZE", "Size: "+arr.get(i).getSize());
            }

            // Display number of files scanned
            filesFound.setText(arr.size()+" files\n");

            // Display the average file size
            avgSize.setText(totalSize/4+ "MB\n");

            // Create list with the extensions and their frequency
            List<Map.Entry<String,Integer>> extensions = new ArrayList<Map.Entry<String,Integer>>(hashmap.entrySet());
            Log.i("MYAPP", "Extensions found: "+extensions.size());

            // Sort extensions list by frequency
            Collections.sort(extensions, new Comparator<Map.Entry<String,Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                    return e2.getValue() - e1.getValue(); // descending
                }
            });

            for(int i=0; i<extensions.size() && i<5; i++) {
                freqExt.append(extensions.get(i).getValue()+
                        " "+
                        extensions.get(i).getKey()+
                        " files\n"
                );
            }

            // Sort files by size
            Collections.sort(arr, new Comparator<MyFile>() {
                @Override
                public int compare(MyFile p1, MyFile p2) {
                    return p2.getSize() - p1.getSize(); // descending
                }
            });

            // Display the ten biggest files
            for(int i=0; i<arr.size() && i<10; i++) {
                bigFiles.append(
                        arr.get(i).getName()+
                        " "+
                        arr.get(i).getSize()+
                        "MB"+
                        "\n"
                );
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected ArrayList<MyFile> doInBackground(String... params) {

            ArrayList<MyFile> myFiles = new ArrayList<MyFile>();

            try {

                //File f = Environment.getExternalStorageDirectory();
                File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                int total = 0;
                int cont = 0;
                f.mkdirs();
                File[] files = f.listFiles();
                if (files.length == 0)
                    return null;
                else {
                    total += files.length;
                    for (int i=0; i<files.length; i++) {
                        if (files[i].isDirectory()) {
                            total--;
                        } else if (files[i].isFile()) {
                            myFiles.add(new MyFile(files[i].getName(), (int) (files[i].length()/1000000)));
                            cont++;
                            publishProgress(cont*100/total);
                            Thread.sleep(125);
                        } else {
                            total--;
                        }
                    }
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }

            return myFiles;
        }

    }

}