package com.ajiew.phonecallapp.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ajiew.phonecallapp.R;
import com.ajiew.phonecallapp.db.DisturbType;


public class AddAddressOrPhoneDialog extends AlertDialog {
    private Context mContext;
    private OnButtonClickedListener mPromptButtonClickedListener;

    private int mLayoutResId;

    private int positiveTxtColor = 0;
    private int negativeTxtColor = 0;

    private EditText popup_dialog_input_message;
    private RadioGroup disturb_group;
    private RadioButton radio_call;
    private RadioButton radio_address;
    private int inputType = 1;

    public AddAddressOrPhoneDialog(Context context) {
        super(context);
        mLayoutResId = R.layout.disturb_dialog;
        mContext = context;
    }


    @Override
    protected void onStart() {
        super.onStart();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final View view = inflater.inflate(mLayoutResId, null);

        disturb_group = (RadioGroup) view.findViewById(R.id.disturb_group);
        radio_address = (RadioButton) view.findViewById(R.id.radio_address);
        radio_call = (RadioButton) view.findViewById(R.id.radio_call);
        popup_dialog_input_message = (EditText) view.findViewById(R.id.popup_dialog_input_message);
        popup_dialog_input_message.requestFocus();
        TextView txtViewOK = (TextView) view.findViewById(R.id.popup_dialog_button_ok);
        TextView txtViewCancel = (TextView) view.findViewById(R.id.popup_dialog_button_cancel);

        if (positiveTxtColor != 0) {
            txtViewOK.setTextColor(positiveTxtColor);
        }
        if (negativeTxtColor != 0) {
            txtViewCancel.setTextColor(negativeTxtColor);
        }

        disturb_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radio_call.getId()) {
                    inputType = DisturbType.DISTURB_CALL.getType();
                    popup_dialog_input_message.setInputType(InputType.TYPE_CLASS_PHONE);
                    popup_dialog_input_message.setHint("输入要屏蔽的电话号码");
                }else if (checkedId == radio_address.getId()){
                    inputType = DisturbType.DISTURB_ADDRESS.getType();
                    popup_dialog_input_message.setInputType(InputType.TYPE_CLASS_TEXT);
                    popup_dialog_input_message.setHint("地址请勿输入后缀:省/市. 山西省输入山西");
                }
            }
        });

        txtViewOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPromptButtonClickedListener != null) {
                    String s = popup_dialog_input_message.getText().toString();
                    mPromptButtonClickedListener.onPositiveButtonClicked(inputType,s);
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

    public AddAddressOrPhoneDialog setPromptButtonClickedListener(OnButtonClickedListener buttonClickedListener) {
        this.mPromptButtonClickedListener = buttonClickedListener;
        return this;
    }

    public interface OnButtonClickedListener {
        void onPositiveButtonClicked(int type, String msg);

        void onNegativeButtonClicked();
    }
}
