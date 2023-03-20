package cn.iruite.service;

import cn.iruite.util.ZipUtil;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: LiRuite
 * @Date: 2023/3/15 11:01
 */
@Service
public class IndexService {

    public void start(MultipartFile file, HttpServletResponse response) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM月dd日");
        Workbook sourceWorkbook = XSSFWorkbookFactory.createWorkbook(file.getInputStream());
        Workbook targetWorkbook = XSSFWorkbookFactory.createWorkbook();
        Sheet sourceSheet = sourceWorkbook.getSheetAt(0);
        Sheet targetSheet = targetWorkbook.createSheet();
        Row head = null ;
        List<Row> rows = Lists.newArrayList();
        for (int i = 0; i < sourceSheet.getPhysicalNumberOfRows(); i++) {
            Row sourceRow = sourceSheet.getRow(i);
            Row targetRow = targetSheet.createRow(i);
            CellStyle sourceRowStyle = sourceRow.getRowStyle();
            CellStyle targetRowStyle = targetWorkbook.createCellStyle();
            try {
                targetRowStyle.cloneStyleFrom(sourceRowStyle);
                targetRow.setRowStyle(targetRowStyle);
            } catch (Exception e) {
            }
            int cellNums = sourceRow.getPhysicalNumberOfCells();
            for (int i1 = 0; i1 < cellNums; i1++) {
                Cell sourceCell = sourceRow.getCell(i1);
                Cell targetCell = targetRow.createCell(i1);
                CellStyle sourceCellStyle = sourceCell.getCellStyle();
                CellStyle targetCellCellStyle = targetWorkbook.createCellStyle();
                try {
                    targetCellCellStyle.cloneStyleFrom(sourceCellStyle);
                    targetCell.setCellStyle(targetCellCellStyle);
                } catch (Exception e) {
                    Cell cell = targetRow.getCell(i1 - 1);
                    CellStyle cellStyle = cell.getCellStyle();
                    targetCell.setCellStyle(cellStyle);
                }
                CellType cellType = sourceCell.getCellType();
                switch (cellType) {
                    case NUMERIC:
                        if (i == 0 ){
                            LocalDateTime localDateTimeCellValue = sourceCell.getLocalDateTimeCellValue();
                            String format = localDateTimeCellValue.format(formatter);
                            targetCell.setCellValue(format);
                            break;
                        }
                        double numericCellValue = sourceCell.getNumericCellValue();
                        targetCell.setCellValue(numericCellValue);
                        break;
                    case STRING:
                        String stringCellValue = sourceCell.getStringCellValue();
                        targetCell.setCellValue(stringCellValue);
                        break;
                    case FORMULA:
                        String cellFormula = sourceCell.getCellFormula();
                        targetCell.setCellFormula(cellFormula);
                        break;
                    case BLANK:
                        targetCell.setBlank();
                        break;
                    case BOOLEAN:
                        boolean booleanCellValue = sourceCell.getBooleanCellValue();
                        targetCell.setCellValue(booleanCellValue);
                        break;
                    case ERROR:
                        byte errorCellValue = sourceCell.getErrorCellValue();
                        targetCell.setCellErrorValue(errorCellValue);
                    default:
                        break;
                }
            }

            if (i > 0 ){
                rows.add(targetRow);
            }else {
                head = targetRow;
            }

        }
        Map<String, List<Row>> rowMap = rows.stream().collect(Collectors.groupingBy(b -> {
            try {
                return b.getCell(5).getStringCellValue();
            } catch (Exception e) {
                return "NA";
            }
        }));

        Row finalHead = head;
        Path savePath = Files.createTempDirectory("relaxJob");
        rowMap.forEach((k,v) -> {
            XSSFWorkbook workbook = XSSFWorkbookFactory.createWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            v.add(0, finalHead);
            for (int i = 0; i < v.size(); i++) {
                Row sourceRow = v.get(i);
                Row targetRow = sheet.createRow(i);
                CellStyle sourceRowStyle = sourceRow.getRowStyle();
                CellStyle targetRowStyle = workbook.createCellStyle();
                try {
                    targetRowStyle.cloneStyleFrom(sourceRowStyle);
                    targetRow.setRowStyle(targetRowStyle);
                } catch (Exception e) {
                }
                int cellNums = sourceRow.getPhysicalNumberOfCells();
                for (int i1 = 0; i1 < cellNums; i1++) {
                    Cell sourceCell = sourceRow.getCell(i1);
                    Cell targetCell = targetRow.createCell(i1);
                    CellStyle sourceCellStyle = sourceCell.getCellStyle();
                    CellStyle targetCellCellStyle = workbook.createCellStyle();
                    try {
                        targetCellCellStyle.cloneStyleFrom(sourceCellStyle);
                        targetCell.setCellStyle(targetCellCellStyle);
                    } catch (Exception e) {
                        Cell cell = targetRow.getCell(i1 - 1);
                        CellStyle cellStyle = cell.getCellStyle();
                        targetCell.setCellStyle(cellStyle);
                    }
                    CellType cellType = sourceCell.getCellType();
                    switch (cellType) {
                        case NUMERIC:
                            double numericCellValue = sourceCell.getNumericCellValue();
                            targetCell.setCellValue(numericCellValue);
                            break;
                        case STRING:
                            String stringCellValue = sourceCell.getStringCellValue();
                            targetCell.setCellValue(stringCellValue);
                            break;
                        case FORMULA:
                            String cellFormula = sourceCell.getCellFormula();
                            targetCell.setCellFormula(cellFormula);
                            break;
                        case BLANK:
                            targetCell.setBlank();
                            break;
                        case BOOLEAN:
                            boolean booleanCellValue = sourceCell.getBooleanCellValue();
                            targetCell.setCellValue(booleanCellValue);
                            break;
                        case ERROR:
                            byte errorCellValue = sourceCell.getErrorCellValue();
                            targetCell.setCellErrorValue(errorCellValue);
                        default:
                            break;
                    }
                }
            }

            try {

                File exportFile = new File(savePath.toAbsolutePath() + File.separator + k + ".xlsx");
                workbook.write(Files.newOutputStream(exportFile.toPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        File relaxJob = File.createTempFile("relaxJob", ".zip");
        String zipFile = relaxJob.getAbsolutePath();
        ZipUtil.doZip(savePath.toAbsolutePath().toString(), zipFile,false);
        System.out.println("zipFile = " + zipFile);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(relaxJob.getName(), "UTF-8"));
        FileInputStream fis = new FileInputStream(relaxJob);
        int len;
        ServletOutputStream fos = response.getOutputStream();
        byte[] b = new byte[1024];
        while ((len= fis.read(b)) != -1){
            fos.write(b,0, len);
        }
        fis.close();
        fos.close();
    }



}
