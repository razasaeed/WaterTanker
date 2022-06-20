package ex.next.watertanker;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ex.next.watertanker.common.RecyclerAdapter;
import ex.next.watertanker.common.RecyclerCallback;
import ex.next.watertanker.common.RecyclerClickInterface;
import ex.next.watertanker.databinding.ActivitySupplierBinding;
import ex.next.watertanker.databinding.ItemsSupplierBinding;
import ex.next.watertanker.databinding.RowItemBinding;
import ex.next.watertanker.models.OrderUtil;
import ex.next.watertanker.models.Orders;
import ex.next.watertanker.models.Users;
import ex.next.watertanker.utils.PrefsUtils;
import ex.next.watertanker.utils.Utils;

public class SupplierActivity extends AppCompatActivity implements RecyclerClickInterface {

    OrderUtil orderUtil;
    PrefsUtils prefsUtils;
    List<Orders> allOrders = new ArrayList<>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(Constants.DATABASE_REF_URL)
            .child(Constants.Orders);
    ActivitySupplierBinding binding;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_supplier);
        setContentView(binding.getRoot());

        orderUtil = new OrderUtil();
        prefsUtils = new PrefsUtils(this);
        utils = Utils.getInstance(this);
        binding.toolbar.tvTitle.setText((utils.users.getfName()+" "+utils.users.getlName()));

        binding.toolbar.ivLogout.setOnClickListener( view -> {
            utils.showLogoutDialogue(this, prefsUtils, LoginScreen.class);
        });

        binding.toolbar.ivBack.setOnClickListener( view -> {
            utils.showExitDialogue(this);
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
                        if ((data.getValue(Orders.class).getStatus().equals(Constants.ACCEPT) || data.getValue(Orders.class).getStatus().equals(Constants.PENDING)) && data.getValue(Orders.class).getKey().contains(utils.users.getKey())) {
                            allOrders.add(data.getValue(Orders.class));
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
        RecyclerAdapter adapter = new RecyclerAdapter(this, allOrders, R.layout.items_supplier, new RecyclerCallback<ItemsSupplierBinding, Orders>() {
            @Override
            public void bindData(ItemsSupplierBinding binder, Orders model) {
//                binder.btnAccept.setVisibility(orderUtil.getStatus(model.getStatus()) ? GONE : VISIBLE);
                setRecyclerData(binder, model);
            }
        });

        binding.rvSuppliers.setAdapter(adapter);
    }

    private void setRecyclerData(ItemsSupplierBinding binder, Orders model) {
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
        databaseReference.child(orders.getKey()).child("status").setValue(Constants.ACCEPT, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(SupplierActivity.this, getString(R.string.order_accepted), Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
            }
        });
    }

    @Override
    public void onReject(Orders orders) {
        utils.cancelOrderDialogue(this, databaseReference.child(orders.getKey()), getString(R.string.reject_order));
    }

    @Override
    public void onTimeSelection(Orders orders) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(SupplierActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                databaseReference.child(orders.getKey()).child("delivery_time").setValue(utils.getTime(selectedHour, selectedMinute), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(SupplierActivity.this, getString(R.string.delivery_time_updated), Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
                    }
                });
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
}