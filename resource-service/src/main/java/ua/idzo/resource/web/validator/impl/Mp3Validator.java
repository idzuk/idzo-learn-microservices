package ua.idzo.resource.web.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.idzo.resource.core.util.FileUtil;
import ua.idzo.resource.web.validator.ValidMp3;

public class Mp3Validator implements ConstraintValidator<ValidMp3, byte[]> {

    @Override
    public boolean isValid(byte[] data, ConstraintValidatorContext context) {
        if (data == null || data.length == 0) {
            return false;
        }

        try {
            return FileUtil.isAudioMpeg(data);
        } catch (Exception e) {
            return false;
        }
    }
}
