package com.professionalcipher.invoicegenerator.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.professionalcipher.invoicegenerator.R;
import com.professionalcipher.invoicegenerator.adapters.NoteShow_RecyclerViewAdapter;
import com.professionalcipher.invoicegenerator.savedata.SaveDataInSharePref;
import com.professionalcipher.invoicegenerator.viewmodels.MakeQuotationView_Model;
import com.professionalcipher.invoicegenerator.viewmodels.NoteActivityView_Model;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class Note_Fragment extends Fragment implements NoteShow_RecyclerViewAdapter.NoteShowRec_Interface {

    private RecyclerView recyclerView;
    private Button addNote_btn;
    private ArrayList<String> noteList = new ArrayList<>();
    private MakeQuotationView_Model view_model = new MakeQuotationView_Model();
    private NoteShow_RecyclerViewAdapter adapter;
    private SaveDataInSharePref sharePref = new SaveDataInSharePref();
    private EditText receivedAmount_edt;
    private NoteActivityView_Model view_modelNote = new NoteActivityView_Model();

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_, container, false);
        initView(view);
        allClickHandle();
        return view;
    }

    private void allClickHandle() {
        addNote_btn.setOnClickListener(view -> addNote_btnClick(-1));
    }

    private void addNote_btnClick( int position ) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.add_note_dialog);
        dialog.show();

        ImageView closeImv = dialog.findViewById(R.id.closeIc_addNoteD);
        closeImv.setOnClickListener(view -> dialog.cancel());
        EditText note_txt = dialog.findViewById(R.id.note_edt_noteD);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn_noteD),
                addBtn = dialog.findViewById(R.id.addBtn_noteD);

        cancelBtn.setOnClickListener(view -> dialog.cancel());
        if (position != -1) {
            addBtn.setText("update");
            note_txt.setText(noteList.get(position));
        }
        addBtn.setOnClickListener(view -> {
            if (note_txt.getText().length() == 0) {
                note_txt.setError("Enter Some Text");
                return;
            }
            if (position == -1) {
                noteList.add(note_txt.getText().toString());
            } else {
                noteList.remove(position);
                noteList.add(position, note_txt.getText().toString());
            }
            dialog.cancel();
            setRecyclerView();

        });
    }

    private void initView( View view ) {
        recyclerView = view.findViewById(R.id.recyclerView_showNoteList);
        addNote_btn = view.findViewById(R.id.addNoteBtn_NoteFragment);
        receivedAmount_edt = view.findViewById(R.id.receivedAmount_edt_addNoteFragment);
        view_modelNote = new ViewModelProvider(requireActivity()).get(NoteActivityView_Model.class);
        view_model = new ViewModelProvider(requireActivity()).get(MakeQuotationView_Model.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        String data = sharePref.getData(requireContext(), getString(R.string.noteList_noteFragment), "");
        if (data.length() != 0) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            noteList = gson.fromJson(data, type);
            setRecyclerView();
        }

    }

    @Override
    public void onStop() {
        Gson gson = new Gson();
        String data = gson.toJson(noteList);
        sharePref.savedData(requireContext(), getString(R.string.noteList_noteFragment), data);
        super.onStop();
    }

    @Override
    public void onPause() {
        if(receivedAmount_edt.getText().length()!=0)
        view_model.setReceivedAmount(Double.parseDouble(receivedAmount_edt.getText().toString()));
        if(view_modelNote.currentSelectedNote_Position.getValue()!=-1){
            view_model.setSelectedNote(noteList.get(view_modelNote.currentSelectedNote_Position.getValue()));
        }else  view_model.setSelectedNote("");
        super.onPause();
    }

    private void setRecyclerView() {
        adapter = new NoteShow_RecyclerViewAdapter(noteList, view_modelNote, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void editImgVClick( int position ) {
        addNote_btnClick(position);
    }

    @Override
    public void layoutLongClick( int position ) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.simpledelete_dialog);
        dialog.show();
        Button ok = dialog.findViewById(R.id.yesBtn_simpleDeleteD),
                cancel = dialog.findViewById(R.id.cancelBtn_simpleDeleteD);

        ImageView closeIc = dialog.findViewById(R.id.closeIc_simpleDeleteD);

        cancel.setOnClickListener(view -> dialog.cancel());
        closeIc.setOnClickListener(view -> dialog.cancel());
        ok.setOnClickListener(view -> {
            noteList.remove(noteList.get(position));
            adapter.notifyDataSetChanged();
            dialog.cancel();
        });
    }
}