package ex.next.watertanker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ex.next.watertanker.databinding.ActivityRegisterBinding;
import ex.next.watertanker.models.Users;
import ex.next.watertanker.utils.Utils;
import ex.next.watertanker.utils.Validations;
import im.delight.android.location.SimpleLocation;

public class RegisterScreen extends AppCompatActivity {

    private SimpleLocation location;
    Utils utils;
    FirebaseAuth auth = null;
    ActivityRegisterBinding binding;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(Constants.DATABASE_REF_URL)
            .child(Constants.Users);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        setContentView(binding.getRoot());


        utils = Utils.getInstance(this);
        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }
        utils.latitude = location.getLatitude();
        utils.longitude = location.getLongitude();

        location.setListener(() -> {
            utils.latitude = location.getLatitude();
            utils.longitude = location.getLongitude();
        });

        auth = FirebaseAuth.getInstance();

        binding.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    binding.etFullPrice.setVisibility(View.GONE);
                    binding.etHalfPrice.setVisibility(View.GONE);
                } else if (i == 2) {
                    binding.etFullPrice.setVisibility(View.VISIBLE);
                    binding.etHalfPrice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnRegister.setOnClickListener(view -> {
            if (binding.etFname.getText().toString().isEmpty()) {
                binding.etFname.setError(getString(R.string.required));
            } else if (binding.etLname.getText().toString().isEmpty()) {
                binding.etLname.setError(getString(R.string.required));
            } else if (binding.etEmail.getText().toString().isEmpty()) {
                binding.etEmail.setError(getString(R.string.required));
            } else if (!Validations.isValidEmail(binding.etEmail.getText().toString())) {
                binding.etEmail.setError(getString(R.string.invalid_email));
            } else if (binding.etPhone.getText().toString().isEmpty()) {
                binding.etPhone.setError(getString(R.string.required));
            } else if (binding.etCnic.getText().toString().isEmpty()) {
                binding.etCnic.setError(getString(R.string.required));
            } else if (binding.etPassword.getText().toString().isEmpty()) {
                binding.etPassword.setError(getString(R.string.required));
            } else if (binding.spType.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Choose type", Toast.LENGTH_SHORT).show();
            } else if (binding.spType.getSelectedItem().toString().equals(Constants.TYPE_SUPPLIER) && binding.etFullPrice.getText().toString().isEmpty()) {
                binding.etFullPrice.setError(getString(R.string.required));
            } else if (binding.spType.getSelectedItem().toString().equals(Constants.TYPE_SUPPLIER) && binding.etHalfPrice.getText().toString().isEmpty()) {
                binding.etHalfPrice.setError(getString(R.string.required));
            } else {
                Log.d("checkflow", "here");
                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                        updateDatabase(
                                user.getUid(),
                                binding.etFname.getText().toString(),
                                binding.etLname.getText().toString(),
                                binding.etEmail.getText().toString(),
                                binding.etPhone.getText().toString(),
                                binding.etCnic.getText().toString(),
                                binding.etPassword.getText().toString(),
                                binding.spType.getSelectedItem().toString()
                        );
                    } else {
                        Toast.makeText(
                                this, "Authentication failed.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });

        binding.tvLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginScreen.class));
        });

    }

    @Override
    protected void onPause() {
        location.endUpdates();
        super.onPause();
    }

    @Override
    protected void onResume() {
        location.beginUpdates();
        super.onResume();
    }

    private void updateDatabase(
            String key,
            String fName,
            String lName,
            String email,
            String phone,
            String cnic,
            String password,
            String type
    ) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = new Users(key, fName, lName, email, phone, cnic, password, type, String.valueOf(utils.latitude),
                        String.valueOf(utils.longitude), binding.etFullPrice.getText().toString(), binding.etHalfPrice.getText().toString(), "address",
                        Constants.ACTIVE);
                databaseReference.child(key).setValue(users, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(RegisterScreen.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
