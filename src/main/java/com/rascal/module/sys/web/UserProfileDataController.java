package com.rascal.module.sys.web;

import com.google.common.collect.Maps;
import com.rascal.core.security.AuthContextHolder;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.auth.entity.User;
import com.rascal.module.sys.entity.UserProfileData;
import com.rascal.module.sys.service.UserProfileDataService;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/sys/user-profile-data")
public class UserProfileDataController {

    @Autowired
    private UserProfileDataService userProfileDataService;

    @RequiresUser
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult editSave(HttpServletRequest request) {
        String[] codes = request.getParameter("codes").split(",");
        User user = AuthContextHolder.findAuthUser();
        for (String code : codes) {
            UserProfileData entity = userProfileDataService.findByUserAndCode(user, code);
            if (entity == null) {
                entity = new UserProfileData();
                entity.setUser(user);
            }
            entity.setCode(code);
            entity.setValue(request.getParameter(code));
            userProfileDataService.save(entity);
        }
        return OperationResult.buildSuccessResult("参数默认值设定成功");
    }

    @RequiresUser
    @RequestMapping(value = "/params", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> params() {
        User user = AuthContextHolder.findAuthUser();
        Map<String, String> datas = Maps.newHashMap();
        List<UserProfileData> items = userProfileDataService.findByUser(user);
        for (UserProfileData item : items) {
            datas.put(item.getCode(), item.getValue());
        }
        return datas;
    }
}
