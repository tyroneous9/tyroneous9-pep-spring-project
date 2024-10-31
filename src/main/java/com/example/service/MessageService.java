package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.BadRequestException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    MessageRepository messageRepository;
    AccountService accountService;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountService accountService) {
        this.messageRepository = messageRepository;
        this.accountService = accountService;
    }

    public Message addMessage(Message message) throws BadRequestException {
        if(verifyMessage(message) == true && accountService.findAccountById(message.getPostedBy()) != null) {
            return messageRepository.save(message);
        }
        throw new BadRequestException();
    }

    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    public Message findMessageById(Integer messageId) {
        Optional<Message> optMessage = messageRepository.findById(messageId);
        if(optMessage.isPresent())
            return optMessage.get();
        return null;
    }

    public Boolean deleteMessageById(Integer messageId) throws BadRequestException {
        Optional<Message> optMessage = messageRepository.findById(messageId);
        if(optMessage.isPresent()) {
            messageRepository.deleteById(messageId);
            return true;
        }
        return false;
    }

    public Boolean updateMessageById(Integer messageId, Message replacement) throws BadRequestException {
        Optional<Message> optMessage = messageRepository.findById(messageId);
        if(optMessage.isPresent()) {
            Message message = optMessage.get();
            if(verifyMessage(replacement) == true) {
                message.setMessageText(replacement.getMessageText());
                messageRepository.save(message);
                return true;
            }
        }
        throw new BadRequestException();
    }

    public Boolean verifyMessage(Message message) {
        if(!message.getMessageText().isBlank() && 
            message.getMessageText().length() <= 255
        ) {
            return true;
        }
        return false;
    }

    public List<Message> getAllMessagesByUser(Integer accountId) {
        return messageRepository.findMessagesByPostedBy(accountId);
    }
}
