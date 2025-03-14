package algo.todo.global.security

import algo.todo.domain.user.entity.Users
import algo.todo.domain.user.repository.UserRepository
import algo.todo.global.exception.CustomException
import algo.todo.global.exception.ErrorType
import algo.todo.global.exception.FailureHandler
import algo.todo.global.response.DomainCode
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletResponse
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
    @Value("\${jwt.access-token-expiration}")
    private val accessTokenExpirationInMillis: Long,
    @Value("\${jwt.refresh-token-expiration}")
    private val refreshTokenExpirationInMillis: Long,
    private val userRepository: UserRepository,
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    companion object {
        private val log = LogManager.getLogger(JwtProvider::class.java)
    }

    fun generateAccessToken(users: Users): String {
        val now = Date()
        val expiration = Date(now.time + accessTokenExpirationInMillis)

        return Jwts.builder()
            .claim("id", users.id)
            .claim("token_type", TokenType.ACCESS)
            .subject(users.email)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }

    fun ensureValidToken(token: String) {
        getClaimsFromToken(token)
    }

    fun generateRefreshToken(users: Users): String {
        val now = Date()
        val expiration = Date(now.time + refreshTokenExpirationInMillis)

        return Jwts.builder()
            .claim("id", users.id)
            .claim("token_type", TokenType.REFRESH)
            .subject(users.email)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }

    // accessToken 을 통해 Authentication 객체를 반환
    fun getAuthentication(
        accessToken: String,
        response: HttpServletResponse,
    ): Result<Authentication?> =
        runCatching {
            val claims = getClaimsFromToken(accessToken)

            ensureIsAccessToken(claims)

            val user = getUserFromClaims(claims)

            val userDetails = CustomUserDetails(user, claims)

            UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities,
            )
        }.onFailure {
            FailureHandler.handleFailure(it, response)
        }

    private fun getUserFromClaims(claims: Map<String, Any>): Users {
        val idLong = claims["id"]
        val email = claims["sub"]

        if (idLong !is Int || email !is String) {
            throw CustomException(ErrorType.INVALID_TOKEN, DomainCode.COMMON)
        }

        val id = idLong.toLong()

        return checkNotNull(userRepository.findByIdAndEmail(id, email)) {
            log.warn("User not found")
            throw CustomException(ErrorType.INVALID_TOKEN, DomainCode.COMMON)
        }
    }

    private fun ensureIsAccessToken(claims: Map<String, Any>) {
        val tokenTypeString = claims["token_type"] as String
        val tokenType = TokenType.valueOf(tokenTypeString)

        if (tokenType != TokenType.ACCESS) {
            throw CustomException(ErrorType.INVALID_TOKEN, DomainCode.COMMON)
        }
    }

    private fun getClaimsFromToken(token: String): Map<String, Any> =
        runCatching {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        }.onFailure {
            log.warn("token parsing failed: ${it.message}")
            throw CustomException(ErrorType.INVALID_TOKEN, DomainCode.COMMON)
        }.getOrThrow()
}
