package com.professionalcipher.invoicegenerator.activitys;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.professionalcipher.invoicegenerator.R;
import com.professionalcipher.invoicegenerator.adapters.ShowPreviousQuotation_RecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ShowPreviousQuotation_RecyclerViewAdapter.ShowPreviousListQ_interFace {

    private Button makeQuotation_Btn;
    private RecyclerView recyclerView;
    private ShowPreviousQuotation_RecyclerViewAdapter adapter;
    private ArrayList<String> listOfFileName = new ArrayList<>();
    private FileLoaderInBackGround fileLoaderInBackGround = new FileLoaderInBackGround();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        allBtnClickHandle();

    }

    private void initView() {
        makeQuotation_Btn = findViewById(R.id.makeQuotation_btnMain);
        recyclerView = findViewById(R.id.recyclerView_quotationList);
        fileLoaderInBackGround.execute(Environment.getExternalStorageDirectory().toString() + "/Invoice Generator");

    }


    private void allBtnClickHandle() {
        makeQuotation_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity(new Intent(MainActivity.this, Make_Quotation.class));
            }
        });
    }

    private void setAdapterToRecyclerView( ArrayList<String> listOfFileName ) {
        adapter = new ShowPreviousQuotation_RecyclerViewAdapter(MainActivity.this, listOfFileName);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        System.out.println(listOfFileName.get(1));
    }

    @Override
    public void deleteBtnClick( int position ) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Invoice Generator/" + listOfFileName.get(position));
        file.delete();
        listOfFileName.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void fileNameClick( int position ) {
        Intent intent = new Intent(MainActivity.this, ShowFinalPdf_Activity.class);
        intent.putExtra("FilePath", Environment.getExternalStorageDirectory().toString() + "/Invoice Generator/" + listOfFileName.get(position));
        startActivity(intent);
    }

    public class FileLoaderInBackGround extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground( String... filePath ) {
            try {
                File file = new File(filePath[0]);
                listOfFileName.addAll(Arrays.asList(file.list()));

                for (int i = 0; i < listOfFileName.size(); i++) {
                    if (!listOfFileName.get(i).toString().endsWith(".pdf"))
                        listOfFileName.remove(i);
                }
                setAdapterToRecyclerView(listOfFileName);
            } catch (Exception e) {

            }
            return listOfFileName;
        }
    }
}