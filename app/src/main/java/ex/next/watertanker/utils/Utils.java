package ex.next.watertanker.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ex.next.watertanker.Constants;
import ex.next.watertanker.LoginScreen;
import ex.next.watertanker.R;
import ex.next.watertanker.RegisterScreen;
import ex.next.watertanker.databinding.ExitDialogBinding;
import ex.next.watertanker.models.Orders;
import ex.next.watertanker.models.Users;

public class Utils {

    private static Utils utils = null;
    public static Users users;
    public static PrefsUtils prefsUtils;

    public static Utils getInstance(Context context) {
        if (utils == null) {
            utils = new Utils();
        }
        prefsUtils = new PrefsUtils(context);
        users = ((Users) prefsUtils.getFromPrefs(Constants.USER_DATA));
        return utils;
    }
    public double latitude = 0.0;
    public double longitude = 0.0;

    public void showExitDialogue(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.exit_dialog);

        TextView textNo = dialog.findViewById(R.id.textNo);
        TextView textDisconnect = dialog.findViewById(R.id.textDisconnect);
        TextView tvDescription = dialog.findViewById(R.id.tvDescription);
        tvDescription.setText(String.format(context.getString(R.string.are_you_sure_you_want_to_exit), "exit"));

        textNo.setOnClickListener(view -> {
            dialog.dismiss();
        });

        textDisconnect.setOnClickListener(view -> {
            ((Activity) context).finishAffinity();
            dialog.dismiss();
        });

        dialog.show();
    }

    public void showLogoutDialogue(Context context, PrefsUtils prefsUtils, Class<LoginScreen> loginScreenClass) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.exit_dialog);

        TextView textNo = dialog.findViewById(R.id.textNo);
        TextView textDisconnect = dialog.findViewById(R.id.textDisconnect);
        TextView tvDescription = dialog.findViewById(R.id.tvDescription);
        tvDescription.setText(String.format(context.getString(R.string.are_you_sure_you_want_to_exit), "logout"));

        textNo.setOnClickListener(view -> {
            dialog.dismiss();
        });

        textDisconnect.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            prefsUtils.deleteAll();
            context.startActivity(new Intent(context, loginScreenClass));
            dialog.dismiss();
            ((Activity) context).finishAffinity();
        });

        dialog.show();
    }

    public void cancelOrderDialogue(Context context, DatabaseReference child, String desc) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.cancel_order_dialog);

        TextView textNo = dialog.findViewById(R.id.textNo);
        TextView textDisconnect = dialog.findViewById(R.id.textDisconnect);
        TextView tvDescription = dialog.findViewById(R.id.tvDescription);
        tvDescription.setText(desc);

        textNo.setOnClickListener(view -> {
            dialog.dismiss();
        });

        textDisconnect.setOnClickListener(view -> {
            child.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(context, context.getString(R.string.order_cancelled), Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
        });

        dialog.show();
    }

    public void orderClickDialogue(Context context, DatabaseReference child, String desc) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.cancel_order_dialog);

        TextView textNo = dialog.findViewById(R.id.textNo);
        TextView textDisconnect = dialog.findViewById(R.id.textDisconnect);
        TextView tvDescription = dialog.findViewById(R.id.tvDescription);
        tvDescription.setText(desc);

        textNo.setOnClickListener(view -> {
            dialog.dismiss();
        });

        textDisconnect.setOnClickListener(view -> {
            child.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(context, context.getString(R.string.order_cancelled), Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
        });

        dialog.show();
    }

    public void showSupplierDetailsDialogue(Context context, Users suppliers, Users user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.DATABASE_REF_URL)
                .child(Constants.Orders);

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.supplier_details_layout);

        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.80);

        dialog.getWindow().setLayout(width, height);

        TextView tv_name = dialog.findViewById(R.id.tv_name);
        TextView tv_email = dialog.findViewById(R.id.tv_email);
        TextView tv_phone = dialog.findViewById(R.id.tv_phone);
        TextView tv_cnic = dialog.findViewById(R.id.tv_cnic);
        TextView tv_tanker_full = dialog.findViewById(R.id.tv_tanker_full);
        TextView tv_tanker_half = dialog.findViewById(R.id.tv_tanker_half);
        Spinner sp_tanker = dialog.findViewById(R.id.sp_tanker);
        Button btn_place_order = dialog.findViewById(R.id.btn_place_order);
        TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);

        tv_name.setText(context.getString(R.string.full_name) + " " + suppliers.getfName() + " " + suppliers.getlName());
        tv_email.setText(context.getString(R.string.email) + " " + suppliers.getEmail());
        tv_phone.setText(context.getString(R.string.phone) + " " + suppliers.getPhone());
        tv_cnic.setText(context.getString(R.string.cnic) + " " + suppliers.getCnic());
        tv_tanker_full.setText(context.getString(R.string.full_price) + " " + suppliers.getFullTankerPrice());
        tv_tanker_half.setText(context.getString(R.string.half_price) + " " + suppliers.getHalfTankerPrice());

        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        btn_place_order.setOnClickListener(view -> {
            /*context.startActivity(new Intent(context, loginScreenClass));
            ((Activity)context).finish();*/
            if (sp_tanker.getSelectedItemPosition() == 0) {
                Toast.makeText(context, context.getString(R.string.choose_type), Toast.LENGTH_SHORT).show();
            } else {
                String type, price;
                if (sp_tanker.getSelectedItem().toString().equals(Constants.Full)) {
                    type = Constants.Full;
                    price = suppliers.getFullTankerPrice();
                } else {
                    type = Constants.Half;
                    price = suppliers.getHalfTankerPrice();
                }
                Orders orders = new Orders(user.getKey().concat(suppliers.getKey()), user.getfName(), user.getlName(), user.getEmail(), user.getPhone(), user.getCnic(), type,
                        user.getLatitude(), user.getLongitude(), price, user.getAddress(), Constants.PENDING, suppliers.getfName()+" "+suppliers.getlName(),
                        suppliers.getPhone(), suppliers.getEmail(), suppliers.getAddress(), suppliers.getLatitude(), suppliers.getLongitude(), currentTime(), "");

                databaseReference.child(user.getKey().concat(suppliers.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(context, context.getString(R.string.already_ordered), Toast.LENGTH_SHORT).show();
                            cancelOrderDialogue(context, databaseReference.child(user.getKey().concat(suppliers.getKey())), context.getString(R.string.cancel_order));
                        } else {
                            databaseReference.child(user.getKey().concat(suppliers.getKey())).setValue(orders, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(context, context.getString(R.string.order_created), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        dialog.show();
    }

    public String currentTime() {
        DateFormat df = new SimpleDateFormat(Constants.TIME_FORMAT);
        String time = df.format(Calendar.getInstance().getTime());
        return time;
    }

    public String getTime(int hr,int min) {
        Time tme = new Time(hr,min,0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat(Constants.TIME_FORMAT);
        return formatter.format(tme);
    }

}
