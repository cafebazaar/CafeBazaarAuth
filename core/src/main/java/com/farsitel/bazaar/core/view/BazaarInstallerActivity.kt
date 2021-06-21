package com.farsitel.bazaar.core.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.core.R
import com.farsitel.bazaar.core.model.InstallerType
import com.farsitel.bazaar.util.getAppName
import com.farsitel.bazaar.util.ext.safeStartActivity
import kotlinx.android.synthetic.main.cafe_installer_view.*
import kotlinx.android.synthetic.main.cafe_update_view.*

class BazaarInstallerActivity : AppCompatActivity() {

    private lateinit var installerType: InstallerType
    private lateinit var appName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        checkNotNull(extras)

        if (!extras.containsKey(INSTALLER_TYPE)) {
            throw IllegalStateException(
                "Use CafeInstallerActivity static method to launch view"
            )
        }

        appName = extras.getString(APPLICATION_NAME) ?: getString(R.string.unknownApp)

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

        installDesc.text = getString(R.string.install_desc, appName)
    }

    private fun initUpdateView() {
        update.setOnClickListener {
            openUpdateBazaarInApplication()
        }

        updateDesc.text = getString(R.string.update_desc, appName)
    }

    private fun openUpdateBazaarInApplication() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("bazaar://details?id=com.farsitel.bazaar")
            setPackage(BAZAAR_PACKAGE_NAME)
        }

        safeStartActivity(intent)
    }

    private fun openInstallBazaarPage() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://cafebazaar.ir/install")
        )

        safeStartActivity(intent)
    }

    companion object {

        private const val INSTALLER_TYPE = "installerType"
        private const val APPLICATION_NAME = "appName"

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
            Intent(context, BazaarInstallerActivity::class.java).apply {
                putExtra(INSTALLER_TYPE, installerType.ordinal)
                putExtra(APPLICATION_NAME, getAppName(context))
            }.also {
                context.safeStartActivity(it)
            }
        }
    }
}