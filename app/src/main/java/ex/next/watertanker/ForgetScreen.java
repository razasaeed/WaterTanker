package ex.next.watertanker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ex.next.watertanker.databinding.ActivityForgetBinding;
import ex.next.watertanker.utils.Validations;

public class ForgetScreen extends AppCompatActivity {

    ActivityForgetBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget);
        setContentView(binding.getRoot());

        binding.btnRecover.setOnClickListener( view -> {
            if (binding.etEmail.getText().toString().isEmpty()) {
                binding.etEmail.setError(getString(R.string.required));
            } else if (!Validations.isValidEmail(binding.etEmail.getText().toString())) {
                binding.etEmail.setError(getString(R.string.invalid_email));
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(binding.etEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetScreen.this, getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ForgetScreen.this, getString(R.string.email_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.tvRemember.setOnClickListener( view -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }
}
