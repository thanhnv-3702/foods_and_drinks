package com.slearn.membermanagement.util;

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
}
