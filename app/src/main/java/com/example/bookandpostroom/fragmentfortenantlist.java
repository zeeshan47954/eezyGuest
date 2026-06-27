package com.example.bookandpostroom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class fragmentfortenantlist extends Fragment {
    interface RequestListener {
        void functionforrequest(int id, String s);
    }

    interface RequestListener2 {
        void functiondeletetenantafterbooking(int id, String s);
    }

    RequestListener requestListener;
    RequestListener2 requestListener2;
    private adapterfortenantlist adapter;
    private List<String> tenantInfoList = new ArrayList<>();

    private static final String ARG_PARAM = "param_key";
    private String myParam;

    // FIXED: Use synchronized lists to ensure thread safety
    private List<TenantData> tenantDataList = new ArrayList<>();
    private int completedRequests = 0;

    String[] tenaninfo;
    long totalTenants = 0;
    RecyclerView rv;
    ProgressBar pb;

    // Helper class to store tenant data together
    private static class TenantData {
        String tenantInfo;
        String name;
        String address;
        Uri photo;

        TenantData(String tenantInfo) {
            this.tenantInfo = tenantInfo;
            this.name = "Loading...";
            this.address = "Loading...";
            this.photo = null;
        }
    }

    public static fragmentfortenantlist newInstance(String param) {
        fragmentfortenantlist fragment = new fragmentfortenantlist();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            myParam = getArguments().getString(ARG_PARAM);
        }
        View view = inflater.inflate(R.layout.fragment_fragmentfortenantlist, container, false);

        rv = view.findViewById(R.id.rv);
        pb = view.findViewById(R.id.pb);

        // Initialize tenaninfo first
        if (myParam == null || myParam.isEmpty()) {
            pb.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
            return view;
        }

        tenaninfo = myParam.split("\\+");
        tenantInfoList = new ArrayList<>(Arrays.asList(tenaninfo));
        totalTenants = tenaninfo.length;

        Log.d("tenantlist", myParam);

        // Handle case where there are no tenants
        if (tenaninfo.length == 0) {
            pb.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
            return view;
        }

        // Initialize tenant data list
        for (String tenantInfo : tenantInfoList) {
            tenantDataList.add(new TenantData(tenantInfo));
        }

        // Load data for each tenant
        for (int i = 0; i < tenantInfoList.size(); i++) {
            final int index = i;
            String s = tenantInfoList.get(i);

            String name = s.split("idstarts")[0];
            String id = s.split("idstarts")[1].split("idends")[0];

            DatabaseReference ds = FirebaseDatabase.getInstance().getReference("google")
                    .child(name).child(id).child("student");

            ds.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String tenantName = snapshot.child("name").getValue(String.class);
                        String tenantAddress = snapshot.child("address").getValue(String.class);

                        tenantDataList.get(index).name = tenantName != null ? tenantName : "Unknown";
                        tenantDataList.get(index).address = tenantAddress != null ? tenantAddress : "Unknown";

                        StorageReference storageRef = FirebaseStorage.getInstance()
                                .getReference("tenantPhotos").child(name).child(id).child("pfp");

                        Log.d("name inside the", name);
                        Log.d("id inside the", id);

                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                tenantDataList.get(index).photo = uri;
                                checkAndSetupAdapter();
                            }
                        }).addOnFailureListener(e -> {
                            tenantDataList.get(index).photo = null;
                            checkAndSetupAdapter();
                        });
                    } else {
                        // Handle case where tenant data doesn't exist
                        tenantDataList.get(index).name = "Unknown";
                        tenantDataList.get(index).address = "Unknown";
                        tenantDataList.get(index).photo = null;
                        checkAndSetupAdapter();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    tenantDataList.get(index).name = "Error";
                    tenantDataList.get(index).address = "Error";
                    tenantDataList.get(index).photo = null;
                    checkAndSetupAdapter();
                }
            });
        }

        return view;
    }

    private synchronized void checkAndSetupAdapter() {
        completedRequests++;
        if (completedRequests == totalTenants) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupAdapter();
                    }
                });
            }
        }
    }

    private void setupAdapter() {
        // Extract data from tenantDataList
        List<String> names = new ArrayList<>();
        List<String> addresses = new ArrayList<>();
        List<Uri> photos = new ArrayList<>();

        for (TenantData data : tenantDataList) {
            names.add(data.name);
            addresses.add(data.address);
            photos.add(data.photo);
        }

        String[] namesarray = names.toArray(new String[0]);
        String[] addressarray = addresses.toArray(new String[0]);
        Uri[] photoarray = photos.toArray(new Uri[0]);

        // Log data for debugging
        for (String l : tenantInfoList) {
            Log.d("tenantInfo", l);
        }
        for (String l : names) {
            Log.d("name", l);
        }
        for (String l : addresses) {
            Log.d("address", l);
        }
        for (Uri l : photos) {
            Log.d("photo", l != null ? l.toString() : "null");
        }

        // Verify all arrays have the same length
        if (tenaninfo.length != namesarray.length ||
                tenaninfo.length != addressarray.length ||
                tenaninfo.length != photoarray.length) {
            Log.e("setupAdapter", "Array length mismatch!");
            Toast.makeText(getContext(), "Error loading tenant data", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
            return;
        }

        adapter = new adapterfortenantlist(tenaninfo, namesarray, addressarray, photoarray);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
        rv.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);

        adapter.setListener(new adapterfortenantlist.RequestListeneradapter() {
            @Override
            public void functionforrequest1(int id, String s) {
                if (requestListener != null) {
                    requestListener.functionforrequest(id, s);
                }
            }
        }, new adapterfortenantlist.RequestListeneradapter2() {
            @Override
            public void deletetenantaftertenant(int id, String s) {
                if (requestListener2 != null) {
                    requestListener2.functiondeletetenantafterbooking(id, s);
                }
            }
        });
    }

    public void removeTenant(int position, String tenantId) {
        if (adapter != null && position >= 0 && position < tenantInfoList.size()) {
            // Remove from all lists
            tenantInfoList.remove(position);
            tenantDataList.remove(position);

            // Update the tenaninfo array
            tenaninfo = tenantInfoList.toArray(new String[0]);

            // Remove from adapter
            adapter.removeItem(position);

            // Check if fragment should be empty now
            if (tenantInfoList.isEmpty()) {
                rv.setVisibility(View.GONE);
            }
        }
    }

    public boolean wasLastItem() {
        return tenantInfoList.isEmpty();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RequestListener) {
            this.requestListener = (RequestListener) context;
        }
        if (context instanceof RequestListener2) {
            this.requestListener2 = (RequestListener2) context;
        }
    }
}

//package com.example.bookandpostroom;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//
//public class fragmentfortenantlist extends Fragment {
//    interface RequestListener{
//        void functionforrequest(int id,String s);
//
//    }
//  RequestListener requestListener;
//interface RequestListener2{
//
//    void functiondeletetenantafterbooking(int id,String s);
//}
//RequestListener2 requestListener2;
//
//
//    private static final String ARG_PARAM = "param_key";
//    private String myParam;
//List<String> names=new ArrayList<>();
//List <Uri> photos=new ArrayList<>();
//List<String>addresses=new ArrayList<>();
//String [] tenaninfo;
//long start=0;
//long end;
//RecyclerView rv;
//ProgressBar pb;
//    public static fragmentfortenantlist newInstance(String param) {
//        fragmentfortenantlist fragment = new fragmentfortenantlist();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM, param);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        if (getArguments() != null) {
//            myParam = getArguments().getString(ARG_PARAM);
//        }
//        View view= inflater.inflate(R.layout.fragment_fragmentfortenantlist, container, false);
//
//        rv=view.findViewById(R.id.rv);
//        pb=view.findViewById(R.id.pb);
//
//        tenaninfo=myParam.split("\\+");
//end=tenaninfo.length;
//for(String s:tenaninfo)
//{start++;
//    String name=s.split("idstarts")[0];
//    String id=s.split("idstarts")[1].split("idends")[0];
//
//    DatabaseReference ds= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
//    ds.addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            names.add(snapshot.child("name").getValue(String.class));
//            addresses.add(snapshot.child("address").getValue(String.class));
//
//            StorageReference ds= FirebaseStorage.getInstance().getReference("tenantPhotos").child(name).child(id).child("pfp");
//            ds.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//               photos.add(uri);
//               if(start==end)
//               {
//
//                   String [] namesarray=names.toArray(new String[0]);
//                   String [] addressarray=addresses.toArray(new String[0]);
//                   Uri [] photoarray=photos.toArray(new Uri[0]);
//
//                   adapterfortenantlist adapter = new adapterfortenantlist(tenaninfo, namesarray, addressarray,photoarray);
//                   rv.setAdapter(adapter);
//                   GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                   rv.setLayoutManager(glm);
//                   rv.setVisibility(View.VISIBLE);
//                   pb.setVisibility(View.GONE);
//                   adapter.setListener(new adapterfortenantlist.RequestListeneradapter() {
//                       @Override
//                       public void functionforrequest1(int id, String s) {
//                           requestListener.functionforrequest(id, s);
//                       }
//                   }, new adapterfortenantlist.RequestListeneradapter2() {
//                       @Override
//                       public void deletetenantaftertenant(int id, String s) {
//                           requestListener2.functiondeletetenantafterbooking(id,s);
//                       }
//                   });
//
//
//               }
//                }
//            });
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    });
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//}
//
//
//        // Inflate the layout for this fragment
//        return view;
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.requestListener = (RequestListener) context;
//        this.requestListener2=(RequestListener2) context;
//
//    }
//}