package ex.next.watertanker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import ex.next.watertanker.databinding.ActivityLoginBinding;
import ex.next.watertanker.models.Users;
import ex.next.watertanker.utils.PrefsUtils;
import ex.next.watertanker.utils.Utils;
import ex.next.watertanker.utils.Validations;

public class LoginScreen extends AppCompatActivity {

    PrefsUtils prefsUtils;
    Utils utils;
    FirebaseAuth auth;
    ActivityLoginBinding binding;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(Constants.DATABASE_REF_URL)
            .child(Constants.Users);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        prefsUtils = new PrefsUtils(this);
        utils = Utils.getInstance(this);
        auth = FirebaseAuth.getInstance();

        if (auth != null) {
            if (prefsUtils.checkKey(Constants.USER_DATA)) {
                if (((Users) prefsUtils.getFromPrefs(Constants.USER_DATA)).getType().equals(Constants.TYPE_USER)) {
                    startActivity(new Intent(LoginScreen.this, UserActivity.class));
                    finish();
                }
                if (((Users) prefsUtils.getFromPrefs(Constants.USER_DATA)).getType().equals(Constants.TYPE_SUPPLIER)) {
                    startActivity(new Intent(LoginScreen.this, SupplierActivity.class));
                    finish();
                }
            }
        } else {
            setContentView(binding.getRoot());
        }
        binding.btnLogin.setOnClickListener(view -> {
            if (binding.etEmail.getText().toString().isEmpty()) {
                binding.etEmail.setError(getString(R.string.required));
            } else if (!Validations.isValidEmail(binding.etEmail.getText().toString())) {
                binding.etEmail.setError(getString(R.string.invalid_email));
            } else if (binding.etPassword.getText().toString().isEmpty()) {
                binding.etPassword.setError(getString(R.string.required));
            } else {
                if (binding.etEmail.getText().toString().equals(getString(R.string.admin_email)) &&
                        binding.etPassword.getText().toString().equals(getString(R.string.admin_password))) {
                    startActivity(new Intent(LoginScreen.this, AdminActivity.class));
                } else {
                    auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Log.d("uidiz", user.getUid());

                            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    utils.users = dataSnapshot.getValue(Users.class);
                                    if (dataSnapshot.getValue(Users.class).getUserStatus().equals(Constants.ACTIVE)) {
                                        prefsUtils.saveToPrefs(Constants.USER_DATA, dataSnapshot.getValue(Users.class));
                                        if (utils.users.getType().equals(Constants.TYPE_USER)) {
                                            startActivity(new Intent(LoginScreen.this, UserActivity.class));
                                        }
                                        if (utils.users.getType().equals(Constants.TYPE_SUPPLIER)) {
                                            startActivity(new Intent(LoginScreen.this, SupplierActivity.class));
                                        }
                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        prefsUtils.deleteAll();
                                        Toast.makeText(LoginScreen.this, getString(R.string.account_disabled), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("onCancelled: ", databaseError.toException().toString());
                                }
                            });

                        } else {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        binding.tvRegister.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterScreen.class));
        });

        binding.tvForgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgetScreen.class));
        });

    }
}
