package com.example.bookandpostroom;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class bottomsheetforstatus extends BottomSheetDialogFragment {

    private static final String T_transferid="bello";
private static final String T_transactionid="bello";
    private RequestQueue requestQueue;
    public static bottomsheetforstatus newInstance(String transactionid,String transferid ) {
        bottomsheetforstatus fragment = new bottomsheetforstatus();
        Bundle args = new Bundle();
        args.putString(T_transactionid, transactionid);
        args.putString(T_transferid, transferid);


        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statusbottomsheetlayout, container, false);

        // Retrieve arguments
        TextView textView = view.findViewById(R.id.tv1);
        TextView textView2 = view.findViewById(R.id.textView);
        ProgressBar pb = view.findViewById(com.razorpay.R.id.progressBar);

        String credentials = "YOUR_RAZORPAY_TRANSFER_KEY_ID:YOUR_RAZORPAY_TRANSFER_KEY_SECRET";
        String authHeader = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        JsonObjectRequest statusRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://api.razorpay.com/v1/transfers/" + T_transferid,
                null,
                response -> {
                    try {
                        String status = response.optString("status", "pending");
                        if ("processed".equals(status)) {
                            pb.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            textView2.setText(status);
                        } else {
                            // Instead of retry, maybe schedule a delayed check
                            pb.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            textView2.setText(status+"\nplease comback later");
                        }
                    } catch (Exception e) {
                        pb.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        Log.e("TransferCheck", "Error processing response", e);
                        // Optional: Show error to user
                        textView2.setText("Error checking status \n please comback later");
                    }
                },
                error -> {
                    pb.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    textView2.setText(error.getMessage());
                    Log.e("TransferCheck", "Network error", error);
                    // Retry after delay

                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", authHeader);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(statusRequest);
        return view;
    }
}
