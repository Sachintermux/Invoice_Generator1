package com.professionalcipher.invoicegenerator.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.professionalcipher.invoicegenerator.R;
import com.professionalcipher.invoicegenerator.adapters.CustomerShow_RecyclerViewAdapter;
import com.professionalcipher.invoicegenerator.datamodels.CustomerList_Model;
import com.professionalcipher.invoicegenerator.savedata.SaveDataInSharePref;
import com.professionalcipher.invoicegenerator.viewmodels.MakeQuotationView_Model;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CustomerName_Fragment extends Fragment implements CustomerShow_RecyclerViewAdapter.RecyclerVClick_InterfaceShowCustomerF {
    private final int RESULT_CONTENT_PICK = 1;
    private FloatingActionButton addCustomer_Btn;
    private ArrayList<CustomerList_Model> customerList_models = new ArrayList<>();
    private CustomerShow_RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private SaveDataInSharePref sharePref = new SaveDataInSharePref();
    private MakeQuotationView_Model view_model;
    private EditText
            customerName_edt_dialog,
            address_edt_dialog,
            gstIn_edt_dialog,
            customerState_edt_dialog,
            invoiceNo_edt_dialog,
            invoiceDate_edt_dialog,
            compState_edt_dialog,
            codeNo_edt_dialog;

    private Button addCustomerBtn_dialog;
    private ImageView closeIc_dialog;
    private CustomerNameF_interface customerNameF_interface;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {


        View view = inflater.inflate(R.layout.fragment_customer_name, container, false);

        initView(view);
        viewModelObservers();
        allClickHandle();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void viewModelObservers() {
        view_model.customerList_ModelView.observe(requireActivity(), value -> {
            customerList_models = value;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void initView( View view ) {
        customerNameF_interface = (CustomerNameF_interface) requireContext();
        addCustomer_Btn = view.findViewById(R.id.addCustomer_floatingActionBtn);
        recyclerView = view.findViewById(R.id.recyclerView_showCustomerList);

        view_model = new ViewModelProvider(requireActivity()).get(MakeQuotationView_Model.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (view_model.companyFragment_DATA.getValue().size() == 0)
            showDialog_ErrorFinder("Add Company", "Please Add Your Company or Shop detail", 0);
    }

    private void showDialog_ErrorFinder( String title, String message, int position ) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int which ) {
                        customerNameF_interface.closeCustomerName_fragment(position);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void allClickHandle() {
        addCustomer_Btn.setOnClickListener(view -> addCustomer_btnClick(-1));


    }

    private void addCustomer_btnClick( int position ) {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.add_customer_dialog);

        dialog.setCancelable(false);
        initViewDialog(dialog);

        closeIc_dialog.setOnClickListener(view1 -> dialog.cancel());

        if (position != -1) {
            CustomerList_Model model = customerList_models.get(position);
            customerName_edt_dialog.setText(model.getName());
            gstIn_edt_dialog.setText(model.getGstIn());
            customerState_edt_dialog.setText(model.getCustomerState());
            invoiceNo_edt_dialog.setText(model.getInvoiceNo());
            address_edt_dialog.setText(model.getAddress());
            invoiceDate_edt_dialog.setText(model.getInvoiceDate());
            compState_edt_dialog.setText(model.getCompState());
            codeNo_edt_dialog.setText(model.getCodeNo());

            addCustomerBtn_dialog.setText("Update Details");
        }

        addCustomerBtn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                if (customerName_edt_dialog.getText().toString().length() == 0) {
                    customerName_edt_dialog.setError("Enter the Customer Name");
                    return;
                }
                if (customerList_models != null) {
                    CustomerList_Model model = new CustomerList_Model(
                            customerName_edt_dialog.getText().toString(),
                            gstIn_edt_dialog.getText().toString(),
                            customerState_edt_dialog.getText().toString(),
                            invoiceNo_edt_dialog.getText().toString(),
                            address_edt_dialog.getText().toString(),
                            invoiceDate_edt_dialog.getText().toString(),
                            compState_edt_dialog.getText().toString(),
                            codeNo_edt_dialog.getText().toString()
                    );
                    if (position == -1)
                        customerList_models.add(model);
                    else {
                        customerList_models.remove(position);
                        customerList_models.add(position, model);
                    }
                    sharePref.savedData(requireContext(), getString(R.string.compState_CustomerNameF), compState_edt_dialog.getText().toString());
                    view_model.setCustomerList_ModelView(customerList_models);
                }
                dialog.cancel();
            }
        });

        dialog.show();

    }

    @SuppressLint("SetTextI18n")
    private void initViewDialog( Dialog dialog ) {
        customerName_edt_dialog = dialog.findViewById(R.id.customerName_edt_addCustomerD);
        gstIn_edt_dialog = dialog.findViewById(R.id.gstIn_edt_addCustomerD);
        customerState_edt_dialog = dialog.findViewById(R.id.state_edt_addCustomerD);
        invoiceNo_edt_dialog = dialog.findViewById(R.id.invoiceNo_edt_addCustomerD);
        address_edt_dialog = dialog.findViewById(R.id.address_edt_addCustomerD);
        invoiceDate_edt_dialog = dialog.findViewById(R.id.invoiceDate_edt_addCustomerD);
        compState_edt_dialog = dialog.findViewById(R.id.stateCompany_edt_addCustomerD);
        codeNo_edt_dialog = dialog.findViewById(R.id.codeNo_edt_addCustomerD);

        closeIc_dialog = dialog.findViewById(R.id.closeIc_addCustomerD);
//        contactIC_dialog = dialog.findViewById(R.id.contactIc_addCustomerD);
        addCustomerBtn_dialog = dialog.findViewById(R.id.addCustomer_Btn_addCustomerD);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        invoiceDate_edt_dialog.setText(invoiceDate_edt_dialog.getText() + " " + simpleDateFormat.format(Calendar.getInstance().getTime()));
        invoiceNo_edt_dialog.setText(invoiceNo_edt_dialog.getText() + " " + sharePref.getData(requireContext(), getString(R.string.invoiceNo_CustomerNameF), 1));
        compState_edt_dialog.setText(sharePref.getData(requireContext(), getString(R.string.compState_CustomerNameF), "*State:*"));

    }

    private void setAdapterToRecyclerView( ArrayList<CustomerList_Model> models ) {

        adapter = new CustomerShow_RecyclerViewAdapter(models, requireActivity(), view_model, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        customerList_models = getSaveData();
        view_model.setCustomerList_ModelView(customerList_models);
        setAdapterToRecyclerView(customerList_models);
    }

    @Override
    public void onPause() {
        saveData(customerList_models);
        super.onPause();
    }

    private ArrayList<CustomerList_Model> getSaveData() {
        ArrayList<CustomerList_Model> models = new ArrayList<>();
        String data = sharePref.getData(requireActivity(), getString(R.string.listOfCustomer_CustomerFragment), "");
        if (!data.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CustomerList_Model>>() {
            }.getType();
            models = gson.fromJson(data, type);
        }
        return models;
    }

    private void saveData( ArrayList<CustomerList_Model> models ) {
        Gson gson = new Gson();
        String data = gson.toJson(models);
        sharePref.savedData(requireActivity(), getString(R.string.listOfCustomer_CustomerFragment), data);
    }

    @Override
    public void onEditBtnClick( int position ) {
        addCustomer_btnClick(position);
    }

    @Override
    public void onRelativeLongClick( int position ) {

        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.simpledelete_dialog);
        dialog.show();
        ImageView close_ic = dialog.findViewById(R.id.closeIc_simpleDeleteD);
        Button yesBtn = dialog.findViewById(R.id.yesBtn_simpleDeleteD),
                cancelBtn = dialog.findViewById(R.id.cancelBtn_simpleDeleteD);

        cancelBtn.setOnClickListener(view -> dialog.cancel());
        close_ic.setOnClickListener(view -> dialog.cancel());

        yesBtn.setOnClickListener(view -> {
            ArrayList<CustomerList_Model> customerList_models = view_model.customerList_ModelView.getValue();
            if (customerList_models != null) {
                customerList_models.remove(position);
            }
            view_model.setCustomerList_ModelView(customerList_models);
            view_model.setCurrentCustomerSelectedPosition(-1);
            dialog.cancel();
        });

    }

    public interface CustomerNameF_interface {
        void closeCustomerName_fragment( int position );
    }
}

