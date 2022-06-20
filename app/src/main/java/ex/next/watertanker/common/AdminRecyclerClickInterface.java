package ex.next.watertanker.common;

import ex.next.watertanker.models.Orders;
import ex.next.watertanker.models.Users;

public interface AdminRecyclerClickInterface {
    public void onClick(Users user);
    public void onBlock(Users users);
    public void onUnblock(Users users);
    public void onDelete(Users users);
}