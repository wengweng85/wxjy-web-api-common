package com.insigma.mvc.service.common.excel;

import java.util.List;

import com.insigma.mvc.model.SysExcelBatch;
import com.insigma.resolver.AppException;




/**
 * excel״̬service
 * @author wengsh
 *
 */
public interface ExcelVS {
	
   public void executeListToTemp(List<String[]> list,SysExcelBatch sExcelBatch) throws AppException;
	
	public void executeProc(SysExcelBatch sExcelBatch)throws AppException;
}
