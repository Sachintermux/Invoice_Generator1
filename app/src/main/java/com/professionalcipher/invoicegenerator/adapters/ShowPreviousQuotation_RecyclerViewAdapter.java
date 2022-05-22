package com.professionalcipher.invoicegenerator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.professionalcipher.invoicegenerator.R;

import java.util.ArrayList;

public class ShowPreviousQuotation_RecyclerViewAdapter extends RecyclerView.Adapter<ShowPreviousQuotation_RecyclerViewAdapter.viewHolder> {
    private ArrayList<String> listOfFileName = new ArrayList<>();
    private Context context;
    private ShowPreviousListQ_interFace showPreviousListQ_interFace;

    public ShowPreviousQuotation_RecyclerViewAdapter( Context context, ArrayList<String> listOfFileName ) {
        this.listOfFileName = listOfFileName;
        this.context = context;
        showPreviousListQ_interFace = (ShowPreviousListQ_interFace) context;
    }

    @NonNull
    @Override
    public ShowPreviousQuotation_RecyclerViewAdapter.viewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showpreviouslistquotation_recyclerview, parent, false);
        return new ShowPreviousQuotation_RecyclerViewAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder( @NonNull ShowPreviousQuotation_RecyclerViewAdapter.viewHolder holder, int position ) {

        holder.serialNo_txt.setText(String.valueOf(position + 1));
        holder.fileName_txt.setText(listOfFileName.get(position));

        holder.delete_imgView.setOnClickListener(view ->
                {
                    showPreviousListQ_interFace.deleteBtnClick(position);
                }
        );

        holder.fileName_txt.setOnClickListener(view -> {
            showPreviousListQ_interFace.fileNameClick(position);
        });

    }

    @Override
    public int getItemCount() {
        return listOfFileName.size();
    }

    public interface ShowPreviousListQ_interFace {
        void deleteBtnClick( int position );

        void fileNameClick( int position );
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView serialNo_txt, fileName_txt;
        ImageView delete_imgView;

        public viewHolder( @NonNull View itemView ) {
            super(itemView);
            serialNo_txt = itemView.findViewById(R.id.serialMo_txtView_showPreviousListQ);
            fileName_txt = itemView.findViewById(R.id.fileName_txtView_showPreviousListQ);
            delete_imgView = itemView.findViewById(R.id.delete_imgView_showPreviousListQ);
        }
    }
}
