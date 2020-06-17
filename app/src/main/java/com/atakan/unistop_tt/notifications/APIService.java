package com.atakan.unistop_tt.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
             "Content-Type:application/json",
             "Authorization:key=AAAAz2N94-E:APA91bGqi4gFE928_jdfxWljXEc3LcWdgQht5vZbXX4IB0rEJRMBMO3X3GdVWW9waYfwjobbq0CHsNkB79YuRO6_rFdc2BKVhgOBj2fzGrVLZfJj9mRSSjqGGpKjpKum_lx1VZOYASe1"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
