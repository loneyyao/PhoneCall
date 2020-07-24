package com.ajiew.phonecallapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajiew.phonecallapp.widget.ItemClickListener;
import com.ajiew.phonecallapp.widget.ItemLongClickListener;
import com.ajiew.phonecallapp.widget.PromptDialog;
import com.ajiew.phonecallapp.R;
import com.ajiew.phonecallapp.db.AppDatabase;
import com.ajiew.phonecallapp.db.CallLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * A fragment representing a list of Items.
 */
public class BadCallIListFragment extends Fragment {

    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_more)
    TextView tv_more;

    private List<CallLog> callLogList;

    private MyBadCallItemRecyclerViewAdapter adapter = new MyBadCallItemRecyclerViewAdapter();
    private Unbinder bind;


    public BadCallIListFragment() {
    }

    public static BadCallIListFragment newInstance(int columnCount) {
        BadCallIListFragment fragment = new BadCallIListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bad_call_list, container, false);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(Observable observable) {
        if (observable == null) {
            observable = Observable.just("获取数据");
        }
        observable.subscribeOn(Schedulers.io()).map(new Function<String, List<CallLog>>() {
            @Override
            public List<CallLog> apply(String s) throws Exception {
                callLogList = AppDatabase.getInstance(getActivity()).callLogDao().getAll();
                return callLogList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<CallLog>>() {
            @Override
            public void accept(List<CallLog> addresses) throws Exception {
                adapter.setmValues(addresses);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData(null);
    }

    private void initView() {
        tv_more.setVisibility(View.GONE);
        tv_title.setText("拦截记录");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public boolean onLongClickListener(View v, int position) {
                CallLog callLog = callLogList.get(position);
                PromptDialog promptDialog = PromptDialog.newInstance(getActivity(), "", "删除通话记录 : " + callLog.getCall() + " " + callLog.getAddress() + "?");
                promptDialog.setPromptButtonClickedListener(new PromptDialog.OnPromptButtonClickedListener() {
                    @Override
                    public void onPositiveButtonClicked(String msg) {
                        initData(Observable.just(msg)
                                .subscribeOn(Schedulers.io())
                                .map(new Function<String, String>() {
                                    @Override
                                    public String apply(String s) throws Exception {
                                        AppDatabase.getInstance(getActivity()).callLogDao().delete(callLog);
                                        return "";
                                    }
                                }));

                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                });
                promptDialog.show();

                return false;
            }
        });

        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CallLog callLog = callLogList.get(position);

                PromptDialog promptDialog = PromptDialog.newInstance(getActivity(), "", "给: " + callLog.getCall() + " " + callLog.getAddress() + " 回拨电话?");
                promptDialog.setPromptButtonClickedListener(new PromptDialog.OnPromptButtonClickedListener() {
                    @Override
                    public void onPositiveButtonClicked(String msg) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + callLog.getCall());
                        intent.setData(data);
                        startActivity(intent);
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                });
                promptDialog.show();
            }
        });

        recyclerView.setAdapter(adapter);
    }
}