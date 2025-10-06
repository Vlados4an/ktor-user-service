package model.entity

import model.table.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var passwordHash by Users.passwordHash
    var firstName by Users.firstName
    var lastName by Users.lastName
    var phone by Users.phone
    var role by Users.role
    var status by Users.status
    var isDeleted by Users.isDeleted
    val createdAt by Users.createdAt
    val updatedAt by Users.updatedAt
}
