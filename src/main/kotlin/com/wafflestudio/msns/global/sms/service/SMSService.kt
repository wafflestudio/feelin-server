package com.wafflestudio.msns.global.sms.service

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import org.springframework.stereotype.Service

@Service
class SMSService(
    private val amazonSNS: AmazonSNS
) {
    suspend fun sendSMS(countryCode: String, phoneNumber: String, code: String) {
        val publishRequest = PublishRequest()
        publishRequest.phoneNumber = countryCode + phoneNumber.replace("-", "")
        publishRequest.message = code
        amazonSNS.publish(publishRequest)
    }
}
