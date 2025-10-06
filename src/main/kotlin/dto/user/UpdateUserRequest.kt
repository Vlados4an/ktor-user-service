package dto.user

import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotNull
import org.valiktor.functions.matches
import validator.Validatable

@Serializable
data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null
) : Validatable {
    override fun validate() {
        org.valiktor.validate(this) {
            validate(UpdateUserRequest::firstName).isNotNull().hasSize(min = 2, max = 100)
            validate(UpdateUserRequest::lastName).isNotNull().hasSize(min = 2, max = 100)
            validate(UpdateUserRequest::phone).isNotNull().hasSize(min = 10, max = 20)
                .matches(Regex("^\\+?[0-9\\s\\-()]+$"))
        }
    }
}