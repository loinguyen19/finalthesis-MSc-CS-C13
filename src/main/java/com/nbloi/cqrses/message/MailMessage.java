package com.nbloi.cqrses.message;

import java.io.Serial;
import java.io.Serializable;

public record MailMessage(String subject, String body, String to) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
