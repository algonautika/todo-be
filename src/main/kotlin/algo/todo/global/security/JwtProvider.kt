package algo.todo.global.security

import algo.todo.domain.user.entity.User
import algo.todo.domain.user.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration}")
    private val expirationInMillis: Long,

    private val userRepository: UserRepository
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    companion object {
        private val log = LogManager.getLogger(JwtProvider::class.java)
    }

    fun generateToken(user: User): String {
        val now = Date()
        val expiration = Date(now.time + expirationInMillis)

        return Jwts.builder()
            .claim("id", user.id)
            .subject(user.email)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }

    fun getAuthentication(accessToken: String): Authentication? {
        try {
            val claims = getClaimsFromToken(accessToken)
                .takeIf { it.isNotEmpty() }
                ?: return null

            val id = claims["id"] as Long
            val email = claims["sub"] as String

            val user = userRepository.findByIdAndEmail(id, email) ?: run {
                log.error("User not found")
                return null
            }

            val userDetails = CustomUserDetails(user, claims)
            return UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            )
        } catch (e: Exception) {
            log.error(e.printStackTrace())
            return null
        }
    }

    private fun getClaimsFromToken(token: String): Map<String, Any> {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
