package dto.auth

import kotlinx.serialization.Serializable
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank
import validator.Validatable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
) : Validatable {
    override fun validate() {
        org.valiktor.validate(this) {
            validate(LoginRequest::email).isNotBlank().isEmail()
            validate(LoginRequest::password).isNotBlank()
        }
    }
}