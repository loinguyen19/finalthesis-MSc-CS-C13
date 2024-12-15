package com.nbloi.cqrses.mail;

import java.io.Serial;
import java.io.Serializable;

public record MailMessage(String subject, String body, String to) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
