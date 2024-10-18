package nl.saxion.conferenceroom.utils.models;

import java.util.UUID;

public record Message(String sender, String receiver, String head, String body, String messageId) {
    public Message(String sender, String receiver, String head, String body) {
        this(sender, receiver, head, body, UUID.randomUUID().toString());
    }
}
