package algo.todo.global.security

import algo.todo.domain.user.entity.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration}")
    private val expirationInMillis: Long
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(user: User): String {
        val now = Date()
        val expiration = Date(now.time + expirationInMillis)

        return Jwts.builder()
            .subject(user.email)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }
}
