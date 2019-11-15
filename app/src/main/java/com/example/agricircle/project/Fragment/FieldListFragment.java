package com.example.agricircle.project.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Util.FieldAdaptor;
import com.example.agricircle.R;

import java.util.List;

public class FieldListFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView results;
    private List<Field> fields;
    View myView;

    public FieldListFragment(){
        initialize();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fieldlist_layout, container, false);
        results = (ListView) myView.findViewById(R.id.Results);
        results.setAdapter(new FieldAdaptor(getContext(),fields));
        results.setOnItemClickListener(this);



        return myView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void initialize(){
        fields = MainScreenActivity.getInstance().controller.getUser().getFields();

    }
}
