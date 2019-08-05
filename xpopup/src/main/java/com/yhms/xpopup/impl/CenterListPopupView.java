package com.yhms.xpopup.impl;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yhms.xpopup.R;
import com.yhms.xpopup.core.CenterPopupView;

/**
 * Description: 在中间的列表对话框
 * Create by dance, at 2018/12/16
 */
public class CenterListPopupView extends CenterPopupView {
    RecyclerView recyclerView;
    TextView tv_title;
    private RecyclerView.Adapter mAdapter;
    private String title;

    public CenterListPopupView(@NonNull Context context) {
        super(context);
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public CenterListPopupView bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    public CenterListPopupView bindTitle(String title) {
        this.title = title;
        return this;
    }

    public CenterListPopupView bindAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_center_impl_list : bindLayoutId;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        recyclerView = findViewById(R.id.recyclerView);
        tv_title = findViewById(R.id.tv_title);
        if(tv_title!=null){
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }
        recyclerView.setAdapter(this.mAdapter);
    }

    @Override
    protected int getMaxWidth() {
        return popupInfo.maxWidth == 0 ? (int) (super.getMaxWidth() * .8f)
                : popupInfo.maxWidth;
    }
}
