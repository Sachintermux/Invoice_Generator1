package com.professionalcipher.invoice.adapters;

import android.annotation.SuppressLint;
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
import com.professionalcipher.invoice.datamodels.ProductList_Model;
import com.professionalcipher.invoice.fragments.ProductName_Fragment;
import com.professionalcipher.invoice.viewmodels.MakeQuotationView_Model;

import java.util.ArrayList;

public class ProductShow_RecyclerViewAdapter extends RecyclerView.Adapter<ProductShow_RecyclerViewAdapter.viewHolder> {

    private ArrayList<ProductList_Model> productList_models = new ArrayList<>();
    private ArrayList<String> selectedProductList = new ArrayList<>();
    private LifecycleOwner lifecycleOwner;
    private MakeQuotationView_Model view_model;
    private RecyclerVClick_InterfaceShowProductF listener;

    public ProductShow_RecyclerViewAdapter( LifecycleOwner lifecycleOwner, ArrayList<ProductList_Model> productList_models, MakeQuotationView_Model view_model, ProductName_Fragment listener ) {
        this.productList_models = productList_models;
        this.lifecycleOwner = (LifecycleOwner) lifecycleOwner;
        this.view_model = view_model;
        view_model.selectedProductList.observe(this.lifecycleOwner, value -> {
            selectedProductList = value;
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showproduct_recyclerview, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder( @NonNull viewHolder holder, int position ) {
        ProductList_Model model = productList_models.get(position);

        holder.productName_txt.setText(Html.fromHtml(textMaker(model.getProductName())));
        holder.productPrice_txt.setText(Html.fromHtml(textMaker("PRICE : " + model.getPrice())));
        holder.productQuantity_txt.setText(Html.fromHtml(textMaker("   QUANTITY : " + model.getQuantity() + " " + model.getType())));
        holder.productDiscount_txt.setText(Html.fromHtml(textMaker("DISCOUNT : " + model.getDiscount_price() + " INR")));
        holder.productTax_txt.setText(Html.fromHtml(textMaker("TAX/GST : " + model.getTax() + "% (" + model.getTax_price() + " INR)")));

        if (model.getTax().toString().length() == 0)  holder.productTax_txt.setText(Html.fromHtml(textMaker("TAX/GST : " + model.getTax() + "0% (" + model.getTax_price() + " INR)")));


        holder.productList_relativeLayout.setOnLongClickListener(view -> {
            listener.onRelativeLongClick(position);
            return true;
        });
        holder.productCheckBox.setChecked(selectedProductList.contains(String.valueOf(position)));

        holder.productList_relativeLayout.setOnClickListener(view -> {
            if (holder.productCheckBox.isChecked())
                selectedProductList.remove(String.valueOf(position));
            else selectedProductList.add(String.valueOf(position));

            view_model.setSelectedProductList(selectedProductList);
            notifyDataSetChanged();
        });

        holder.productEditImageView.setOnClickListener(view -> listener.onEditBtnClick(position));

    }

    @Override
    public int getItemCount() {
        return productList_models.size();
    }

    public interface RecyclerVClick_InterfaceShowProductF {
        void onEditBtnClick( int position );

        void onRelativeLongClick( int position );
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView productName_txt, productPrice_txt, productQuantity_txt, productDiscount_txt, productTax_txt;
        CheckBox productCheckBox;
        ImageView productEditImageView;
        RelativeLayout productList_relativeLayout;

        public viewHolder( @NonNull View itemView ) {
            super(itemView);

            productName_txt = itemView.findViewById(R.id.productName_txt_showProductRecView);
            productPrice_txt = itemView.findViewById(R.id.productPrice_txt_showProductRecView);
            productQuantity_txt = itemView.findViewById(R.id.productQuantity_txt_showProductRecView);
            productDiscount_txt = itemView.findViewById(R.id.productDiscount_txt_showProductRecView);
            productTax_txt = itemView.findViewById(R.id.productTax_txt_showProductRecView);

            productCheckBox = itemView.findViewById(R.id.checkbox_showProductRecView);

            productEditImageView = itemView.findViewById(R.id.editIc_showProductRecView);

            productList_relativeLayout = itemView.findViewById(R.id.relativeLayout_showProductListRecView);

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
