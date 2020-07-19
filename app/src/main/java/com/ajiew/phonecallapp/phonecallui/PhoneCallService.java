package com.ajiew.phonecallapp.phonecallui;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.Call;
import android.telecom.InCallService;
import android.widget.Toast;

import com.ajiew.phonecallapp.ActivityStack;
import com.ajiew.phonecallapp.CallAddress;
import com.ajiew.phonecallapp.Const;
import com.ajiew.phonecallapp.GetPhoneAddressService;
import com.ajiew.phonecallapp.SPUtils;
import com.readystatesoftware.chuck.ChuckInterceptor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private GetPhoneAddressService getPhoneAddressService;
    private String keyWord;
    private PhoneCallManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(this))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://api.k780.com:88")
                .client(okHttpClient)
                .build();
        keyWord = (String)SPUtils.get(this, Const.KEYWORD, "");
        getPhoneAddressService = retrofit.create(GetPhoneAddressService.class);
        manager = new PhoneCallManager(this);
    }

    private Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);

            switch (state) {
                case Call.STATE_ACTIVE: {

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

        if (call.getState() == Call.STATE_RINGING) {
            callType = CallType.CALL_IN;


            getPhoneAddressService.getUsers(call.getDetails().getHandle().getSchemeSpecificPart())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<CallAddress>() {
                        @Override
                        public void accept(CallAddress callAddress) throws Exception {
                            Toast.makeText(PhoneCallService.this, callAddress.getResult().getAtt(), Toast.LENGTH_LONG).show();
                            if (callAddress.getResult().getAtt().contains(keyWord)){
                                manager.disconnect();
                            }
                        }
                    });

        } else if (call.getState() == Call.STATE_CONNECTING) {
            callType = CallType.CALL_OUT;
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
        manager.destroy();
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
