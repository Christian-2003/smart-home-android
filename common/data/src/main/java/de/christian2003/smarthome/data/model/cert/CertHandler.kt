package de.christian2003.smarthome.data.model.cert

import android.content.Context
import android.content.SharedPreferences
import android.security.KeyChain
import android.util.Log
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager


/**
 * Class implements the certificate handler which manages all certificates within the app.
 */
class CertHandler(

    /**
     * Attribute stores the context to use for all operations.
     */
    val context: Context

) {

    /**
     * Attribute stores the preferences in which the certificate alias is stored.
     */
    private val preferences = context.getSharedPreferences("smart_home", Context.MODE_PRIVATE)


    /**
     * Method validates the certificate passed.
     *
     * @param cert  Certificate to validate.
     * @return      Response indicating the result of the validation.
     */
    fun validateCert(cert: X509Certificate): SslTrustResponse {
        if (preferences.getBoolean("unsafe_cert_validation", false)) {
            return SslTrustResponse(SslTrustStatus.Trusted, cert) //Skip validation if unsafe cert validation is enabled.
        }
        val fingerprint = getCertFingerprint(cert)
        return if (isCertTrusted(fingerprint)) {
            SslTrustResponse(SslTrustStatus.Trusted, cert)
        } else {
            SslTrustResponse(SslTrustStatus.Untrusted, cert)
        }
    }


    fun getClientCert(): ClientCert? {
        val alias: String? = preferences.getString("cert_alias", null)
        if (alias != null) {
            try {
                val key: PrivateKey? = KeyChain.getPrivateKey(context, alias)
                val chain: Array<X509Certificate>? = KeyChain.getCertificateChain(context, alias)

                Log.d("CertHandler", "getClientCert(): Key = $key")
                Log.d("CertHandler", "getClientCert(): Chain = $chain")


                if (key != null && chain != null) {
                    return ClientCert(
                        key = key,
                        chain = chain
                    )
                }
            }
            catch (e: Exception) {
                Log.e("CertHandler", "Exception occurred: ${e.message}")
            }
        }
        return null
    }


    /**
     * Method saves the certificate passed. Certificates saved with this are considered as trusted
     * by the user.
     *
     * @param cert  Certificate to save / trust.
     */
    fun saveCert(cert: X509Certificate) {
        val trustedCerts: MutableSet<String> = preferences.getStringSet("trusted_certs", emptySet())!!.toMutableSet()
        trustedCerts.add(getCertFingerprint(cert))
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putStringSet("trusted_certs", trustedCerts)
        editor.apply()
    }


    /**
     * Method returns the SSL context to use for client authentication. This can return null if no
     * certificate has been selected from the Android key store.
     *
     * @return  SSL context to use for client authentication.
     */
    fun getSSLContext(): SSLContext? {
        try {
            val alias = preferences.getString("cert_alias", null) ?: return null
            val keyManager = KeyChainKeyManager(context, alias)

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(arrayOf(keyManager), null, null)
            return sslContext
        } catch (e: Exception) {
            Log.e("Certificates", "Cannot create SSLContext object: ${e.message}")
        }
        return null
    }


    /**
     * Method tests whether the certificate, whose fingerprint is passed, is trusted.
     *
     * @param fingerprint   Fingerprint of the certificate to test.
     * @return              Whether the certificate is trusted.
     */
    private fun isCertTrusted(fingerprint: String): Boolean {
        val trustedCerts: Set<String>? = preferences.getStringSet("trusted_certs", emptySet())
        trustedCerts!!.forEach { cert ->
            Log.d("CertHandler", "Trusted Fingerprint: $cert")
        }
        Log.d("CertHandler", "Cert Fingerprint: $fingerprint")
        return trustedCerts!!.contains(fingerprint)
    }


    /**
     * Method returns the fingerprint for the certificate passed.
     *
     * @param cert  Certificate for which to return it's fingerprint.
     * @return      Fingerprint of the certificate passed.
     */
    private fun getCertFingerprint(cert: X509Certificate): String {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            val publicKey = md.digest(cert.encoded)

            val hexString = StringBuilder()
            for (b in publicKey) {
                hexString.append(String.format("%02X:", b))
            }
            return hexString.substring(0, hexString.length - 1)
        }
        catch (e: Exception) {
            return ""
        }
    }

}
