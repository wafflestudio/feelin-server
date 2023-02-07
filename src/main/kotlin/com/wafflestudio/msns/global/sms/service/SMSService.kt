package com.wafflestudio.msns.global.sms.service

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SMSService(
    private val amazonSNS: AmazonSNS,
    @Value("\${spring.sms.app-hash}") private val appHash: String,
) {
    suspend fun sendSMS(countryCode: String, phoneNumber: String, code: String) {
        val publishRequest = PublishRequest()
        publishRequest.phoneNumber = countryCode + phoneNumber.replace("-", "")
        publishRequest.message = "[Feelin’] $code is your verification code.$appHash"
        amazonSNS.publish(publishRequest)
    }
}
