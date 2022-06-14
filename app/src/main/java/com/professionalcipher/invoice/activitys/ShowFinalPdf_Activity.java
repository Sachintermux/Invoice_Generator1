package com.professionalcipher.invoice.activitys;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;
import com.professionalcipher.invoice.R;

import java.io.File;

public class ShowFinalPdf_Activity extends AppCompatActivity {
    private PDFView pdfView;
    private Button sharePdfBtn;
    private String filePath;
    private File file;
    private Context context;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_final_pdf);
        filePath = getIntent().getStringExtra("FilePath");
        file = new File(filePath);
        getSupportActionBar().setTitle(file.getName());
        initView();
        showPdf(file);
        allClickHandle();

    }

    private void allClickHandle() {
        sharePdfBtn.setOnClickListener(view -> sharePdfBtnClick());
    }

    private void sharePdfBtnClick() {
        Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share"));

    }

    private void initView() {
        context = getApplicationContext();
        pdfView = findViewById(R.id.pdfViewer_showFinalPdfA);
        sharePdfBtn = findViewById(R.id.sharePdfBtn_showFinalPdfA);
    }

    private void showPdf( File file ) {
        try {
            pdfView.fromFile(file).pages(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(false)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .load();

        } catch (NullPointerException e) {
            System.out.println("Pdf is Loaded");
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ShowFinalPdf_Activity.this, MainActivity.class));
        finish();
    }
}