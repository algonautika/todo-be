package algo.todo.domain.todo.validate

import algo.todo.global.util.TimeUtil
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class TimeZoneValidator : ConstraintValidator<ValidTimeZone, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) {
            return false
        }

        return runCatching {
            TimeUtil.convertToTimeZone(value)
        }.isSuccess
    }
}
