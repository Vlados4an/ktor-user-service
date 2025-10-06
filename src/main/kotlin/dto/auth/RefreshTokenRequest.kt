package dto.auth

import kotlinx.serialization.Serializable
import org.valiktor.functions.isNotBlank
import validator.Validatable

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
) : Validatable {
    override fun validate() {
        org.valiktor.validate(this) {
            validate(RefreshTokenRequest::refreshToken).isNotBlank()
        }
    }
}