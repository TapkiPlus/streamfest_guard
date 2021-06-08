package ru.streamfest.guard;

import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.nio.ByteBuffer;

import static android.graphics.ImageFormat.YUV_420_888;
import static android.graphics.ImageFormat.YUV_422_888;
import static android.graphics.ImageFormat.YUV_444_888;

public class QRCodeImageAnalyzer implements ImageAnalysis.Analyzer {

    private static int SCAN_DELAY = 10000;

    private QRCodeFoundListener listener;

    private LruCache<String, Long> cache = new LruCache<String, Long>(10);

    public QRCodeImageAnalyzer(QRCodeFoundListener listener) {
        this.listener = listener;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (image.getFormat() == YUV_420_888 || image.getFormat() == YUV_422_888 || image.getFormat() == YUV_444_888) {
            final ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            final byte[] imageData = new byte[byteBuffer.capacity()];
            byteBuffer.get(imageData);

            final PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
              imageData,
              image.getWidth(), image.getHeight(),
              0, 0,
              image.getWidth(), image.getHeight(),
              false
            );

            final BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                final Result result = new QRCodeMultiReader().decode(binaryBitmap);
                final String text = result.getText();
                final Long cached = cache.get(text);
                final Long now = System.currentTimeMillis();
                if (cached == null || now - cached > SCAN_DELAY) {
                    cache.put(text, now);
                    listener.onQRCodeFound(text);
                }
            } catch (FormatException | ChecksumException | NotFoundException e) {
                listener.qrCodeNotFound();
            }
        }

        image.close();
    }
}
