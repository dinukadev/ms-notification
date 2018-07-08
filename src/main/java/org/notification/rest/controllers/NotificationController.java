package org.notification.rest.controllers;

import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.notification.constants.MSNotificationConstants;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.dto.Dto;
import org.notification.rest.dto.EmailNotificationRequestDto;
import org.notification.rest.dto.NotificationResponseDto;
import org.notification.rest.dto.SMSNotificationRequestDto;
import org.notification.rest.dto.assembler.NotificationResponseDtoAssembler;
import org.notification.service.NotificationService;
import org.notification.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dinuka
 */
@RestController
@RequestMapping(MSNotificationConstants.API_VERSION_1)
public class NotificationController {

  private static Logger log = LoggerFactory.getLogger(NotificationController.class);

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private ModelMapper mapper;

  @Autowired
  private CommonUtil commonUtil;


  /**
   * POST /v1/notification/sync/sendEmail
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(
      method = RequestMethod.POST,
      value = MSNotificationConstants.API_SYNC_SENDEMAIL,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation("This operation will send out an email using the details provided")
  public Dto<NotificationResponseDto> sendEmailSync(
      @Valid @RequestBody final EmailNotificationRequestDto emailNotificationRequestDto,
      HttpServletRequest request) {
    String clientId = commonUtil.getClientIdFromHTTPHeader(request);
    log.info("Received a request to send out an email synchronously by client : {}", clientId);
    NotificationTransactionInfo notificationTransactionInfo = mapper
        .map(emailNotificationRequestDto, NotificationTransactionInfo.class);
    notificationTransactionInfo.setClientId(clientId);
    log.info("Calling the notification service to send out the email.");
    NotificationResponseDto notificationResponseDto = notificationService
        .sendEmailSync(notificationTransactionInfo);
    log.info("Response received : {}", notificationResponseDto.toString());
    return new Dto(notificationResponseDto);
  }

  /**
   * POST /v1/notification/async/sendEmail
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(
      method = RequestMethod.POST,
      value = MSNotificationConstants.API_ASYNC_SENDEMAIL,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Dto<NotificationResponseDto> sendEmailAsync(
      @Valid @RequestBody final EmailNotificationRequestDto emailNotificationRequestDto,
      HttpServletRequest request) {
    String clientId = commonUtil.getClientIdFromHTTPHeader(request);
    NotificationTransactionInfo notificationTransactionInfo = mapper
        .map(emailNotificationRequestDto, NotificationTransactionInfo.class);
    log.info("Calling the email sending functionality asynchronously");
    notificationTransactionInfo.setClientId(clientId);
    notificationService.sendEmailAsync(notificationTransactionInfo);
    NotificationResponseDto responseDto = NotificationResponseDtoAssembler.assemble(
        notificationTransactionInfo.getReferenceNumber(),
        NotificationStatus.QUEUED.toString(), clientId);
    log.info("Sending out the response as the email sending functionality was" +
            " queued up for sending. reference number : {}",
        notificationTransactionInfo.getReferenceNumber());
    return new Dto(responseDto);
  }

  /**
   * POST /v1/notification/sync/sendSMS
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(
      method = RequestMethod.POST,
      value = MSNotificationConstants.API_SYNC_SENDSMS,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation("This operation will send out an email using the details provided")
  public Dto<NotificationResponseDto> sendSMSSync(
      @Valid @RequestBody final SMSNotificationRequestDto emailNotificationRequestDto,
      HttpServletRequest request) {
    String clientId = commonUtil.getClientIdFromHTTPHeader(request);
    log.info("Received a request to send out an sms synchronously by client : {}", clientId);
    NotificationTransactionInfo notificationTransactionInfo = mapper
        .map(emailNotificationRequestDto, NotificationTransactionInfo.class);
    notificationTransactionInfo.setClientId(clientId);
    log.info("Calling the notification service to send out the sms.");
    NotificationResponseDto notificationResponseDto = notificationService
        .sendSmsSync(notificationTransactionInfo);
    log.info("Response received : {}", notificationResponseDto.toString());
    return new Dto(notificationResponseDto);
  }

  /**
   * POST /v1/notification/async/sendSMS
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(
      method = RequestMethod.POST,
      value = MSNotificationConstants.API_ASYNC_SENDSMS,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Dto<NotificationResponseDto> sendSMSAsync(
      @Valid @RequestBody final SMSNotificationRequestDto emailNotificationRequestDto,
      HttpServletRequest request) {
    String clientId = commonUtil.getClientIdFromHTTPHeader(request);
    NotificationTransactionInfo notificationTransactionInfo = mapper.map(
        emailNotificationRequestDto, NotificationTransactionInfo.class);
    log.info("Calling the sms sending functionality asynchronously");
    notificationTransactionInfo.setClientId(clientId);
    notificationService.sendSMSAsync(notificationTransactionInfo);
    NotificationResponseDto responseDto = NotificationResponseDtoAssembler.assemble(
        notificationTransactionInfo.getReferenceNumber(),
        NotificationStatus.QUEUED.toString(),
        clientId);
    log.info("Sending out the response as the sms sending functionality was " +
            "queued up for sending. reference number : {}",
        notificationTransactionInfo.getReferenceNumber());
    return new Dto(responseDto);
  }
}
