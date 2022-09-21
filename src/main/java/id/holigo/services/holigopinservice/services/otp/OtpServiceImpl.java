package id.holigo.services.holigopinservice.services.otp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.common.model.OtpDto;
import id.holigo.services.common.model.OtpStatusEnum;
import id.holigo.services.holigopinservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final JmsTemplate jmsTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public OtpDto getOtpForResetPin(OtpDto otpDto)
            throws JsonProcessingException, JMSException {
        Message received = jmsTemplate.sendAndReceive(JmsConfig.OTP_RESET_PIN_VALIDATION_QUEUE, session -> {
            Message message = null;
            try {
                message = session.createTextMessage(objectMapper.writeValueAsString(otpDto));
            } catch (JsonProcessingException e) {
                throw new JMSException(e.getMessage());
            }
            message.setStringProperty("_type", "id.holigo.services.common.model.OtpDto");

            return message;
        });
        assert received != null;
        return objectMapper.readValue(received.getBody(String.class), OtpDto.class);
    }
}
