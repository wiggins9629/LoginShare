package com.yunjin.microlove.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.yunjin.microlove.R;
import com.yunjin.microlove.adapter.DialogBottomGridAdapter;
import com.yunjin.microlove.bean.DialogGrid;
import com.yunjin.microlove.listener.OnDataListPositionListener;
import com.yunjin.microlove.widget.loading.ShapeLoadingDialog;

import java.util.List;

/**
 * @Description Dialog工具类
 * @Author 一花一世界
 */
public class DialogUtil {

    private static ShapeLoadingDialog shapeLoadingDialog;

    /**
     * @Description 显示加载状态框
     */
    public static void showDialogLoading(Context context, String message) {
        if (shapeLoadingDialog != null) {
            hideDialogLoading();
            shapeLoadingDialog = new ShapeLoadingDialog(context);
        } else {
            shapeLoadingDialog = new ShapeLoadingDialog(context);
        }

        if (!StringUtil.isEmpty(message)) {
            shapeLoadingDialog.setLoadingText(message);
        } else {
            shapeLoadingDialog.setLoadingText(UIUtils.getString(R.string.loading));
        }

        shapeLoadingDialog.setCanceledOnTouchOutside(false);
        shapeLoadingDialog.show();
    }

    /**
     * @Description 关闭加载框
     */
    public static void hideDialogLoading() {
        if (shapeLoadingDialog != null) {
            if (shapeLoadingDialog.isShowing()) {
                shapeLoadingDialog.dismiss();
            }
            shapeLoadingDialog = null;
        }
    }

    /**
     * @param context    上下文
     * @param dialogList 数据集合
     * @param listener   按键监听
     * @Description 底部Grid列表Dialog
     */
    public static void showBottomGridSelection(Context context, List<DialogGrid> dialogList, final OnDataListPositionListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.theme_dialog);
        View view = UIUtils.inflate(R.layout.dialog_bottom_grid_selection);
        GridView mGvGrid = (GridView) view.findViewById(R.id.gv_grid);
        Button mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();
        DialogBottomGridAdapter dialogBottomGrid = null;
        if (dialogBottomGrid == null) {
            dialogBottomGrid = new DialogBottomGridAdapter(context, dialogList);
            mGvGrid.setAdapter(dialogBottomGrid);
        }
        mGvGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (listener != null) {
                    listener.onSelectItem(position);
                }
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.getWindow().setWindowAnimations(R.style.dialogListWindowAnim);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        // 设置横向全屏显示
        WindowManager windowManager = dialog.getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth();
        dialog.getWindow().setAttributes(lp);

        dialog.setContentView(view);
    }
}
