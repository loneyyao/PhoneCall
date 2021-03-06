package com.ajiew.phonecallapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajiew.phonecallapp.ActivityStack;
import com.ajiew.phonecallapp.Event;
import com.ajiew.phonecallapp.NumberUtil;
import com.ajiew.phonecallapp.R;
import com.ajiew.phonecallapp.ScheduleDateUtil;
import com.ajiew.phonecallapp.db.Address;
import com.ajiew.phonecallapp.db.AppDatabase;
import com.ajiew.phonecallapp.db.CallLog;
import com.ajiew.phonecallapp.db.DisturbType;
import com.ajiew.phonecallapp.net.AreaCallAddress;
import com.ajiew.phonecallapp.net.GetCallAddressService;
import com.ajiew.phonecallapp.net.GetPhoneAddressService;
import com.ajiew.phonecallapp.net.PhoneAddress;
import com.ajiew.phonecallapp.service.PhoneCallManager;
import com.ajiew.phonecallapp.service.PhoneCallService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.readystatesoftware.chuck.ChuckInterceptor;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ajiew.phonecallapp.service.CallListenerService.formatPhoneNumber;


/**
 * 提供接打电话的界面，仅支持 Android M (6.0, API 23) 及以上的系统
 *
 * @author aJIEw
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PhoneCallActivity extends RxAppCompatActivity implements View.OnClickListener {

    private static final String TAG = PhoneCallActivity.class.getSimpleName();
    private TextView tvCallNumberLabel;
    private TextView tvCallNumber;
    private TextView tvCallAddress;
    private TextView tvPickUp;
    private TextView tvCallingTime;
    private TextView tvHangUp;

    private PhoneCallManager phoneCallManager;
    private PhoneCallService.CallType callType;
    private String phoneNumber;

    private Timer onGoingCallTimer;
    private int callingTime;
    private TextView speakerOn;
    private TextView speakerOff;
    private int currVolume;
    private GetPhoneAddressService getPhoneAddressService;
    private GetCallAddressService getCallAddressService;
    private RxErrorHandler rxErrorHandler = RxErrorHandler.builder().with(this)
            .responseErrorListener(new ResponseErrorListener() {
                @Override
                public void handleResponseError(Context context, Throwable t) {
                    Log.d(TAG, "handleResponseError: " + t.toString());
                }

            }).build();


    public static void actionStart(Context context, String phoneNumber,
                                   PhoneCallService.CallType callType) {
        Intent intent = new Intent(context, PhoneCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, callType);
        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_call);
        ActivityStack.getInstance().addActivity(this);
        initView();
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(this))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://api.k780.com:88")
                .client(okHttpClient)
                .build();
        getPhoneAddressService = retrofit.create(GetPhoneAddressService.class);
        getCallAddressService = retrofit.create(GetCallAddressService.class);

        phoneCallManager = new PhoneCallManager(this);
        onGoingCallTimer = new Timer();
        if (getIntent() != null) {
            phoneNumber = getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            callType = (PhoneCallService.CallType) getIntent().getSerializableExtra(Intent.EXTRA_MIME_TYPES);
            Log.e(TAG, "initData: " + phoneNumber + "-----"+callType);
            tvCallNumber.setText(formatPhoneNumber(phoneNumber));
            // 打进的电话
            if (callType == PhoneCallService.CallType.CALL_IN) {
                tvCallNumberLabel.setText("来电号码");
                tvPickUp.setVisibility(View.VISIBLE);
            }
            // 打出的电话
            else if (callType == PhoneCallService.CallType.CALL_OUT) {
                tvCallNumberLabel.setText("呼叫号码");
                tvPickUp.setVisibility(View.GONE);
                speakerOn.setVisibility(View.VISIBLE);
                speakerOff.setVisibility(View.GONE);
            }


            //1 先判断来电的话 进行一下黑名单拦截
            if (callType == PhoneCallService.CallType.CALL_IN) {
                //优先电话拦截
                List<Address> addressList = AppDatabase.getInstance(PhoneCallActivity.this).addressDao().getAll();
                for (Address address : addressList) {
                    if (address.getType() == DisturbType.DISTURB_CALL.getType() && phoneNumber.contains(address.getName())) {
                        phoneCallManager.disconnect();
                        String stamp = ScheduleDateUtil.stampToDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
                        Toast.makeText(PhoneCallActivity.this, "已拦截 \"" + phoneNumber + "\" 的来电", Toast.LENGTH_LONG).show();
                        AppDatabase.getInstance(PhoneCallActivity.this).callLogDao()
                                .insertAll(new CallLog(phoneNumber, "", stamp));
                        return;
                    }
                }
            }


            //2 然后去查询归属地
            if (NumberUtil.isCellPhone(phoneNumber)) {
                //查询手机归属地
                cellPhoneDisturb();
            } else if (NumberUtil.isFixedPhone(phoneNumber)) {
                //查询区号归属地
                NumberUtil.Number number = NumberUtil.checkNumber(phoneNumber);
                areaCallDisturb(number.getCode());
            } else {
                tvCallAddress.setText("暂不支持查询");
            }


        } else {
            //没有数据 结束页面
            finish();
        }

    }

    private void areaCallDisturb(String area) {
        getCallAddressService.getResult(area)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindToLifecycle())
                .subscribe(new ErrorHandleSubscriber<AreaCallAddress>(rxErrorHandler) {
                    @Override
                    public void onNext(AreaCallAddress result) {
                        if (result.getSuccess().equals("0")) {
                            tvCallAddress.setText("查询归属地失败");
                            return;
                        }
                        AreaCallAddress.ResultBean.ListsBean listsBean = result.getResult().getLists().get(0);
                        if (listsBean != null && !TextUtils.isEmpty(listsBean.getSimcall())) {
                            //查询到了区号归属地
                            tvCallAddress.setText(listsBean.getSimcall());
                        } else {
                            tvCallAddress.setText("查询归属地失败");
                        }

                        //设置好文字 判断是否是去电, 是的话, 直接返回
                        if (callType == PhoneCallService.CallType.CALL_OUT) {
                            //去电的话, 直接返回
                            return;
                        }

                        //不是的话  进行拦截逻辑
                        List<Address> addressList = AppDatabase.getInstance(PhoneCallActivity.this).addressDao().getAll();
                        for (Address disturbAddress : addressList) {
                            if (disturbAddress.getType() == DisturbType.DISTURB_ADDRESS.getType() && listsBean.getSimcall().contains(disturbAddress.getName())) {
                                phoneCallManager.disconnect();
                                String stamp = ScheduleDateUtil.stampToDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
                                Toast.makeText(PhoneCallActivity.this, "已拦截 \"" + disturbAddress.getName() + "\" 的来电", Toast.LENGTH_LONG).show();
                                AppDatabase.getInstance(PhoneCallActivity.this).callLogDao()
                                        .insertAll(new CallLog(phoneNumber, listsBean.getSimcall(), stamp));
                                return;
                            }
                        }
                    }
                });
    }

    private void cellPhoneDisturb() {

        getPhoneAddressService.getResult(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindToLifecycle())
                .subscribe(new ErrorHandleSubscriber<PhoneAddress>(rxErrorHandler) {
                    @Override
                    public void onNext(PhoneAddress phoneAddress) {
                        if (phoneAddress.getSuccess().equals("0")) {
                            tvCallAddress.setText("查询归属地失败");
                            return;
                        }
                        String att = phoneAddress.getResult().getAtt();
                        String phone = phoneAddress.getResult().getPhone();
                        tvCallAddress.setText(att == null ? "查询归属地失败" : att);
                        if (callType == PhoneCallService.CallType.CALL_OUT) {
                            //去电的话, 直接返回
                            return;
                        }

                        //来电则判断是否拦截该手机号
                        List<Address> addressList = AppDatabase.getInstance(PhoneCallActivity.this).addressDao().getAll();
                        for (Address disturbAddress : addressList) {
                            if (disturbAddress.getType() == DisturbType.DISTURB_ADDRESS.getType() && att.contains(disturbAddress.getName())) {
                                phoneCallManager.disconnect();
                                String stamp = ScheduleDateUtil.stampToDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
                                Toast.makeText(PhoneCallActivity.this, "已拦截 \"" + disturbAddress.getName() + "\" 的来电", Toast.LENGTH_LONG).show();
                                AppDatabase.getInstance(PhoneCallActivity.this).callLogDao()
                                        .insertAll(new CallLog(phone, att, stamp));
                                return;
                            }
                        }
                    }
                });

    }

    private void initView() {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //hide navigationBar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        tvCallNumberLabel = findViewById(R.id.tv_call_number_label);
        tvCallNumber = findViewById(R.id.tv_call_number);
        tvCallAddress = findViewById(R.id.tv_call_address);
        tvPickUp = findViewById(R.id.tv_phone_pick_up);
        tvCallingTime = findViewById(R.id.tv_phone_calling_time);
        tvHangUp = findViewById(R.id.tv_phone_hang_up);
        speakerOn = findViewById(R.id.tv_phone_speaker_on);
        speakerOff = findViewById(R.id.tv_phone_speaker_off);
        ImageView img_bg = findViewById(R.id.img_bg);
        Glide.with(this)
                .load(R.mipmap.bg)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(1, 5)))
                .into(img_bg);


        tvPickUp.setOnClickListener(this);
        tvHangUp.setOnClickListener(this);
        speakerOn.setOnClickListener(this);
        speakerOff.setOnClickListener(this);
        showOnLockScreen();
    }

    public void showOnLockScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_phone_pick_up) {
            phoneCallManager.answer();
            tvPickUp.setVisibility(View.GONE);
            speakerOn.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.tv_phone_hang_up) {
            phoneCallManager.disconnect();
            stopTimer();
        } else if (v.getId() == R.id.tv_phone_speaker_on) {
            speakerOn.setVisibility(View.GONE);
            speakerOff.setVisibility(View.VISIBLE);
            OpenSpeaker();

        } else if (v.getId() == R.id.tv_phone_speaker_off) {
            speakerOn.setVisibility(View.VISIBLE);
            speakerOff.setVisibility(View.GONE);
            CloseSpeaker();
        }
    }

    private String getCallingTime() {
        int minute = callingTime / 60;
        int second = callingTime % 60;
        return (minute < 10 ? "0" + minute : minute) +
                ":" +
                (second < 10 ? "0" + second : second);
    }

    private void stopTimer() {
        if (onGoingCallTimer != null) {
            onGoingCallTimer.cancel();
        }

        callingTime = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        phoneCallManager.destroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallActive(Event.CallActiveEvent callActiveEvent) {
        //展示通话时长等
        tvCallingTime.setVisibility(View.VISIBLE);
        onGoingCallTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        callingTime++;
                        tvCallingTime.setText("通话中：" + getCallingTime());
                    }
                });
            }
        }, 0, 1000);
    }

    //打开扬声器
    public void OpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //关闭扬声器
    public void CloseSpeaker() {

        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(context,"扬声器已经关闭",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
    }
}
