package com.myga.konnekt.infra.inbound.mail;

import com.sun.mail.imap.IMAPMessage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@UtilityClass
public final class EmailUtils {

    public static void saveAttachment(IMAPMessage message) {
        try {
            log.info("**********************" + message.getSubject());
            if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                        String fileName = bodyPart.getFileName();
                        if (StringUtils.isNotBlank(fileName)) {
                            File attachmentsDir = new File("attachments");
                            Path filePath = new File(attachmentsDir, fileName).toPath();
                            Files.copy(bodyPart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                            log.info("Attachment saved: " + filePath);
                        }
                    }
                }
            }
        } catch (IOException | MessagingException e) {
            log.error("Error while trying to save attachment", e);
        }
    }
}
