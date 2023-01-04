package com.admin.util;

import com.admin.domain.inquiry.Inquiry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class ExcelDownloadUtil {
    public void inquiryData(String sheetName, String fileName, String[] header, List<Inquiry.Response> list, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultRowHeight((short) 500);
        sheet.setDefaultColumnWidth(20);
        sheet.setColumnWidth(7, 10000);

        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle cs = workbook.createCellStyle();
        cs.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);

        Row row = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(cs);
            cell.setCellValue(header[i]);
        }

        int dataLength = list.size();
        for (int i = 0; i < dataLength; i++) {
            Inquiry.Response inquiry = list.get(i);
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(inquiry.getCompany());
            row.createCell(1).setCellValue(inquiry.getIndustry());
            row.createCell(2).setCellValue(inquiry.getName());
            row.createCell(3).setCellValue(inquiry.getTel());
            row.createCell(4).setCellValue(inquiry.getEmail());
            row.createCell(5).setCellValue(inquiry.getWorkType());
            row.createCell(6).setCellValue(inquiry.getWorkLoad());
            row.createCell(7).setCellValue(inquiry.getContents());
            row.createCell(8).setCellValue(inquiry.getRegisterTime());
        }

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8")+".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
