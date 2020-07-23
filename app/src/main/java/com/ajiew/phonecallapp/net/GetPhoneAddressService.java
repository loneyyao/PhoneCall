package com.ajiew.phonecallapp.net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetPhoneAddressService {

    @GET("?app=phone.get&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json")
    Observable<CallAddress> getUsers(@Query("phone") String phone);
}
