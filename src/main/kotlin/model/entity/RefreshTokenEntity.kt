package model.entity

import model.table.RefreshTokens
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RefreshTokenEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RefreshTokenEntity>(RefreshTokens)

    var userId by RefreshTokens.userId
    var token by RefreshTokens.token
    var expiresAt by RefreshTokens.expiresAt
    val createdAt by RefreshTokens.createdAt
    var isRevoked by RefreshTokens.isRevoked
}