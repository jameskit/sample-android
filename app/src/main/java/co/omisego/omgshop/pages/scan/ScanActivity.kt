package co.omisego.omgshop.pages.scan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import co.omisego.omgshop.R
import co.omisego.omgshop.helpers.Preference
import co.omisego.omgshop.network.ClientProvider
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract
import kotlinx.android.synthetic.main.activity_scan.*
import kotlinx.android.synthetic.main.toolbar.*

class ScanActivity : AppCompatActivity(), OMGQRScannerContract.Callback {
    private val omgAPIClient by lazy {
        ClientProvider.provideOMGClient(Preference.loadCredential().omisegoAuthenticationToken).client
    }

    companion object {
        const val ACTIVITY_RESULT_TRANSACTION_REQUEST = "transaction_request"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.activity_scan_qr_toolbar_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun scannerDidCancel(view: OMGQRScannerContract.View) {}

    override fun scannerDidDecode(view: OMGQRScannerContract.View, transactionRequest: OMGResponse<TransactionRequest>) {
        val result = Intent().apply {
            putExtra(ACTIVITY_RESULT_TRANSACTION_REQUEST, transactionRequest.data)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun scannerDidFailToDecode(view: OMGQRScannerContract.View, error: OMGResponse<APIError>) {
        Toast.makeText(this, error.data.description, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        scannerView.stopCamera()
        scannerView.startCamera(omgAPIClient, this)
    }
}
