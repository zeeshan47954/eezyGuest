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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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


public class fragmentforpayment extends Fragment implements RefreshListener {
    String number[];
    int lm;
    int ll;
    long x;

    private long finalIndex = 0;
    long index = 0;

    // Inner class to hold transaction data
    private static class TransactionData {
        String information;
        String orderid;
        String date;
        String time;
        String payment;
        String transfer;

        TransactionData(String information, String orderid, String date, String time,
                        String payment, String transfer) {
            this.information = information;
            this.orderid = orderid;
            this.date = date;
            this.time = time;
            this.payment = payment;
            this.transfer = transfer;
        }
    }

    private List<TransactionData> transactionList = new ArrayList<>();
    private RecyclerView rv;
    private String name;
    private String id;
    private DatabaseReference dbrroom;
    private ValueEventListener paymentListener;
    private AdapterForOwnerpart88 currentAdapter;
    private boolean isAscending = false; // Track current sort order

    interface Listenerhomepayment {
        void itemclicked2inpayment(int position, long s, String k);
    }

    interface Listenerhomepayment2 {
        void statuschecking(int id, long s, String k, String order, String date, String time, String paymentid, String transfer);
    }

    Listenerhomepayment2 listenerforstatus;

    @Override
    public void onRefresh() {
        Toast.makeText(getContext(), "refreshing payments", Toast.LENGTH_SHORT).show();

        index = 0;
        x = 0;
        finalIndex = 0;

        // Clear lists
        transactionList.clear();

        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged();
        }
        pb.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);

        loaddata(name, id);
    }

    private ImageButton sort;
    private Listenerhomepayment listenerhome1;

    ProgressBar pb;
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmentforpayment2, container, false);
        rv = view.findViewById(R.id.rv);
        pb = view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);

        sort = view.findViewById(R.id.btnSortBy);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account != null) {
            name = account.getDisplayName();
            id = account.getUid();

            loaddata(name, id);
        }

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    e.printStackTrace();
                }

                int tealColor = getResources().getColor(R.color.teal);
                for (int i = 0; i < popup.getMenu().size(); i++) {
                    android.view.MenuItem menuItem = popup.getMenu().getItem(i);
                    android.text.SpannableString s = new android.text.SpannableString(menuItem.getTitle());
                    s.setSpan(new android.text.style.ForegroundColorSpan(tealColor), 0, s.length(), 0);
                    menuItem.setTitle(s);
                }

                // Handle menu item clicks
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
            }
        });

        return view;
    }

    public void loaddata(String name, String id) {
        // Remove previous listener if exists
        if (dbrroom != null && paymentListener != null) {
            dbrroom.removeEventListener(paymentListener);
        }

        dbrroom = FirebaseDatabase.getInstance()
                .getReference("google").child(name).child(id).child("owner").child("transactionsrecievedno");

        // Create real-time listener
        paymentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear previous data
                transactionList.clear();
                finalIndex = 0;

                long transactionCount = snapshot.getChildrenCount();
                x = transactionCount;

                if (!snapshot.exists() ||transactionCount == 0) {
                    showEmptyView();
                } else {
                    for (DataSnapshot chilled : snapshot.getChildren()) {
                        finalIndex++;

                        String roomid = chilled.child("idss").getValue(String.class);
                        long transactionNo1 = chilled.child("transactionno").getValue(long.class);
                        String status = chilled.child("status").getValue(String.class);
                        String orderid = chilled.child("orderid").getValue(String.class);
                        String transferid = chilled.child("transferid").getValue(String.class);
                        String date = chilled.child("transactionDate").getValue(String.class);
                        String time = chilled.child("transactionTime").getValue(String.class);
                        String tranid = chilled.child("transactionid").getValue(String.class);

                        String information = roomid + "transactionnumberstartshere" + transactionNo1 + "status" + status;

                        TransactionData data = new TransactionData(information, orderid, date, time, tranid, transferid);
                        transactionList.add(data);

                        if (finalIndex == x) {
                            sortAndUpdateAdapter();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                pb.setVisibility(View.GONE);
            }
        };
        dbrroom.addValueEventListener(paymentListener);
        // Attach real-time listener

    }
    @Override
    public void onPause()
    {
        super.onPause();
        if(dbrroom!=null && paymentListener!=null)
        {dbrroom.removeEventListener(paymentListener);}
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(dbrroom!=null && paymentListener!=null)
        {dbrroom.addValueEventListener(paymentListener);}
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(dbrroom!=null && paymentListener!=null)
        {dbrroom.removeEventListener(paymentListener);}
    }
    private void sortAndUpdateAdapter() {
        if (getActivity() == null) return;
        if (transactionList.isEmpty()) return;

        // Sort the transaction list based on date
        Collections.sort(transactionList, new Comparator<TransactionData>() {
            @Override
            public int compare(TransactionData t1, TransactionData t2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(t1.date);
                    Date date2 = sdf.parse(t2.date);

                    if (date1 == null || date2 == null) return 0;

                    // If dates are equal, compare by time
                    if (date1.equals(date2)) {
                        if (t1.time != null && t2.time != null) {
                            return isAscending ? t1.time.compareTo(t2.time) : t2.time.compareTo(t1.time);
                        }
                        return 0;
                    }

                    return isAscending ? date1.compareTo(date2) : date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        // Convert sorted list to arrays
        String[] informationArray = new String[transactionList.size()];
        String[] orderarray = new String[transactionList.size()];
        String[] transactiondatearray = new String[transactionList.size()];
        String[] transactiontimearray = new String[transactionList.size()];
        String[] paymentarray = new String[transactionList.size()];
        String[] transferarray = new String[transactionList.size()];

        for (int i = 0; i < transactionList.size(); i++) {
            TransactionData data = transactionList.get(i);
            informationArray[i] = data.information;
            orderarray[i] = data.orderid;
            transactiondatearray[i] = data.date;
            transactiontimearray[i] = data.time;
            paymentarray[i] = data.payment;
            transferarray[i] = data.transfer;
        }

        rv.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        sort.setVisibility(View.VISIBLE);

        if (currentAdapter == null) {
            // First time - create new adapter
            currentAdapter = new AdapterForOwnerpart88(informationArray, orderarray,
                    transactiondatearray, transactiontimearray, paymentarray, transferarray);
            rv.setAdapter(currentAdapter);
            GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
            rv.setLayoutManager(glm);

            currentAdapter.setListener(new AdapterForOwnerpart88.Listener1() {
                @Override
                public void iteminadapterforowneroccupiedforpaymentclicked(int position, long s, String k) {
                    if (listenerhome1 != null) {
                        listenerhome1.itemclicked2inpayment(position, s, k);
                    }
                }
            }, new AdapterForOwnerpart88.Listener2() {
                @Override
                public void clickforstatuscheck(int id, long s, String k, String order, String date, String time, String paymentid, String transfer) {
                    if (listenerforstatus != null) {
                        listenerforstatus.statuschecking(id, s, k, order, date, time, paymentid, transfer);
                    }
                }
            });
        } else {
            // Update existing adapter with new data
            currentAdapter.updateData(informationArray, orderarray,
                    transactiondatearray, transactiontimearray, paymentarray, transferarray);
        }
    }

    private void updateAdapter() {
        sortAndUpdateAdapter();
    }

    private void showEmptyView() {
        if (getActivity() == null) return;

        rv.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        sort.setVisibility(View.GONE);
        AdapterForemptyvalue adapter = new AdapterForemptyvalue(8);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
        currentAdapter = null; // Reset current adapter
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listenerhome1 = (Listenerhomepayment) context;
        listenerforstatus = (Listenerhomepayment2) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove listener when fragment view is destroyed to prevent memory leaks
        if (dbrroom != null && paymentListener != null) {
            dbrroom.removeEventListener(paymentListener);
        }
    }
}
/*package com.example.bookandpostroom;

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
*/