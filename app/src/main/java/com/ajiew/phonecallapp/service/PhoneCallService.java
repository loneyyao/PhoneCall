package com.ajiew.phonecallapp.service;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import com.ajiew.phonecallapp.ActivityStack;
import com.ajiew.phonecallapp.Event;
import com.ajiew.phonecallapp.ui.PhoneCallActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * 监听电话通信状态的服务，实现该类的同时必须提供电话管理的 UI
 *
 * @author aJIEw
 * @see PhoneCallActivity
 * @see android.telecom.InCallService
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PhoneCallService extends InCallService {

    private static final String TAG = PhoneCallService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);

            switch (state) {
                case Call.STATE_ACTIVE: {
                    EventBus.getDefault().post(new Event.CallActiveEvent());
                    break;
                }

                case Call.STATE_DISCONNECTED: {
                    ActivityStack.getInstance().finishActivity(PhoneCallActivity.class);
                    break;
                }

            }
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        call.registerCallback(callback);
        PhoneCallManager.call = call;

        CallType callType = null;
        Log.d(TAG, "onCallAdded: callState" + call.getState());
        switch (call.getState()) {
            case Call.STATE_RINGING:
                callType = CallType.CALL_IN;
                break;
            case Call.STATE_CONNECTING:
                callType = CallType.CALL_OUT;
                break;
            default:
                break;
        }
        if (callType != null) {
            Call.Details details = call.getDetails();
            String phoneNumber = details.getHandle().getSchemeSpecificPart();
            PhoneCallActivity.actionStart(this, phoneNumber, callType);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("lzj", " PhoneCall Service onDestroy");
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        call.unregisterCallback(callback);
        PhoneCallManager.call = null;
    }

    public enum CallType {
        CALL_IN,
        CALL_OUT,
    }
}
