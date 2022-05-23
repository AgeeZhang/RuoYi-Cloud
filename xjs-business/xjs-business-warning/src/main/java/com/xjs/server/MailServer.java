package com.xjs.server;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.common.redis.service.RedisService;
import com.xjs.annotation.MailLog;
import com.xjs.domain.mall.MailBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.xjs.consts.RedisConst.MAIL_STATUS;

/**
 * 邮箱发送工具
 *
 * @author xiejs
 * @since 2022-04-13
 */
@Component
@Log4j2
public class MailServer {

    @Value("${spring.mail.username}")
    //邮件发送者
    private String MAIL_SENDER;

    @Resource
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisService redisService;


    /**
     * 发送邮件统一出口
     *
     * @param mailBean 邮箱实体
     */
    @MailLog
    //当抛出MailException异常时，该方法重试两次
    @Retryable(maxAttempts = 2, value = MailException.class)
    public Boolean sendMail(MailBean mailBean) {

        if (redisService.hasKey(MAIL_STATUS)) {
            throw new RuntimeException("邮件发送频繁!请稍后重试！");
        }

        int code = mailBean.getMailType().getCode();
        try {
            if (code == MailBean.MailType.SIMPLE.getCode()) {
                this.sendSimpleMail(mailBean);
            } else if (code == MailBean.MailType.HTML.getCode()) {
                this.sendHTMLMail(mailBean);
            } else if (code == MailBean.MailType.ATTACHMENT.getCode()) {
                this.sendAttachmentMail(mailBean);
            } else if (code == MailBean.MailType.INLINE.getCode()) {
                this.sendInlineMail(mailBean);
            } else if (code == MailBean.MailType.TEMPLATE.getCode()) {
                this.sendTempLateMail(mailBean);
            }

            redisService.setCacheObject(MAIL_STATUS, true, 3L, TimeUnit.SECONDS);

            return Boolean.TRUE;
        } catch (Exception e) {
            throw new RuntimeException("邮件发送失败");
        }
    }


    /**
     * 发送文本邮件
     *
     * @param mailBean 邮箱实体
     */
    private void sendSimpleMail(MailBean mailBean) throws Exception {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(MAIL_SENDER);
            mailMessage.setTo(mailBean.getRecipient());
            mailMessage.setSubject(mailBean.getSubject());
            mailMessage.setText(mailBean.getContent());

            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            log.error("文本邮件发送失败:{}", e.getMessage());
            throw e;
        }
    }

    /**
     * 发送HTML格式邮件
     *
     * @param mailBean 邮箱实体
     */
    private void sendHTMLMail(MailBean mailBean) throws MessagingException {
        MimeMessage mimeMailMessage = null;
        try {
            mimeMailMessage = javaMailSender.createMimeMessage();
            //true 表示需要创建一个multipart message
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());

            //邮件抄送
            if (CollUtil.isNotEmpty(mailBean.getCc())) {
                for (String c : mailBean.getCc()) {
                    mimeMessageHelper.addCc(c);
                }
            }
            mimeMessageHelper.setText(mailBean.getContent(), true);
            javaMailSender.send(mimeMailMessage);
        } catch (Exception e) {
            log.error("HTML格式邮件发送失败:{}", e.getMessage());
            throw e;
        }
    }


    /**
     * 附件格式邮件发送
     *
     * @param mailBean 邮箱实体
     */
    private void sendAttachmentMail(MailBean mailBean) throws MessagingException, IOException {
        MimeMessage mimeMailMessage = null;
        try {
            mimeMailMessage = javaMailSender.createMimeMessage();
            //true 表示需要创建一个multipart message
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());
            mimeMessageHelper.setText(mailBean.getContent(), true);

            //邮件抄送
            if (CollUtil.isNotEmpty(mailBean.getCc())) {
                for (String c : mailBean.getCc()) {
                    mimeMessageHelper.addCc(c);
                }
            }

            //发送附件
            if (mailBean.getFileList() != null && mailBean.getFileList().length > 0) {
                for (MultipartFile multipartFile : mailBean.getFileList()) {
                    try (InputStream inputStream = multipartFile.getInputStream()) {
                        byte[] bytes = inputStream.readAllBytes();
                        ByteArrayResource bar = new ByteArrayResource(bytes);
                        mimeMessageHelper.addAttachment(Objects.requireNonNull(multipartFile.getOriginalFilename()), bar);
                    }
                }
            }

            javaMailSender.send(mimeMailMessage);
        } catch (Exception e) {
            log.error("附件格式邮件发送失败:{}", e.getMessage());
            throw e;
        }
    }


    /**
     * 静态资源格式邮件发送
     *
     * @param mailBean 邮箱实体
     */
    private void sendInlineMail(MailBean mailBean) throws MessagingException {
        MimeMessage mimeMailMessage = null;
        try {
            mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());
            mimeMessageHelper.setText(mailBean.getContent(), true);
            //文件路径
            FileSystemResource file = new FileSystemResource(new File(mailBean.getAbsolutePath()));
            //FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/image/email.png"))
            //添加多个图片可以使用多条 <img src='cid:" + rscId + "' > 和 mimeMessageHelper.addInline(rscId, res) 来实现
            mimeMessageHelper.addInline("picture", file);

            javaMailSender.send(mimeMailMessage);
        } catch (Exception e) {
            log.error("静态资源格式邮件发送失败:{}", e.getMessage());
            throw e;
        }
    }

    /**
     * 发送Thymeleaf模版邮件
     */
    private void sendTempLateMail(MailBean mailBean) throws MessagingException {
        //注意：Context 类是在org.thymeleaf.context.Context包下的。
        Context context = new Context();
        //html中填充动态属性值
        context.setVariable("username", mailBean.getUserName());
        context.setVariable("url", "#");
        //注意：process第一个参数名称要和templates下的模板名称一致。要不然会报错
        //org.thymeleaf.exceptions.TemplateInputException: Error resolving template [email]
        String emailContent = templateEngine.process("email", context);
        mailBean.setContent(null);
        mailBean.setContent(emailContent);
        this.sendHTMLMail(mailBean);
    }


}