package com.farsitel.bazaar.core.security

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*

object Security {

    private const val bazaarCertificateHex: String =
        "30:82:01:20:30:0D:06:09:2A:86:48:86:F7:0D:01:01:01:05:00:03:82:01:0D:00:30:82:01:08:02" +
                ":82:01:01:00:DE:99:07:76:6B:F9:93:30:EE:58:01:57:4A:68:5A:EB:97:42:8C:BF:FB:3F" +
                ":CF:CA:1A:7C:92:73:DE:41:AE:F4:FB:98:2F:A2:60:19:53:60:04:39:58:89:0B:8C:EB:51" +
                ":6F:48:49:F5:61:56:F5:11:C0:8F:0F:75:9E:2F:D2:7C:CC:52:E5:46:DD:14:13:C5:58:8B" +
                ":3B:18:70:98:7F:F5:70:73:49:11:11:11:44:27:84:BE:67:C6:A2:B1:2E:83:CE:F4:2A:72" +
                ":ED:C0:FD:59:DF:16:73:75:EB:C4:D7:91:74:0B:20:3B:67:10:44:41:C0:FE:3C:B5:4F:DF" +
                ":7F:98:92:36:C8:9E:AC:77:5B:71:A4:42:0C:95:D9:DC:3A:6D:6C:6D:2B:22:69:98:53:A2" +
                ":1F:01:21:08:7B:1D:D1:B8:18:83:9A:72:46:FC:0B:26:02:DF:DE:4C:F4:73:87:6A:05:FF" +
                ":FA:35:29:C9:A0:A0:B1:B2:0E:1F:84:64:53:E1:58:D7:E5:E5:C7:99:7F:81:85:33:1A:26" +
                ":F0:BC:27:FD:72:17:F3:95:D2:50:12:1A:10:33:69:C2:C3:4A:83:00:68:FD:8A:5D:66:0B" +
                ":73:F0:0D:90:44:64:02:BF:C2:B2:AC:2C:39:9E:6C:81:09:8A:35:49:5F:91:F7:0B:4C:F3" +
                ":02:01:03"

    fun verifyBazaarIsInstalled(context: Context): Boolean {
        val packageManager: PackageManager = context.packageManager
        val packageName = BAZAAR_PACKAGE_NAME

        val signatures: Array<Signature> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            packageInfo.signingInfo.apkContentsSigners
        } else {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            packageInfo.signatures
        }

        for (sig in signatures) {
            val input: InputStream = ByteArrayInputStream(sig.toByteArray())
            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X509")
            val certificate: X509Certificate = certificateFactory.generateCertificate(input) as X509Certificate
            val publicKey: PublicKey = certificate.publicKey
            val certificateHex = byte2HexFormatted(publicKey.encoded)
            if (bazaarCertificateHex != certificateHex) {
                return false
            }
        }

        return true
    }

    private fun byte2HexFormatted(array: ByteArray): String? {
        val stringBuilder = StringBuilder(array.size * 2)
        for (index in array.indices) {
            var suggestedHex = Integer.toHexString(array[index].toInt())
            val length = suggestedHex.length
            if (length == 1) {
                suggestedHex = "0$suggestedHex"
            } else if (length > 2) {
                suggestedHex = suggestedHex.substring(length - 2, length)
            }
            stringBuilder.append(suggestedHex.toUpperCase(Locale.getDefault()))
            if (index < array.size - 1) {
                stringBuilder.append(':')
            }
        }
        return stringBuilder.toString()
    }
}