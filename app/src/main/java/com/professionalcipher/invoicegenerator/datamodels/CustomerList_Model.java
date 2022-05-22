package com.professionalcipher.invoicegenerator.datamodels;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerList_Model  {

    private String name;
    private String gstIn;
    private String customerState;
    private String invoiceNo;
    private String address;
    private String invoiceDate;
    private String compState;
    private String codeNo;

    public CustomerList_Model() {

    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getGstIn() {
        return gstIn;
    }

    public void setGstIn( String gstIn ) {
        this.gstIn = gstIn;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState( String customerState ) {
        this.customerState = customerState;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo( String invoiceNo ) {
        this.invoiceNo = invoiceNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate( String invoiceDate ) {
        this.invoiceDate = invoiceDate;
    }

    public String getCompState() {
        return compState;
    }

    public void setCompState( String compState ) {
        this.compState = compState;
    }

    public String getCodeNo() {
        return codeNo;
    }

    public void setCodeNo( String codeNo ) {
        this.codeNo = codeNo;
    }

    public CustomerList_Model( String name, String companyName, String customerState, String invoiceNo, String address, String invoiceDate, String compState, String codeNo ) {
        this.name = name;
        this.gstIn = companyName;
        this.customerState = customerState;
        this.invoiceNo = invoiceNo;
        this.address = address;
        this.invoiceDate = invoiceDate;
        this.compState = compState;
        this.codeNo = codeNo;
    }


}
