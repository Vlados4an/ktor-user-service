package model.entity

import model.table.UserPenalties
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserPenaltyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserPenaltyEntity>(UserPenalties)

    var userId by UserPenalties.userId
    var amount by UserPenalties.amount
    var reason by UserPenalties.reason
    var isPaid by UserPenalties.isPaid
    var createdAt by UserPenalties.createdAt
    var paidAt by UserPenalties.paidAt
}