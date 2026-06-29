package com.slearn.membermanagement.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Kết quả import CSV: tổng số dòng dữ liệu, số dòng thành công, và danh sách lỗi theo dòng.
 */
@Getter
public class ImportResult {

    private int total;
    private int success;
    private final List<RowError> errors = new ArrayList<>();

    public void incrementSuccess() {
        this.total++;
        this.success++;
    }

    public void addError(int line, String message) {
        this.total++;
        this.errors.add(new RowError(line, message));
    }

    public int getFailed() {
        return errors.size();
    }

    @Getter
    public static class RowError {
        private final int line;
        private final String message;

        public RowError(int line, String message) {
            this.line = line;
            this.message = message;
        }
    }
}
