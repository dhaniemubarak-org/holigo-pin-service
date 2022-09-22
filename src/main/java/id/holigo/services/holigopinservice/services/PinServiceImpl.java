package id.holigo.services.holigopinservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.common.model.OtpDto;
import id.holigo.services.common.model.OtpStatusEnum;
import id.holigo.services.holigopinservice.domain.User;
import id.holigo.services.holigopinservice.repositories.UserRepository;
import id.holigo.services.holigopinservice.services.otp.OtpService;
import id.holigo.services.holigopinservice.web.exceptions.ForbiddenException;
import id.holigo.services.holigopinservice.web.exceptions.NotFoundException;
import id.holigo.services.holigopinservice.web.model.ChangePinDto;
import id.holigo.services.holigopinservice.web.model.CreateNewPinDto;
import id.holigo.services.holigopinservice.web.model.ResetPinDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.Optional;

@Slf4j
@Service
public class PinServiceImpl implements PinService {

    private UserRepository userRepository;

    private OtpService otpService;

    @Autowired
    public void setOtpService(OtpService otpService) {
        this.otpService = otpService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Boolean createNewPin(Long userId, CreateNewPinDto createNewPinDto) {

        Optional<User> fetchUser = userRepository.findById(userId);
        if (fetchUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = fetchUser.get();
        if (user.getPin() != null) {
            throw new ForbiddenException("PIN has been set!");
        }
        user.setPin(createNewPinDto.getPin(), true);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean updatePin(Long userId, ChangePinDto changePinDto) {

        Optional<User> fetchUser = userRepository.findById(userId);
        if (fetchUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = fetchUser.get();
        boolean isValid = new BCryptPasswordEncoder().matches(changePinDto.getCurrentPin(), user.getPin());
        if (!isValid) {
            throw new ForbiddenException("Current PIN not valid");
        }
        user.setPin(changePinDto.getPin(), true);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean resetPin(Long userId, ResetPinDto resetPinDto) throws JMSException, JsonProcessingException {
        Optional<User> fetchUser = userRepository.findById(userId);
        if (fetchUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = fetchUser.get();
        OtpDto otpDto = otpService.getOtpForResetPin(OtpDto.builder().phoneNumber(user.getPhoneNumber())
                .oneTimePassword(resetPinDto.getOneTimePassword()).build());

        boolean isOtpValid = otpDto.getStatus() == OtpStatusEnum.CONFIRMED;
        if (isOtpValid) {
            user.setPin(resetPinDto.getPin(), true);
            try {
                userRepository.save(user);
            } catch (Exception e) {
                log.error("Failed set PIN. Error : {}", e.getMessage());
                return false;
            }
        } else {
            log.error("Otp is wring");
            return false;
        }
        return true;
    }
}
