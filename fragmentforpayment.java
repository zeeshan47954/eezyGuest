package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class fragmentforpayment extends Fragment implements RefreshListener {
    String number[];
    int lm;
    int ll;
long x;

    private long finalIndex=0;
long index=0;
    List<String> information=new ArrayList<>();
    List<String> transfer=new ArrayList<>();
    List<String> order=new ArrayList<>();
    List <String> transactiondate=new ArrayList<>();
    List <String> transactiontime=new ArrayList<>();
    List <String> payment=new ArrayList<>();
    private RecyclerView rv;
    private String name;
    private String id;
    private  DatabaseReference dbrroom;

    interface Listenerhomepayment {
        void itemclicked2inpayment(int position, long s, String k);
    }
    interface  Listenerhomepayment2{
        void statuschecking(int id, long s, String k,String order,String date,String time,String paymentid,String transfer);
    }

    Listenerhomepayment2 listenerforstatus;
    @Override
    public void onRefresh() {
        Toast.makeText(getContext(), "refreshing payments", Toast.LENGTH_SHORT).show();

        index = 0;
        x=0;
        // Reset the index counter
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged(); // Notify adapter
        }
        pb.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);


        // Call load() to fetch and display fresh data
    }


    private Listenerhomepayment listenerhome1;

ProgressBar pb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view=  inflater.inflate(R.layout.fragment_fragmentforpayment, container, false);
        rv=view.findViewById(R.id.rv);
        pb=view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
         GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        if (account != null) {
             name = account.getDisplayName();
             id = account.getId();

             loaddata(name,id);

        }

        return view;
    }

    public void loaddata(String name,String id)
    {            DatabaseReference dbrfff = FirebaseDatabase.getInstance()
            .getReference("google").child(name).child(id).child("owner").child("transactionsrecievedno");

        dbrfff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long transactionCount = snapshot.getChildrenCount();
                x=transactionCount;
                if(transactionCount==0)
                {

                    rv.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    AdapterForemptyvalue adapter = new AdapterForemptyvalue(8);
                    rv.setAdapter(adapter);
                    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                    rv.setLayoutManager(glm);
                }
                else{

                    for(DataSnapshot chilled:snapshot.getChildren())
                    {finalIndex++;

                        String roomid = chilled.child("idss").getValue(String.class);
                        long transactionNo1 = chilled.child("transactionno").getValue(long.class);
                        String status=chilled.child("status").getValue(String.class);
                        String orderid=chilled.child("orderid").getValue(String.class);
                        String transferid=chilled.child("transferid").getValue(String.class);
                        String date=chilled.child("transactionDate").getValue(String.class);
                        String time=chilled.child("transactionTime").getValue(String.class);
                        String tranid=chilled.child("transactionid").getValue(String.class);
                        order.add(orderid);
                        transactiondate.add(date);
                        transactiontime.add(time);
                        payment.add(tranid);
                        transfer.add(transferid);

                        information.add(roomid+"transactionnumberstartshere"+transactionNo1+"status"+status);

                        if(finalIndex==x)
                        {


                            String[] informationArray = information.toArray(new String[0]);
                            String[] orderarray = order.toArray(new String[0]);
                            String[] transactiondatearray = transactiondate.toArray(new String[0]);
                            String[] transactiontimearray = transactiontime.toArray(new String[0]);
                            String[] paymentarray = payment.toArray(new String[0]);
                            String[] transferarray = transfer.toArray(new String[0]);



                            rv.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);

                            AdapterForOwnerpart88 adapter = new AdapterForOwnerpart88(informationArray,orderarray,transactiondatearray,transactiontimearray,paymentarray,transferarray);
                            rv.setAdapter(adapter);
                            GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                            rv.setLayoutManager(glm);

                            adapter.setListener(new AdapterForOwnerpart88.Listener1() {
                                @Override
                                public void iteminadapterforowneroccupiedforpaymentclicked(int position, long s, String k) {
                                    if (listenerhome1 != null) {
                                        listenerhome1.itemclicked2inpayment(position, s, k);
                                    }
                                }
                            }, new AdapterForOwnerpart88.Listener2() {
                                @Override
                                public void clickforstatuscheck(int id, long s, String k,String order,String date,String time,String paymentid,String transfer) {
                                    if(listenerforstatus!=null)
                                    {
                                        listenerforstatus.statuschecking(id,s,k,order,date,time,paymentid,transfer);

                                    }


                                }
                            });


                        }


                    }





                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listenerhome1 = (Listenerhomepayment) context;
        listenerforstatus=(Listenerhomepayment2) context;
    }
}
