package com.professionalcipher.invoice.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.professionalcipher.invoice.R;
import com.professionalcipher.invoice.savedata.SaveDataInSharePref;
import com.professionalcipher.invoice.viewmodels.MakeQuotationView_Model;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class Company_Fragment extends Fragment {

    private static final int REQUEST_GET_SINGLE_FILE = 1;
    private Button updateDetail_btn;
    private HashMap<String, String> companyFragmentHashMapData = new HashMap<>();
    private SaveDataInSharePref sharePref = new SaveDataInSharePref();
    private MakeQuotationView_Model view_model;
    private ImageView signatureImgV;
    private EditText
            companyName_edt,
            personName_edt,
            gstNo_edt,
            contactNo_edt;
    private Uri signatureUri;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.fragment_company_, container, false);

        intiView(view);
        observer();
        allClickHandle();

        return view;
    }

    private void observer() {
        view_model.signatureUri.observe(requireActivity(), data -> {
            if (data != null)
                signatureImgV.setImageURI(data);
            view_model.signatureDrawable.setValue(signatureImgV.getDrawable());
        });
    }

    private void intiView( View view ) {
        //EditText
        companyName_edt = view.findViewById(R.id.companyName_edt_CompanyT);
        personName_edt = view.findViewById(R.id.invoiceType_edt_CompanyT);
        gstNo_edt = view.findViewById(R.id.companyInfo1_edt_CompanyT);
        contactNo_edt = view.findViewById(R.id.companyInfo2_edt_CompanyT);

        signatureImgV = view.findViewById(R.id.signature_imgView_companyF);
        //Button

        updateDetail_btn = view.findViewById(R.id.updateDetail_btn_CompanyT);

        view_model = new ViewModelProvider(requireActivity()).get(MakeQuotationView_Model.class);
        signatureImgV.setImageURI(signatureUri);
        if (signatureUri != null)
            view_model.signatureDrawable.setValue(signatureImgV.getDrawable());

    }

    private void allClickHandle() {

        updateDetail_btn.setOnClickListener(this::updateDetailBtn_click);
        signatureImgV.setOnClickListener(view -> signatureImgVClick());
    }

    @SuppressLint("IntentReset")
    private void signatureImgVClick() {

        Dialog signatureDialog = new Dialog(requireContext());
        signatureDialog.setContentView(R.layout.signatureclick_dialog);
        signatureDialog.show();

        TextView fromStorage = signatureDialog.findViewById(R.id.storage_signatureD),
                signaturePad = signatureDialog.findViewById(R.id.signaturePad_signatureD);

        Button cancel = signatureDialog.findViewById(R.id.cancelBtn_signatureD),
                removeSignature = signatureDialog.findViewById(R.id.removeSignature_signatureD);

        cancel.setOnClickListener(view -> signatureDialog.cancel());
        fromStorage.setOnClickListener(view -> {

            Intent getIntent = new Intent(Intent.ACTION_CAMERA_BUTTON);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, REQUEST_GET_SINGLE_FILE);
            signatureDialog.cancel();
        });
        removeSignature.setOnClickListener(view -> {
            view_model.signatureUri.setValue(Uri.EMPTY);
            sharePref.savedData(requireContext(), "uri", "");
            signatureImgV.setImageURI(Uri.EMPTY);
            signatureUri = Uri.EMPTY;
            signatureDialog.cancel();
        });

        signaturePad.setOnClickListener(view -> {
            showSignaturePadView();
            signatureDialog.cancel();
        });

    }

    private void showSignaturePadView() {
        Dialog signaturePadDialog = new Dialog(requireContext());
        signaturePadDialog.setContentView(R.layout.signature_pad_dialog);
        signaturePadDialog.show();
        SignaturePad signaturePad1 = signaturePadDialog.findViewById(R.id.signature_pad_SignaturePadDialog);
        Button clearPadBtn = signaturePadDialog.findViewById(R.id.clearPad_signaturePadD),
                saveSignatureBtn = signaturePadDialog.findViewById(R.id.saveSignature_signaturePadD);
        ImageView closeIc = signaturePadDialog.findViewById(R.id.closeIc_signaturePadD);
        closeIc.setOnClickListener(view -> signaturePadDialog.cancel());
        signaturePad1.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {

            }
        });
        clearPadBtn.setOnClickListener(view -> signaturePad1.clear());
        saveSignatureBtn.setOnClickListener(view -> {

            signatureImgV.setImageBitmap(signaturePad1.getSignatureBitmap());
           Bitmap bitmap = signaturePad1.getSignatureBitmap();
           File file = new File(Environment.getExternalStorageDirectory().toString() + "/Invoice Generator/temp");
           if(!file.exists()) file.mkdirs();
            try (FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/Invoice Generator/temp/signature.png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),"Signature Pad Save : " +e,Toast.LENGTH_SHORT).show();
            }
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/Invoice Generator/temp/signature.png");
            signatureUri = uri;
            view_model.signatureUri.setValue(uri);
            signaturePadDialog.cancel();
        });
        signaturePadDialog.setCancelable(false);
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        System.out.println(requestCode);
        if (requestCode == REQUEST_GET_SINGLE_FILE && resultCode == RESULT_OK) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(requireContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                sharePref.savedData(requireContext(), "uri", uri.toString());
                view_model.signatureUri.setValue(uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void updateDetailBtn_click( View view ) {

//        if(isEmptyEditText(companyName_edt,"Enter the Company Name") ||
//        isEmptyEditText(contactNo_edt,"Enter the Contact Number") ||
//        isEmptyEditText(email_edt, "Enter the Email") ||
//        isEmptyEditText(address_edt,"Enter the Address")) return;
        if (companyName_edt.length() > 35) {
            companyName_edt.setError("Company Name cannot be greater then 35 character");
            return;
        }
        companyFragmentHashMapData = setViewDataToHashMap(companyFragmentHashMapData);

        view_model.setCompanyFragment_DATA(companyFragmentHashMapData);
        Toast.makeText(requireActivity(), "Company Detail Added Successfully", Toast.LENGTH_SHORT).show();

    }

    private boolean isEmptyEditText( EditText editText, String errorMsg ) {
        if (editText.getText().toString().length() == 0) {
            editText.setError(errorMsg);
            return true;
        }
        return false;
    }

    private HashMap<String, String> getSaveDate() {
        HashMap<String, String> hashMap = new HashMap<>();
        String data = sharePref.getData(requireActivity(), getString(R.string.companyHashMapData), "");
        if (!data.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            hashMap = gson.fromJson(data, type);
        }
        return hashMap;
    }

    private void saveData( HashMap<String, String> hashMap ) {
        Gson gson = new Gson();
        String data = gson.toJson(hashMap);
        sharePref.savedData(requireActivity(), getString(R.string.companyHashMapData), data);

    }

    @Override
    public void onStart() {
        super.onStart();
        companyFragmentHashMapData = getSaveDate();
        setHashMapDataToView(companyFragmentHashMapData);
        view_model.setCompanyFragment_DATA(companyFragmentHashMapData);
        signatureUri = Uri.parse(sharePref.getData(requireContext(), "uri", ""));
        view_model.signatureUri.setValue(signatureUri);
    }

    private void setHashMapDataToView( HashMap<String, String> companyFragmentHashMapData ) {
        if (companyFragmentHashMapData.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                companyName_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.companyName_CompanyF), ""));
                personName_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.invoiceType_CompanyF), ""));
                gstNo_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.companyInfo1_CompanyF), ""));
                contactNo_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.companyInfo2_CompanyF), ""));

//                email_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.email_CompanyF),""));
//                address_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.address_CompanyF),""));
//                city_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.city_CompanyF),""));
//                state_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.state_CompanyF),""));
//                country_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.country_CompanyF),""));
//                zipCode_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.zipCode_CompanyF),""));
//                website_edt.setText(companyFragmentHashMapData.getOrDefault(getString(R.string.webSite_CompanyF),""));
            }
        }

    }

    private HashMap<String, String> setViewDataToHashMap( HashMap<String, String> hashMap ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            hashMap.putIfAbsent(getString(R.string.companyName_CompanyF), companyName_edt.getText().toString());
            hashMap.putIfAbsent(getString(R.string.invoiceType_CompanyF), personName_edt.getText().toString());
            hashMap.putIfAbsent(getString(R.string.companyInfo1_CompanyF), gstNo_edt.getText().toString());
            hashMap.putIfAbsent(getString(R.string.companyInfo2_CompanyF), contactNo_edt.getText().toString());
//            hashMap.putIfAbsent(getString(R.string.email_CompanyF),email_edt.getText().toString());
//            hashMap.putIfAbsent(getString(R.string.address_CompanyF),address_edt.getText().toString());
//            hashMap.putIfAbsent(getString(R.string.city_CompanyF),city_edt.getText().toString());
//            hashMap.putIfAbsent(getString(R.string.state_CompanyF),state_edt.getText().toString());
//            hashMap.putIfAbsent(getString(R.string.country_CompanyF),country_edt.getText().toString());
//            hashMap.putIfAbsent(getString(R.string.zipCode_CompanyF),zipCode_edt.getText().toString());
//            hashMap.putIfAbsent(getString(R.string.webSite_CompanyF),website_edt.getText().toString());

            hashMap.replace(getString(R.string.companyName_CompanyF), companyName_edt.getText().toString());
            hashMap.replace(getString(R.string.invoiceType_CompanyF), personName_edt.getText().toString());
            hashMap.replace(getString(R.string.companyInfo1_CompanyF), gstNo_edt.getText().toString());
            hashMap.replace(getString(R.string.companyInfo2_CompanyF), contactNo_edt.getText().toString());
//            hashMap.replace(getString(R.string.email_CompanyF),email_edt.getText().toString());
//            hashMap.replace(getString(R.string.address_CompanyF),address_edt.getText().toString());
//            hashMap.replace(getString(R.string.city_CompanyF),city_edt.getText().toString());
//            hashMap.replace(getString(R.string.state_CompanyF),state_edt.getText().toString());
//            hashMap.replace(getString(R.string.country_CompanyF),country_edt.getText().toString());
//            hashMap.replace(getString(R.string.zipCode_CompanyF),zipCode_edt.getText().toString());
//            hashMap.replace(getString(R.string.webSite_CompanyF),website_edt.getText().toString());
        }
        return hashMap;
    }

    @Override
    public void onPause() {
        saveData(companyFragmentHashMapData);
        sharePref.savedData(requireContext(), "uri", signatureUri.toString());
        super.onPause();

    }

    @Override
    public void onStop() {
        sharePref.savedData(requireContext(), "uri", signatureUri.toString());
        view_model.signatureUri.setValue(signatureUri);
        view_model.signatureDrawable.setValue(signatureImgV.getDrawable());
        super.onStop();
    }
}