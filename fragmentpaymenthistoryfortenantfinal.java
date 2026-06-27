package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class fragmentpaymenthistoryfortenantfinal extends Fragment {
    String number[];
    long x;
    private long finalIndex=0;
    List<String> information=new ArrayList<>();
    List<String> transfer=new ArrayList<>();
    List<String> order=new ArrayList<>();
    List <String> transactiondate=new ArrayList<>();
    List <String> transactiontime=new ArrayList<>();
    List <String> payment=new ArrayList<>();
    List <String> refundid=new ArrayList<>();

    interface Listenerhomepayment {
        void itemclickedintenantpaymenthistory(int position, long s,String k);
    }
    interface Listenerforstatus{
        void statuschecking(int position,long s,String k,String order,String date,String time,String paymentid,String transfer);
    }
    interface  Listenerforrefund{

        void refundstatus(int position,String s);
    }

    private Listenerhomepayment listenerhome1;
    Listenerforstatus listenerforstatus;
    Listenerforrefund listenerforrefund;
    private
    RecyclerView rv;
    ProgressBar pb;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fragmentforpayment, container, false);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
 rv=view.findViewById(R.id.rv);
         pb=view.findViewById(R.id.pb);
swipeRefreshLayout=view.findViewById(R.id.swipeRefresh);

        if (account != null) {
            String name = account.getDisplayName();
            String id = account.getId();

loaddata(name,id);

swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        finalIndex=0;
        x=0;
        transactiondate.clear();
        transactiontime.clear();
        payment.clear();
        order.clear();
        transfer.clear();
        information.clear();
        rv.setAdapter(null);
loaddata(name,id);
        new android.os.Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
    }
});


        }
        return view;
    }
public void loaddata(String name,String id)
{            DatabaseReference dbrfff = FirebaseDatabase.getInstance()
        .getReference("google").child(name).child(id).child("student").child("transactionmadeno");

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

                    String roomid = chilled.child("info").getValue(String.class);
                    long transactionNo1 = chilled.child("transactionno").getValue(long.class);
                    String status=chilled.child("status").getValue(String.class);
                    String orderid=chilled.child("orderid").getValue(String.class);
                    String transferid=chilled.child("transferid").getValue(String.class);
                    String date=chilled.child("transactionDate").getValue(String.class);
                    String time=chilled.child("transactionTime").getValue(String.class);
                    String tranid=chilled.child("transactionid").getValue(String.class);
                    String refid=chilled.child("refundid").getValue(String.class);

                    order.add(orderid);
                    transactiondate.add(date);
                    transactiontime.add(time);
                    payment.add(tranid);
                    transfer.add(transferid);
                    refundid.add(refid);

                    information.add(roomid+"transactionnumberstartshere"+transactionNo1+"status"+status);

                    if(finalIndex==x)
                    {


                        String[] informationArray = information.toArray(new String[0]);
                        String[] orderarray = order.toArray(new String[0]);
                        String[] transactiondatearray = transactiondate.toArray(new String[0]);
                        String[] transactiontimearray = transactiontime.toArray(new String[0]);
                        String[] paymentarray = payment.toArray(new String[0]);
                        String[] transferarray = transfer.toArray(new String[0]);
                        String[] refundidarray = refundid.toArray(new String[0]);


                        rv.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);

                        AdapterForOwnerpart8 adapter = new AdapterForOwnerpart8(informationArray,orderarray,transactiondatearray,transactiontimearray,paymentarray,transferarray,refundidarray);
                        rv.setAdapter(adapter);
                        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                        rv.setLayoutManager(glm);

                        adapter.setListener(new AdapterForOwnerpart8.Listener1() {
                            @Override
                            public void iteminadapterforowneroccupiedforpaymentclicked(int position, long s, String k) {
                                if (listenerhome1 != null) {
                                    listenerhome1.itemclickedintenantpaymenthistory(position, s, k);
                                }
                            }
                        }, new AdapterForOwnerpart8.Listener2() {
                            @Override
                            public void clickforstatuscheck(int id, long s, String k, String order, String date, String time, String paymentid, String transfer) {
                                if (listenerforstatus != null) {
                                    listenerforstatus.statuschecking(id, s, k, order, date, time, paymentid, transfer);

                                }


                            }
                        }, new AdapterForOwnerpart8.Listener3() {
                            @Override
                            public void clickforrefund(int id, String refund) {
                                listenerforrefund.refundstatus(id,refund);
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
       listenerhome1=(Listenerhomepayment)context;
       listenerforstatus=(Listenerforstatus)context;
       listenerforrefund=(Listenerforrefund) context;
    }
}
