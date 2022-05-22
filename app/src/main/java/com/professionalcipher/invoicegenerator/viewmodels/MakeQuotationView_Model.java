package com.professionalcipher.invoicegenerator.viewmodels;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.professionalcipher.invoicegenerator.datamodels.CustomerList_Model;
import com.professionalcipher.invoicegenerator.datamodels.ProductList_Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MakeQuotationView_Model extends ViewModel {

    public MutableLiveData<ArrayList<CustomerList_Model>> customerList_ModelView = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<HashMap<String, String>> companyFragment_DATA = new MutableLiveData<>(new HashMap<>());
    public MutableLiveData<Integer> currentCustomerSelectedPosition = new MutableLiveData<>(-1);
    public MutableLiveData<ArrayList<ProductList_Model>> productList_ModelView = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<ArrayList<String>> selectedProductList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<String> selectedNote = new MutableLiveData<>("");
    public MutableLiveData<HashMap<String,Boolean>> isCheckBoxChecked_productF = new MutableLiveData<>(new HashMap<>());
    public MutableLiveData<Double> receivedAmount = new MutableLiveData<>(0d);
    public MutableLiveData<Drawable> signatureDrawable = new MutableLiveData<>();
    public MutableLiveData<Uri> signatureUri = new MutableLiveData<>(null);

    public void setSelectedNote(String note){
        selectedNote.setValue(note);
    }

    public void setReceivedAmount(double amount){
        receivedAmount.setValue(amount);
    }

    public void setIsCheckBoxChecked_productF(HashMap<String,Boolean> checkBoxChecked_productF){
        this.isCheckBoxChecked_productF.setValue(checkBoxChecked_productF);
    }

    public void setSelectedProductList(ArrayList<String> selectedProductList) {
        Collections.sort(selectedProductList, new Comparator<String>() {
            @Override
            public int compare( String s, String t1 ) {
                return s.compareTo(t1);
            }
        });
        this.selectedProductList.setValue(selectedProductList);
    }

    public void setProductList_ModelView( ArrayList<ProductList_Model> models ) {
        productList_ModelView.setValue(models);
    }

    public void setCurrentCustomerSelectedPosition( int position ) {
        currentCustomerSelectedPosition.setValue(position);
    }

    public void setCompanyFragment_DATA( HashMap<String, String> companyFragment_data ) {
        this.companyFragment_DATA.setValue(companyFragment_data);
    }

    public void setCustomerList_ModelView( ArrayList<CustomerList_Model> models ) {
        this.customerList_ModelView.setValue(models);
    }
}
