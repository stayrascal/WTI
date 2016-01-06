package com.rascal.module.sys.service;


import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.pagination.GroupPropertyFilter;
import com.rascal.core.pagination.PropertyFilter;
import com.rascal.core.pagination.PropertyFilter.MatchType;
import com.rascal.core.service.BaseService;
import com.rascal.core.util.DateUtils;
import com.rascal.module.auth.entity.User;
import com.rascal.module.sys.dao.UserMessageDao;
import com.rascal.module.sys.entity.UserMessage;
import com.rascal.support.service.MailService;
import com.rascal.support.service.MessagePushService;
import com.rascal.support.service.SmsService;
import com.rascal.support.service.SmsService.SmsMessageTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserMessageService extends BaseService<UserMessage, Long> {

    private static final Logger logger = LoggerFactory.getLogger(UserMessageService.class);

    @Autowired
    private UserMessageDao userMessageDao;

    @Autowired(required = false)
    private MailService mailService;

    @Autowired(required = false)
    private SmsService smsService;

    @Autowired(required = false)
    private MessagePushService messagePushService;

    @Override
    protected BaseDao<UserMessage, Long> getEntityDao() {
        return userMessageDao;
    }

    /**
     * 查询用户未读消息个数
     *
     * @param user 当前登录用户
     */
    @Transactional(readOnly = true)
    public Long findCountToRead(User user) {
        GroupPropertyFilter filter = GroupPropertyFilter.buildDefaultAndGroupFilter();
        filter.append(new PropertyFilter(MatchType.EQ, "targetUser", user));
        filter.append(new PropertyFilter(MatchType.NU, "firstReadTime", Boolean.TRUE));
        return count(filter);
    }

    public void processUserRead(UserMessage entity) {
        if (entity.getFirstReadTime() == null) {
            entity.setFirstReadTime(DateUtils.currentDate());
            entity.setLastReadTime(entity.getFirstReadTime());
            entity.setReadTotalCount(1);
        } else {
            entity.setLastReadTime(DateUtils.currentDate());
            entity.setReadTotalCount(entity.getReadTotalCount() + 1);
        }
        userMessageDao.save(entity);
    }

    /**
     * 消息推送处理
     */
    public void pushMessage(UserMessage entity) {
        //定向用户消息处理
        User targetUser = entity.getTargetUser();

        //邮件推送处理
        if (entity.getEmailPush() && entity.getEmailPushTime() == null) {
            String email = targetUser.getEmail();
            if (StringUtils.isNotBlank(email)) {
                mailService.sendHtmlMail(entity.getTitle(), entity.getMessage(), true, email);
                entity.setEmailPushTime(DateUtils.currentDate());
            }
        }

        //短信推送处理
        if (entity.getSmsPush() && entity.getSmsPushTime() == null) {
            if (smsService != null) {
                String mobileNum = targetUser.getMobile();
                if (StringUtils.isNotBlank(mobileNum)) {
                    String errorMessage = smsService.sendSMS(entity.getNotification(), mobileNum, SmsMessageTypeEnum.Default);
                    if (StringUtils.isBlank(errorMessage)) {
                        entity.setSmsPushTime(DateUtils.currentDate());
                    }
                }
            } else {
                logger.warn("SmsService implement NOT found.");
            }

        }

        //APP推送
        if (entity.getAppPush() && entity.getAppPushTime() == null) {
            if (messagePushService != null) {
                Boolean pushResult = messagePushService.sendPush(entity);
                if (pushResult == null || pushResult) {
                    entity.setAppPushTime(DateUtils.currentDate());
                }
            } else {
                logger.warn("MessagePushService implement NOT found.");
            }
        }

        userMessageDao.save(entity);
    }

    @Override
    public UserMessage save(UserMessage entity) {
        super.save(entity);
        pushMessage(entity);
        return entity;
    }
}
