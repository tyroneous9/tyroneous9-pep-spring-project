package com.example.controller;


import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.BadRequestException;
import com.example.exception.UnauthorizedLoginException;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@Controller
public class SocialMediaController {

AccountService accountService;
MessageService messageService;

@Autowired
public SocialMediaController(AccountService accountService, MessageService messageService) {
    this.accountService = accountService;
    this.messageService = messageService;
}

@RequestMapping(value = "/register", method = RequestMethod.POST)
public ResponseEntity<Account> postRegister(@RequestBody Account account) throws BadRequestException, DuplicateUsernameException {
    Account createdAccount = accountService.addAccount(account);
    return ResponseEntity.ok(createdAccount);
}


@RequestMapping(value="login", method=RequestMethod.POST)
public ResponseEntity<Account> postLogin(@RequestBody Account account) throws UnauthorizedLoginException {
    Account foundAccount = accountService.verifyAccount(account);
    return ResponseEntity.ok(foundAccount);
}


@RequestMapping(value="messages", method=RequestMethod.POST)
public ResponseEntity<Message> postMessage(@RequestBody Message message) throws BadRequestException {
    Message createdMessage = messageService.addMessage(message);
    return ResponseEntity.ok(createdMessage);
}


@RequestMapping(value="messages", method=RequestMethod.GET)
public ResponseEntity<List<Message>> getMessages() {
    return ResponseEntity.status(200).body(messageService.findAllMessages());
}

@RequestMapping(value="messages/{messageId}", method=RequestMethod.GET)
public ResponseEntity<Message> getMessage(@PathVariable Integer messageId) {
    return ResponseEntity.status(200).body(messageService.findMessageById(messageId));
}

@RequestMapping(value="messages/{messageId}", method=RequestMethod.DELETE)
public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId) throws BadRequestException {
    if(messageService.deleteMessageById(messageId) == true)
        return ResponseEntity.status(200).body(1);
    return ResponseEntity.status(200).body(null);
}

@RequestMapping(value="messages/{messageId}", method=RequestMethod.PATCH)
public ResponseEntity<Integer> updateMessage(@PathVariable Integer messageId, @RequestBody Message replacement) throws BadRequestException {
    messageService.updateMessageById(messageId, replacement);
    return ResponseEntity.status(200).body(1);
}

// As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{accountId}/messages.
@RequestMapping(value="accounts/{accountId}/messages", method=RequestMethod.GET)
public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable Integer accountId) {
    return ResponseEntity.status(200).body(messageService.getAllMessagesByUser(accountId));
}

@ExceptionHandler(DuplicateUsernameException.class)
@ResponseStatus(HttpStatus.CONFLICT)
public String handleDuplicateUsernameException(Exception e) {
    return e.getMessage();
}

@ExceptionHandler(BadRequestException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public String handleBadRequestException(Exception e) {
    return e.getMessage();
}

@ExceptionHandler(UnauthorizedLoginException.class)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public String handleUnauthorizedLoginException(Exception e) {
    return e.getMessage();
}

}
