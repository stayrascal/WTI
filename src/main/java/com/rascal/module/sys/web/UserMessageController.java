package com.rascal.module.sys.web;

import com.rascal.core.annotation.MenuData;
import com.rascal.core.service.BaseService;
import com.rascal.core.web.BaseController;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.sys.entity.UserMessage;
import com.rascal.module.sys.service.UserMessageService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/admin/sys/user-message")
public class UserMessageController extends BaseController<UserMessage, Long> {

    @Autowired
    private UserMessageService userMessageService;

    @Override
    protected BaseService<UserMessage, Long> getEntityService() {
        return userMessageService;
    }

    @RequiresUser
    @ModelAttribute
    public void prepareModel(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(request, model, id);
    }

    @MenuData("配置管理:系统管理:消息管理")
    @RequiresPermissions("配置管理:系统管理:消息管理")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "admin/sys/userMessage-index";
    }

    @RequiresPermissions("配置管理:系统管理:消息管理")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Page<UserMessage> findByPage(HttpServletRequest request) {
        return super.findByPage(UserMessage.class, request);
    }

    @RequiresPermissions("配置管理:系统管理:消息管理")
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editShow() {
        return "admin/sys/userMessage-inputBasic";
    }

    @RequiresPermissions("配置管理:系统管理:消息管理")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult editSave(@ModelAttribute("entity") UserMessage entity) {
        return super.editSave(entity);
    }

    @RequiresPermissions("配置管理:系统管理:消息管理")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult delete(@ModelAttribute("entity") UserMessage entity) {
       /* Integer readCount = userMessageService.countByUserMessage(entity);
        Validation.isTrue(readCount <=0, "该消息已经被阅读，不能被删除");*/
        userMessageService.delete(entity);
        return OperationResult.buildSuccessResult();
    }
}
