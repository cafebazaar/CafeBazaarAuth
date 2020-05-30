package com.farsitel.bazaar.auth.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.farsitel.bazaar.auth.R
import kotlinx.android.synthetic.main.cafe_installer_view.*
import kotlinx.android.synthetic.main.cafe_update_view.*

class CafeInstallerActivity : AppCompatActivity() {

    private lateinit var installerType: InstallerType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        checkNotNull(extras)

        if (!extras.containsKey(INSTALLER_TYPE)) {
            throw IllegalStateException(
                "Use CafeInstallerActivity static method to launch view"
            )
        }

        installerType = InstallerType.values()[extras.getInt(INSTALLER_TYPE)]
        when (installerType) {
            InstallerType.INSTALL -> {
                setContentView(R.layout.cafe_installer_view)
                initInstallView()
            }
            InstallerType.UPDATE -> {
                setContentView(R.layout.cafe_update_view)
                initUpdateView()
            }
        }
    }

    private fun initInstallView() {
        install.setOnClickListener {
            openInstallBazaarPage()
        }
    }

    private fun initUpdateView() {
        update.setOnClickListener {
            openUpdateBazaarInApplication()
        }
    }

    private fun openUpdateBazaarInApplication() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("bazaar://details?id=com.farsitel.bazaar")
            setPackage("com.farsitel.bazaar")
        }

        startActivity(intent)
    }

    private fun openInstallBazaarPage() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://cafebazaar.ir/install")
        )

        startActivity(intent)
    }

    companion object {

        private const val INSTALLER_TYPE = "installerType"

        fun startCafeInstallerActivityForInstallBazaar(context: Context) {
            startCafeInstallerActivityByInstallerType(context, InstallerType.INSTALL)
        }

        fun startCafeInstallerActivityForUpdateBazaar(context: Context) {
            startCafeInstallerActivityByInstallerType(context, InstallerType.UPDATE)
        }

        private fun startCafeInstallerActivityByInstallerType(
            context: Context,
            installerType: InstallerType
        ) {
            Intent(context, CafeInstallerActivity::class.java).apply {
                putExtra(INSTALLER_TYPE, installerType.ordinal)
            }.also {
                context.startActivity(it)
            }
        }
    }

    enum class InstallerType {
        INSTALL,
        UPDATE
    }
}