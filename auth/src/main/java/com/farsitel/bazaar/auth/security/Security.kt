package com.farsitel.bazaar.auth.security

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object Security {

    private val bazaarCertificateList: List<ByteArray> = emptyList()
    fun verifyBazaarIsInstalled(context: Context) {
        val pm: PackageManager = context.packageManager
        val packageName = "com.farsitel.bazaar"

        val signatures: Array<Signature> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val packageInfo =
                pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            packageInfo.signingInfo.apkContentsSigners
        } else {
            val packageInfo =
                pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            packageInfo.signatures
        }

        for (sig in signatures) {
            val input: InputStream = ByteArrayInputStream(sig.toByteArray())
            val cf: CertificateFactory = CertificateFactory.getInstance("X509")
            val cert: X509Certificate = cf.generateCertificate(input) as X509Certificate
            val pb: PublicKey = cert.publicKey
            if (!bazaarCertificateList.contains(pb.encoded)) {
                throw Exception("public key is not valid")
            }
        }
    }
}