package algo.todo.global.security

import algo.todo.domain.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomUserDetails(
    val user: User,
    val claims: Map<String, Any> // 커스텀 필드
) : UserDetails {
    override fun getUsername() = user.email
    override fun getPassword() = null
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}
