package com.rascal.support.service;

import com.google.common.collect.Sets;
import com.rascal.aud.entity.SendMessageLog;
import com.rascal.aud.service.SendMessageLogService;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.service.GlobalConfigService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Set;

@Service
public class MailService {

    private static final ThreadLocal<Set<MailMessage>> mimeMessages = new NamedInheritableThreadLocal<Set<MailMessage>>("Transaction Mail MimeMessages");
    private final Logger logger = LoggerFactory.getLogger(MailService.class);
    @Autowired
    private SendMessageLogService sendMessageLogService;
    @Autowired
    private DynamicConfigService dynamicConfigService;
    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    public boolean isEnabled() {
        return javaMailSender != null;
    }

    public void sendHtmlMail(String subject, String text, boolean singleMode, String... toAddrs) {
        Assert.isTrue(isEnabled(), "Mail service unavailable");
        if (logger.isDebugEnabled()) {
            logger.debug("Submit tobe send mail: To {} , Subject: {}", StringUtils.join(toAddrs, ","), subject);
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            logger.debug("Register mails with database Transaction Synchronization...");
            Set<MailMessage> mails = mimeMessages.get();
            if (mails == null) {
                mails = Sets.newHashSet();
                mimeMessages.set(mails);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        logger.debug("Processing afterCommit of TransactionSynchronizationManager...");
                        Set<MailMessage> transactionMails = mimeMessages.get();
                        transactionMails.forEach(mailMessage -> {
                            sendMail(mailMessage.getSubject(), mailMessage.getText(), mailMessage.isSingleMode(), true, mailMessage.getToAddrs());
                        });
                    }
                });
            }
            MailMessage mail = new MailMessage();
            mail.setSubject(subject);
            mail.setText(text);
            mail.setSingleMode(singleMode);
            mail.setToAddrs(toAddrs);
            mails.add(mail);
        } else {
            this.sendMail(subject, text, singleMode, false, toAddrs);
        }
    }

    private void sendMail(String subject, String text, boolean singleMode, boolean transactional, String... toAddrs) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sending mail: \nTo: {} \nSubject: {} \nSingle Mode: {} \n Transactional Mode: {}" +
                    " \nContent:\n-------\n{}\n---------", StringUtils.join(toAddrs, ","), subject, singleMode, transactional, text);
        }

        if (GlobalConfigService.isDevMode()) {
            logger.debug("Mock sending mail at DEV mode....");
            return;
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        String from = dynamicConfigService.getString("cfg_mail_from", null);
        Assert.notNull(from);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            if (StringUtils.isNotBlank(from)) {
                helper.setFrom(from);
            }
            helper.setSubject(subject);
            helper.setText(text, true);
            if (!singleMode) {
                helper.setTo(toAddrs);
                javaMailSender.send(message);
            } else {
                for (String to : toAddrs) {
                    helper.setTo(to);
                    javaMailSender.send(message);
                }
            }

            //历史消息记录
            SendMessageLog sml = new SendMessageLog();
            sml.setMessageType(SendMessageLog.SendMessageType.APP_PUSH);
            sml.setTargets(StringUtils.join(toAddrs));
            sml.setTitle(subject);
            sml.setMessage(text);
            sml.setSendTime(new Date());
            sendMessageLogService.asynSave(sml);
        } catch (MessagingException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private static class MailMessage {
        private String subject;
        private String text;
        @MetaData(value = "单用户发送模式")
        private boolean singleMode;
        private String[] toAddrs;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isSingleMode() {
            return singleMode;
        }

        public void setSingleMode(boolean singleMode) {
            this.singleMode = singleMode;
        }

        public String[] getToAddrs() {
            return toAddrs;
        }

        public void setToAddrs(String[] toAddrs) {
            this.toAddrs = toAddrs;
        }
    }
}
