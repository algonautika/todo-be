package algo.todo.global.security

import algo.todo.domain.user.entity.Users
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomUserDetails(
    val users: Users,
    val claims: Map<String, Any>,
) : UserDetails {
    override fun getUsername() = users.email

    override fun getPassword() = null

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}
