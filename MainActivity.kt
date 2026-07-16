package com.example.bist_portfoy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bist_portfoy.adapter.HisseSayfaAdapter
import com.example.bist_portfoy.databinding.ActivityMainBinding
import com.example.bist_portfoy.service.BorsaTakipService
import com.example.bist_portfoy.viewmodel.PortfoyViewModel
import com.google.android.material.tabs.TabLayoutMediator
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PortfoyViewModel by viewModels()

    private val bildirimIzniIsteyici =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        kurulumSayfalar()
        bildirimIzniIstesGerekirse()
        canliTakipServisiniBaslat()
    }

    private fun kurulumSayfalar() {
        val adapter = HisseSayfaAdapter(this, viewModel.hisseKodlari)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.baslikVer(position)
        }.attach()
    }

    private fun bildirimIzniIstesGerekirse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            bildirimIzniIsteyici.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /**
     * Canlı veri çekme işini, bu Activity'nin yaşam döngüsünden bağımsız çalışan bir "foreground
     * service"e devrediyoruz: böylece kullanıcı uygulamayı kapatsa/ekranı kapatsa bile
     * BorsaTakipService arka planda çalışmaya devam eder ve fiyat değiştiğinde bildirim gösterir.
     */
    private fun canliTakipServisiniBaslat() {
        val intent = Intent(this, BorsaTakipService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}
