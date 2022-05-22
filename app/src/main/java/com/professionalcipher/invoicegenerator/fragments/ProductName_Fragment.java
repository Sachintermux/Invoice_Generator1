package com.professionalcipher.invoicegenerator.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.professionalcipher.invoicegenerator.R;
import com.professionalcipher.invoicegenerator.adapters.ProductShow_RecyclerViewAdapter;
import com.professionalcipher.invoicegenerator.datamodels.ProductList_Model;
import com.professionalcipher.invoicegenerator.savedata.SaveDataInSharePref;
import com.professionalcipher.invoicegenerator.viewmodels.MakeQuotationView_Model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


public class ProductName_Fragment extends Fragment implements ProductShow_RecyclerViewAdapter.RecyclerVClick_InterfaceShowProductF {

    private String price_str = "", quantity_str = "", discount_str = "", tax_str = "";
    private Button addItem_btn,addColumns_btn;
    private RecyclerView recyclerView;
    private ArrayList<ProductList_Model> productList_models = new ArrayList<>();
    private MakeQuotationView_Model view_model;
    private ProductShow_RecyclerViewAdapter adapter;
    private SaveDataInSharePref saveDataInSharePref = new SaveDataInSharePref();
    private ProductNameF_interface productNameF_interface;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.fragment_product_name_, container, false);

        initView(view);
        viewModelObservers();
        allClickHandle();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void viewModelObservers() {
        view_model.productList_ModelView.observe(requireActivity(), value -> {
            productList_models = value;
            if (adapter != null) adapter.notifyDataSetChanged();
        });
    }

    private void allClickHandle() {
        addItem_btn.setOnClickListener(view -> addProductBtn_click(-1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addColumns_btn.setOnClickListener(view -> addColumnsBtn_click());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addColumnsBtn_click() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.add_item_columns_dialog);
        dialog.show();
        CheckBox discount_checkB = dialog.findViewById(R.id.discount_addItemColumnsD),
                tax_checkB = dialog.findViewById(R.id.taxCheckBox_addItemColumnsD),
                itemCode_checkB = dialog.findViewById(R.id.itemCodeCheckBox_addItemColumnsD);
        Button close_btn = dialog.findViewById(R.id.closeBtn_addItemColumnsD),
                apply_btn = dialog.findViewById(R.id.apply_addItemColumnsD);

        HashMap<String,Boolean> checkIsClick = new HashMap<>();
        checkIsClick = view_model.isCheckBoxChecked_productF.getValue();
        discount_checkB.setChecked(checkIsClick.getOrDefault("Discount",false));
        tax_checkB.setChecked(checkIsClick.getOrDefault("Tax",false));
        itemCode_checkB.setChecked(checkIsClick.getOrDefault("ItemCode",false));
        close_btn.setOnClickListener(view -> dialog.cancel());

        HashMap<String, Boolean> finalCheckIsClick = checkIsClick;
        apply_btn.setOnClickListener(view -> {
            finalCheckIsClick.put("Discount",discount_checkB.isChecked());
            finalCheckIsClick.put("Tax",tax_checkB.isChecked());
            finalCheckIsClick.put("ItemCode",itemCode_checkB.isChecked());
            view_model.setIsCheckBoxChecked_productF(finalCheckIsClick);
            dialog.cancel();
                });

    }

    private void setAdapterToRecyclerView( ArrayList<ProductList_Model> models ) {
        adapter = new ProductShow_RecyclerViewAdapter(requireActivity(), models, view_model, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void addProductBtn_click( int position ) {
        String[] nameList = {"Choose *", "Quantity", "Set","Pcs", "Other"};
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.add_product_dialog);
        dialog.show();
        EditText productName = dialog.findViewById(R.id.productName_edt_addProductD),
                quantity = dialog.findViewById(R.id.quantity_edt_addProductD),
                type = dialog.findViewById(R.id.enterType_edt_addProductD),
                tax = dialog.findViewById(R.id.tax_edt_addProductD),
                price = dialog.findViewById(R.id.price_edt_addProductD),
                discount = dialog.findViewById(R.id.discount_edt_addProductD),
        itemCode = dialog.findViewById(R.id.itemCode_edt_addProductD);

        type.setVisibility(View.GONE);

        TextView discountPrice = dialog.findViewById(R.id.showDiscountPrice_txt_addProductD),
                taxPrice = dialog.findViewById(R.id.showTax_txt_addProductD),
                amountFinalPrice = dialog.findViewById(R.id.showFinalPrice_txt_addProductD);

        Button addProductBtn = dialog.findViewById(R.id.addProduct_Btn_addProductD);

        ImageView closeBtn = dialog.findViewById(R.id.closeIc_addProductD);

        Spinner chooseType_Spinner = dialog.findViewById(R.id.showType_spinner_addProductD);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseType_Spinner.setAdapter(adapter);
        chooseType_Spinner.setSelection(3);

        if (position != -1) {
            productName.setText(productList_models.get(position).getProductName());
            quantity.setText(productList_models.get(position).getQuantity());
            price.setText(productList_models.get(position).getPrice());
            tax.setText(productList_models.get(position).getTax());
            discount.setText(productList_models.get(position).getDiscount());
            discountPrice.setText(productList_models.get(position).getDiscount_price());
            taxPrice.setText(productList_models.get(position).getTax_price());
            itemCode.setText(productList_models.get(position).getItemCode());
            addProductBtn.setText("Update");

            price_str = price.getText().toString();
            discount_str = discount.getText().toString();
            tax_str = taxPrice.getText().toString();
            quantity_str = quantity.getText().toString();

            amountFinalPrice.setText(calculateAmount(productList_models.get(position).getPrice(), productList_models.get(position).getQuantity(),
                    productList_models.get(position).getDiscount(), productList_models.get(position).getTax(), taxPrice, discountPrice));

            type.setVisibility(View.GONE);
            chooseType_Spinner.setVisibility(View.VISIBLE);
            String tempType = productList_models.get(position).getType();
            switch (tempType) {
                case "Choose *":
                    chooseType_Spinner.setSelection(0);
                    break;
                case "Quantity":
                    chooseType_Spinner.setSelection(1);
                    break;
                case "Set":
                    chooseType_Spinner.setSelection(2);
                    break;
                    case "Pcs":
                    chooseType_Spinner.setSelection(3);
                    break;
                default:
                    type.setVisibility(View.VISIBLE);
                    chooseType_Spinner.setVisibility(View.GONE);
                    chooseType_Spinner.setSelection(3);
                    type.setText(productList_models.get(position).getType());
                    break;
            }
        } else {
            addProductBtn.setText("Add");
            price_str = "";
            discount_str = "";
            tax_str = "";
            quantity_str = "";
        }
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                if (isEmptyEditText(productName, "Enter the Product Name") ||
                        isEmptyEditText(price, "Enter the Price") || isEmptyEditText(quantity, "Enter the Product Quantity"))
                    return;


                if (chooseType_Spinner.getSelectedItemPosition() == 0 || (type.getText().toString().length() == 0 && chooseType_Spinner.getVisibility() == View.GONE)) {
                    Snackbar snackbar = Snackbar.make(dialog.findViewById(R.id.relativeLayout_addProductDialog), "Please Select Quantity Type", Snackbar.LENGTH_LONG);
                    if (chooseType_Spinner.getVisibility() == View.VISIBLE)
                        snackbar.show();
                    if (chooseType_Spinner.getVisibility() == View.GONE && type.getText().toString().length() == 0)
                        type.setError("Enter Type");
                    return;
                }

                String type_str = "";
                if (chooseType_Spinner.getVisibility() == View.VISIBLE)
                    type_str = nameList[chooseType_Spinner.getSelectedItemPosition()];
                else type_str = type.getText().toString();

                ProductList_Model model = new ProductList_Model(productName.getText().toString(), price.getText().toString(), quantity.getText().toString(),
                        discount.getText().toString(), tax.getText().toString(), type_str, taxPrice.getText().toString(), discountPrice.getText().toString(),
                        amountFinalPrice.getText().toString(),itemCode.getText().toString());
                if (position == -1)
                    productList_models.add(model);
                else {
                    productList_models.remove(position);
                    productList_models.add(position, model);
                    tax_str = "";
                    discount_str = "";
                }
                view_model.setProductList_ModelView(productList_models);
                dialog.cancel();
            }
        });


        closeBtn.setOnClickListener(view -> dialog.cancel());


        chooseType_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
                if (i == 4) {
                    chooseType_Spinner.setVisibility(View.GONE);
                    type.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected( AdapterView<?> adapterView ) {

            }
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
            }

            @Override
            public void onTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
                price_str = charSequence.toString();
                amountFinalPrice.setText(calculateAmount(price_str, quantity_str, discount_str, tax_str, taxPrice, discountPrice));
            }

            @Override
            public void afterTextChanged( Editable editable ) {
            }
        });


        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {

            }

            @Override
            public void onTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
                quantity_str = charSequence.toString();
                amountFinalPrice.setText(calculateAmount(price_str, quantity_str, discount_str, tax_str, taxPrice, discountPrice));
            }

            @Override
            public void afterTextChanged( Editable editable ) {

            }
        });

        discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {

            }

            @Override
            public void onTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
                discount_str = charSequence.toString();
                if (charSequence.toString().length() == 0) discountPrice.setText("0.00");
                amountFinalPrice.setText(calculateAmount(price_str, quantity_str, discount_str, tax_str, taxPrice, discountPrice));
            }

            @Override
            public void afterTextChanged( Editable editable ) {

            }
        });

        tax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {

            }

            @Override
            public void onTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
                tax_str = charSequence.toString();
                if (charSequence.toString().length() == 0) taxPrice.setText("0.00");
                amountFinalPrice.setText(calculateAmount(price_str, quantity_str, discount_str, tax_str, taxPrice, discountPrice));
            }

            @Override
            public void afterTextChanged( Editable editable ) {

            }
        });

    }

    @SuppressLint("DefaultLocale")
    private CharSequence calculateAmount( String price_txt, String quantity_txt, String discount_txt,
                                          String tax_txt, TextView taxPrice, TextView discountPrice ) {
        double finalAmount;
        try {
            if (price_txt.length() == 0) return "Amount : 0";
            if (quantity_txt.length() == 0) return "Amount : 0";
            Double price = Double.parseDouble(price_txt);
            Double quantity = Double.parseDouble(quantity_txt);
            finalAmount = price * quantity;

            double discount;
            double tax;

            if (discount_txt.length() != 0) {
                discount = Double.parseDouble(discount_txt);
                double temAmountDiscount = finalAmount / 100 * discount;
                discountPrice.setText(String.format("%.2f", temAmountDiscount));
                finalAmount -= temAmountDiscount;
            }

            if (tax_txt.length() != 0) {
                tax = Double.parseDouble(tax_txt);
                double temAmountTax = finalAmount / 100 * tax;
                taxPrice.setText(String.format("%.2f", temAmountTax));
                finalAmount += temAmountTax;
            }

        } catch (Exception mismatchException) {
            return "Amount : 0";
        }

        return "Amount : " + String.format("%.2f", finalAmount) + " INR";
    }

    private boolean isEmptyEditText( EditText editText, String errorMsg ) {
        if (editText.getText().toString().length() == 0) {
            editText.setError(errorMsg);
            return true;
        }
        return false;
    }

    private void initView( View view ) {
        productNameF_interface = (ProductNameF_interface) requireContext();
        addItem_btn = view.findViewById(R.id.addItem_btn_ProductF);
        recyclerView = view.findViewById(R.id.recyclerView_showProductList);
        addColumns_btn = view.findViewById(R.id.addColumn_btn_ProductF);

        view_model = new ViewModelProvider(requireActivity()).get(MakeQuotationView_Model.class);
    }

    private void saveData( ArrayList<ProductList_Model> modelArrayList ) {
        Gson gson = new Gson();
        String data = gson.toJson(modelArrayList);
        saveDataInSharePref.savedData(requireActivity(), getString(R.string.listOfProduct_ProductFragment), data);
    }

    private ArrayList<ProductList_Model> getSaveData() {
        ArrayList<ProductList_Model> productList_models = new ArrayList<>();
        String data = saveDataInSharePref.getData(requireActivity(), getString(R.string.listOfProduct_ProductFragment), "");
        if (!data.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ProductList_Model>>() {
            }.getType();
            productList_models = gson.fromJson(data, type);
        }
        return productList_models;
    }

    @Override
    public void onStart() {
        super.onStart();
        productList_models = getSaveData();
        view_model.setProductList_ModelView(productList_models);
        setAdapterToRecyclerView(productList_models);

    }

    @Override
    public void onPause() {
        saveData(productList_models);
        super.onPause();
    }

    @Override
    public void onEditBtnClick( int position ) {
        addProductBtn_click(position);

    }

    @Override
    public void onRelativeLongClick( int position ) {

        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.simpledelete_dialog);
        dialog.show();

        dialog.findViewById(R.id.closeIc_simpleDeleteD).setOnClickListener(view -> dialog.cancel());

        dialog.findViewById(R.id.cancelBtn_simpleDeleteD).setOnClickListener(view -> dialog.cancel());

        dialog.findViewById(R.id.yesBtn_simpleDeleteD).setOnClickListener(view -> {
            ArrayList<String> selectItem = view_model.selectedProductList.getValue();
            if (selectItem.contains(String.valueOf(position))) {
                selectItem.remove(String.valueOf(position));
                view_model.setSelectedProductList(selectItem);
            }
            productList_models.remove(position);
            view_model.setProductList_ModelView(productList_models);
            dialog.cancel();
        });

    }

    private void showDialog_ErrorFinder( String title, String message, int position ) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int which ) {
                        productNameF_interface.closeProductName_fragment(position);
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view_model.companyFragment_DATA.getValue().size() == 0)
            showDialog_ErrorFinder("Add Company", "Please Add Your Company or Shop detail", 0);
        else if (view_model.currentCustomerSelectedPosition.getValue() == -1)
            showDialog_ErrorFinder("Add Customer", "Please Add Customer detail or Select Customer", 1);

    }

    public interface ProductNameF_interface {
        void closeProductName_fragment( int position );
    }
}