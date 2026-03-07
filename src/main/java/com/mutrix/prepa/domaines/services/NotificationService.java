package com.mutrix.prepa.domaines.services;



public interface NotificationService {

    public  void  sendOtpEmail(String email, String otp);

    public void sendOtpSms(String phone, String otp);

    public void sendWhatsAppOtp(String phone, String message);
}
