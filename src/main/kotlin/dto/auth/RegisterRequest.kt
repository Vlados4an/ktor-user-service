package dto.auth

import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.functions.matches
import validator.Validatable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null
) : Validatable {
    override fun validate() {
        org.valiktor.validate(this) {
            validate(RegisterRequest::email).isNotBlank().isEmail()
            validate(RegisterRequest::password).isNotBlank().hasSize(min = 4, max = 100)
            validate(RegisterRequest::firstName).isNotBlank().hasSize(min = 2, max = 100)
            validate(RegisterRequest::lastName).isNotBlank().hasSize(min = 2, max = 100)
            validate(RegisterRequest::phone).isNotNull().hasSize(min = 10, max = 20)
                .matches(Regex("^\\+?[0-9\\s\\-()]+$"))
        }
    }
}