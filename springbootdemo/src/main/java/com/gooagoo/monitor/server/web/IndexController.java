package com.gooagoo.monitor.server.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.birdboy.monitor.server.cache.ClientsCache;
import com.birdboy.monitor.server.common.NormalResponse;
import com.gooagoo.monitor.server.request.HTTPClientParam;
import com.gooagoo.monitor.server.services.MessageSenderService;
import com.gooagoo.monitor.server.status.FromTypeEnum;
import com.gooagoo.monitor.server.status.HttpStatusEnum;
import com.google.gson.Gson;

@Controller
public class IndexController {
    static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/index")
    @ResponseBody
    public Object index(HttpServletRequest req,
                        HttpServletResponse resp) {
        NormalResponse nr = NormalResponse.create();
        return nr;
    }
    /**
     * 注册服务监控
     * @param json
     * @param resp
     * @return
     */
    @RequestMapping(value = "/v1/client/registry",method = RequestMethod.POST)
    @ResponseBody
    
    public Object regisit(@RequestBody String json,
                        HttpServletResponse resp) {
    	NormalResponse re = NormalResponse.create();
    	Gson gson = new Gson();
    	try {
    		HTTPClientParam hcp = gson.fromJson(json, HTTPClientParam.class);
    		if(hcp.getType()==FromTypeEnum.CLIENT.getValue()){
    			System.out.println("获取注册的服务器: "+hcp.getServerNode());
    			ClientsCache.getInstance().freshCache(hcp.getServerNode());
    		}else{
    			re.setRet(HttpStatusEnum.ERROR.getValue());
    			re.setMsg("请求参数不对!");
    		}   		
		} catch (Exception e) {
			re.setMsg(e.getMessage());
			
		}
		re.setRet(HttpStatusEnum.OK.getValue());
		logger.info("/v1/client/registry 注册成功:"+re.toString());
        return re;
    }
    
    
    
    /**
     * 注册服务监控
     * @param json
     * @param resp
     * @return
     */
    @RequestMapping(value = "/v1/service/sendmgs",method = RequestMethod.POST)
    @ResponseBody
    public Object sendmgs(@RequestParam String content,@RequestParam String tos,
                        HttpServletResponse resp) {
    	
    	NormalResponse re = NormalResponse.create();  
    	String msg = "";
    	try {
    		msg = MessageSenderService.getInstance().sendMessage(tos, content);
		} catch (Exception e) {
			re.setMsg(e.getMessage());			
		}
		re.setRet(HttpStatusEnum.OK.getValue());
		logger.info("/v1/service/sendmgs 发送短信成功:"+re.toString()+" 短信服务器返回状态:"+msg);
        return re;
    }    
    
    
}
