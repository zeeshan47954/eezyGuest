package com.example.bookandpostroom;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DeleteDocumentWorker extends Worker {

    public DeleteDocumentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String documentId = getInputData().getString("document_id");
        String token = getInputData().getString("reciepienttoken");
        String tenantInfo = getInputData().getString("tenant_info");

        if (documentId == null || token == null || tenantInfo == null) {
            return Result.failure();
        }

        String tenantName = tenantInfo.split("gapp")[0];
        String tenantId = tenantInfo.split("gapp")[1];

        DatabaseReference dfff = FirebaseDatabase.getInstance().getReference("google")
                .child(tenantName).child(tenantId).child("student");

        dfff.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                List<String> keysToDelete = new ArrayList<>();

                for (MutableData ds : currentData.child("requestsenttotheownernumbers").getChildren()) {
                    String key = ds.getKey();
                    String value = ds.getValue(String.class);

                    if (value != null && value.contains(documentId)) {
                        keysToDelete.add(key);
                    }
                }

                // Abort transaction if paid = 1
                if (currentData.child(documentId).child("paid").getValue(Long.class) != null &&
                        currentData.child(documentId).child("paid").getValue(Long.class) == 1) {
                    return Transaction.success(currentData);
                }

                if (!keysToDelete.isEmpty()) {
                    for (String key : keysToDelete) {
                        currentData.child("requestsenttotheownernumbers").child(key).setValue(null);
                    }
                    currentData.child(documentId).setValue(null);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed && currentData != null && !currentData.hasChild(documentId)) {
                    Sendnotification notificationsSender = new Sendnotification(token, "Sorry",
                            "It has been 24 hours and you did not make the payment, so booking cancelled",
                            getApplicationContext());
                    notificationsSender.SendNotifications();
                }
            }
        });

        return Result.success();
    }
}
