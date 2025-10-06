package model.entity

import model.table.UserActivityLogs
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserActivityLogEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserActivityLogEntity>(UserActivityLogs)

    var userId by UserActivityLogs.userId
    var action by UserActivityLogs.action
    var details by UserActivityLogs.details
    var ipAddress by UserActivityLogs.ipAddress
    val createdAt by UserActivityLogs.createdAt
}