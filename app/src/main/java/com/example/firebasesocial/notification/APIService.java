package com.example.firebasesocial.notification;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAQHy6tJo:APA91bFuuLtA9pKF337EFRDW8rLnur6rVqLeSmHFJ8Dw24wQY_NmXTEHB_ZId7P19aT10Unmo0khrCHmapS5sAHu5JzQDVzl_nrjzVoUFuELz3HLbbaMHL0r-VhqktoFXX_Doae7N2Ev"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
