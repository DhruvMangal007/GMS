package com.example.garbagemanagementsystem;

import android.Manifest;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AddLocationFragment extends Fragment {

    FloatingActionButton floatingActionButton;
    ArrayList<String> address, tag;
    ArrayList<String> address_for_delete;
    ArrayAdapter<String> arrayAdapter;
    ListView locationListView;
    TextView emptyTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
       super.onRequestPermissionsResult(requestCode,permissions,grantResults);
       if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
           startActivity(new Intent(getActivity(), MapsActivity.class));
       }
    }

    public void updateListView(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Locations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address.clear();
                tag.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals("Address")) {
                            address.add((String) snapshot1.getValue().toString());
                        } else if (snapshot1.getKey().equals("Tag")) {
                            tag.add((String) snapshot1.getValue().toString());
                        }
                    }
                }
                arrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()),android.R.layout.simple_list_item_2,android.R.id.text1, tag){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        ViewGroup.LayoutParams params = view.getLayoutParams();
                        params.height = 170;
                        view.setLayoutParams(params);

                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        text1.setText(tag.get(position));
                        text2.setText(address.get(position));
                        text1.setTextSize(20);
                        return view;
                    }
                };
                locationListView.setAdapter(arrayAdapter);
                if(address.size()==0){
                    emptyTextView.setVisibility(TextView.VISIBLE);
                } else {
                    emptyTextView.setVisibility(TextView.INVISIBLE);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteaddress(final String address_to_delete){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Locations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals("Address"))  {
                            if(snapshot1.getValue().equals(address_to_delete)){
                                snapshot.getRef().removeValue();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_location,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationListView = getView().findViewById(R.id.locationListView);
        address = new ArrayList<String>();
        tag = new ArrayList<String>();
        address_for_delete = new ArrayList<String>();
        emptyTextView = getView().findViewById(R.id.empty);

        updateListView();

        floatingActionButton = getView().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                } else {
                    startActivity(new Intent(getActivity(), MapsActivity.class));
                }
            }
        });

        locationListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        locationListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(checked==true) {
                    address_for_delete.add(address.get(position));
                    locationListView.getChildAt(position).setBackgroundColor(Color.GRAY);
                } else {
                    address_for_delete.remove(address.get(position));
                    locationListView.getChildAt(position).setBackgroundColor(Color.BLACK);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.my_context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_id:
                        for(String address : address_for_delete){
                            deleteaddress(address);
                        }
                        updateListView();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                int num = locationListView.getChildCount();
                for(int i=0;i<num;i++){
                    locationListView.getChildAt(i).setBackgroundColor(Color.BLACK);
                }
            }
        });
    }
}
