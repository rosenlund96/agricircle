package com.example.agricircle.project.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.project.Activities.DrawNewFieldActivity;
import com.example.agricircle.project.Activities.MainActivity;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Util.FieldAdaptor;
import com.example.agricircle.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class FieldListFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView results;
    private List<Field> fields;
    private Button newField;
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
        newField = myView.findViewById(R.id.drawnewfield);
        newField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DrawNewFieldActivity.class);


                startActivity(i);
            }
        });
        myView.setFocusableInTouchMode(true);
        myView.requestFocus();
        myView.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.article_fragment
                                    , new MapFragment())
                            .commit();
                    return true;
                }
                return false;
            }
        } );



        return myView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        MapFragment fragment2 = new MapFragment();

        Bundle bundle = new Bundle();
        Field obj = fields.get(position);
        bundle.putSerializable("Field", obj);
        fragment2.setArguments(bundle);
        ft.replace(R.id.article_fragment, fragment2);
        ft.addToBackStack(null);
        ft.commit();





        System.out.println("Field: " + fields.get(position).getDisplay_name() + " Centerpoint: " + fields.get(position).getCenterpoint().getCoordinates().get(0).longitude+","+fields.get(position).getCenterpoint().getCoordinates().get(0).latitude);
    }

    public void initialize(){
        fields = MainScreenActivity.getInstance().controller.getUser().getFields();

    }
}
