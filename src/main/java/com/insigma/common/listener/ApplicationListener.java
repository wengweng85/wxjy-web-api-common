package com.insigma.common.listener;

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.insigma.common.util.EhCacheUtil;
import com.insigma.mvc.model.Aa01;
import com.insigma.mvc.service.common.init.ApiInitService;

/**
 * 
 * @author wengsh
 * 
 */
public class ApplicationListener implements   ServletContextListener  {
	Log log=LogFactory.getLog(ApplicationListener.class);


	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ApiInitService initservice = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext()).getBean(ApiInitService.class);
		List<Aa01> aa01List =initservice.getAa01List().getObj();
		if (aa01List.size() > 0) {
			HashMap hm = new HashMap();
			for (Aa01 aa01 : aa01List) {
				hm.put(aa01.getAaa001(), aa01.getAaa005());
			}
			EhCacheUtil.getManager().getCache("webcache").put(new Element("aa01",hm));
		}
	}
}
