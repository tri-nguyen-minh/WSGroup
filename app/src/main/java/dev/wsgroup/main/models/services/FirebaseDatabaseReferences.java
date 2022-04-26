package dev.wsgroup.main.models.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.MessageFirebase;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class FirebaseDatabaseReferences {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public FirebaseDatabaseReferences() {
        database = FirebaseDatabase.getInstance();
    }

    public Task<Void> addMessage(Message message) {
        MessageFirebase messageFirebase = new MessageFirebase();
        messageFirebase.setFrom(message.getFromId());
        messageFirebase.setTo(message.getToId());
        if (message.getMessage() != null) {
            messageFirebase.setMessage(message.getMessage());
        } else {
            messageFirebase.setFile(message.getLink());
        }
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        databaseReference = database.getReference("chat-message");
        return databaseReference.setValue(messageFirebase);
    }

    public Query getConversation(String userId, String foreignId) {
        databaseReference = database.getReference("message")
                                    .child(userId)
                                    .child(foreignId)
                                    .child("data");
        return databaseReference.orderByKey();
    }

    public Query getUserMessages(String accountId) {
        databaseReference = database.getReference().child("message").child(accountId);
        return databaseReference.orderByKey();
    }

    public Query getCustomerServiceId() {
        databaseReference = database.getReference().child("customer-service");
        return databaseReference.orderByKey();
    }

    public Query getUserNotifications(String accountId) {
        databaseReference = database.getReference().child("notif").child(accountId);
        return databaseReference.orderByKey();
    }

    public Query get(DatabaseReference databaseReference) {
        return databaseReference.orderByKey();
    }
}
