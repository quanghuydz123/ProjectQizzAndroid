package com.example.projectqizz.Fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.projectqizz.Adapter.CategoryAdapter;
import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.MainActivity;
import com.example.projectqizz.Model.CategoryModel;
import com.example.projectqizz.R;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment {

    private GridView categoryView;

    public CategoryFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Categories");
        categoryView = view.findViewById(R.id.category_Grid);
        //loadCategories();
        CategoryAdapter categoryAdapter = new CategoryAdapter(DbQuery.g_catList);
        categoryView.setAdapter(categoryAdapter);
        return view;
    }

//    private void loadCategories() {
//        categoryModelList.clear();
//        categoryModelList.add(new CategoryModel("1","GK",20));
//        categoryModelList.add(new CategoryModel("2","HISTORY",30));
//        categoryModelList.add(new CategoryModel("3","ENGLISH",10));
//        categoryModelList.add(new CategoryModel("4","SCIENCE",25));
//        categoryModelList.add(new CategoryModel("5","MATHS",20));
//
//    }
}