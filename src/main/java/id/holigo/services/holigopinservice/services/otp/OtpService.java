package id.holigo.services.holigopinservice.services.otp;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.common.model.OtpDto;

import javax.jms.JMSException;

public interface OtpService {
    OtpDto getOtpForResetPin(OtpDto otpDto) throws JsonProcessingException, JMSException;

}
