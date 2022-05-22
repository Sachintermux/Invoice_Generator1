package com.professionalcipher.invoicegenerator.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.professionalcipher.invoicegenerator.fragments.Company_Fragment;
import com.professionalcipher.invoicegenerator.fragments.CustomerName_Fragment;
import com.professionalcipher.invoicegenerator.fragments.Note_Fragment;
import com.professionalcipher.invoicegenerator.fragments.Preview_Fragment;
import com.professionalcipher.invoicegenerator.fragments.ProductName_Fragment;
import com.professionalcipher.invoicegenerator.viewmodels.MakeQuotationView_Model;

public class ViewPager_Adapter extends FragmentStateAdapter {
    MakeQuotationView_Model view_model;
    public ViewPager_Adapter( @NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle , MakeQuotationView_Model view_model ) {
        super(fragmentManager, lifecycle);
        this.view_model = view_model;
    }

    @NonNull
    @Override
    public Fragment createFragment( int position ) {
        switch (position){
            case 1:
                return new CustomerName_Fragment();
//                return new Company_Fragment();
            case 2:
//                if(Integer.parseInt(view_model.currentCustomerSelectedPosition.getValue().toString()) != -1)
                return new ProductName_Fragment();
//                return new Company_Fragment();

            case 4:
//                return new Company_Fragment();
                return new Preview_Fragment();

            case 3:
                return new Note_Fragment();
//                return new Company_Fragment();
        }
        return new Company_Fragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
