package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageButton summary_menu;
    private TextView summary_saving_amount, summary_food_amount, summary_travel_amount, summary_improve_amount, summary_insurance_amount, summary_free_amount, summary_total_income, summary_total_expense;
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";
    private MonthlyIncomeAdapter monthlyIncomeAdapter;
    private List<DataIncome> myDataList;
    private MonthlyExpenseAdapter monthlyExpenseAdapter;
    private List<Data> myDataList2;
    private Button logout_menu;
    private TextView tvSaving, tvFood, tvTravel, tvImprove, tvInsurance, tvFree;
    private PieChart pieChart;

    public static double totalincome;
    public double totalexpense;
    public double totalexpenseSaving;
    public double totalexpenseFood;
    public double totalexpenseTravel;
    public double totalexpenseImprove;
    public double totalexpenseInsurance;
    public double totalexpenseFree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //All the element in the this layout
        summary_menu = findViewById(R.id.summary_menu);
        summary_saving_amount = findViewById(R.id.summary_saving_amount);
        summary_food_amount = findViewById(R.id.summary_food_amount);
        summary_travel_amount = findViewById(R.id.summary_travel_amount);
        summary_improve_amount = findViewById(R.id.summary_improve_amount);
        summary_insurance_amount = findViewById(R.id.summary_insurance_amount);
        summary_free_amount = findViewById(R.id.summary_free_amount);
        summary_total_income = findViewById(R.id.summary_total_income);
        summary_total_expense = findViewById(R.id.summary_total_expense);
        drawerLayout = findViewById(R.id.drawer_layout);
        logout_menu = findViewById(R.id.logout_menu);

        //PieChart
        tvSaving = findViewById(R.id.tvSaving);
        tvFood = findViewById(R.id.tvFood);
        tvTravel = findViewById(R.id.tvTravel);
        tvImprove = findViewById(R.id.tvImprove);
        tvInsurance = findViewById(R.id.tvInsurance);
        tvFree = findViewById(R.id.tvFree);

        pieChart = findViewById(R.id.piechart);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        //read Income
        myDataList = new ArrayList<>();
        monthlyIncomeAdapter = new MonthlyIncomeAdapter(MainActivity.this, myDataList);

        //read Expense
        myDataList2 = new ArrayList<>();
        monthlyExpenseAdapter = new MonthlyExpenseAdapter(MainActivity.this, myDataList2);

        summary_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, menupage.class);
                startActivity(intent);
                finish();
            }
        });

        preread_data();
        readTotalIncome();
        readTotalExpense();
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
        recreate();
    }

    public void ClickEnvelope(View view){
        redirectActivity(this,Envelope_Management_Page.class);
    }

    public void ClickTransaction(View view){
        redirectActivity(this,Transaction.class);
    }

    public void ClickPersonalProfile(View view){
        redirectActivity(this,Personal_Profile.class);
    }

    public void ClickLogout(View view)
    {
        logout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Flexi Budget").setMessage("Are you sure you want to exit?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebase_auth.signOut();
                        Intent intent = new Intent(MainActivity.this, LoginPage.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", null).show();
            }
        });
        redirectActivity(this,LoginPage.class);
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private void preread_data() {
        database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("saving_remaining_amount").exists()) {
                    String saving_amount = dataSnapshot.child("saving_remaining_amount").getValue().toString();
                    summary_saving_amount.setText(saving_amount);
                } else {
                    database_ref.child("saving_remaining_amount").setValue(0);
                    summary_saving_amount.setText("0");
                }

                if (dataSnapshot.child("food_remaining_amount").exists()) {
                    String food_amount = dataSnapshot.child("food_remaining_amount").getValue().toString();
                    summary_food_amount.setText(food_amount);
                } else {
                    database_ref.child("food_remaining_amount").setValue(0);
                    summary_saving_amount.setText("0");
                }

                if (dataSnapshot.child("travel_remaining_amount").exists()) {
                    String travel_amount = dataSnapshot.child("travel_remaining_amount").getValue().toString();
                    summary_travel_amount.setText(travel_amount);
                } else {
                    database_ref.child("travel_remaining_amount").setValue(0);
                    summary_travel_amount.setText("0");
                }

                if (dataSnapshot.child("improvement_remaining_amount").exists()) {
                    String improvement_amount = dataSnapshot.child("improvement_remaining_amount").getValue().toString();
                    summary_improve_amount.setText(improvement_amount);
                } else {
                    database_ref.child("improvement_remaining_amount").setValue(0);
                    summary_improve_amount.setText("0");
                }

                if (dataSnapshot.child("insurance_remaining_amount").exists()) {
                    String insurance_amount = dataSnapshot.child("insurance_remaining_amount").getValue().toString();
                    summary_insurance_amount.setText(insurance_amount);
                } else {
                    database_ref.child("insurance_remaining_amount").setValue(0);
                    summary_insurance_amount.setText("0");
                }

                if (dataSnapshot.child("free_remaining_amount").exists()) {
                    String free_amount = dataSnapshot.child("free_remaining_amount").getValue().toString();
                    summary_free_amount.setText(free_amount);
                } else {
                    database_ref.child("free_remaining_amount").setValue(0);
                    summary_free_amount.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Display error message if an error occurs
                Toast.makeText(MainActivity.this, "Error Occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void readTotalIncome() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("incomelist").child(onlineUserID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataIncome data = dataSnapshot.getValue(DataIncome.class);
                    myDataList.add(data);
                }

                monthlyIncomeAdapter.notifyDataSetChanged();

                totalincome = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    double pTotal = Double.parseDouble(String.valueOf(total));
                    totalincome += pTotal;

                    summary_total_income.setText(df.format(totalincome));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void readTotalExpense() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("expenselist").child(onlineUserID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList2.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList2.add(data);
                }

                monthlyExpenseAdapter.notifyDataSetChanged();

                totalexpense = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    double pTotal = Double.parseDouble(String.valueOf(total));
                    totalexpense += (pTotal);

                    summary_total_expense.setText(df.format(totalexpense));

                    //Show percentage of Saving envelope
                    Query query1 = reference.orderByChild("item").equalTo("Saving");
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myDataList2.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Data data = dataSnapshot.getValue(Data.class);
                                myDataList2.add(data);
                            }

                            monthlyExpenseAdapter.notifyDataSetChanged();

                            totalexpenseSaving = 0;
                            String totalexpense1 = summary_total_expense.getText().toString();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                DecimalFormat df = new DecimalFormat("#.##");
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                Object total = map.get("amount");
                                double pTotal1 = Double.parseDouble(String.valueOf(total));
                                totalexpenseSaving += ((pTotal1 / Double.parseDouble(totalexpense1)) * 100);
                                tvSaving.setText(df.format(totalexpenseSaving));

                                pieChart.clearChart();
                                pieChart.addPieSlice(new PieModel("Saving", (float) totalexpenseSaving, Color.parseColor("#FFA726")));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                    //Show percentage of Food envelope
                    Query query2 = reference.orderByChild("item").equalTo("Food");
                    query2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myDataList2.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Data data = dataSnapshot.getValue(Data.class);
                                myDataList2.add(data);
                            }

                            monthlyExpenseAdapter.notifyDataSetChanged();

                            totalexpenseFood = 0;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                DecimalFormat df = new DecimalFormat("#.##");
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                Object total = map.get("amount");
                                double pTotal = Double.parseDouble(String.valueOf(total));
                                totalexpenseFood += ((pTotal / totalexpense) * 100);
                                tvFood.setText(df.format(totalexpenseFood));

                                pieChart.addPieSlice(new PieModel("Food", (float) totalexpenseFood, Color.parseColor("#66BB6A")));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                    //Show percentage of Travel envelope
                    Query query3 = reference.orderByChild("item").equalTo("Travel");
                    query3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myDataList2.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Data data = dataSnapshot.getValue(Data.class);
                                myDataList2.add(data);
                            }

                            monthlyExpenseAdapter.notifyDataSetChanged();

                            totalexpenseTravel = 0;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                DecimalFormat df = new DecimalFormat("#.##");
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                Object total = map.get("amount");
                                double pTotal = Double.parseDouble(String.valueOf(total));
                                totalexpenseTravel += ((pTotal / totalexpense) * 100);
                                tvTravel.setText(df.format(totalexpenseTravel));

                                pieChart.addPieSlice(new PieModel("Travel", (float) totalexpenseTravel, Color.parseColor("#EF5350")));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                    //Show percentage of Travel envelope
                    Query query4 = reference.orderByChild("item").equalTo("Improve");
                    query4.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myDataList2.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Data data = dataSnapshot.getValue(Data.class);
                                myDataList2.add(data);
                            }

                            monthlyExpenseAdapter.notifyDataSetChanged();

                            totalexpenseImprove = 0;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                DecimalFormat df = new DecimalFormat("#.##");
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                Object total = map.get("amount");
                                double pTotal = Double.parseDouble(String.valueOf(total));
                                totalexpenseImprove += ((pTotal / totalexpense) * 100);
                                tvImprove.setText(df.format(totalexpenseImprove));

                                pieChart.addPieSlice(new PieModel("Improve", (float) totalexpenseImprove, Color.parseColor("#29B6F6")));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                    //Show percentage of "Insurance" envelope
                    Query query5 = reference.orderByChild("item").equalTo("Insurance");
                    query5.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myDataList2.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Data data = dataSnapshot.getValue(Data.class);
                                myDataList2.add(data);
                            }

                            monthlyExpenseAdapter.notifyDataSetChanged();

                            totalexpenseInsurance = 0;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                DecimalFormat df = new DecimalFormat("#.##");
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                Object total = map.get("amount");
                                double pTotal = Double.parseDouble(String.valueOf(total));
                                totalexpenseInsurance += ((pTotal / totalexpense) * 100);
                                tvInsurance.setText(df.format(totalexpenseInsurance));

                                pieChart.addPieSlice(new PieModel("Insurance", (float) totalexpenseInsurance, Color.parseColor("#FF6200EE")));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                    //Show percentage of "Free" envelope
                    Query query6 = reference.orderByChild("item").equalTo("Free");
                    query6.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myDataList2.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Data data = dataSnapshot.getValue(Data.class);
                                myDataList2.add(data);
                            }

                            monthlyExpenseAdapter.notifyDataSetChanged();

                            totalexpenseFree = 0;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                DecimalFormat df = new DecimalFormat("#.##");
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                Object total = map.get("amount");
                                double pTotal = Double.parseDouble(String.valueOf(total));
                                totalexpenseFree += ((pTotal / totalexpense) * 100);
                                tvFree.setText(df.format(totalexpenseFree));

                                pieChart.addPieSlice(new PieModel("Free", (float) totalexpenseFree, Color.parseColor("#FFBB86FC")));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
                pieChart.startAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //close Drawer
        closeDrawer(drawerLayout);
    }
}