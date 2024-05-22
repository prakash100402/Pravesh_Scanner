package com.example.praveshscanner20;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        barcodeView = findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.pause();

                String qrCodeContents = result.getText();
                Gson gson = new Gson();
                QRCodeData qrCodeData = gson.fromJson(qrCodeContents, QRCodeData.class);

                // Pass the data to ResultActivity
                Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                intent.putExtra("qrCodeContents", qrCodeContents);
                intent.putExtra("name", qrCodeData.getName());
                intent.putExtra("email", qrCodeData.getEmail());
                intent.putExtra("adhaar", qrCodeData.getAdhaar());
                intent.putExtra("phone", qrCodeData.getPhone());
                startActivity(intent);
                finish();
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    private static class QRCodeData {
        private String name;
        private String email;
        private String adhaar;
        private String phone;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getAdhaar() {
            return adhaar;
        }

        public String getPhone() {
            return phone;
        }
    }
}
