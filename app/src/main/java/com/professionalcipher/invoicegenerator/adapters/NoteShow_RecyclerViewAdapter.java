package com.professionalcipher.invoicegenerator.adapters;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.professionalcipher.invoicegenerator.R;
import com.professionalcipher.invoicegenerator.fragments.Note_Fragment;
import com.professionalcipher.invoicegenerator.viewmodels.NoteActivityView_Model;

import java.util.ArrayList;

public class NoteShow_RecyclerViewAdapter extends RecyclerView.Adapter<NoteShow_RecyclerViewAdapter.viewHolder> {
    private ArrayList<String> notes = new ArrayList<>();
    private NoteActivityView_Model view_model;
    private NoteShowRec_Interface listener;

    public NoteShow_RecyclerViewAdapter( ArrayList<String> notes, NoteActivityView_Model view_model, Note_Fragment listener ) {
        this.notes = notes;
        this.view_model = view_model;
        this.listener = (NoteShowRec_Interface) listener;
    }

    @NonNull
    @Override
    public NoteShow_RecyclerViewAdapter.viewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shownote_recyclerview, parent, false);
        return new NoteShow_RecyclerViewAdapter.viewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder( @NonNull NoteShow_RecyclerViewAdapter.viewHolder holder, int position ) {
        holder.noteTxt_view.setText(Html.fromHtml(textMaker(notes.get(position))));
        holder.noteCheckBox.setOnClickListener(view -> {
            if (!holder.noteCheckBox.isChecked()) {
                view_model.setCurrentSelectedNote_Position(-1);
            } else view_model.setCurrentSelectedNote_Position(position);
            notifyDataSetChanged();
        });

        holder.editNoteIc.setOnClickListener(view -> listener.editImgVClick(position));
        holder.linearLayoutCompat.setOnClickListener(view -> {
            if (holder.noteCheckBox.isChecked()) {
                view_model.setCurrentSelectedNote_Position(-1);
            } else {
                view_model.setCurrentSelectedNote_Position(position);
            }
            notifyDataSetChanged();
        });
        holder.linearLayoutCompat.setOnLongClickListener(view -> {
            listener.layoutLongClick(position);
            return false;
        });
        holder.noteCheckBox.setChecked(view_model.currentSelectedNote_Position.getValue() == position);

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private String textMaker( String text ) {

        ArrayList<String> texts = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
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
                    texts.add(boldStartPosition, "<b>");
                    texts.remove(i);
                    texts.add(i, "</b>");
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
                    texts.add(italicStartPosition, "<i>");
                    texts.remove(i);
                    texts.add(i, "</i>");
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
                    texts.add(lineThroughStartPosition, "<s>");
                    texts.remove(i);
                    texts.add(i, "</s>");
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
                    texts.add(underLineStartPosition, "<u>");
                    texts.remove(i);
                    texts.add(i, "</u>");
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

    public interface NoteShowRec_Interface {
        void editImgVClick( int position );

        void layoutLongClick( int position );
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView noteTxt_view;
        CheckBox noteCheckBox;
        ImageView editNoteIc;
        LinearLayout linearLayoutCompat;

        public viewHolder( @NonNull View itemView ) {
            super(itemView);
            noteTxt_view = itemView.findViewById(R.id.noteTxt_view_showNoteRecView);
            noteCheckBox = itemView.findViewById(R.id.checkbox_showNoteRecView);
            editNoteIc = itemView.findViewById(R.id.editIc_showNoteRecView);
            linearLayoutCompat = itemView.findViewById(R.id.linearLayout_showNoteRecV);
        }
    }
}
