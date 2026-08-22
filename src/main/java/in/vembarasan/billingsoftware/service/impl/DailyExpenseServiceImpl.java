package in.vembarasan.billingsoftware.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.vembarasan.billingsoftware.Exception.ApiException;
import in.vembarasan.billingsoftware.entity.DailyExpenseEntity;
import in.vembarasan.billingsoftware.io.DailyExpenseRequest;
import in.vembarasan.billingsoftware.io.DailyExpenseResponse;
import in.vembarasan.billingsoftware.repository.BillRepository;
import in.vembarasan.billingsoftware.repository.DailyExpenseRepository;
import in.vembarasan.billingsoftware.service.DailyExpenseService;
import in.vembarasan.billingsoftware.io.DailyReportDataResponse;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import java.awt.Color;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyExpenseServiceImpl implements DailyExpenseService {

    private final DailyExpenseRepository dailyExpenseRepository;
    private final BillRepository billRepository;
    private final ObjectMapper objectMapper;

    @Override
    public DailyExpenseResponse add(DailyExpenseRequest request) {
        String dailyExpenseId = generateDailyExpenseId();
        
        Double lastClosed = request.getLastClosed();
        if (lastClosed == null || lastClosed == 0.0) {
            lastClosed = getLastClosedAmount(request.getBranch(), request.getDate());
        }

        DailyExpenseEntity dailyExpense = DailyExpenseEntity.builder()
                .dailyExpenseId(dailyExpenseId)
                .date(request.getDate())
                .branch(request.getBranch())
                .cashInHand(request.getCashInHand())
                .lastClosed(lastClosed)
                .shortage(request.getShortage())
                .image(request.getImage())
                .totalCash(request.getTotalCash())
                .expensive(convertToJson(request.getExpensive()))
                .otherExpensive(convertToJson(request.getOtherExpensive()))
                .advancePaid(convertToJson(request.getAdvancePaid()))
                .checkPayment(convertToJson(request.getCheckPayment()))
                .cashDeposit(convertToJson(request.getCashDeposit()))
                .otherIncomes(convertToJson(request.getOtherIncomes()))
                .machineReading(convertToJson(request.getMachineReading()))
                .build();

        dailyExpense = dailyExpenseRepository.save(dailyExpense);
        return convertToResponse(dailyExpense);
    }

    @Override
    public DailyExpenseResponse update(String dailyExpenseId, DailyExpenseRequest request) {
        DailyExpenseEntity existingExpense = dailyExpenseRepository.findByDailyExpenseId(dailyExpenseId)
                .orElseThrow(() -> new ApiException("Daily expense not found: " + dailyExpenseId, HttpStatus.NOT_FOUND));

        if (request.getDate() != null) {
            existingExpense.setDate(request.getDate());
        }
        if (request.getBranch() != null) {
            existingExpense.setBranch(request.getBranch());
        }
        if (request.getCashInHand() != null) {
            existingExpense.setCashInHand(request.getCashInHand());
        }
        if (request.getLastClosed() != null) {
            existingExpense.setLastClosed(request.getLastClosed());
        }
        if (request.getShortage() != null) {
            existingExpense.setShortage(request.getShortage());
        }
        if (request.getImage() != null) {
            existingExpense.setImage(request.getImage());
        }
        if (request.getTotalCash() != null) {
            existingExpense.setTotalCash(request.getTotalCash());
        }
        if (request.getExpensive() != null) {
            existingExpense.setExpensive(convertToJson(request.getExpensive()));
        }
        if (request.getOtherExpensive() != null) {
            existingExpense.setOtherExpensive(convertToJson(request.getOtherExpensive()));
        }
        if (request.getAdvancePaid() != null) {
            existingExpense.setAdvancePaid(convertToJson(request.getAdvancePaid()));
        }
        if (request.getCheckPayment() != null) {
            existingExpense.setCheckPayment(convertToJson(request.getCheckPayment()));
        }
        if (request.getCashDeposit() != null) {
            existingExpense.setCashDeposit(convertToJson(request.getCashDeposit()));
        }
        if (request.getOtherIncomes() != null) {
            existingExpense.setOtherIncomes(convertToJson(request.getOtherIncomes()));
        }
        if (request.getMachineReading() != null) {
            existingExpense.setMachineReading(convertToJson(request.getMachineReading()));
        }

        existingExpense = dailyExpenseRepository.save(existingExpense);
        return convertToResponse(existingExpense);
    }

    @Override
    public DailyExpenseResponse getById(String dailyExpenseId) {
        DailyExpenseEntity dailyExpense = dailyExpenseRepository.findByDailyExpenseId(dailyExpenseId)
                .orElseThrow(() -> new ApiException("Daily expense not found: " + dailyExpenseId, HttpStatus.NOT_FOUND));
        return convertToResponse(dailyExpense);
    }

    @Override
    public DailyExpenseResponse getByBranchAndDate(String branch, Date date) {
        DailyExpenseEntity dailyExpense = dailyExpenseRepository.findByBranchAndDate(branch, date)
                .orElseThrow(() -> new ApiException("Daily expense not found for branch: " + branch + " and date: " + date, HttpStatus.NOT_FOUND));
        return convertToResponse(dailyExpense);
    }

    @Override
    public Page<DailyExpenseResponse> getAll(int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    public Page<DailyExpenseResponse> getByBranch(String branch, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findByBranch(branch, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<DailyExpenseResponse> getByDate(Date date, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findByDate(date, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<DailyExpenseResponse> getByDateRange(Date startDate, Date endDate, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findByDateRange(startDate, endDate, pageable).map(this::convertToResponse);
    }

    @Override
    public Page<DailyExpenseResponse> getByBranchAndDateRange(String branch, Date startDate, Date endDate, int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return dailyExpenseRepository.findByBranchAndDateRange(branch, startDate, endDate, pageable).map(this::convertToResponse);
    }

    @Override
    public void delete(String dailyExpenseId) {
        DailyExpenseEntity existingExpense = dailyExpenseRepository.findByDailyExpenseId(dailyExpenseId)
                .orElseThrow(() -> new ApiException("Daily expense not found: " + dailyExpenseId, HttpStatus.NOT_FOUND));
        dailyExpenseRepository.delete(existingExpense);
    }

    @Override
    public List<DailyReportDataResponse> getDailyReports(Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<DailyExpenseEntity> expensesPage = dailyExpenseRepository.findByDateRange(startDate, endDate, pageable);
        List<DailyReportDataResponse> responses = new ArrayList<>();
        
        for (DailyExpenseEntity entity : expensesPage.getContent()) {
            responses.add(mapToDailyReportDataResponse(entity));
        }
        return responses;
    }

    private DailyReportDataResponse mapToDailyReportDataResponse(DailyExpenseEntity entity) {
        DailyReportDataResponse response = new DailyReportDataResponse();
        response.setDailyExpenseId(entity.getDailyExpenseId());
        response.setDate(entity.getDate());
        response.setBranch(entity.getBranch());
        response.setCashInHand(entity.getCashInHand());
        
        Double lastClosed = entity.getLastClosed();
        if (lastClosed == null || lastClosed == 0.0) {
            lastClosed = getLastClosedAmount(entity.getBranch(), entity.getDate());
        }
        response.setLastClosed(lastClosed);
        response.setShortage(entity.getShortage());
        response.setTotalCash(entity.getTotalCash());
        response.setImage(entity.getImage());
        
        Double totalSales = billRepository.sumTotalAmountByDateRange(entity.getDate(), entity.getDate());
        response.setTotalSales(totalSales != null ? totalSales : 0.0);
        
        // Expenses
        List<DailyExpenseRequest.ExpenseItem> expenses = parseJson(entity.getExpensive(), new TypeReference<>() {});
        Map<String, Double> expensesMap = new HashMap<>();
        double expensesTotal = 0.0;
        if (expenses != null) {
            for (DailyExpenseRequest.ExpenseItem item : expenses) {
                expensesMap.put(item.getItemName(), item.getPrice());
                if (item.getPrice() != null) expensesTotal += item.getPrice();
            }
        }
        response.setExpenses(expensesMap);
        response.setExpensesTotal(expensesTotal);
        
        // Other Expenses
        List<DailyExpenseRequest.OtherExpense> otherExpenses = parseJson(entity.getOtherExpensive(), new TypeReference<>() {});
        Map<String, Double> otherExpensesMap = new HashMap<>();
        double otherExpensesTotal = 0.0;
        if (otherExpenses != null) {
            for (DailyExpenseRequest.OtherExpense item : otherExpenses) {
                otherExpensesMap.put(item.getType(), item.getAmount());
                if (item.getAmount() != null) otherExpensesTotal += item.getAmount();
            }
        }
        response.setOtherExpenses(otherExpensesMap);
        response.setOtherExpensesTotal(otherExpensesTotal);
        
        // Check Payment
        List<DailyExpenseRequest.CheckPayment> checkPayments = parseJson(entity.getCheckPayment(), new TypeReference<>() {});
        Map<String, Double> checkPaymentMap = new HashMap<>();
        double checkPaymentTotal = 0.0;
        if (checkPayments != null) {
            for (DailyExpenseRequest.CheckPayment item : checkPayments) {
                checkPaymentMap.put(item.getCheckNo(), item.getAmount());
                if (item.getAmount() != null) checkPaymentTotal += item.getAmount();
            }
        }
        response.setCheckPayment(checkPaymentMap);
        response.setCheckPaymentTotal(checkPaymentTotal);
        
        // Advance Paid
        List<DailyExpenseRequest.AdvancePayment> advancePaid = parseJson(entity.getAdvancePaid(), new TypeReference<>() {});
        Map<String, Double> advancePaidMap = new HashMap<>();
        double advancePaidTotal = 0.0;
        if (advancePaid != null) {
            for (DailyExpenseRequest.AdvancePayment item : advancePaid) {
                advancePaidMap.put(item.getType(), item.getAmount());
                if (item.getAmount() != null) advancePaidTotal += item.getAmount();
            }
        }
        response.setAdvancePaid(advancePaidMap);
        response.setAdvancePaidTotal(advancePaidTotal);
        
        // Cash Deposit
        List<DailyExpenseRequest.CashDeposit> cashDeposits = parseJson(entity.getCashDeposit(), new TypeReference<>() {});
        Map<String, Double> cashDepositMap = new HashMap<>();
        double cashDepositTotal = 0.0;
        if (cashDeposits != null) {
            for (DailyExpenseRequest.CashDeposit item : cashDeposits) {
                cashDepositMap.put(item.getRefNo(), item.getAmount());
                if (item.getAmount() != null) cashDepositTotal += item.getAmount();
            }
        }
        response.setCashDeposit(cashDepositMap);
        response.setCashDepositTotal(cashDepositTotal);
        
        // Other Incomes
        List<DailyExpenseRequest.OtherIncome> otherIncomes = parseJson(entity.getOtherIncomes(), new TypeReference<>() {});
        Map<String, Double> otherIncomesMap = new HashMap<>();
        double otherIncomesTotal = 0.0;
        if (otherIncomes != null) {
            for (DailyExpenseRequest.OtherIncome item : otherIncomes) {
                otherIncomesMap.put(item.getReason(), item.getAmount());
                if (item.getAmount() != null) otherIncomesTotal += item.getAmount();
            }
        }
        response.setOtherIncomes(otherIncomesMap);
        response.setOtherIncomesTotal(otherIncomesTotal);
        
        // Machine Reading
        List<DailyExpenseRequest.MachineReading> machineReadings = parseJson(entity.getMachineReading(), new TypeReference<>() {});
        Map<String, Double> machineReadingMap = new HashMap<>();
        if (machineReadings != null) {
            for (DailyExpenseRequest.MachineReading item : machineReadings) {
                machineReadingMap.put(item.getMachine(), item.getDifference());
            }
        }
        response.setMachineReading(machineReadingMap);
        
        return response;
    }

    @Override
    public byte[] generateDailyReportPdf(String dailyExpenseId) {
        DailyExpenseEntity entity = dailyExpenseRepository.findByDailyExpenseId(dailyExpenseId)
                .orElseThrow(() -> new ApiException("Daily expense not found: " + dailyExpenseId, HttpStatus.NOT_FOUND));
        
        DailyReportDataResponse data = mapToDailyReportDataResponse(entity);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(41, 128, 185));
            Paragraph title = new Paragraph("Daily Expense & Sales Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            
            // Summary Info Table
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(20f);
            
            addSummaryRow(summaryTable, "Date", String.valueOf(data.getDate()));
            addSummaryRow(summaryTable, "Branch", data.getBranch());
            addSummaryRow(summaryTable, "Total Sales", String.valueOf(data.getTotalSales()));
            addSummaryRow(summaryTable, "Cash in Hand", String.valueOf(data.getCashInHand()));
            addSummaryRow(summaryTable, "Total Cash", String.valueOf(data.getTotalCash()));
            
            document.add(summaryTable);
            
            // Detailed Maps
            addMapToPdf(document, "Expenses", data.getExpenses(), data.getExpensesTotal());
            addMapToPdf(document, "Other Expenses", data.getOtherExpenses(), data.getOtherExpensesTotal());
            addMapToPdf(document, "Check Payments", data.getCheckPayment(), data.getCheckPaymentTotal());
            addMapToPdf(document, "Advance Paid", data.getAdvancePaid(), data.getAdvancePaidTotal());
            addMapToPdf(document, "Cash Deposits", data.getCashDeposit(), data.getCashDepositTotal());
            addMapToPdf(document, "Other Incomes", data.getOtherIncomes(), data.getOtherIncomesTotal());
            
            if (data.getMachineReading() != null && !data.getMachineReading().isEmpty()) {
                Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(44, 62, 80));
                Paragraph sectionTitle = new Paragraph("Machine Readings", sectionFont);
                sectionTitle.setSpacingAfter(10f);
                document.add(sectionTitle);
                
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setSpacingAfter(20f);
                
                PdfPCell header1 = new PdfPCell(new Paragraph("Machine", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
                header1.setBackgroundColor(new Color(52, 73, 94));
                header1.setPadding(8f);
                table.addCell(header1);
                
                PdfPCell header2 = new PdfPCell(new Paragraph("Reading/Difference", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
                header2.setBackgroundColor(new Color(52, 73, 94));
                header2.setPadding(8f);
                table.addCell(header2);
                
                for (Map.Entry<String, Double> entry : data.getMachineReading().entrySet()) {
                    PdfPCell cell1 = new PdfPCell(new Paragraph(entry.getKey()));
                    cell1.setPadding(6f);
                    cell1.setBorderColor(new Color(189, 195, 199));
                    table.addCell(cell1);
                    
                    PdfPCell cell2 = new PdfPCell(new Paragraph(String.valueOf(entry.getValue())));
                    cell2.setPadding(6f);
                    cell2.setBorderColor(new Color(189, 195, 199));
                    table.addCell(cell2);
                }
                document.add(table);
            }
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Failed to generate PDF", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(52, 73, 94));
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, labelFont));
        labelCell.setPadding(8f);
        labelCell.setBorderColor(new Color(189, 195, 199));
        labelCell.setBackgroundColor(new Color(236, 240, 241));
        
        PdfPCell valueCell = new PdfPCell(new Paragraph(value != null ? value : "N/A"));
        valueCell.setPadding(8f);
        valueCell.setBorderColor(new Color(189, 195, 199));
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addMapToPdf(Document document, String title, Map<String, Double> dataMap, Double total) throws Exception {
        if (dataMap == null || dataMap.isEmpty()) return;
        
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(44, 62, 80));
        Paragraph sectionTitle = new Paragraph(title, sectionFont);
        sectionTitle.setSpacingAfter(10f);
        document.add(sectionTitle);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);
        
        PdfPCell header1 = new PdfPCell(new Paragraph("Description", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
        header1.setBackgroundColor(new Color(52, 73, 94));
        header1.setPadding(8f);
        table.addCell(header1);
        
        PdfPCell header2 = new PdfPCell(new Paragraph("Amount", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
        header2.setBackgroundColor(new Color(52, 73, 94));
        header2.setPadding(8f);
        table.addCell(header2);
        
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            PdfPCell cell1 = new PdfPCell(new Paragraph(entry.getKey()));
            cell1.setPadding(6f);
            cell1.setBorderColor(new Color(189, 195, 199));
            table.addCell(cell1);
            
            PdfPCell cell2 = new PdfPCell(new Paragraph(String.valueOf(entry.getValue())));
            cell2.setPadding(6f);
            cell2.setBorderColor(new Color(189, 195, 199));
            table.addCell(cell2);
        }
        
        PdfPCell totalLabelCell = new PdfPCell(new Paragraph("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        totalLabelCell.setPadding(8f);
        totalLabelCell.setBackgroundColor(new Color(236, 240, 241));
        totalLabelCell.setBorderColor(new Color(189, 195, 199));
        table.addCell(totalLabelCell);
        
        PdfPCell totalValueCell = new PdfPCell(new Paragraph(String.valueOf(total), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        totalValueCell.setPadding(8f);
        totalValueCell.setBackgroundColor(new Color(236, 240, 241));
        totalValueCell.setBorderColor(new Color(189, 195, 199));
        table.addCell(totalValueCell);
        
        document.add(table);
    }

    private DailyExpenseResponse convertToResponse(DailyExpenseEntity entity) {
        Double lastClosed = entity.getLastClosed();
        if (lastClosed == null || lastClosed == 0.0) {
            lastClosed = getLastClosedAmount(entity.getBranch(), entity.getDate());
        }

        return DailyExpenseResponse.builder()
                .dailyExpenseId(entity.getDailyExpenseId())
                .date(entity.getDate())
                .branch(entity.getBranch())
                .cashInHand(entity.getCashInHand())
                .lastClosed(lastClosed)
                .shortage(entity.getShortage())
                .image(entity.getImage())
                .totalCash(entity.getTotalCash())
                .expensive(parseJson(entity.getExpensive(), new TypeReference<>() {}))
                .otherExpensive(parseJson(entity.getOtherExpensive(), new TypeReference<>() {}))
                .advancePaid(parseJson(entity.getAdvancePaid(), new TypeReference<>() {}))
                .checkPayment(parseJson(entity.getCheckPayment(), new TypeReference<>() {}))
                .cashDeposit(parseJson(entity.getCashDeposit(), new TypeReference<>() {}))
                .otherIncomes(parseJson(entity.getOtherIncomes(), new TypeReference<>() {}))
                .machineReading(parseJson(entity.getMachineReading(), new TypeReference<>() {}))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Double getLastClosedAmount(String branch, Date date) {
        if (date == null) return 0.0;

        java.time.LocalDate targetLocalDate = date.toLocalDate();
        java.time.LocalDate yesterdayLocalDate = targetLocalDate.minusDays(1);
        Date yesterdayDate = Date.valueOf(yesterdayLocalDate);

        // 1. Check for yesterday's exact record
        Optional<DailyExpenseEntity> yesterdayRecord;
        if (branch != null && !branch.trim().isEmpty()) {
            yesterdayRecord = dailyExpenseRepository.findByBranchAndDate(branch, yesterdayDate);
        } else {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 1);
            org.springframework.data.domain.Page<DailyExpenseEntity> page = dailyExpenseRepository.findByDate(yesterdayDate, pageable);
            yesterdayRecord = page.hasContent() ? Optional.of(page.getContent().get(0)) : Optional.empty();
        }

        if (yesterdayRecord.isPresent() && yesterdayRecord.get().getCashInHand() != null) {
            return yesterdayRecord.get().getCashInHand();
        }

        // 2. Fallback: check most recent record prior to target date
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 1);
        org.springframework.data.domain.Page<DailyExpenseEntity> previousPage = dailyExpenseRepository.findPreviousRecords(branch, date, pageable);
        if (previousPage.hasContent()) {
            DailyExpenseEntity previous = previousPage.getContent().get(0);
            return previous.getCashInHand() != null ? previous.getCashInHand() : 0.0;
        }

        return 0.0;
    }

    private String convertToJson(Object object) {
        try {
            if (object == null) return null;
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new ApiException("Failed to convert object to JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private <T> T parseJson(String json, TypeReference<T> typeReference) {
        try {
            if (json == null || json.isEmpty()) return null;
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new ApiException("Failed to parse JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateDailyExpenseId() {
        return "DLE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
