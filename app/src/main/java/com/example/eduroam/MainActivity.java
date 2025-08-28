package com.example.eduroam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button connectButton;
    private TextView forgotPasswordLink;
    
    private static final int WIFI_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        connectButton = findViewById(R.id.connectButton);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        // Set click listeners
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToEduroam();
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPasswordUrl();
            }
        });

        // Check and request permissions
        checkWifiPermissions();
    }

    private void checkWifiPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                
                ActivityCompat.requestPermissions(this,
                    new String[]{
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE
                    },
                    WIFI_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void connectToEduroam() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configure and connect to eduroam
        if (configureEduroamWifi(email, password)) {
            Toast.makeText(this, "Connecting to eduroam...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to configure WiFi. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean configureEduroamWifi(String identity, String password) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        
        if (wifiManager == null) {
            return false;
        }

        // Enable WiFi if it's disabled
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // Create WiFi configuration for eduroam
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"eduroam\"";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);

        // Configure Enterprise settings for EAP-TTLS with PAP
        WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
        enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TTLS);
        enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.PAP);
        enterpriseConfig.setIdentity(identity);
        enterpriseConfig.setPassword(password);
        
        // Don't validate CA certificate as per requirements
        enterpriseConfig.setCaCertificate(null);
        
        wifiConfig.enterpriseConfig = enterpriseConfig;

        try {
            // Remove any existing eduroam configuration
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            if (configuredNetworks != null) {
                for (WifiConfiguration existingConfig : configuredNetworks) {
                    if (existingConfig.SSID != null && existingConfig.SSID.equals("\"eduroam\"")) {
                        wifiManager.removeNetwork(existingConfig.networkId);
                    }
                }
            }

            // Add the new configuration
            int networkId = wifiManager.addNetwork(wifiConfig);
            if (networkId == -1) {
                return false;
            }

            // Enable and connect to the network
            wifiManager.enableNetwork(networkId, true);
            wifiManager.reconnect();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openForgotPasswordUrl() {
        // Open forgot password URL in browser
        String url = "https://example.com/forgot-password"; // Replace with actual URL
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No browser found to open the link", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == WIFI_PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            
            if (!allPermissionsGranted) {
                Toast.makeText(this, "WiFi permissions are required for this app to function properly", Toast.LENGTH_LONG).show();
            }
        }
    }
}