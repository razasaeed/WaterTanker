package ex.next.watertanker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ex.next.watertanker.common.RecyclerAdapter;
import ex.next.watertanker.common.RecyclerCallback;
import ex.next.watertanker.common.RecyclerClickInterface;
import ex.next.watertanker.databinding.ActivityOrdersBinding;
import ex.next.watertanker.databinding.ActivitySupplierBinding;
import ex.next.watertanker.databinding.ItemsOrdersBinding;
import ex.next.watertanker.databinding.ItemsSupplierBinding;
import ex.next.watertanker.models.Orders;
import ex.next.watertanker.models.Users;
import ex.next.watertanker.utils.PrefsUtils;
import ex.next.watertanker.utils.Utils;

public class OrdersActivity extends AppCompatActivity implements RecyclerClickInterface {

    PrefsUtils prefsUtils;
    List<Orders> allOrders = new ArrayList<>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(Constants.DATABASE_REF_URL)
            .child(Constants.Orders);
    ActivityOrdersBinding binding;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_orders);
        setContentView(binding.getRoot());

        prefsUtils = new PrefsUtils(this);
        utils = Utils.getInstance(this);

        binding.toolbar.ivLogout.setOnClickListener( view -> {
            utils.showLogoutDialogue(this, prefsUtils, LoginScreen.class);
        });

        binding.toolbar.ivBack.setOnClickListener( view -> {
            onBackPressed();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    allOrders.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Orders orders = data.getValue(Orders.class);
                        if (orders.getKey().contains(utils.users.getKey())) {
                            allOrders.add(orders);
                        }
                    }
                    if (allOrders.size() > 0) {
                        binding.rvSuppliers.setVisibility(View.VISIBLE);
                        binding.tvNoData.setVisibility(View.GONE);
                        populateOrdersRv((ArrayList<Orders>) allOrders);
                    } else {
                        binding.rvSuppliers.setVisibility(View.GONE);
                        binding.tvNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.rvSuppliers.setVisibility(View.GONE);
                    binding.tvNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled: ", databaseError.toException().toString());
            }
        });
    }

    private void populateOrdersRv(ArrayList<Orders> allOrders) {
        RecyclerAdapter adapter = new RecyclerAdapter(this, allOrders, R.layout.items_orders, new RecyclerCallback<ItemsOrdersBinding, Orders>() {
            @Override
            public void bindData(ItemsOrdersBinding binder, Orders model) {
                setRecyclerData(binder, model);
            }
        });

        binding.rvSuppliers.setAdapter(adapter);
    }

    private void setRecyclerData(ItemsOrdersBinding binder, Orders model) {
        binder.setOrders(model);
        binder.setHandler(this);
    }

    @Override
    public void onClick(Users user) {

    }

    @Override
    public void onOrderClick(Orders orders) {

    }

    @Override
    public void onAccept(Orders orders) {
    }

    @Override
    public void onReject(Orders orders) {
    }

    @Override
    public void onTimeSelection(Orders orders) {

    }
}