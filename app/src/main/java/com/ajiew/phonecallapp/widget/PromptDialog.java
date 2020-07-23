package com.ajiew.phonecallapp.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ajiew.phonecallapp.R;


public class PromptDialog extends AlertDialog {
    private Context mContext;
    private OnPromptButtonClickedListener mPromptButtonClickedListener;
    private String mTitle;
    private String mPositiveButton;
    private String mNegativeButton;
    private String mMessage;
    private int mLayoutResId;
    private boolean disableCancel;
    private int positiveTxtColor = 0;
    private int negativeTxtColor = 0;
    private TextView txtViewMessage;
    private boolean inputMode;
    private EditText popup_dialog_input_message;

    public static PromptDialog newInstance(final Context context, String title, String message) {
        return new PromptDialog(context, title, message);
    }

    public static PromptDialog newInstance(final Context context, String message) {
        return new PromptDialog(context, message);
    }

    public static PromptDialog newInstance(final Context context, String title, String message, String positiveButton) {
        return new PromptDialog(context, title, message, positiveButton);
    }

    public static PromptDialog newInstance(final Context context, String title, String message, boolean inputMode) {
        return new PromptDialog(context, title, message, inputMode);
    }

    public static PromptDialog newInstance(final Context context, String title, String message, String positiveButton, String negativeButton) {
        return new PromptDialog(context, title, message, positiveButton, negativeButton);
    }

    public PromptDialog(final Context context, String title, String message, String positiveButton, String negativeButton) {
        this(context, title, message, positiveButton);
        this.mNegativeButton = negativeButton;
    }

    public PromptDialog(final Context context, String title, String message, String positiveButton) {
        this(context, title, message);
        mPositiveButton = positiveButton;
    }

    public PromptDialog(final Context context, String title, String message) {
        super(context);
        mLayoutResId = R.layout.rce_dialog_popup_prompt;
        mContext = context;
        mTitle = title;
        mMessage = message;
    }

    public PromptDialog(final Context context, String title, String message, boolean inputMode) {
        super(context);
        mLayoutResId = R.layout.rce_dialog_popup_prompt;
        mContext = context;
        mTitle = title;
        mMessage = message;
        this.inputMode = inputMode;
    }

    public PromptDialog(final Context context, String message) {
        this(context, "", message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final View view = inflater.inflate(mLayoutResId, null);
        TextView txtViewTitle = (TextView) view.findViewById(R.id.popup_dialog_title);
        View buttonDivider = view.findViewById(R.id.button_divider);
        txtViewMessage = (TextView) view.findViewById(R.id.popup_dialog_message);
        popup_dialog_input_message = (EditText) view.findViewById(R.id.popup_dialog_input_message);
        popup_dialog_input_message.requestFocus();
        TextView txtViewOK = (TextView) view.findViewById(R.id.popup_dialog_button_ok);
        TextView txtViewCancel = (TextView) view.findViewById(R.id.popup_dialog_button_cancel);
        if (disableCancel) {
            txtViewCancel.setVisibility(View.GONE);
            buttonDivider.setVisibility(View.GONE);
        }
        if (positiveTxtColor != 0) {
            txtViewOK.setTextColor(positiveTxtColor);
        }
        if (negativeTxtColor != 0) {
            txtViewCancel.setTextColor(negativeTxtColor);
        }
        txtViewOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPromptButtonClickedListener != null) {
                    String s = mMessage;
                    if (inputMode) {
                        s = popup_dialog_input_message.getText().toString();
                    }

                    mPromptButtonClickedListener.onPositiveButtonClicked(s);
                }
                dismiss();
            }
        });
        txtViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPromptButtonClickedListener != null) {
                    mPromptButtonClickedListener.onNegativeButtonClicked();
                }
                dismiss();
            }
        });
        if (!TextUtils.isEmpty(mTitle)) {
            txtViewTitle.setText(mTitle);
            txtViewTitle.setVisibility(View.VISIBLE);
            view.findViewById(R.id.title_divider).setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(mPositiveButton)) {
            txtViewOK.setText(mPositiveButton);
        }

        if (!TextUtils.isEmpty(mNegativeButton)) {
            txtViewCancel.setText(mNegativeButton);
        }

        txtViewMessage.setText(mMessage);
        txtViewMessage.setVisibility(inputMode ? View.GONE : View.VISIBLE);
        popup_dialog_input_message.setVisibility(inputMode ? View.VISIBLE : View.GONE);
        setContentView(view);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                popup_dialog_input_message.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(popup_dialog_input_message, 0);
                    }
                }, 200);

            }
        });
    }

    public void disableCancelButton() {
        disableCancel = true;
    }

    public PromptDialog setPromptButtonClickedListener(OnPromptButtonClickedListener buttonClickedListener) {
        this.mPromptButtonClickedListener = buttonClickedListener;
        return this;
    }

    public PromptDialog setLayoutRes(int resId) {
        this.mLayoutResId = resId;
        return this;
    }

    public void setPositiveTextColor(int color) {
        positiveTxtColor = color;
    }

    public void setNegativeTextColor(int color) {
        negativeTxtColor = color;
    }

    public View getEditText() {
        return txtViewMessage;
    }

    public interface OnPromptButtonClickedListener {
        void onPositiveButtonClicked(String msg);

        void onNegativeButtonClicked();
    }

    private int gePopupWidth() {
        int distanceToBorder = (int) mContext.getResources().getDimension(R.dimen.dp_40);
        return getScreenWidth() - 2 * (distanceToBorder);
    }

    private int getScreenWidth() {
        return ((WindowManager) (mContext.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getWidth();
    }
}
