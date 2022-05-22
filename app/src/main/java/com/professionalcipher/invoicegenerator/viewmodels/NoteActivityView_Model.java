package com.professionalcipher.invoicegenerator.viewmodels;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.nio.channels.MulticastChannel;

public class NoteActivityView_Model extends ViewModel {

    public MutableLiveData<Integer> currentSelectedNote_Position = new MutableLiveData<>(-1);

    public void setCurrentSelectedNote_Position(int position){
        currentSelectedNote_Position.setValue(position);
    }
}
