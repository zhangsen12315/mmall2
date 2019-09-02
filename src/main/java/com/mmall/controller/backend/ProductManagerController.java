package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.FileService;
import com.mmall.service.IUserService;
import com.mmall.service.ProductService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ProductService iProductService;

    @Autowired
    private FileService iFileService;

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,HttpServletRequest httpServletRequest,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(iUserService.checkRoleIsAdmin(user).isSuccess()){
            //填充业务
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest httpServletRequest, Product product){

        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if (iUserService.checkRoleIsAdmin(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("setSaleStatus.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest httpServletRequest,Integer productId, Integer status){

        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if (iUserService.checkRoleIsAdmin(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpServletRequest httpServletRequest,Integer productId, Integer status){

        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if (iUserService.checkRoleIsAdmin(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse productSearch(HttpServletRequest httpServletRequest,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){

        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(iUserService.checkRoleIsAdmin(user).isSuccess()){
            //填充业务
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpServletRequest httpServletRequest,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){

        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (org.apache.commons.lang.StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);

        if(iUserService.checkRoleIsAdmin(user).isSuccess()){

            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }


    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpServletRequest httpServletRequest,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//            "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if(iUserService.checkRoleIsAdmin(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}
