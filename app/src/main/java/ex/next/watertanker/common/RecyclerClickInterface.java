package ex.next.watertanker.common;

import ex.next.watertanker.models.Orders;
import ex.next.watertanker.models.Users;

public interface RecyclerClickInterface {
    public void onClick(Users user);
    public void onOrderClick(Orders orders);
    public void onAccept(Orders orders);
    public void onReject(Orders orders);
    public void onTimeSelection(Orders orders);
}