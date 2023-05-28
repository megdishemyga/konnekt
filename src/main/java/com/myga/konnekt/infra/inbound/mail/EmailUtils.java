package com.myga.konnekt.infra.inbound.mail;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.mail.BodyPart;
import javax.mail.Message;
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

    public static void saveAttachment(Message message) {
        try {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                    String fileName = bodyPart.getFileName();
                    if (fileName != null && !fileName.isEmpty()) {
                        File attachmentsDir = new File("attachments");
                        if (!attachmentsDir.exists()) {
                            attachmentsDir.mkdir();
                        }
                        Path filePath = new File(attachmentsDir, fileName).toPath();
                        Files.copy(bodyPart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                        log.info("Attachment saved: " + filePath);
                    }
                }
            }
        } catch (IOException | MessagingException e) {
            log.error("Error while trying to save attachment", e);
        }
    }
}
