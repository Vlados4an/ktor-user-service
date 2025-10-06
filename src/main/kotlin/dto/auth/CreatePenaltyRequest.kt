package dto.auth

import kotlinx.serialization.Serializable
import org.valiktor.functions.isGreaterThan
import org.valiktor.functions.isNotBlank
import validator.Validatable
import java.math.BigDecimal

@Serializable
data class CreatePenaltyRequest(
    val userId: Int,
    val amount: String,
    val reason: String
) : Validatable {
    override fun validate() {
        org.valiktor.validate(this) {
            validate(CreatePenaltyRequest::userId).isGreaterThan(0)
            validate(CreatePenaltyRequest::amount).isNotBlank()
            validate(CreatePenaltyRequest::reason).isNotBlank()
        }

        val amountValue = BigDecimal(amount)
        require(amountValue > BigDecimal.ZERO) { "Amount must be positive" }
    }
}