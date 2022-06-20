package ex.next.watertanker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ex.next.watertanker.common.AdminRecyclerClickInterface;
import ex.next.watertanker.common.RecyclerAdapter;
import ex.next.watertanker.common.RecyclerCallback;
import ex.next.watertanker.common.SharedClass;
import ex.next.watertanker.databinding.ActivityAdminBinding;
import ex.next.watertanker.databinding.ItemsSupplierBinding;
import ex.next.watertanker.databinding.ItemsUsersBinding;
import ex.next.watertanker.models.Orders;
import ex.next.watertanker.models.Users;
import ex.next.watertanker.utils.Utils;

public class AdminActivity extends AppCompatActivity implements AdminRecyclerClickInterface {

    List<Users> allUsers = new ArrayList<>();
    List<Users> allSuppliers = new ArrayList<>();
    Utils utils;
    ActivityAdminBinding binding;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(Constants.DATABASE_REF_URL)
            .child(Constants.Users);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin);
        setContentView(binding.getRoot());

        utils = Utils.getInstance(this);
        binding.toolbar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showExitDialogue(AdminActivity.this);
            }
        });
        binding.toolbar.ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showExitDialogue(AdminActivity.this);
            }
        });
        binding.toolbar.tvTitle.setText("Admin");

        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSuppliers.setLayoutManager(new LinearLayoutManager(this));

        binding.rgType.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_users:
                    SharedClass.type = 0;
                    binding.rvUsers.setVisibility(View.VISIBLE);
                    binding.rvSuppliers.setVisibility(View.GONE);
                    break;
                case R.id.rb_suppliers:
                    SharedClass.type = 1;
                    binding.rvUsers.setVisibility(View.GONE);
                    binding.rvSuppliers.setVisibility(View.VISIBLE);
                    break;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SharedClass.type == 0) {
            binding.rbUsers.setChecked(true);
            binding.rbSuppliers.setChecked(false);
        } else {
            binding.rbUsers.setChecked(false);
            binding.rbSuppliers.setChecked(true);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allUsers.clear();
                allSuppliers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue(Users.class).getType().equals(Constants.TYPE_USER)) {
                        allUsers.add(snapshot.getValue(Users.class));
                    }
                    if (snapshot.getValue(Users.class).getType().equals(Constants.TYPE_SUPPLIER)) {
                        allSuppliers.add(snapshot.getValue(Users.class));
                    }
                }
                if (allUsers.size() > 0) {
                    populateUsersRv((ArrayList<Users>) allUsers);
                }
                if (allSuppliers.size() > 0) {
                    populateSuppliersRv((ArrayList<Users>) allSuppliers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled: ", databaseError.toException().toString());
            }
        });
    }

    private void populateUsersRv(ArrayList<Users> allUsers) {
        RecyclerAdapter adapter = new RecyclerAdapter(this, allUsers, R.layout.items_users, new RecyclerCallback<ItemsUsersBinding, Users>() {
            @Override
            public void bindData(ItemsUsersBinding binder, Users model) {
                Log.d("aluserzsiss", model.getfName() + "");
                setRecyclerData(binder, model);
            }
        });

        binding.rvUsers.setAdapter(adapter);
    }

    private void populateSuppliersRv(ArrayList<Users> allSuppliers) {
        RecyclerAdapter adapter = new RecyclerAdapter(this, allSuppliers, R.layout.items_users, new RecyclerCallback<ItemsUsersBinding, Users>() {
            @Override
            public void bindData(ItemsUsersBinding binder, Users model) {
                setRecyclerData(binder, model);
            }
        });

        binding.rvSuppliers.setAdapter(adapter);
    }

    private void setRecyclerData(ItemsUsersBinding binder, Users model) {
        binder.setUsers(model);
        binder.setHandler(this);
    }

    @Override
    public void onClick(Users user) {

    }

    @Override
    public void onBlock(Users users) {
        databaseReference.child(users.getKey()).child("userStatus").setValue(Constants.INACTIVE, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(AdminActivity.this, "Blocked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUnblock(Users users) {
        databaseReference.child(users.getKey()).child("userStatus").setValue(Constants.ACTIVE, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(AdminActivity.this, "Unblocked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDelete(Users users) {
        databaseReference.child(users.getKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(AdminActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}