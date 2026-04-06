package com.womensafety.app.network

import com.google.gson.annotations.SerializedName

/**
 * WhatsApp Business API Request Models
 * Matches the exact structure from backend's contact.service.js
 */
data class WhatsAppMessageRequest(
    @SerializedName("to")
    val to: String,
    
    @SerializedName("recipient_type")
    val recipientType: String = "individual",
    
    @SerializedName("type")
    val type: String = "template",
    
    @SerializedName("template")
    val template: WhatsAppTemplate
)

data class WhatsAppTemplate(
    @SerializedName("language")
    val language: WhatsAppLanguage,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("components")
    val components: List<WhatsAppComponent>
)

data class WhatsAppLanguage(
    @SerializedName("policy")
    val policy: String = "deterministic",
    
    @SerializedName("code")
    val code: String = "en_US"
)

data class WhatsAppComponent(
    @SerializedName("type")
    val type: String = "body",
    
    @SerializedName("parameters")
    val parameters: List<WhatsAppParameter>
)

data class WhatsAppParameter(
    @SerializedName("type")
    val type: String = "text",
    
    @SerializedName("text")
    val text: String
)

/**
 * WhatsApp Business API Response Models
 */
data class WhatsAppMessageResponse(
    @SerializedName("message")
    val message: WhatsAppMessageStatus
)

data class WhatsAppMessageStatus(
    @SerializedName("message_status")
    val messageStatus: String,
    
    @SerializedName("message_id")
    val messageId: String?
)
