package com.rascal.aud.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.rascal.aud.entity.SendMessageLog;
import com.rascal.aud.entity.SendMessageLog.SendMessageTypeEnum;
import com.rascal.aud.service.SendMessageLogService;
import com.rascal.core.annotation.MenuData;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.service.BaseService;
import com.rascal.core.util.EnumUtils;
import com.rascal.core.web.BaseController;
import com.rascal.core.web.json.JsonViews;
import com.rascal.core.web.view.OperationResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@MetaData("发送消息记录管理")
@Controller
@RequestMapping(value = "/admin/aud/send-message-log")
public class SendMessageLogController extends BaseController<SendMessageLog, Long> {

    @Autowired
    private SendMessageLogService sendMessageLogService;

    @Override
    protected BaseService<SendMessageLog, Long> getEntityService() {
        return sendMessageLogService;
    }

    @ModelAttribute
    public void prepareModel(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(request, model, id);
    }

    @MenuData("配置管理:系统记录:发送消息记录")
    @RequiresPermissions("配置管理:系统记录:发送消息记录")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("messageTypeMap", EnumUtils.getEnumDataMap(SendMessageTypeEnum.class));
        return "admin/aud/sendMessageLog-index";
    }

    @RequiresPermissions("配置管理:系统记录:发送消息记录")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.Admin.class)
    public Page<SendMessageLog> findByPage(HttpServletRequest request) {
        return super.findByPage(SendMessageLog.class, request);
    }

    @RequestMapping(value = "/edit-tabs", method = RequestMethod.GET)
    public String editTabs() {
        return "admin/aud/sendMessageLog-inputTabs";
    }

    //@RequiresPermissions("TODO {PATH}:SendMessageLog")
    @RequiresPermissions("配置管理:系统记录:发送消息记录")
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editShow(Model model) {
        model.addAttribute("messageTypeMap", EnumUtils.getEnumDataMap(SendMessageTypeEnum.class));
        return "admin/aud/sendMessageLog-inputBasic";
    }

    @RequiresPermissions("配置管理:系统记录:发送消息记录")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult editSave(@ModelAttribute("entity") SendMessageLog entity) {
        return super.editSave(entity);
    }

    @RequiresPermissions("配置管理:系统记录:发送消息记录")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult delete(@RequestParam("ids") Long... ids) {
        return super.delete(ids);
    }

}