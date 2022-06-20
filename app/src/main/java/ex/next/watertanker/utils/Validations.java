package ex.next.watertanker.utils;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Validations {

    public static boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
