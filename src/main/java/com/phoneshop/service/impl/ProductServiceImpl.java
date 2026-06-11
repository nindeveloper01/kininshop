package com.phoneshop.service.impl;

import com.phoneshop.dto.PriceDTO;
import com.phoneshop.dto.ProductImportDTO;
import com.phoneshop.entity.Product;
import com.phoneshop.entity.ProductImportHistory;
import com.phoneshop.exception.ApiException;
import com.phoneshop.exception.ResourceNotFoundException;
import com.phoneshop.mapper.ProductMapper;
import com.phoneshop.repository.ProductImportHistoryRepository;
import com.phoneshop.repository.ProductRepository;
import com.phoneshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImportHistoryRepository importHistoryRepository;
    @Override
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Override
    public Product create(Product product) {
        String name = "%s %s"
                .formatted(product.getModel().getName(), product.getColor().getName()) ;
        product.setName(name);
        return productRepository.save(product);
    }
    @Override
    public void importProduct(ProductImportDTO importDTO) {
        // update available product unit
        Product product = getById(importDTO.getProductId());
        Integer availableUnit = 0;
        if(product.getAvailableUnit() != null) {
            availableUnit = product.getAvailableUnit();
        }
        product.setAvailableUnit(availableUnit + importDTO.getImportUnit());
        productRepository.save(product);
        log.info("Product imported successfully{}", product);

        // save product import history
        ProductImportHistory importHistory = productMapper.toProductImportHistory(importDTO, product);
        importHistoryRepository.save(importHistory);
    }

    @Override
    public Map<Integer, String> uploadProduct(MultipartFile file) {
        Map<Integer, String> map = new HashedMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet("products");
            Iterator<Row> rowIterator = sheet.iterator();

            rowIterator.next(); // @TODO improve checking error

            while(rowIterator.hasNext()) {
                Integer rowNumber = 0;
                try {
                    Row row = rowIterator.next();
                    int cellIndex = 0;

                    Cell cellNo = row.getCell(cellIndex++);
                    rowNumber = (int) cellNo.getNumericCellValue();

                    Cell cellModelId = row.getCell(cellIndex++);
                    Long modelId =  (long) cellModelId.getNumericCellValue();

                    Cell cellColorId = row.getCell(cellIndex++);
                    Long colorId =  (long) cellColorId.getNumericCellValue();

                    Cell cellImportPrice = row.getCell(cellIndex++);
                    Double importPrice =  cellImportPrice.getNumericCellValue();

                    Cell cellImportUnit = row.getCell(cellIndex++);
                    Integer importUnit =  (int) cellImportUnit.getNumericCellValue();
                    if(importUnit < 1) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Unit must be greater than 0");
                    }

                    Cell cellImportDate = row.getCell(cellIndex++);
                    LocalDateTime importDate = cellImportDate.getLocalDateTimeCellValue();

                    Product product = getByModelIdAndColorId(modelId, colorId);


                    //System.out.println(modelId);
                    Integer availableUnit = 0;
                    if(product.getAvailableUnit() != null) {
                        availableUnit = product.getAvailableUnit();
                    }
                    product.setAvailableUnit(availableUnit + importUnit);
                    productRepository.save(product);

                    // save product import history
                    //ProductImportHistory importHistory = productMapper.toProductImportHistory(importDTO, product);
                    ProductImportHistory importHistory = new ProductImportHistory();
                    importHistory.setDateImport(importDate);
                    importHistory.setImportUnit(importUnit);
                    importHistory.setPricePerUnit(BigDecimal.valueOf(importPrice));
                    importHistory.setProduct(product);
                    importHistoryRepository.save(importHistory);
                }catch(Exception e) {
                    map.put(rowNumber, e.getMessage());
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Product getByModelIdAndColorId(Long modelId, Long colorId) {

        String text = "Product with model id =%s and color id = %d was not found";
        return productRepository.findByModelIdAndColorId(modelId, colorId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,text.formatted(modelId, colorId)));
    }

    @Override
    public void setSalePrice(Long productId, PriceDTO priceDTO) {
        Product product = getById(productId);
        product.setSalePrice(priceDTO.getPrice());
        productRepository.save(product);
    }


}
