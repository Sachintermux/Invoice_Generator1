package com.professionalcipher.invoice.activitys;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.professionalcipher.invoice.R;
import com.professionalcipher.invoice.adapters.ViewPager_Adapter;
import com.professionalcipher.invoice.fragments.CustomerName_Fragment;
import com.professionalcipher.invoice.fragments.Preview_Fragment;
import com.professionalcipher.invoice.fragments.ProductName_Fragment;
import com.professionalcipher.invoice.viewmodels.MakeQuotationView_Model;

public class Make_Quotation extends AppCompatActivity implements Preview_Fragment.PreviewF_interface, ProductName_Fragment.ProductNameF_interface, CustomerName_Fragment.CustomerNameF_interface {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private MakeQuotationView_Model view_model;
    private String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_quotation);
        intiView();
        if (checkPermission())
            setViewPager2();
        else requestPermission();
    }

    private void intiView() {
        viewPager2 = findViewById(R.id.viewPager_InvoiceGenerator);
        tabLayout = findViewById(R.id.tabLayout_InvoiceGenerate);
        view_model = new ViewModelProvider(this).get(MakeQuotationView_Model.class);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult( ActivityResult result ) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager())
                        setViewPager2();
                    else
                        Toast.makeText(Make_Quotation.this, "You Denied the permission you are not able to generate Quotations", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Make_Quotation.this, "You Denied the permission you are not able to generate Quotations", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setViewPager2() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPager_Adapter adapter = new ViewPager_Adapter(fragmentManager, getLifecycle(), view_model);
        viewPager2.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected( TabLayout.Tab tab ) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected( TabLayout.Tab tab ) {
            }

            @Override
            public void onTabReselected( TabLayout.Tab tab ) {
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected( int position ) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            new AlertDialog.Builder(Make_Quotation.this)
                    .setTitle("Permission")
                    .setMessage("Please give the Storage permission to make quotation")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick( DialogInterface dialog, int which ) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                                activityResultLauncher.launch(intent);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                activityResultLauncher.launch(intent);
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();

        } else {

            ActivityCompat.requestPermissions(Make_Quotation.this, permissions, 30);
        }
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 30) {
            if (grantResults.length > 0) {
                boolean readCheck = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeCheck = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (readCheck && writeCheck) {
                    setViewPager2();
                } else
                    Toast.makeText(Make_Quotation.this, "You Denied the permission you are not able to generate Quotations", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(Make_Quotation.this, "You Denied the permission you are not able to generate Quotations", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void closePreview_fragment( int position ) {
        viewPager2.setCurrentItem(position);
    }

    @Override
    public void closeProductName_fragment( int position ) {
        viewPager2.setCurrentItem(position);
    }

    @Override
    public void closeCustomerName_fragment( int position ) {
        viewPager2.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Make_Quotation.this, MainActivity.class));
        finish();
    }
}