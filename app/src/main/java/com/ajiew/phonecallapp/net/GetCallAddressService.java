package com.ajiew.phonecallapp.net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetCallAddressService {

    @GET("?app=life.areacode&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json")
    Observable<AreaCallAddress> getResult(@Query("areacode") String areacode);
}
