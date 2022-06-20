package ex.next.watertanker.common;

import androidx.databinding.ViewDataBinding;

public interface RecyclerCallback<VM extends ViewDataBinding, T> {
    public void bindData(VM binder,T model);
}
