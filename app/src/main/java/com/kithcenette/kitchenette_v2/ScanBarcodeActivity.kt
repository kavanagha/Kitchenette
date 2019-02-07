package com.kithcenette.kitchenette_v2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_scan_barcode.*
import kotlinx.android.synthetic.main.app_bar_scan_barcode.*

class ScanBarcodeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var svBarcode : SurfaceView
    private lateinit var tvBarcode : TextView
    private lateinit var detector: BarcodeDetector
    private lateinit var cameraSource : CameraSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
          val intent = Intent(this@ScanBarcodeActivity, BarcodeHistoryActivity::class.java)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val context = this
        var db = DataBaseHandler(context)
        ///////////////////////////////////////////////
        var barcodeNumber  = 0

        svBarcode = findViewById(R.id.sv_barcode)
        tvBarcode = findViewById(R.id.tv_barcode)

        detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object: Detector.Processor<Barcode>{
            override fun release() { }
            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val barcode =detections?.detectedItems
                if(barcode!!.size()>0){
                    tvBarcode.post{
                        tvBarcode.text= barcode.valueAt(0).displayValue
                    }
                    ////////////////////
                    barcodeNumber = barcode.valueAt(0).format
                }
            }
        })
        //////////////////////////////////////
        if (barcodeNumber > 0 )
        {
            var barcode = Barcodes(barcodeNumber)
            db.insertBarcode(barcode)
        }
        /*var barcodeNum = tvBarcode.text.toString().toInt()
        if(!db.checkBarcode(barcodeNum)){
            db.insertBarcode(dbBarcode)
        }*/

        cameraSource = CameraSource.Builder(this, detector).setRequestedPreviewSize(1024,768)
            .setRequestedFps(25f).setAutoFocusEnabled(true).build()

        svBarcode.holder.addCallback(object : SurfaceHolder.Callback2{
            override fun surfaceRedrawNeeded(p0: SurfaceHolder?) {}
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                cameraSource.stop()
            }
            override fun surfaceCreated(holder: SurfaceHolder?) {
                if(ContextCompat.checkSelfPermission(this@ScanBarcodeActivity, Manifest.permission
                            .CAMERA) == PackageManager.PERMISSION_GRANTED)
                    cameraSource.start(holder)
                else ActivityCompat.requestPermissions(this@ScanBarcodeActivity,
                    arrayOf(Manifest.permission.CAMERA), 123)
            }
        })
    }


    ///////////////////////////// NAV MENU METHODS ////////////////////////////////////
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.scan_barcode, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@ScanBarcodeActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {

            }
            R.id.nav_shopping -> {

            }
            R.id.nav_favourite -> {

            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@ScanBarcodeActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /////////////////////////// SCAN BARCODE METHODS /////////////////////////////////

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cameraSource.start(svBarcode.holder)
            else Toast.makeText(this, "Cannot open scanner without permission",
                    Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }





}
