package com.insigma.mvc.service.common.excel;

import java.util.HashMap;
import java.util.List;

import com.insigma.dto.AjaxReturnMsg;
import com.insigma.mvc.model.Ac60ExcelTemp;
import com.insigma.mvc.model.ExcelExportModel;
import com.insigma.mvc.model.SysExcelBatch;




/**
 * excel״̬service
 * @author wengsh
 *
 */
public interface ApiFuPingExcelImportService {
	
	public AjaxReturnMsg<HashMap<String,Object>> getList( SysExcelBatch sExcelBatch );
	
	public AjaxReturnMsg<HashMap<String,Object>> queryPovertyDataTotalByexcelBatchNumber( Ac60ExcelTemp ac60ExcelTemp );
	
	public AjaxReturnMsg<HashMap<String,Object>> getPovertyImprtDataList( Ac60ExcelTemp ac60ExcelTemp );
	
	public AjaxReturnMsg<String> deleteTempDataByNumber(String number);
	
	 public List<ExcelExportModel> queryExportDataByNumber(String number);
	 
	 public AjaxReturnMsg<String> exportData(String number);
	 
	 public  Ac60ExcelTemp queryImpDataById(String aac002);
}
