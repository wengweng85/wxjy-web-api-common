package com.insigma.common.sysuser;

import com.insigma.mvc.model.SUser;

/**
 * 系统工具类
 *
 * @author wengsh
 */
public class SysUserUtil {


    private static ThreadLocal<SUser> local = new ThreadLocal<SUser>();

    public static void setCurrentUser(SUser suser) {
        local.set(suser);  
    }  
  
    public static SUser getCurrentUser() {  
        return local.get();  
    } 
    
    public static  void removeCurrentUser () {  
         local.remove();
    } 

  
}
