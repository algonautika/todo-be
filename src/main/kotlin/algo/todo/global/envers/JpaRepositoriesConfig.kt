package algo.todo.global.envers

import org.springframework.context.annotation.Configuration
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(
    basePackages = ["algo.todo"],
    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean::class
)
class JpaRepositoriesConfig

