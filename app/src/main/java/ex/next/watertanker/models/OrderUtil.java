package ex.next.watertanker.models;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import ex.next.watertanker.Constants;

public class OrderUtil {

    public boolean getStatus(String status) {
        if (status.equals(Constants.ACCEPT)) {
            return true;
        } else {
            return false;
        }
    }

}
