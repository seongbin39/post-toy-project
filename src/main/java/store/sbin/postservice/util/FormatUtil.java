package store.sbin.postservice.util;

import org.springframework.stereotype.Component;

/**
 * 용량 데이터를 포맷팅하는 유틸리티 클래스입니다.
 */
@Component("formatUtil")
public class FormatUtil {
    public String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int unitIndex = (int) (Math.log10(bytes) / 3);
        double unitValue = bytes / Math.pow(1024, unitIndex);
        return String.format("%.1f %s", unitValue, units[unitIndex]);
    }
}