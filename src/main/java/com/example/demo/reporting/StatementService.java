package com.example.demo.reporting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;

/**
 * Generates account statements as PDF (for printing / sharing with a customer) and Excel
 * (for a branch back-office reconciling records). Kept as its own service rather than bolted
 * onto TransactionService, since document rendering is a different concern from transaction
 * business logic and pulls in a much heavier dependency (PDFBox/POI) that the rest of the
 * app doesn't need.
 */
@Service
public class StatementService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    public byte[] generatePdfStatement(Account account, List<Transaction> transactions) {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font headerFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            float rowHeight = 16;

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                content.beginText();
                content.setFont(titleFont, 16);
                content.newLineAtOffset(margin, y);
                content.showText("Account Statement");
                content.endText();
                y -= 28;

                content.beginText();
                content.setFont(bodyFont, 10);
                content.newLineAtOffset(margin, y);
                content.showText("Account Number: " + account.getAccountNumber());
                content.endText();
                y -= 14;

                content.beginText();
                content.setFont(bodyFont, 10);
                content.newLineAtOffset(margin, y);
                content.showText("Account Holder: " + account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName());
                content.endText();
                y -= 14;

                content.beginText();
                content.setFont(bodyFont, 10);
                content.newLineAtOffset(margin, y);
                content.showText("Current Balance: " + formatAmount(account.getBalance()));
                content.endText();
                y -= 26;

                String[] headers = {"Date", "Reference", "Type", "Amount", "Balance After"};
                float[] colX = {margin, margin + 90, margin + 220, margin + 300, margin + 390};

                content.setFont(headerFont, 9);
                for (int i = 0; i < headers.length; i++) {
                    content.beginText();
                    content.newLineAtOffset(colX[i], y);
                    content.showText(headers[i]);
                    content.endText();
                }
                y -= 6;
                content.moveTo(margin, y);
                content.lineTo(page.getMediaBox().getWidth() - margin, y);
                content.stroke();
                y -= rowHeight;

                content.setFont(bodyFont, 8);
                for (Transaction txn : transactions) {

                    if (y < margin) {
                        break;
                    }

                    String[] row = {
                            txn.getTransactionDate() != null ? txn.getTransactionDate().format(DATE_FORMAT) : "-",
                            txn.getTransactionReference(),
                            txn.getTransactionType(),
                            formatAmount(txn.getAmount()),
                            formatAmount(txn.getBalanceAfterTransaction())
                    };

                    for (int i = 0; i < row.length; i++) {
                        content.beginText();
                        content.newLineAtOffset(colX[i], y);
                        content.showText(row[i] != null ? row[i] : "-");
                        content.endText();
                    }
                    y -= rowHeight;
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate PDF statement", e);
        }
    }

    public byte[] generateExcelStatement(Account account, List<Transaction> transactions) {

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet sheet = workbook.createSheet("Statement");

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);

            Row summary1 = sheet.createRow(0);
            summary1.createCell(0).setCellValue("Account Number:");
            summary1.createCell(1).setCellValue(account.getAccountNumber());

            Row summary2 = sheet.createRow(1);
            summary2.createCell(0).setCellValue("Account Holder:");
            summary2.createCell(1).setCellValue(account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName());

            Row summary3 = sheet.createRow(2);
            summary3.createCell(0).setCellValue("Current Balance:");
            summary3.createCell(1).setCellValue(account.getBalance());

            int headerRowIdx = 4;
            Row header = sheet.createRow(headerRowIdx);
            String[] headers = {"Date", "Reference", "Type", "Amount", "Balance After", "Remarks", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = headerRowIdx + 1;
            for (Transaction txn : transactions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(txn.getTransactionDate() != null ? txn.getTransactionDate().format(DATE_FORMAT) : "-");
                row.createCell(1).setCellValue(txn.getTransactionReference());
                row.createCell(2).setCellValue(txn.getTransactionType());
                row.createCell(3).setCellValue(txn.getAmount());
                row.createCell(4).setCellValue(txn.getBalanceAfterTransaction());
                row.createCell(5).setCellValue(txn.getRemarks());
                row.createCell(6).setCellValue(txn.getStatus());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate Excel statement", e);
        }
    }

    private String formatAmount(Double amount) {
        return amount == null ? "-" : String.format("%,.2f", amount);
    }
}
