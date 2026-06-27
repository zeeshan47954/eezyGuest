package com.example.bookandpostroom;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragmentpaymenthistoryfortenantfinal extends Fragment {

    private List<TransactionData> transactionList = new ArrayList<>();

    interface Listenerhomepayment {
        void itemclickedintenantpaymenthistory(int position, long s, String k);
    }

    interface Listenerforstatus {
        void statuschecking(int position, long s, String k, String order, String date, String time, String paymentid, String transfer);
    }

    interface Listenerforrefund {
        void refundstatus(int position, String s);
    }

    private Listenerhomepayment listenerhome1;
    private Listenerforstatus listenerforstatus;
    private Listenerforrefund listenerforrefund;

    private RecyclerView rv;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton sort;
    private AdapterForOwnerpart8 adapter;

    private DatabaseReference transactionRef;
    private ValueEventListener transactionListener;

    private boolean isAscending = false; // Track current sort order

    // Inner class to hold transaction data
    private static class TransactionData {
        String information;
        String orderid;
        String date;
        String time;
        String payment;
        String transfer;
        String refundid;

        TransactionData(String information, String orderid, String date, String time,
                        String payment, String transfer, String refundid) {
            this.information = information;
            this.orderid = orderid;
            this.date = date;
            this.time = time;
            this.payment = payment;
            this.transfer = transfer;
            this.refundid = refundid;
        }
    }

    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmentforpayment, container, false);

        rv = view.findViewById(R.id.rv);
        pb = view.findViewById(R.id.pb);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        sort = view.findViewById(R.id.btnSortBy);

        // Initialize RecyclerView
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        sort.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenuInflater().inflate(R.menu.sortmenu, popup.getMenu());

            // Enable icons
            try {
                java.lang.reflect.Field[] fields = popup.getClass().getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        java.lang.reflect.Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("PopupMenu", "Error enabling icons", e);
            }

            int tealColor = getResources().getColor(R.color.teal);
            for (int i = 0; i < popup.getMenu().size(); i++) {
                android.view.MenuItem menuItem = popup.getMenu().getItem(i);
                android.text.SpannableString s = new android.text.SpannableString(menuItem.getTitle());
                s.setSpan(new android.text.style.ForegroundColorSpan(tealColor), 0, s.length(), 0);
                menuItem.setTitle(s);
            }

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.ascending) {
                    isAscending = true;
                    sortAndUpdateAdapter();
                } else if (id == R.id.descending) {
                    isAscending = false;
                    sortAndUpdateAdapter();
                }
                return true;
            });

            popup.show();
        });

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();

        if (account != null) {
            String name = account.getDisplayName();
            String id = account.getUid();
            setupSwipeRefresh(name, id);
            setupRealtimeUpdates(name, id);
        } else {
            showEmptyAdapter();
        }

        return view;
    }

    private void setupSwipeRefresh(String name, String id) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            clearData();
            setupRealtimeUpdates(name, id);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void clearData() {
        transactionList.clear();
        if (adapter != null) {
            adapter.updateData(new String[0], new String[0], new String[0], new String[0], new String[0], new String[0], new String[0]);
        }
    }

    private void setupRealtimeUpdates(String name, String id) {
        pb.setVisibility(View.VISIBLE);
        sort.setVisibility(View.GONE);

        transactionRef = FirebaseDatabase.getInstance()
                .getReference("google").child(name).child(id)
                .child("student").child("transactionmadeno");

        if (transactionListener != null) {
            transactionRef.removeEventListener(transactionListener);
        }

        transactionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();

                long transactionCount = snapshot.getChildrenCount();
                if (!snapshot.exists() ||transactionCount == 0) {
                    showEmptyAdapter();
                    return;
                }

                for (DataSnapshot child : snapshot.getChildren()) {
                    String roomid = child.child("info").getValue(String.class);
                    long transactionNo = child.child("transactionno").getValue(Long.class) != null ? child.child("transactionno").getValue(Long.class) : 0;
                    String status = child.child("status").getValue(String.class);
                    String orderid = child.child("orderid").getValue(String.class);
                    String transferid = child.child("transferid").getValue(String.class);
                    String date = child.child("transactionDate").getValue(String.class);
                    String time = child.child("transactionTime").getValue(String.class);
                    String tranid = child.child("transactionid").getValue(String.class);
                    String refid = child.child("refundid").getValue(String.class);

                    String information = roomid + "transactionnumberstartshere" + transactionNo + "status" + status;

                    TransactionData data = new TransactionData(information, orderid, date, time, tranid, transferid, refid);
                    transactionList.add(data);
                }

                sortAndUpdateAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pb.setVisibility(View.GONE);
                Log.e("Firebase", "Database error: " + error.getMessage());
                showEmptyAdapter();
            }
        };

        transactionRef.addValueEventListener(transactionListener);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(transactionRef!=null && transactionListener!=null)
        {transactionRef.removeEventListener(transactionListener);}
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(transactionRef!=null && transactionListener!=null)
        {transactionRef.addValueEventListener(transactionListener);}
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(transactionRef!=null && transactionListener!=null)
        {transactionRef.removeEventListener(transactionListener);}
    }
    private void sortAndUpdateAdapter() {
        if (transactionList.isEmpty()) {
            showEmptyAdapter();
            return;
        }

        Collections.sort(transactionList, (t1, t2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date1 = sdf.parse(t1.date != null ? t1.date : "");
                Date date2 = sdf.parse(t2.date != null ? t2.date : "");

                if (date1 == null || date2 == null) return 0;

                if (date1.equals(date2)) {
                    if (t1.time != null && t2.time != null) {
                        return isAscending ? t1.time.compareTo(t2.time) : t2.time.compareTo(t1.time);
                    }
                    return 0;
                }

                return isAscending ? date1.compareTo(date2) : date2.compareTo(date1);
            } catch (ParseException e) {
                Log.e("SortError", "Error parsing dates", e);
                return 0;
            }
        });

        // Convert sorted list to arrays
        String[] informationArray = new String[transactionList.size()];
        String[] orderArray = new String[transactionList.size()];
        String[] dateArray = new String[transactionList.size()];
        String[] timeArray = new String[transactionList.size()];
        String[] paymentArray = new String[transactionList.size()];
        String[] transferArray = new String[transactionList.size()];
        String[] refundArray = new String[transactionList.size()];

        for (int i = 0; i < transactionList.size(); i++) {
            TransactionData data = transactionList.get(i);
            informationArray[i] = data.information;
            orderArray[i] = data.orderid;
            dateArray[i] = data.date;
            timeArray[i] = data.time;
            paymentArray[i] = data.payment;
            transferArray[i] = data.transfer;
            refundArray[i] = data.refundid;
        }

        if (adapter == null) {
            adapter = new AdapterForOwnerpart8(
                    informationArray, orderArray, dateArray, timeArray,
                    paymentArray, transferArray, refundArray
            );
            rv.setAdapter(adapter);
            adapter.setListener(
                    (position, s, k) -> {
                        if (listenerhome1 != null)
                            listenerhome1.itemclickedintenantpaymenthistory(position, s, k);
                    },
                    (id1, s1, roomid, transactionNo, date1, time1, paymentid, transfer) -> {
                        if (listenerforstatus != null)
                            listenerforstatus.statuschecking(id1, s1, roomid, transactionNo, date1, time1, paymentid, transfer);
                    },
                    (id1, refund) -> {
                        if (listenerforrefund != null)
                            listenerforrefund.refundstatus(id1, refund);
                    }
            );
        } else {
            adapter.updateData(informationArray, orderArray, dateArray, timeArray,
                    paymentArray, transferArray, refundArray);
        }

        sort.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
    }

    private void showEmptyAdapter() {
        rv.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        sort.setVisibility(View.GONE);
        AdapterForemptyvalue adapter = new AdapterForemptyvalue(8);
        rv.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listenerhome1 = (Listenerhomepayment) context;
        listenerforstatus = (Listenerforstatus) context;
        listenerforrefund = (Listenerforrefund) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (transactionRef != null && transactionListener != null) {
            transactionRef.removeEventListener(transactionListener);
        }
    }
}
//package com.example.bookandpostroom;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class fragmentpaymenthistoryfortenantfinal extends Fragment {
//    String number[];
//    long x;
//    private long finalIndex=0;
//    List<String> information=new ArrayList<>();
//    List<String> transfer=new ArrayList<>();
//    List<String> order=new ArrayList<>();
//    List <String> transactiondate=new ArrayList<>();
//    List <String> transactiontime=new ArrayList<>();
//    List <String> payment=new ArrayList<>();
//    List <String> refundid=new ArrayList<>();
//
//    interface Listenerhomepayment {
//        void itemclickedintenantpaymenthistory(int position, long s,String k);
//    }
//    interface Listenerforstatus{
//        void statuschecking(int position,long s,String k,String order,String date,String time,String paymentid,String transfer);
//    }
//    interface  Listenerforrefund{
//
//        void refundstatus(int position,String s);
//    }
//
//    private Listenerhomepayment listenerhome1;
//    Listenerforstatus listenerforstatus;
//    Listenerforrefund listenerforrefund;
//    private
//    RecyclerView rv;
//    ProgressBar pb;
//    SwipeRefreshLayout swipeRefreshLayout;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view =  inflater.inflate(R.layout.fragment_fragmentforpayment, container, false);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
// rv=view.findViewById(R.id.rv);
//         pb=view.findViewById(R.id.pb);
//swipeRefreshLayout=view.findViewById(R.id.swipeRefresh);
//
//        if (account != null) {
//            String name = account.getDisplayName();
//            String id = account.getId();
//
//loaddata(name,id);
//
//swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//    @Override
//    public void onRefresh() {
//        finalIndex=0;
//        x=0;
//        transactiondate.clear();
//        transactiontime.clear();
//        payment.clear();
//        order.clear();
//        transfer.clear();
//        information.clear();
//        rv.setAdapter(null);
//loaddata(name,id);
//        new android.os.Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
//    }
//});
//
//
//        }
//        return view;
//    }
//public void loaddata(String name,String id)
//{            DatabaseReference dbrfff = FirebaseDatabase.getInstance()
//        .getReference("google").child(name).child(id).child("student").child("transactionmadeno");
//
//    dbrfff.addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            long transactionCount = snapshot.getChildrenCount();
//            x=transactionCount;
//            if(transactionCount==0)
//            {
//
//                rv.setVisibility(View.VISIBLE);
//                pb.setVisibility(View.GONE);
//                AdapterForemptyvalue adapter = new AdapterForemptyvalue(8);
//                rv.setAdapter(adapter);
//                GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                rv.setLayoutManager(glm);
//            }
//            else{
//
//                for(DataSnapshot chilled:snapshot.getChildren())
//                {finalIndex++;
//
//                    String roomid = chilled.child("info").getValue(String.class);
//                    long transactionNo1 = chilled.child("transactionno").getValue(long.class);
//                    String status=chilled.child("status").getValue(String.class);
//                    String orderid=chilled.child("orderid").getValue(String.class);
//                    String transferid=chilled.child("transferid").getValue(String.class);
//                    String date=chilled.child("transactionDate").getValue(String.class);
//                    String time=chilled.child("transactionTime").getValue(String.class);
//                    String tranid=chilled.child("transactionid").getValue(String.class);
//                    String refid=chilled.child("refundid").getValue(String.class);
//
//                    order.add(orderid);
//                    transactiondate.add(date);
//                    transactiontime.add(time);
//                    payment.add(tranid);
//                    transfer.add(transferid);
//                    refundid.add(refid);
//
//                    information.add(roomid+"transactionnumberstartshere"+transactionNo1+"status"+status);
//
//                    if(finalIndex==x)
//                    {
//
//
//                        String[] informationArray = information.toArray(new String[0]);
//                        String[] orderarray = order.toArray(new String[0]);
//                        String[] transactiondatearray = transactiondate.toArray(new String[0]);
//                        String[] transactiontimearray = transactiontime.toArray(new String[0]);
//                        String[] paymentarray = payment.toArray(new String[0]);
//                        String[] transferarray = transfer.toArray(new String[0]);
//                        String[] refundidarray = refundid.toArray(new String[0]);
//
//
//                        rv.setVisibility(View.VISIBLE);
//                        pb.setVisibility(View.GONE);
//
//                        AdapterForOwnerpart8 adapter = new AdapterForOwnerpart8(informationArray,orderarray,transactiondatearray,transactiontimearray,paymentarray,transferarray,refundidarray);
//                        rv.setAdapter(adapter);
//                        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                        rv.setLayoutManager(glm);
//
//                        adapter.setListener(new AdapterForOwnerpart8.Listener1() {
//                            @Override
//                            public void iteminadapterforowneroccupiedforpaymentclicked(int position, long s, String k) {
//                                if (listenerhome1 != null) {
//                                    listenerhome1.itemclickedintenantpaymenthistory(position, s, k);
//                                }
//                            }
//                        }, new AdapterForOwnerpart8.Listener2() {
//                            @Override
//                            public void clickforstatuscheck(int id, long s, String k, String order, String date, String time, String paymentid, String transfer) {
//                                if (listenerforstatus != null) {
//                                    listenerforstatus.statuschecking(id, s, k, order, date, time, paymentid, transfer);
//
//                                }
//
//
//                            }
//                        }, new AdapterForOwnerpart8.Listener3() {
//                            @Override
//                            public void clickforrefund(int id, String refund) {
//                                listenerforrefund.refundstatus(id,refund);
//                            }
//                        });
//
//
//                    }
//
//
//                }
//
//
//
//
//
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    });
//
//}
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//       listenerhome1=(Listenerhomepayment)context;
//       listenerforstatus=(Listenerforstatus)context;
//       listenerforrefund=(Listenerforrefund) context;
//    }
//}
