package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Transaction extends AppCompatActivity {

    private Button elist_add;
    private Button ilist_add;
    private RecyclerView inRView, exRView;
    private ProgressDialog loader;
    private ImageButton transaction_backbutton;
    private DrawerLayout drawerLayout;
    private DatabaseReference database_ref;
    private DatabaseReference ref;
    private DatabaseReference ref1;
    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private List<Data> myDataList;
    private List<DataIncome> myDataList2;
    public static double totalAmount;
    private MonthlyExpenseAdapter monthlyExpenseAdapter;
    private MonthlyIncomeAdapter monthlyIncomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        elist_add = findViewById(R.id.elist_add);
        ilist_add = findViewById(R.id.ilist_add);
        drawerLayout = findViewById(R.id.drawer_layout);

        inRView = findViewById(R.id.inRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        inRView.setHasFixedSize(true);
        inRView.setLayoutManager(linearLayoutManager);
        loader = new ProgressDialog(this);

        transaction_backbutton = findViewById(R.id.transaction_backbutton);
        exRView = findViewById(R.id.exRView);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        exRView.setHasFixedSize(true);
        exRView.setLayoutManager(linearLayoutManager2);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("expenselist").child(onlineUserID);
        ref1 = FirebaseDatabase.getInstance().getReference().child("incomelist").child(onlineUserID);
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        myDataList = new ArrayList<>();
        monthlyExpenseAdapter = new MonthlyExpenseAdapter(Transaction.this, myDataList);
        exRView.setAdapter(monthlyExpenseAdapter);

        myDataList2 = new ArrayList<>();
        monthlyIncomeAdapter = new MonthlyIncomeAdapter(Transaction.this, myDataList2);
        inRView.setAdapter(monthlyIncomeAdapter);

        elist_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExpList();
            }
        });

        ilist_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIncList();
            }
        });

        transaction_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Transaction.this, menupage.class);
                startActivity(intent);
                finish();
            }
        });

        SpinnerView();
        IncomeView();
    }

    private void SpinnerView(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("expenselist").child(onlineUserID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }

                monthlyExpenseAdapter.notifyDataSetChanged();

                totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map< String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    double pTotal = Double.parseDouble(String.valueOf(total));
                    totalAmount+=pTotal;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void IncomeView(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("incomelist").child(onlineUserID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList2.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    DataIncome data = dataSnapshot.getValue(DataIncome.class);
                    myDataList2.add(data);
                }

                monthlyIncomeAdapter.notifyDataSetChanged();

                totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map< String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    double pTotal = Double.parseDouble(String.valueOf(total));
                    totalAmount+=pTotal;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void addExpList() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myview = inflater.inflate(R.layout.expenselist, null);
        myDialog.setView(myview);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemsSpinner = myview.findViewById(R.id.expensespinner);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.items));
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemsSpinner.setAdapter(itemsAdapter);

        final EditText amount = myview.findViewById(R.id.expenselist_amount);
        final EditText notes = myview.findViewById(R.id.expenselist_note);
        final Button Btnsave = myview.findViewById(R.id.expenselist_savebutton);
        final Button Btncancel = myview.findViewById(R.id.expenselist_cancelbutton);

        Btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hAmount = amount.getText().toString();
                String hNotes = notes.getText().toString();
                String item = itemsSpinner.getSelectedItem().toString();

                if (hAmount.isEmpty()) {
                    amount.setError("Amount is required!");
                    return;
                }
                if (hNotes.isEmpty()) {
                    notes.setError("Note is required!");
                    return;
                }
                if (item.equals("Select Envelope")) {
                    Toast.makeText(Transaction.this, "Please select an expense", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    loader.setMessage("Adding expense to database");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    DatabaseReference budgetRef = null;
                    DatabaseReference remainingRef = null;
                    if (item.equals("Food")) {
                        budgetRef = database_ref.child("food_budget_amount");
                        remainingRef = database_ref.child("food_remaining_amount");
                    } else if (item.equals("Travel")) {
                        budgetRef = database_ref.child("travel_budget_amount");
                        remainingRef = database_ref.child("travel_remaining_amount");
                    } else if (item.equals("Improve")) {
                        budgetRef = database_ref.child("improvement_budget_amount");
                        remainingRef = database_ref.child("improvement_remaining_amount");
                    }else if (item.equals("Insurance")) {
                        budgetRef = database_ref.child("insurance_budget_amount");
                        remainingRef = database_ref.child("insurance_remaining_amount");
                    }else if (item.equals("Saving")) {
                        budgetRef = database_ref.child("saving_budget_amount");
                        remainingRef = database_ref.child("saving_remaining_amount");
                    }else if (item.equals("Free")) {
                        budgetRef = database_ref.child("free_budget_amount");
                        remainingRef = database_ref.child("free_remaining_amount");
                    }
                    if (budgetRef != null && remainingRef != null) {
                        DatabaseReference finalRemainingRef = remainingRef;
                        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double currentBudgetAmount = snapshot.getValue(Double.class);
                                // Calculate the new remaining amount after deducting hAmount from currentBudgetAmount
                                double newRemainingAmount = currentBudgetAmount - Double.parseDouble(hAmount.toString());
                                // Update the value of the remaining amount node with the new remaining amount
                                finalRemainingRef.setValue(newRemainingAmount);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle the error
                            }
                        });
                    }

                    String id = ref.push().getKey();

                    @SuppressLint("SimpleDateFormat") DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String date = dateformat.format(calendar.getTime());

                    Data data = new Data(item, date, id, hNotes, Double.parseDouble(hAmount));
                    ref.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Transaction.this, "Expense added successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Transaction.this, "Failed to add expense.", Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        Btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void addIncList() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myview = inflater.inflate(R.layout.incomelist, null);
        myDialog.setView(myview);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemsSpinner = myview.findViewById(R.id.incomespinner);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.items1));
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemsSpinner.setAdapter(itemsAdapter);

        final EditText amount = myview.findViewById(R.id.incomelist_amount);
        final Button Btnsave = myview.findViewById(R.id.incomelist_savebutton);
        final Button Btncancel = myview.findViewById(R.id.incomelist_cancelbutton);

        Btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hAmount = amount.getText().toString();
                String item = itemsSpinner.getSelectedItem().toString();

                if (hAmount.isEmpty()) {
                    amount.setError("Amount is required!");
                    return;
                }
                if (item.equals("Select Income")) {
                    Toast.makeText(Transaction.this, "Please select an income", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    loader.setMessage("Adding income to database");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    DatabaseReference budgetRef = null;
                    DatabaseReference remainingRef = null;
                    if (item.equals("Food")) {
                        budgetRef = database_ref.child("food_remaining_amount");
                        remainingRef = database_ref.child("food_remaining_amount");
                    } else if (item.equals("Travel")) {
                        budgetRef = database_ref.child("travel_remaining_amount");
                        remainingRef = database_ref.child("travel_remaining_amount");
                    } else if (item.equals("Improve")) {
                        budgetRef = database_ref.child("improvement_remaining_amount");
                        remainingRef = database_ref.child("improvement_remaining_amount");
                    }else if (item.equals("Insurance")) {
                        budgetRef = database_ref.child("insurance_remaining_amount");
                        remainingRef = database_ref.child("insurance_remaining_amount");
                    }else if (item.equals("Saving")) {
                        budgetRef = database_ref.child("saving_remaining_amount");
                        remainingRef = database_ref.child("saving_remaining_amount");
                    }else if (item.equals("Free")) {
                        budgetRef = database_ref.child("free_remaining_amount");
                        remainingRef = database_ref.child("free_remaining_amount");
                    }
                    if (budgetRef != null && remainingRef != null) {
                        DatabaseReference finalRemainingRef1 = remainingRef;
                        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double currentBudgetAmount = snapshot.getValue(Double.class);
                                // Calculate the new remaining amount after deducting hAmount from currentBudgetAmount
                                double newRemainingAmount = currentBudgetAmount + Double.parseDouble(hAmount);
                                // Update the value of the remaining amount node with the new remaining amount
                                finalRemainingRef1.setValue(newRemainingAmount);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle the error
                            }
                        });
                    }

                    String id = ref.push().getKey();

                    @SuppressLint("SimpleDateFormat") DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String date = dateformat.format(calendar.getTime());

                    DataIncome data = new DataIncome(item, date, id, Double.parseDouble(hAmount));
                    ref1.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Transaction.this, "income added successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Transaction.this, "Failed to add income.", Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        Btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void ClickMenu(View view)
    {
        //open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout)
    {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public  void ClickLogo(View view)
    {
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout)
    {
        //close drawer layout
        //Check Condition
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }



    public void Click_Summary(View view){
        //recreate activity
        redirectActivity(this,MainActivity.class);
    }

    public void ClickEnvelope(View view){
        redirectActivity(this,Envelope_Management_Page.class);
    }

    public void ClickTransaction(View view){
        recreate();
    }

    public void ClickPersonalProfile(View view){
        redirectActivity(this,Personal_Profile.class);
    }

    public void ClickLogout(View view)
    {
        redirectActivity(this,LoginPage.class);
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(intent);
    }
}