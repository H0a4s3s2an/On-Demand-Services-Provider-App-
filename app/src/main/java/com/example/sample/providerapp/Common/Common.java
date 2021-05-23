package com.example.sample.providerapp.Common;

import com.example.sample.providerapp.Interface.FCMClient;
import com.example.sample.providerapp.Interface.IFCMService;
import com.example.sample.providerapp.Interface.IGoogleAPI;
import com.example.sample.providerapp.Interface.RetrofitClient;

/**
 * Created by Hassan Javaid on 8/17/2018.
 */

public class Common {
    public static final String fcmURL="https://fcm.googleapis.com/";
    public static final String baseURL="https://maps.googleapis.com/";
    public static double base_fare=100;
    private static double time_rate=5;
    private static double distance_rate=10;
    private static double total_min=2;
    public static double calculatePrice(double Km,double min,double time_min)
    {
     return base_fare+(distance_rate*Km)+(time_rate*min)+(total_min*time_min);
    }
public static IFCMService getFCMService()
{
return FCMClient.getClient(fcmURL).create(IFCMService.class);
}
public static IGoogleAPI getGoogleAPI()
{
return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
}

}
