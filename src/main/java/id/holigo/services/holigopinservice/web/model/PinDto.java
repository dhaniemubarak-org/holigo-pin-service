package id.holigo.services.holigopinservice.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PinDto {
    @NotBlank
    @Size(min = 6, max = 6)
    @Pattern(regexp = "^([+-]?(\\d+)([,.]\\d+)?)?$" )
    private String pin;
}
