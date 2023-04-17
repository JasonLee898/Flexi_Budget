package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MonthlyIncomeAdapter extends RecyclerView.Adapter<MonthlyIncomeAdapter.ViewHolder>{

    private Context hContext;
    private List<DataIncome> myDataList2;
    private String postid;
    private double amount;
    private String item;
    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private DatabaseReference database_ref;

    public MonthlyIncomeAdapter(Context hContext, List<DataIncome> myDataList2) {
        this.hContext = hContext;
        this.myDataList2 = myDataList2;
        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(hContext).inflate(R.layout.income_itemdisplay, parent, false);
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);
        return new MonthlyIncomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final DataIncome data = myDataList2.get(position);
        holder.item.setText("Budget Type: "+data.getItem());
        holder.amount.setText("Spent: RM"+data.getAmount());
        holder.date.setText("Date: "+data.getDate());

        switch (data.getItem()){
            case"Saving":
                holder.imageView.setImageResource(R.drawable.savings);
                break;
            case"Food":
                holder.imageView.setImageResource(R.drawable.food);
                break;
            case"Travel":
                holder.imageView.setImageResource(R.drawable.travel);
                break;
            case"Improve":
                holder.imageView.setImageResource(R.drawable.improve);
                break;
            case"Insurance":
                holder.imageView.setImageResource(R.drawable.insurance);
                break;
            case"Free":
                holder.imageView.setImageResource(R.drawable.free);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postid = data.getId();
                amount = data.getAmount();
                item = data.getItem();

                updateIncome();
            }
        });
    }

    private void updateIncome() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(hContext);
        LayoutInflater inflater = LayoutInflater.from(hContext);
        View myView = inflater.inflate(R.layout.updateincome, null);

        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();

        final TextView hItem = myView.findViewById(R.id.item);
        final EditText hAmount = myView.findViewById(R.id.amount);
        final EditText hNote = myView.findViewById(R.id.note);

        hItem.setText(item);

        hAmount.setText(String.valueOf(amount));
        hAmount.setSelection(String.valueOf(amount).length());

        Button Btnupdate  = myView.findViewById(R.id.update);
        Button Btndelete = myView.findViewById(R.id.delete);

        Btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Double.parseDouble(hAmount.getText().toString());

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                String date = dateFormat.format(calendar.getTime());

                DataIncome data = new DataIncome(item, date ,postid, amount);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("incomelist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                    DatabaseReference finalRemainingRef = remainingRef;
                    budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            double currentBudgetAmount = snapshot.getValue(Double.class);
                            double newRemainingAmount = currentBudgetAmount + Double.parseDouble(hAmount.getText().toString());
                            finalRemainingRef.setValue(newRemainingAmount);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }

                reference.child(postid).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(hContext, "Updated successfully!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(hContext, "Failed to update!" +task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        Btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    DatabaseReference finalRemainingRef = remainingRef;
                    budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            double currentBudgetAmount = snapshot.getValue(Double.class);

                            // Calculate the new remaining amount after deducting hAmount from currentBudgetAmount
                            double newRemainingAmount = currentBudgetAmount - Double.parseDouble(hAmount.getText().toString()) ;

                            // Update the value of the remaining amount node with the new remaining amount
                            finalRemainingRef.setValue(newRemainingAmount);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error
                        }
                    });
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("incomelist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(postid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(hContext, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(hContext, "Failed to delete!" +task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return myDataList2.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item, amount, date;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}