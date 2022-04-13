package com.parkngo.parkngo.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.parkngo.parkngo.data.layout.Duration;
import com.parkngo.parkngo.data.parkinghistory.ParkingHistory;
import com.parkngo.parkngo.data.user.User;
import com.parkngo.parkngo.data.layout.Layout;
import com.parkngo.parkngo.data.layout.SlotsOccupied;
import com.parkngo.parkngo.data.rates.Rate;
import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ParkRepository {
    public static final String TAG = ParkRepository.class.getSimpleName();
    public static ParkRepository instance;

    private ParkRepository() {

    }

    public static ParkRepository getInstance() {
        if (instance == null)
            instance = new ParkRepository();

        return instance;
    }

    public void loginUserWithEmailAndPassword(String email, String password, LoadData<Boolean> loadData) {
        Log.e(TAG, "loginUserWithEmailAndPassword: email and pass:" + email + "," + password);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadData.onDataLoaded(true);
            } else {
                Log.e(TAG, "loginUserWithEmailAndPassword: error:" + task.getException().getMessage());
                loadData.onDataLoaded(false);
            }
        });
    }

    public void signUpUserWithEmailAndPassword(String name, String email, String password, LoadData<Boolean> loadData) {
        Log.e(TAG, "signUpUserWithEmailAndPassword: called");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(signUpTask -> {
            Log.e(TAG, "signUpUserWithEmailAndPassword: signing up");
            if (signUpTask.isSuccessful()) {
                loginUserWithEmailAndPassword(email, password, success -> {
                    if (success) {
                        if (mAuth.getCurrentUser() != null) {
                            writeInitialUserData(mAuth.getCurrentUser().getUid(), name, email, written -> {
                                if (written) {
                                    Log.e(TAG, "signUpUserWithEmailAndPassword: signup completed");
                                    loadData.onDataLoaded(true);
                                }
                            });
                        } else {
                            loadData.onDataLoaded(false);
                            Log.e(TAG, "signUpUserWithEmailAndPassword: signed up: logged in: current user null");
                        }
                    } else {
                        loadData.onDataLoaded(false);
                        Log.e(TAG, "signUpUserWithEmailAndPassword: signed up :failed login");
                    }
                });
            } else {
                Log.e(TAG, "signUpUserWithEmailAndPassword: sign up failed:" + signUpTask.getException().getMessage());
                loadData.onDataLoaded(false);
            }
        });
    }

    public void writeInitialUserData(String uid, String name, String email, LoadData<Boolean> loadData) {

        // check if already exists
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.DB_USERS)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.e(TAG, "writeInitialUserData: data already esists");
                            loadData.onDataLoaded(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Log.e(TAG, "writeInitialUserData: new data creation called:uid" + uid);
        Map<String, Object> userInitialData = new HashMap<>();
        userInitialData.put("uid", uid);
        userInitialData.put("email", email);
        userInitialData.put("name", name);
        userInitialData.put("admin", false);

        FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS).child(uid).setValue(userInitialData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(TAG, "writeInitialUserData: success");
                loadData.onDataLoaded(true);
            } else {
                Log.e(TAG, "writeInitialUserData: failed");
                loadData.onDataLoaded(false);
            }
        });
    }

    public void getUserNameByUid(String uid, LoadData<String> loadData) {
        FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = (String) snapshot.child("name").getValue();
                Log.e(TAG, "onDataChange: getUserNameByUid:Name:" + name);
                loadData.onDataLoaded(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error fetching user name:" + error.getMessage());
                loadData.onDataLoaded(null);
            }
        });
    }


    public void isAdmin(String uid, LoadData<Boolean> loadData) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.DB_USERS)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null)
                            loadData.onDataLoaded(user.getAdmin());
                        else
                            loadData.onDataLoaded(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadData.onDataLoaded(false);
                    }
                });
    }

    public void getParkingRates(LoadData<List<Rate>> loadData) {

        FirebaseDatabase.getInstance().getReference().child(Constants.DB_RATES).orderByChild("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Rate> rateList = new ArrayList<>();
                Log.e(TAG, "getParkingRates():" + snapshot.getChildrenCount());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    rateList.add(dataSnapshot.getValue(Rate.class));
                }

                loadData.onDataLoaded(rateList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadData.onDataLoaded(new ArrayList<>());
            }
        });
    }

    public void getSlotArray(int rows, int columns, List<SlotsOccupied> occupiedList, LoadData<boolean[]> loadData) {
        boolean[] occupiedArray = new boolean[rows*columns];
        for (SlotsOccupied slotOccupied : occupiedList) {
            occupiedArray[slotOccupied.row*columns + slotOccupied.column]=true;
        }
        loadData.onDataLoaded(occupiedArray);
    }

    public void getParkingHistoriesForUser(String uid, LoadData<List<ParkingHistory>> loadData) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.DB_PARKING_HISTORIES)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ParkingHistory> parkingHistories = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            parkingHistories.add(dataSnapshot.getValue(ParkingHistory.class));
                        }

                        loadData.onDataLoaded(parkingHistories);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadData.onDataLoaded(new ArrayList<>());
                    }
                });
    }

    public void generateBookingId(LoadData<String> loadData) {
        generateBookingIdSerial(serial -> {
            loadData.onDataLoaded(String.format("P%s", serial));
        });
    }

    public void generateBookingIdSerial(LoadData<Long> loadData) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.DB_COUNTERS)
                .child(Constants.BOOKING_COUNTER)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData bookingId) {
                        Long currentValue = bookingId.getValue(Long.class);
                        if (currentValue == null) {
                            bookingId.setValue(1);
                        } else {
                            bookingId.setValue(currentValue + 1);
                        }

                        return Transaction.success(bookingId);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        Log.e(TAG, "generateBookingId(): onComplete: " + currentData.getValue());
                        Long bookingId = currentData.getValue(Long.class);
                        loadData.onDataLoaded(bookingId);
                    }
                });
    }

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public void getSlotList(String layoutId, Long startTime, Long endTime, LoadData<boolean[]> loadData) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.DB_LAYOUTS_SLOTS_OCCUPIED)
                .child(layoutId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Log.e(TAG, "onDataChange: layout:" + snapshot.getValue().toString());
                        List<SlotsOccupied> occupied = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SlotsOccupied slotsOccupied = dataSnapshot.getValue(SlotsOccupied.class);
                            Date startTimeDate = new Date(startTime);
                            Date endTimeDate = new Date(endTime);
                            Date existingStartTimeDate = new Date(slotsOccupied.duration.startTime);
                            Date existingEndTimeDate = new Date(slotsOccupied.duration.endTime);

                            if (isOverlapping(startTimeDate, endTimeDate, existingStartTimeDate, existingEndTimeDate)) {
                                Log.e(TAG, String.format("if isOverlapping() : Occupied (%d,%d)", slotsOccupied.row, slotsOccupied.column));
                                occupied.add(slotsOccupied);
                            }
                        }

                        FirebaseDatabase.getInstance().getReference()
                                .child(Constants.DB_LAYOUTS)
                                .child(layoutId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Layout layout = snapshot.getValue(Layout.class);
                                        if (layout != null) {
                                            getSlotArray(layout.rows, layout.columns, occupied, loadData);
                                        } else {
                                            loadData.onDataLoaded(null);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        loadData.onDataLoaded(null);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadData.onDataLoaded(null);
                    }
                });
    }

    public static boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
        Log.e(TAG, "isOverlapping: " + String.format("start1:%s end1:%s start2:%s end2:%s", start1, end1, start2, end2));
        return (start1.before(end2) && start2.before(end1));
    }

    public void getTotalParkingAmount(Long from, Long to, LoadData<Double> loadData) {
        getParkingRates(list -> {
            long difference = to - from;
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long elapsedDays = difference / daysInMilli;
            difference = difference % daysInMilli;
            long elapsedHours = difference / hoursInMilli;

            Log.e(TAG, String.format("getTotalParkingAmount: days: %d hours: %d", elapsedDays, elapsedHours));
            if (elapsedDays > 0) {
                if (elapsedHours > 0) {
                    loadData.onDataLoaded((elapsedDays + 1) * list.get(4).price);
                } else {
                    loadData.onDataLoaded(elapsedDays * list.get(4).price);
                }
            } else if (elapsedDays == 0) {
                if (elapsedHours > 0) {
                    if (elapsedHours >= 1 && elapsedHours < 3) {
                        loadData.onDataLoaded(elapsedHours * list.get(1).price);
                    } else if (elapsedHours >= 3 && elapsedHours < 6) {
                        loadData.onDataLoaded(elapsedHours * list.get(2).price);
                    } else if (elapsedHours >= 6 && elapsedHours < 12) {
                        loadData.onDataLoaded(elapsedHours * list.get(3).price);
                    } else if (elapsedHours >= 12) {
                        loadData.onDataLoaded(elapsedHours * list.get(4).price);
                    }
                } else {
                    loadData.onDataLoaded(list.get(0).price);
                }
            }
        });
    }

    public void reserveSlot(String layoutId, long startTime, long endTime, String uid, int row, int column, String layoutTitle, LoadData<String> loadData) {
        Map<String, Object> map = new HashMap<>();
        String slotId = generateUUID();
        map.put("column", column);
        map.put("row", row);
        map.put("occupied_by", uid);
        map.put("slot_code", String.format(Locale.getDefault(), "%s-%d%d", layoutTitle, row, column));
        map.put("slot_id", slotId);
        map.put("status", "RESERVED");
        Map<String, Long> durationMap = new HashMap<>();
        durationMap.put("start_time", startTime);
        durationMap.put("end_time", endTime);
        map.put("duration", durationMap);

        FirebaseDatabase.getInstance().getReference()
                .child(Constants.DB_LAYOUTS_SLOTS_OCCUPIED)
                .child(layoutId)
                .child(slotId).setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadData.onDataLoaded(slotId);
            } else {
                loadData.onDataLoaded(null);
            }
        });
    }

    public void confirmSlot(String layoutId, String slotId, LoadData<String> loadData) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "CONFIRMED");
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.DB_LAYOUTS_SLOTS_OCCUPIED)
                .child(layoutId)
                .orderByChild("slot_id")
                .limitToFirst(1)
                .equalTo(slotId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, "snapshot: " + snapshot);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SlotsOccupied slotsOccupied = dataSnapshot.getValue(SlotsOccupied.class);
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.DB_LAYOUTS_SLOTS_OCCUPIED)
                            .child(layoutId)
                            .child(slotId)
                            .updateChildren(map).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveParkingHistory(slotsOccupied,layoutId,slotId,FirebaseAuth.getInstance().getCurrentUser().getUid(),loadData);
                        } else {
                            loadData.onDataLoaded(null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadData.onDataLoaded(null);
            }
        });
    }



    private void saveParkingHistory(SlotsOccupied slotsOccupied, String layoutId, String slotId, String uid, LoadData<String> loadData) {
        generateBookingId(bookingId->{
            Map<String,Object> map = new HashMap<>();
            map.put("booking_id",bookingId);
            map.put("confirmed_on", System.currentTimeMillis());
            map.put("slot_code", slotsOccupied.slotCode);
            map.put("parking_id", slotId);
            map.put("date",slotsOccupied.duration.startTime);
            Map<String, Long> durationMap = new HashMap<>();
            durationMap.put("start_time", slotsOccupied.duration.startTime);
            durationMap.put("end_time",  slotsOccupied.duration.endTime);
            map.put("duration", durationMap);

            FirebaseDatabase.getInstance().getReference()
                    .child(Constants.DB_PARKING_HISTORIES)
                    .child(uid)
                    .child(slotId)
                    .setValue(map).addOnCompleteListener(task->{
                        if (task.isSuccessful()){
                            loadData.onDataLoaded(slotId);
                        }else{
                            loadData.onDataLoaded(null);
                        }
            });
        });
    }

    public void getAllLayouts(LoadData<List<Layout>> loadData){
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.DB_LAYOUTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Layout> layouts = new ArrayList<>();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Layout layout = dataSnapshot.getValue(Layout.class);
                            layouts.add(layout);
                        }

                        loadData.onDataLoaded(layouts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadData.onDataLoaded(new ArrayList<>());
                    }
                });
    }

    public void getLayoutByLayoutId(String layoutId,LoadData<Layout> loadData){
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.DB_LAYOUTS)
                .child(layoutId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Layout layout = snapshot.getValue(Layout.class);
                        loadData.onDataLoaded(layout);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadData.onDataLoaded(null);
                    }
                });
    }

    public void removeReservedSlot(String layoutId,String slotId,LoadData<Boolean> loadData){
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.DB_LAYOUTS_SLOTS_OCCUPIED)
                .child(layoutId)
                .child(slotId).removeValue().addOnCompleteListener(task->{
                    if (task.isSuccessful()){
                        loadData.onDataLoaded(true);
                    }else{
                        loadData.onDataLoaded(false);
                    }
        });
    }

    public void createLayout(int rows,int columns,String layoutTitle,LoadData<String> loadData){
        Map<String,Object> map = new HashMap<>();
        String layoutId = generateUUID();
        map.put("active",true);
        map.put("layout_id",layoutId);
        map.put("layout_title",layoutTitle);
        map.put("rows",rows);
        map.put("columns",columns);

        FirebaseDatabase.getInstance().getReference()
                .child(Constants.DB_LAYOUTS)
                .child(layoutId)
                .setValue(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loadData.onDataLoaded(layoutId);
                    }else{
                        loadData.onDataLoaded(null);
                    }
        });
    }


    public void updateStatusOfLayout(String layoutID,boolean status,LoadData<Boolean> loadData){
        Map<String,Object> map = new HashMap<>();
        map.put("active",status);
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.DB_LAYOUTS)
                .child(layoutID)
                .updateChildren(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loadData.onDataLoaded(true);
                    }else{
                        loadData.onDataLoaded(false);
                    }
        });
    }

    public void updatePriceOfRate(List<Rate> rates,LoadData<Boolean> loadData){
        for (Rate rate: rates){
            Map<String,Object> map = new HashMap<>();
            map.put("price",rate.price);
            FirebaseDatabase.getInstance().getReference()
                    .child(Constants.DB_RATES)
                    .child(rate.rateId)
                    .updateChildren(map).addOnCompleteListener(task -> {
                        if (!task.isSuccessful()){
                            loadData.onDataLoaded(false);
                        }
            });
        }

        loadData.onDataLoaded(true);
    }

    public void getUserByUid(String uid,LoadData<User> loadData){
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.DB_USERS)
                .child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user!=null){
                    loadData.onDataLoaded(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadData.onDataLoaded(null);
            }
        });
    }

    public void checkGoogleSignIn(LoadData<Boolean> loadData){
        for (int i=0;i<FirebaseAuth.getInstance().getCurrentUser().getProviderData().size();++i){
            if (FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(i).getProviderId().toLowerCase(Locale.ROOT).contains("google")){
                loadData.onDataLoaded(true);
            }
        }
        loadData.onDataLoaded(false);
    }

    public void updateUserPassword(String password,String newPassword, LoadData<Integer> loadData){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                loadData.onDataLoaded(0);
                            } else {
                                Log.e(TAG, "Error password not updated");
                                loadData.onDataLoaded(1);
                            }
                        });
                    } else {
                        loadData.onDataLoaded(2);
                    }
                });
    }


    public void getProfilePicOfUser(LoadData<Uri> loadData) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                loadData.onDataLoaded(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
            } else {
                loadData.onDataLoaded(null);
            }
        } else {
            loadData.onDataLoaded(null);
        }

    }

    public void sendPasswordResetRequest(String email,LoadData<Boolean> loadData){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                loadData.onDataLoaded(true);
            }else{
                loadData.onDataLoaded(false);
            }
        });
    }
}
