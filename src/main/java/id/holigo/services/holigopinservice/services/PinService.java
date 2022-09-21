package id.holigo.services.holigopinservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.holigopinservice.web.model.ChangePinDto;
import id.holigo.services.holigopinservice.web.model.CreateNewPinDto;
import id.holigo.services.holigopinservice.web.model.ResetPinDto;

import javax.jms.JMSException;

public interface PinService {
    Boolean createNewPin(Long userId, CreateNewPinDto createNewPinDto);

    Boolean updatePin(Long userId, ChangePinDto changePinDto);

    Boolean resetPin(Long userId, ResetPinDto resetPinDto) throws JMSException, JsonProcessingException;
}