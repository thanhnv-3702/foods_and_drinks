package com.slearn.membermanagement.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiện ích sinh nội dung CSV (RFC 4180): escape dấu phẩy/nháy/xuống dòng,
 * dùng CRLF và thêm BOM UTF-8 để Excel hiển thị đúng tiếng Việt.
 */
public final class CsvUtil {

    private static final String BOM = "\uFEFF";
    private static final String CRLF = "\r\n";

    private CsvUtil() {
    }

    public static String build(List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder(BOM);
        sb.append(joinRow(headers)).append(CRLF);
        for (List<String> row : rows) {
            sb.append(joinRow(row)).append(CRLF);
        }
        return sb.toString();
    }

    private static String joinRow(List<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escape(fields.get(i)));
        }
        return sb.toString();
    }

    private static String escape(String value) {
        String v = (value == null) ? "" : value;
        boolean mustQuote = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        if (mustQuote) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    /**
     * Parse nội dung CSV (RFC 4180) thành danh sách các dòng (mỗi dòng là list field).
     * Hỗ trợ field có dấu ngoặc kép, dấu phẩy/xuống dòng bên trong, và BOM UTF-8.
     */
    public static List<List<String>> parse(String content) {
        List<List<String>> rows = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return rows;
        }
        if (content.startsWith(BOM)) {
            content = content.substring(BOM.length());
        }
        List<String> current = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        int n = content.length();
        for (int i = 0; i < n; i++) {
            char c = content.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < n && content.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(c);
                }
            } else {
                switch (c) {
                    case '"' -> inQuotes = true;
                    case ',' -> {
                        current.add(field.toString());
                        field.setLength(0);
                    }
                    case '\r' -> {
                        // bỏ qua, xử lý ở '\n'
                    }
                    case '\n' -> {
                        current.add(field.toString());
                        field.setLength(0);
                        rows.add(current);
                        current = new ArrayList<>();
                    }
                    default -> field.append(c);
                }
            }
        }
        // dòng cuối không kết thúc bằng newline
        if (field.length() > 0 || !current.isEmpty()) {
            current.add(field.toString());
            rows.add(current);
        }
        return rows;
    }
}
