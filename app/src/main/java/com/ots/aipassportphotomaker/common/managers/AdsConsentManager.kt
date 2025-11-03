package com.ots.aipassportphotomaker.common.managers

import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

// Created by amanullah on 22/09/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
class AdsConsentManager (val context: Context) {

    private val TAG = AdsConsentManager::class.java.simpleName

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    fun gatherConsentInfo(activity: Activity, onComplete: () -> Unit) {
        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                onComplete()
            },
            { formError ->
                Log.e("AdsConsentManager", "Consent info update failed: ${formError.message}")
                onComplete()
            }
        )
    }

    /*fun showGDPRConsent(activity: Activity, debug: Boolean, onComplete: (FormError?) -> Unit) {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
            onComplete(formError)
        }
    }*/

    fun showGDPRConsent(activity: Activity, isTest: Boolean, onConsentGatheringCompleteListener: (FormError?) -> Unit) {
        // Set tag for under age of consent. false means users are not under age
        // of consent.
        val builder = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)

        if (isTest) {
            consentInformation.reset()
            val android_id = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
            val deviceId = md5(android_id).uppercase(Locale.getDefault())
            val debugSettings = activity.let {
                ConsentDebugSettings.Builder(it)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(deviceId)
                    .build()
            }
            builder.setConsentDebugSettings(debugSettings)
        }

        val params = builder.build()
        consentInformation.requestConsentInfoUpdate(activity, params, {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                activity
            ) { loadAndShowError ->
                // Consent gathering failed.
                Log.e(
                    TAG, String.format(
                        "%s: %s", loadAndShowError?.errorCode, loadAndShowError?.message
                    )
                )
                // Consent gathered.
                onConsentGatheringCompleteListener(loadAndShowError)
            }
        }, { requestConsentError ->
            // Consent gathering failed.
            Log.w(
                TAG, String.format(
                    "%s: %s", requestConsentError.errorCode, requestConsentError.message
                )
            )
            onConsentGatheringCompleteListener(requestConsentError)
        })



    }




    /** Helper variable to determine if the app can request ads.
     * Note: canRequestAds() always returns false until you have called requestConsentInfoUpdate()
     * */
    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    /*in testing your app with the UMP SDK, you might find it helpful to reset the
       state of the SDK so that you can simulate a user's first install experience.
        The SDK provides the reset() method to do this.*/

    fun reset() {
        consentInformation.reset()
    }
    private fun md5(s: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

}