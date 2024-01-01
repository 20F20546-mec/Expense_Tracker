package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView incomeTotalSum;
    //1-edit

    public IncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static IncomeFragment newInstance(String param1, String param2) {
//        IncomeFragment fragment = new IncomeFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    //Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recycler View
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    //1-edit

    //Update edit text
    private EditText editAmount;
    private EditText editType;
    private EditText editNote;

    //Button for update and delete
    private Button btnUpdate;
    private Button btnDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance("https://expense-tracker-14b95-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("IncomeData").child(uid);

        //1-edit
        incomeTotalSum = myView.findViewById(R.id.income_txt_result);
        //1-edit

        recyclerView = myView.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //1-edit
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_value = 0;
                for(DataSnapshot mySnapshot: snapshot.getChildren()) {
                    Data data = mySnapshot.getValue(Data.class);
                    total_value = total_value + data.getAmount();
                    String st_total_value = String.valueOf(total_value);

                    incomeTotalSum.setText(st_total_value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //1-edit

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mIncomeDatabase, Data.class).build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
               return new MyViewHolder(myView);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateDataItem();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_income_txt);
            mType.setText(type);
        }

        void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_income_txt);
            mNote.setText(note);
        }

        void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_income_txt);
            mDate.setText(date);
        }

        void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.amount_income_txt);
            String st_amount = String.valueOf(amount);
            mAmount.setText(st_amount);
        }
    }

    public void updateDataItem() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.update_data_item, null);

        mydialog.setView(myView);

        editAmount = myView.findViewById(R.id.amount_edit);
        editType = myView.findViewById(R.id.type_edit);
        editNote = myView.findViewById(R.id.note_edit);

        btnUpdate = myView.findViewById(R.id.btnUpdate);
        btnDelete = myView.findViewById(R.id.btnDelete);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}