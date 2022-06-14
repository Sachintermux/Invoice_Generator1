package com.professionalcipher.invoice.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.professionalcipher.invoice.R;
import com.professionalcipher.invoice.datamodels.CustomerList_Model;
import com.professionalcipher.invoice.fragments.CustomerName_Fragment;
import com.professionalcipher.invoice.viewmodels.MakeQuotationView_Model;

import java.util.ArrayList;

public class CustomerShow_RecyclerViewAdapter extends RecyclerView.Adapter<CustomerShow_RecyclerViewAdapter.viewHolder> {
    private ArrayList<CustomerList_Model> customerList_models = new ArrayList<>();
    private Context context;
    private int currentSelectedCustomerPosition = -1;
    private MakeQuotationView_Model view_model;
    private RecyclerVClick_InterfaceShowCustomerF listener;

    public CustomerShow_RecyclerViewAdapter( ArrayList<CustomerList_Model> customerList_models, Context context, MakeQuotationView_Model view_model, CustomerName_Fragment listener ) {
        this.context = context;
        this.customerList_models = customerList_models;
        this.view_model = view_model;
        this.listener = listener;
        view_model.currentCustomerSelectedPosition.observe((LifecycleOwner) context, value->currentSelectedCustomerPosition = value);
    }

    @NonNull
    @Override
    public CustomerShow_RecyclerViewAdapter.viewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showcustomer_recyclerview, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder( @NonNull CustomerShow_RecyclerViewAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position ) {
        CustomerList_Model model = customerList_models.get(position);
        holder.setIsRecyclable(false);



        holder.customerName_tv.setText(Html.fromHtml(textMaker(model.getName())));

        holder.company_tv.setText(Html.fromHtml(textMaker( model.getGstIn())));

        holder.mobile_tv.setText(Html.fromHtml(textMaker( model.getCustomerState())));
        holder.email_tv.setText(Html.fromHtml(textMaker(model.getInvoiceNo())));

        holder.address_tv.setText(Html.fromHtml(textMaker(model.getAddress() + " " + model.getInvoiceDate() + " " + model.getCompState() + "  " + model.getCodeNo())));

        holder.checkBox.setChecked(currentSelectedCustomerPosition == position);

        holder.relativeLayout.setOnClickListener(view -> {
            if (position == currentSelectedCustomerPosition) {
                currentSelectedCustomerPosition = -1;
                view_model.setCurrentCustomerSelectedPosition(-1);
            } else {
                view_model.setCurrentCustomerSelectedPosition(position);
                currentSelectedCustomerPosition = position;

            }
            notifyDataSetChanged();
        });

        holder.relativeLayout.setOnLongClickListener(view -> {
            listener.onRelativeLongClick(position);
            return false;
        });
        holder.edit_imgView.setOnClickListener(view -> {
            listener.onEditBtnClick(position);
        });
    }


    @Override
    public int getItemCount() {
        return customerList_models.size();
    }

    public interface RecyclerVClick_InterfaceShowCustomerF {
        void onEditBtnClick( int position );
        void onRelativeLongClick( int position );
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView customerName_tv, company_tv, mobile_tv, address_tv, email_tv;
        ImageView edit_imgView;
        CheckBox checkBox;
        RelativeLayout relativeLayout;

        public viewHolder( @NonNull View itemView ) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout_showCustomerRecView);

            customerName_tv = itemView.findViewById(R.id.customerName_txt_showCustomerRecView);
            company_tv = itemView.findViewById(R.id.companyName_txt_showCustomerRecView);
            mobile_tv = itemView.findViewById(R.id.mobileNo_txt_showCustomerRecView);
            address_tv = itemView.findViewById(R.id.address_txt_showCustomerRecView);
            email_tv = itemView.findViewById(R.id.email_txt_showCustomerRecView);

            edit_imgView = itemView.findViewById(R.id.editIc_showCustomerRecView);

            checkBox = itemView.findViewById(R.id.checkbox_showCustomerRecView);

        }
    }
    private String textMaker( String text ) {

        ArrayList<String> texts = new ArrayList<>();
        for(int i=0; i<text.length(); i++){
            texts.add(String.valueOf(text.charAt(i)));
        }

        StringBuilder stringBuilder = new StringBuilder(text);

        boolean isStartBold = false;
        boolean isStartItalic = false;
        boolean isStartLineThrough = false;
        boolean isStartUnderLine = false;

        int boldStartPosition = -1;
        int italicStartPosition = -1;
        int lineThroughStartPosition = -1;
        int underLineStartPosition = -1;

        for (int i = 0; i < text.length(); i++) {

            if (stringBuilder.charAt(i) == '*') {
                if (isStartBold) {
                    texts.remove(boldStartPosition);
                    texts.add(boldStartPosition,"<b>");
                    texts.remove(i);
                    texts.add(i,"</b>");
                    isStartBold = false;
                    boldStartPosition = -1;

                } else {
                    isStartBold = true;
                    boldStartPosition = i;
                }
            }

            if (stringBuilder.charAt(i) == '_') {
                if (isStartItalic) {
                    texts.remove(italicStartPosition);
                    texts.add(italicStartPosition,"<i>");
                    texts.remove(i);
                    texts.add(i,"</i>");
                    isStartItalic = false;
                    italicStartPosition = -1;
                } else {
                    isStartItalic = true;
                    italicStartPosition = i;
                }
            }

            if (stringBuilder.charAt(i) == '~') {
                if (isStartLineThrough) {
                 texts.remove(lineThroughStartPosition);
                 texts.add(lineThroughStartPosition,"<s>");
                 texts.remove(i);
                 texts.add(i,"</s>");
                    isStartLineThrough = false;
                    lineThroughStartPosition = -1;
                } else {
                    isStartLineThrough = true;
                    lineThroughStartPosition = i;
                }
            }

            if (stringBuilder.charAt(i) == '`') {
                if (isStartUnderLine) {
                 texts.remove(underLineStartPosition);
                 texts.add(underLineStartPosition,"<u>");
                 texts.remove(i);
                 texts.add(i,"</u>");
                    isStartUnderLine = false;
                    underLineStartPosition = -1;
                } else {
                    isStartUnderLine = true;
                    underLineStartPosition = i;
                }
            }
        }
String finalText = "";
        for (int i = 0; i < texts.size(); i++) {
          finalText += texts.get(i);
        }

        return finalText;
    }

}
