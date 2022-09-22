package id.holigo.services.holigopinservice.web.controllers;

import id.holigo.services.common.model.OtpDto;
import id.holigo.services.holigopinservice.components.Decryption;
import id.holigo.services.holigopinservice.domain.User;
import id.holigo.services.holigopinservice.repositories.UserRepository;
import id.holigo.services.holigopinservice.services.PinService;
import id.holigo.services.holigopinservice.web.exceptions.ForbiddenException;
import id.holigo.services.holigopinservice.web.exceptions.NotFoundException;
import id.holigo.services.holigopinservice.web.model.ChangePinDto;
import id.holigo.services.holigopinservice.web.model.CreateNewPinDto;
import id.holigo.services.holigopinservice.web.model.PinValidationDto;
import id.holigo.services.holigopinservice.web.model.ResetPinDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
public class PinController {

    private UserRepository userRepository;

    private PinService pinService;


    @Autowired
    public void setPinService(PinService pinService) {
        this.pinService = pinService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(path = {"/api/v1/pin/validate"})
    public ResponseEntity<PinValidationDto> pinValidation(@RequestBody PinValidationDto pinValidationDto,
                                                          @RequestHeader("user-id") Long userId) {
        String decryptedText = pinValidationDto.getPin();
        Optional<User> fetchUser = userRepository.findById(userId);
        if (fetchUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = fetchUser.get();
        if (user.getPin() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String[] pin = user.getPin().split("\\.\\$");
        log.info("pin -> {}", (Object) pin);
        log.info("pin length -> {}", pin.length);
        log.info("pin[0] -> {}", pin[0]);
        if (pin.length > 6) {
            pinValidationDto.setAttemptGranted(0);
            return new ResponseEntity<>(pinValidationDto, HttpStatus.FORBIDDEN);
        }

//        try {
//            decryptedText = decrypted(pinValidationDto.getPin(), String.valueOf(userId));
//        } catch (Exception e) {
//            user.setPin(user.getPin() + ".$" + System.currentTimeMillis(), false);
//            userRepository.save(user);
//            pinValidationDto.setAttemptGranted(6 - pin.length);
//            return new ResponseEntity<>(pinValidationDto, HttpStatus.NOT_ACCEPTABLE);
//        }
        boolean isValid = new BCryptPasswordEncoder().matches(decryptedText, pin[0]);
        if (!isValid) {
            user.setPin(user.getPin() + ".$" + System.currentTimeMillis(), false);
            userRepository.save(user);
            pinValidationDto.setAttemptGranted(6 - pin.length);
            return new ResponseEntity<>(pinValidationDto, HttpStatus.NOT_ACCEPTABLE);
        }
        user.setPin(decryptedText, true);
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = {"/api/v1/pin/users/{id}/pin"})
    public ResponseEntity<HttpStatus> pinCheckAvailability(@PathVariable("id") Long id,
                                                           @RequestHeader("user-id") Long userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User fetchUser = user.get();
        if (fetchUser.getPin() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = {"/api/v1/pin/users/{id}/pin"})
    public ResponseEntity<HttpStatus> createNewPin(@PathVariable("id") Long id,
                                                   @Valid @RequestBody CreateNewPinDto createNewPinDto,
                                                   @RequestHeader("user-id") Long userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        Boolean isCreated = pinService.createNewPin(id, createNewPinDto);
        if (!isCreated) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(path = {"/api/v1/pin/users/{id}/pin"})
    public ResponseEntity<HttpStatus> updatePin(@PathVariable("id") Long id,
                                                @Valid @RequestBody ChangePinDto changePinDto,
                                                @RequestHeader("user-id") Long userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        Boolean isUpdated = pinService.updatePin(id, changePinDto);
        if (!isUpdated) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = {"/api/v1/pin/users/{id}/resetPin"})
    public ResponseEntity<OtpDto> resetPin(@PathVariable("id") Long id,
                                           @Valid @RequestBody ResetPinDto resetPinDto,
                                           @RequestHeader("user-id") Long userId) throws Exception {
        if (!id.equals(userId)) {
            throw new NotFoundException();
        }
        Boolean isReset = pinService.resetPin(id, resetPinDto);
        if (!isReset) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private String decrypted(String encryptedText, String password) throws Exception {
        String decryptedText;
        Decryption decryption = new Decryption();
        decryptedText = decryption.decrypt(encryptedText, password);
        return decryptedText;
    }
}
