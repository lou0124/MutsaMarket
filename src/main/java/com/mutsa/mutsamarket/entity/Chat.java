package com.mutsa.mutsamarket.entity;

import com.mutsa.mutsamarket.exception.NotAllowAccessChatException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Chat {

    @Id @GeneratedValue
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "seller_id")
    private Users seller;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "buyer_id")
    private Users buyer;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    public void addMessage(ChatMessage message) {
        this.chatMessages.add(message);
        message.setChat(this);
    }

    public void verifyAccess(String username) {
        if (!seller.equals(username) &&  !buyer.equals(username)) {
            throw new NotAllowAccessChatException();
        }
    }
}
