package com.insigma.mvc.dao.common.excel;

import java.util.List;

import com.insigma.mvc.model.Ac60ExcelTemp;
import com.insigma.mvc.model.ExcelExportModel;
import com.insigma.mvc.model.SysExcelBatch;


/**
 * PovertyEmployInfoImportMapper
 * @author wengsh
 *
 */
public interface ApiFuPingExcelImportMapper {
	
    public void insertExcelTempData(Ac60ExcelTemp ac60Temp);
    
    public void executePovertyData(SysExcelBatch sExcelBatch);
    
    public List<Ac60ExcelTemp> queryPovertyDataTotalByexcelBatchNumber(Ac60ExcelTemp ac60ExcelTemp);
    
    public List<Ac60ExcelTemp> queryPovertyDataByexcelBatchNumber(Ac60ExcelTemp ac60ExcelTemp);
    
    public int deleteTempDataByNumber(String number);
    
    public List<ExcelExportModel> queryExportDataByNumber(String number);
    
    public  Ac60ExcelTemp queryImpDataById(String aac001);
   
    
}
